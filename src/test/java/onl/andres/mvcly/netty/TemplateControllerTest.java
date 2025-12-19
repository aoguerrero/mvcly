package onl.andres.mvcly.netty;

import io.netty.handler.codec.http.*;
import onl.andres.mvcly.mock.ContextMock;
import onl.andres.mvcly.sample.SampleTemplateController;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static onl.andres.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

public class TemplateControllerTest {

	@Test
	public void testTemplateController() {
        SampleTemplateController sampleTemplateController = new SampleTemplateController();
        sampleTemplateController.setTemplateMap(new HashMap<>());
        sampleTemplateController.setPath("classpath:///sample.vm");
		ControllerHandler controllerHandler = getControllerHandler("/template", sampleTemplateController);
		ContextMock context = new ContextMock();
		DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/template");
		controllerHandler.channelRead0(context, request);
		LastHttpContent content = new DefaultLastHttpContent();
		controllerHandler.channelRead0(context, content);
		String output = new String(context.getContent(), StandardCharsets.UTF_8);
		assertEquals("<html>hello world</html>", output);
	}
}
