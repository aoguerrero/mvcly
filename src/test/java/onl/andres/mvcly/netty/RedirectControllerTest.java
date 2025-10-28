package onl.andres.mvcly.netty;

import static onl.andres.mvcly.netty.TestUtilities.getControllerHandler;
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
import onl.andres.mvcly.sample.SampleRedirectController;

public class RedirectControllerTest {

	@Test
	public void testRedirectlController() {
		ControllerHandler controllerHandler = getControllerHandler("/redirect",
				new SampleRedirectController("/new-path"));
		ContextMock context = new ContextMock();
		DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/redirect");
		controllerHandler.channelRead0(context, request);
		ByteBuf buffer = Unpooled.copiedBuffer(new byte[] {});
		LastHttpContent content = new DefaultLastHttpContent(buffer);
		controllerHandler.channelRead0(context, content);
		assertEquals("/new-path", context.getLocationHeader());
	}
}
