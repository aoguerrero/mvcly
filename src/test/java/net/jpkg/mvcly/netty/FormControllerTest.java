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
import net.jpkg.mvcly.mock.ContextMock;
import net.jpkg.mvcly.sample.SampleFormController;
import org.junit.Test;

public class FormControllerTest {

  @Test
  public void testFormControllerRedirects() {
    SampleFormController controller = new SampleFormController();
    controller.setTarget("/path/to");
    ControllerHandler handler = getControllerHandler("/form", controller);
    ContextMock context = new ContextMock();
    DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
        "/form");
    handler.channelRead0(context, request);
    ByteBuf buffer = Unpooled.copiedBuffer("id=123".getBytes());
    LastHttpContent content = new DefaultLastHttpContent(buffer);
    handler.channelRead0(context, content);
    assertEquals("/path/to", context.getLocationHeader());
    assertEquals(HttpResponseStatus.TEMPORARY_REDIRECT, context.getStatus());
  }
}
