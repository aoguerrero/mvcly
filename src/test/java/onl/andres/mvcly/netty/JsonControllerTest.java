package onl.andres.mvcly.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import onl.andres.mvcly.mock.ContextMock;
import onl.andres.mvcly.sample.SampleInput;
import onl.andres.mvcly.sample.SampleJsonController;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static onl.andres.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

public class JsonControllerTest {

	@Test
	public void testJsonController() {
        SampleJsonController sampleJsonRedirectController = new SampleJsonController();
        sampleJsonRedirectController.setInputType(SampleInput.class);
        ControllerHandler controllerHandler = getControllerHandler("/json",
                sampleJsonRedirectController);
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
