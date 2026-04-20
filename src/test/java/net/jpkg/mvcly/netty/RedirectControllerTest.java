package net.jpkg.mvcly.netty;

import static net.jpkg.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import net.jpkg.mvcly.mock.ContextMock;
import net.jpkg.mvcly.sample.SampleRedirectController;
import org.junit.Test;

public class RedirectControllerTest {

  @Test
  public void testRedirectController() {
    SampleRedirectController controller = new SampleRedirectController();
    controller.setTarget("/new-path");
    ControllerHandler handler = getControllerHandler("/redirect", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
        "/redirect");
    handler.channelRead0(context, request);
    LastHttpContent content = new DefaultLastHttpContent();
    handler.channelRead0(context, content);
    assertEquals("/new-path", context.getLocationHeader());
    assertEquals(HttpResponseStatus.TEMPORARY_REDIRECT, context.getStatus());
  }
}
