package onl.andres.mvcly;

import static onl.andres.mvcly.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import onl.andres.mvcly.mock.ContextMock;
import onl.andres.mvcly.sample.SampleFormController;

public class FormControllerTest {

	@Test
	public void testFormController() {
		ControllerHandler controllerHandler = getControllerHandler("/form", new SampleFormController("/path/to/{id}"));
		ContextMock context = new ContextMock();
		DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, "/form");
		controllerHandler.channelRead0(context, request);
		ByteBuf buffer = Unpooled.copiedBuffer("id=123".getBytes());
		LastHttpContent content = new DefaultLastHttpContent(buffer);
		controllerHandler.channelRead0(context, content);
		assertEquals("/path/to/123", context.getLocationHeader());
	}
}
