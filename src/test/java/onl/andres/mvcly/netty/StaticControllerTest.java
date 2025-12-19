package onl.andres.mvcly.netty;

import io.netty.handler.codec.http.*;
import onl.andres.mvcly.ctrl.StaticController;
import onl.andres.mvcly.mock.ContextMock;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static onl.andres.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

public class StaticControllerTest {

    @Test
    public void testStaticController() {
        StaticController staticController = new StaticController();
        staticController.setStaticMap(new HashMap<>());
        staticController.setResourcePath("classpath:///sample.html");
        ControllerHandler controllerHandler = getControllerHandler("/static",
                staticController);
        ContextMock context = new ContextMock();
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/static");
        controllerHandler.channelRead0(context, request);
        LastHttpContent content = new DefaultLastHttpContent();
        controllerHandler.channelRead0(context, content);
        String output = new String(context.getContent(), StandardCharsets.UTF_8);
        assertEquals("<html></html>", output);
    }
}
