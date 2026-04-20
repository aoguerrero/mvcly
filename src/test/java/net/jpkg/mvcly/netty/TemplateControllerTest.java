package net.jpkg.mvcly.netty;

import static net.jpkg.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import net.jpkg.mvcly.mock.ContextMock;
import net.jpkg.mvcly.sample.SampleTemplateController;
import org.junit.Test;

public class TemplateControllerTest {

  @Test
  public void testTemplateController() {
    SampleTemplateController controller = new SampleTemplateController();
    controller.setTemplateMap(new HashMap<>());
    controller.setTarget("classpath:///sample.vm");
    ControllerHandler handler = getControllerHandler("/template", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/template");
    handler.channelRead0(context, request);
    LastHttpContent content = new DefaultLastHttpContent();
    handler.channelRead0(context, content);
    String output = new String(context.getContent(), StandardCharsets.UTF_8);
    assertEquals("<html>hello world</html>", output);
  }

  @Test
  public void testTemplateControllerReturnsHtmlContentType() {
    SampleTemplateController controller = new SampleTemplateController();
    controller.setTemplateMap(new HashMap<>());
    controller.setTarget("classpath:///sample.vm");
    ControllerHandler handler = getControllerHandler("/template", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/template");
    handler.channelRead0(context, request);
    handler.channelRead0(context, new DefaultLastHttpContent());

    assertEquals(HttpResponseStatus.OK, context.getStatus());
    assertEquals("text/html; charset=utf-8", context.getContentTypeHeader());
  }
}
