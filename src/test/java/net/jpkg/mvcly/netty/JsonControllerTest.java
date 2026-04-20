package net.jpkg.mvcly.netty;

import static net.jpkg.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import java.nio.charset.StandardCharsets;
import net.jpkg.mvcly.mock.ContextMock;
import net.jpkg.mvcly.sample.SampleInput;
import net.jpkg.mvcly.sample.SampleJsonController;
import org.junit.Test;

public class JsonControllerTest {

  @Test
  public void testJsonController() {
    SampleJsonController controller = new SampleJsonController();
    controller.setInputType(SampleInput.class);
    ControllerHandler handler = getControllerHandler("/json", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
        "/json");
    handler.channelRead0(context, request);
    ByteBuf buffer = Unpooled.copiedBuffer(
        "{\"firstParam\": \"123\", \"secondParam\": \"456\"}".getBytes());
    LastHttpContent content = new DefaultLastHttpContent(buffer);
    handler.channelRead0(context, content);
    String output = new String(context.getContent(), StandardCharsets.UTF_8);
    assertEquals("{\"result\":\"123-456\"}", output);
  }

  @Test
  public void testJsonControllerReturnsCorrectStatusAndContentType() {
    SampleJsonController controller = new SampleJsonController();
    controller.setInputType(SampleInput.class);
    ControllerHandler handler = getControllerHandler("/json", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
        "/json");
    handler.channelRead0(context, request);
    ByteBuf buffer = Unpooled.copiedBuffer(
        "{\"firstParam\": \"a\", \"secondParam\": \"b\"}".getBytes());
    handler.channelRead0(context, new DefaultLastHttpContent(buffer));

    assertEquals(HttpResponseStatus.OK, context.getStatus());
    assertEquals("application/json", context.getContentTypeHeader());
  }

  @Test
  public void testJsonControllerEmptyBodyReturns400() {
    SampleJsonController controller = new SampleJsonController();
    controller.setInputType(SampleInput.class);
    ControllerHandler handler = getControllerHandler("/json", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
        "/json");
    handler.channelRead0(context, request);
    handler.channelRead0(context, new DefaultLastHttpContent());

    assertEquals(HttpResponseStatus.BAD_REQUEST, context.getStatus());
  }

  @Test
  public void testJsonControllerMalformedJsonReturns400() {
    SampleJsonController controller = new SampleJsonController();
    controller.setInputType(SampleInput.class);
    ControllerHandler handler = getControllerHandler("/json", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
        "/json");
    handler.channelRead0(context, request);
    ByteBuf buffer = Unpooled.copiedBuffer("not valid json".getBytes());
    handler.channelRead0(context, new DefaultLastHttpContent(buffer));

    assertEquals(HttpResponseStatus.BAD_REQUEST, context.getStatus());
  }
}
