package onl.andres.mvcly.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.SocketException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import onl.andres.mvcly.ctrl.BaseController;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.FileSystemUtils;

public class ControllerHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

	private Map<String, BaseController> controllers;

	public ControllerHandler(Map<String, BaseController> controllers) {
		this.controllers = controllers;
	}

	private HttpRequest request;
	private byte[] body = new byte[] {};

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
			BaseController controller = getController(request.uri());

			body = readContent(httpContent.content());

			if (message instanceof LastHttpContent) {
				Response response = controller.execute(request, body);
				writeResponse(context, response.status(), response.headers(), response.body());
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
		for (Entry<String, BaseController> entry : controllers.entrySet()) {
			if (Pattern.matches(entry.getKey(), uri)) {
				return entry.getValue();
			}
		}
		throw new ServiceException.NotFound();
	}

	private void writeResponse(ChannelHandlerContext context, HttpResponseStatus status, HttpHeaders headers,
			byte[] body) {
		FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(body));
		httpResponse.headers().add(headers);
		context.write(httpResponse);
		context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	private void writeError(ChannelHandlerContext context, HttpResponseStatus status) {
		try {
			FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status,
					Unpooled.copiedBuffer(FileSystemUtils.getContent("classpath:///error/" + status.code() + ".html")));
			context.write(httpResponse);
			context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		} catch (Exception e) {
			logger.error("Exception loading error page", e);
		}
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause != null && !(cause instanceof SocketException)) {
            super.exceptionCaught(ctx, cause);
        }
    }
}