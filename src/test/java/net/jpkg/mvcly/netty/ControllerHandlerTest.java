package net.jpkg.mvcly.netty;

import static net.jpkg.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import java.util.HashMap;
import net.jpkg.mvcly.ctrl.BaseController;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.mock.ContextMock;
import net.jpkg.mvcly.sample.SampleErrorController;
import net.jpkg.mvcly.sample.SampleInput;
import net.jpkg.mvcly.sample.SampleJsonController;
import org.junit.Test;

public class ControllerHandlerTest {

  @Test
  public void testNoMatchingControllerReturns404() {
    ControllerHandler controllerHandler = new ControllerHandler(
        new HashMap<String, BaseController>());
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/some-path");
    controllerHandler.channelRead0(context, request);
    LastHttpContent content = new DefaultLastHttpContent();
    controllerHandler.channelRead0(context, content);
    assertEquals(HttpResponseStatus.NOT_FOUND, context.getStatus());
  }

  @Test
  public void testBadRequestReturns400() {
    SampleErrorController controller = new SampleErrorController(new ServiceException.BadRequest());
    ControllerHandler handler = getControllerHandler("/error", controller);
    ContextMock context = sendRequest(handler, "/error");

    assertEquals(HttpResponseStatus.BAD_REQUEST, context.getStatus());
    assertEquals("text/html", context.getContentTypeHeader());
  }

  @Test
  public void testUnauthorizedReturns401() {
    SampleErrorController controller = new SampleErrorController(
        new ServiceException.Unauthorized());
    ControllerHandler handler = getControllerHandler("/error", controller);
    ContextMock context = sendRequest(handler, "/error");

    assertEquals(HttpResponseStatus.UNAUTHORIZED, context.getStatus());
  }

  @Test
  public void testInternalErrorReturns500() {
    SampleErrorController controller = new SampleErrorController(
        new ServiceException.InternalServer());
    ControllerHandler handler = getControllerHandler("/error", controller);
    ContextMock context = sendRequest(handler, "/error");

    assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR, context.getStatus());
  }

  @Test
  public void testUnexpectedExceptionReturns500() {
    SampleErrorController controller = new SampleErrorController(
        new RuntimeException("unexpected"));
    ControllerHandler handler = getControllerHandler("/error", controller);
    ContextMock context = sendRequest(handler, "/error");

    assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR, context.getStatus());
  }

  @Test
  public void testRegexRoutingMatchesPath() {
    SampleErrorController controller = new SampleErrorController(new ServiceException.BadRequest());
    ControllerHandler handler = getControllerHandler("/api/.*", controller);
    ContextMock context = sendRequest(handler, "/api/users/123");

    assertEquals(HttpResponseStatus.BAD_REQUEST, context.getStatus());
  }

  @Test
  public void testMultiChunkBodyIsAssembled() {
    SampleJsonController jsonController = new SampleJsonController();
    jsonController.setInputType(SampleInput.class);
    ControllerHandler handler = getControllerHandler("/json", jsonController);
    ContextMock context = new ContextMock();

    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
        "/json");
    handler.channelRead0(context, request);

    ByteBuf chunk1 = Unpooled.copiedBuffer("{\"firstPa".getBytes());
    handler.channelRead0(context, new DefaultHttpContent(chunk1));

    ByteBuf chunk2 = Unpooled.copiedBuffer("ram\": \"a\", \"secondParam\": \"b\"}".getBytes());
    handler.channelRead0(context, new DefaultLastHttpContent(chunk2));

    String output = new String(context.getContent(), java.nio.charset.StandardCharsets.UTF_8);
    assertEquals("{\"result\":\"a-b\"}", output);
  }

  @Test
  public void testError404ResponseHasHtmlContentType() {
    ControllerHandler handler = new ControllerHandler(new HashMap<>());
    ContextMock context = sendRequest(handler, "/nonexistent");

    assertEquals(HttpResponseStatus.NOT_FOUND, context.getStatus());
    assertEquals("text/html", context.getContentTypeHeader());
  }

  private ContextMock sendRequest(ControllerHandler handler, String uri) {
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
    handler.channelRead0(context, request);
    LastHttpContent content = new DefaultLastHttpContent();
    handler.channelRead0(context, content);
    return context;
  }
}
