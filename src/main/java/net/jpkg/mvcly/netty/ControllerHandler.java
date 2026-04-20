package net.jpkg.mvcly.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.jpkg.mvcly.ctrl.BaseController;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.mdl.Response;
import net.jpkg.mvcly.utl.FileSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a basic HTTP server that looks for the appropriate controller based on the URL of the
 * request to delegate the response construction. It sends the request headers and the body contents
 * to the controller.
 * <p>
 * The constructor receives as parameter a {@code java.util.Map} where the key is a Regular
 * Expression that will be applied to the URL, if it matches, the Value of the Map that contains the
 * Controller implementing {@code BaseController} is used to process the request.
 * <p>
 * It also sends error responses to the client, accordingly to the exception received.
 *
 */
public class ControllerHandler extends SimpleChannelInboundHandler<Object> {

  private static final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

  private final List<Entry<Pattern, BaseController>> controllers;
  private HttpRequest request;
  private byte[] body = new byte[]{};

  public ControllerHandler(Map<String, ? extends BaseController> controllers) {
    this.controllers = new ArrayList<>();
    controllers.forEach((regex, ctrl) ->
        this.controllers.add(Map.entry(Pattern.compile(regex), ctrl)));
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, Object message) {
    try {
      messageReceived(context, message);
    } catch (ServiceException.BadRequest e) {
      writeError(context, BAD_REQUEST);
    } catch (ServiceException.NotFound e) {
      writeError(context, NOT_FOUND);
    } catch (ServiceException.Unauthorized e) {
      writeError(context, UNAUTHORIZED);
    } catch (ServiceException.InternalServer e) {
      writeError(context, INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error("Unexpected error", e);
      writeError(context, INTERNAL_SERVER_ERROR);
    }
  }

  private void messageReceived(ChannelHandlerContext context, Object message) {
    if (message instanceof HttpRequest httpRequest) {
      request = httpRequest;
    }
    if (message instanceof HttpContent httpContent) {
      body = readContent(httpContent.content());

      if (message instanceof LastHttpContent) {
        BaseController controller = getController(request.uri());
        Response response = controller.execute(request, body);
        writeResponse(context, response.status(), response.headers(), response.body());
        body = new byte[0];
      }
    }
  }

  private byte[] readContent(ByteBuf byteBuf) {
    byte[] buffer = new byte[byteBuf.readableBytes()];
    byteBuf.readBytes(buffer);
    byte[] joined = new byte[body.length + buffer.length];
    System.arraycopy(body, 0, joined, 0, body.length);
    System.arraycopy(buffer, 0, joined, body.length, buffer.length);
    return joined;
  }

  private BaseController getController(String uri) {
    for (Entry<Pattern, BaseController> entry : controllers) {
      if (entry.getKey().matcher(uri).matches()) {
        return entry.getValue();
      }
    }
    throw new ServiceException.NotFound();
  }

  private void writeResponse(ChannelHandlerContext context, HttpResponseStatus status,
      HttpHeaders headers,
      byte[] body) {
    FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status,
        Unpooled.copiedBuffer(body));
    httpResponse.headers().add(headers);
    context.write(httpResponse);
    context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
  }

  private void writeError(ChannelHandlerContext context, HttpResponseStatus status) {
    try {
      FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status,
          Unpooled.copiedBuffer(
              FileSystemUtils.getContent("classpath:///error/" + status.code() + ".html")));
      httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
      context.write(httpResponse);
      context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    } catch (Exception e) {
      logger.error("Exception loading error page", e);
      FullHttpResponse fallback = new DefaultFullHttpResponse(HTTP_1_1, status,
          Unpooled.copiedBuffer(status.toString().getBytes()));
      fallback.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
      context.write(fallback);
      context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause != null && !(cause instanceof SocketException)) {
      super.exceptionCaught(ctx, cause);
    }
  }
}