package onl.andres.mvcly;

import static onl.andres.mvcly.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import onl.andres.mvcly.ctrl.StaticController;
import onl.andres.mvcly.mock.ContextMock;

public class StaticControllerTest {

	@Test
	public void testStaticController() {
		ControllerHandler controllerHandler = getControllerHandler("/static",
				new StaticController("classpath:///sample.html"));
		ContextMock context = new ContextMock();
		DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/static");
		controllerHandler.channelRead0(context, request);
		LastHttpContent content = new DefaultLastHttpContent();
		controllerHandler.channelRead0(context, content);
		String output = new String(context.getContent(), StandardCharsets.UTF_8);
		assertEquals("<html></html>", output);
	}
}
