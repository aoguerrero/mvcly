package onl.andres.mvcly.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import onl.andres.mvcly.mock.ContextMock;
import onl.andres.mvcly.sample.SampleRedirectController;
import org.junit.Test;

import static onl.andres.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

public class RedirectControllerTest {

    @Test
    public void testRedirectController() {
        SampleRedirectController sampleRedirectController = new SampleRedirectController();
        sampleRedirectController.setRedirectPath("/new-path");
        ControllerHandler controllerHandler = getControllerHandler("/redirect",
                sampleRedirectController);
        ContextMock context = new ContextMock();
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/redirect");
        controllerHandler.channelRead0(context, request);
        ByteBuf buffer = Unpooled.copiedBuffer(new byte[]{});
        LastHttpContent content = new DefaultLastHttpContent(buffer);
        controllerHandler.channelRead0(context, content);
        assertEquals("/new-path", context.getLocationHeader());
    }
}
