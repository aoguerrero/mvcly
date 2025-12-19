package onl.andres.mvcly.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import onl.andres.mvcly.mock.ContextMock;
import onl.andres.mvcly.sample.SampleFormController;
import org.junit.Test;

import static onl.andres.mvcly.netty.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

public class FormControllerTest {

    @Test
    public void testFormController() {
        SampleFormController sampleFormController = new SampleFormController();
        sampleFormController.setRedirectPath("/path/to");
        ControllerHandler controllerHandler = getControllerHandler("/form", sampleFormController);
        ContextMock context = new ContextMock();
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, "/form");
        controllerHandler.channelRead0(context, request);
        ByteBuf buffer = Unpooled.copiedBuffer("id=123".getBytes());
        LastHttpContent content = new DefaultLastHttpContent(buffer);
        controllerHandler.channelRead0(context, content);
        assertEquals("/path/to", context.getLocationHeader());
    }
}
