package onl.andres.mvcly;

import static onl.andres.mvcly.TestUtilities.getControllerHandler;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import onl.andres.mvcly.mock.ContextMock;
import onl.andres.mvcly.sample.SampleTemplateController;

public class TemplateControllerTest {

	@Test
	public void testTemplateController() {
		ControllerHandler controllerHandler = getControllerHandler("/template",
				new SampleTemplateController("classpath:///sample.vm", new Map<String, byte[]>() {
                    @Override
                    public int size() {
                        return 0;
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public boolean containsKey(Object o) {
                        return false;
                    }

                    @Override
                    public boolean containsValue(Object o) {
                        return false;
                    }

                    @Override
                    public byte[] get(Object o) {
                        return new byte[0];
                    }

                    @Override
                    public byte[] put(String redirectPath, byte[] bytes) {
                        return new byte[0];
                    }

                    @Override
                    public byte[] remove(Object o) {
                        return new byte[0];
                    }

                    @Override
                    public void putAll(Map<? extends String, ? extends byte[]> map) {

                    }

                    @Override
                    public void clear() {

                    }

                    @Override
                    public Set<String> keySet() {
                        return Set.of();
                    }

                    @Override
                    public Collection<byte[]> values() {
                        return List.of();
                    }

                    @Override
                    public Set<Entry<String, byte[]>> entrySet() {
                        return Set.of();
                    }
                }));
		ContextMock context = new ContextMock();
		DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/template");
		controllerHandler.channelRead0(context, request);
		LastHttpContent content = new DefaultLastHttpContent();
		controllerHandler.channelRead0(context, content);
		String output = new String(context.getContent(), StandardCharsets.UTF_8);
		assertEquals("<html>hello world</html>", output);
	}
}
