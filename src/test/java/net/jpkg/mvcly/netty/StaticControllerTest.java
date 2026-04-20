package net.jpkg.mvcly.netty;

import static net.jpkg.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import net.jpkg.mvcly.ctrl.StaticController;
import net.jpkg.mvcly.mock.ContextMock;
import org.junit.Test;

public class StaticControllerTest {

  @Test
  public void testStaticController() {
    StaticController staticController = new StaticController();
    staticController.setStaticMap(new HashMap<>());
    staticController.setTarget("classpath:///sample.html");
    ControllerHandler handler = getControllerHandler("/static", staticController);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/static");
    handler.channelRead0(context, request);
    LastHttpContent content = new DefaultLastHttpContent();
    handler.channelRead0(context, content);
    String output = new String(context.getContent(), StandardCharsets.UTF_8);
    assertEquals("<html></html>", output);
  }

  @Test
  public void testStaticControllerReturns200AndCacheControl() {
    StaticController staticController = new StaticController();
    staticController.setStaticMap(new HashMap<>());
    staticController.setTarget("classpath:///sample.html");
    ControllerHandler handler = getControllerHandler("/static", staticController);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/static");
    handler.channelRead0(context, request);
    handler.channelRead0(context, new DefaultLastHttpContent());

    assertEquals(HttpResponseStatus.OK, context.getStatus());
    assertNotNull(context.getContent());
  }

  @Test
  public void testPathTraversalWithDotDotReturns400() {
    StaticController staticController = new StaticController();
    staticController.setStaticMap(new HashMap<>());
    staticController.setTarget("classpath:///");
    ControllerHandler handler = getControllerHandler("/static/.*", staticController);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/static/?path=../../etc/passwd");
    handler.channelRead0(context, request);
    handler.channelRead0(context, new DefaultLastHttpContent());

    assertEquals(HttpResponseStatus.BAD_REQUEST, context.getStatus());
  }

  @Test
  public void testPathTraversalWithColonReturns400() {
    StaticController staticController = new StaticController();
    staticController.setStaticMap(new HashMap<>());
    staticController.setTarget("classpath:///");
    ControllerHandler handler = getControllerHandler("/static/.*", staticController);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/static/?path=something:evil");
    handler.channelRead0(context, request);
    handler.channelRead0(context, new DefaultLastHttpContent());

    assertEquals(HttpResponseStatus.BAD_REQUEST, context.getStatus());
  }

  @Test
  public void testPathTraversalWithLeadingSlashReturns400() {
    StaticController staticController = new StaticController();
    staticController.setStaticMap(new HashMap<>());
    staticController.setTarget("classpath:///");
    ControllerHandler handler = getControllerHandler("/static/.*", staticController);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/static/?path=/etc/passwd");
    handler.channelRead0(context, request);
    handler.channelRead0(context, new DefaultLastHttpContent());

    assertEquals(HttpResponseStatus.BAD_REQUEST, context.getStatus());
  }
}
