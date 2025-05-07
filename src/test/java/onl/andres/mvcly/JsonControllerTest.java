package onl.andres.mvcly;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import onl.andres.mvcly.mock.ContextMock;
import onl.andres.mvcly.sample.SampleInput;
import onl.andres.mvcly.sample.SampleJsonController;
import static onl.andres.mvcly.TestUtilities.getControllerHandler;

public class JsonControllerTest {

	@Test
	public void testJsonController() {
		ControllerHandler controllerHandler = getControllerHandler("/json",
				new SampleJsonController(SampleInput.class));
		ContextMock context = new ContextMock();
		DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, "/json");
		controllerHandler.channelRead0(context, request);
		ByteBuf buffer = Unpooled.copiedBuffer("{\"firstParam\": \"123\", \"secondParam\": \"456\"}".getBytes());
		LastHttpContent content = new DefaultLastHttpContent(buffer);
		controllerHandler.channelRead0(context, content);
		String output = new String(context.getContent(), StandardCharsets.UTF_8);
		assertEquals("{\"result\":\"123-456\"}", output);
	}
}
