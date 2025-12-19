package onl.andres.mvcly.netty;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import onl.andres.mvcly.ctrl.BaseController;
import onl.andres.mvcly.mock.ContextMock;

public class ControllerHandlerTest {

	@Test
	public void testControllerHandlerError() {
		ControllerHandler controllerHandler = new ControllerHandler(new HashMap<String, BaseController>());
		ContextMock context = new ContextMock();
		DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/some-path");
		controllerHandler.channelRead0(context, request);
		ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {});
		LastHttpContent content = new DefaultLastHttpContent(buffer);
		controllerHandler.channelRead0(context, content);
		assertEquals(HttpResponseStatus.NOT_FOUND, context.getStatus());
	}
}
