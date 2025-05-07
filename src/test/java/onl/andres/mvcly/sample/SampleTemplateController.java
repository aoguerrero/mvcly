package onl.andres.mvcly.sample;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.cntr.TemplateController;

public class SampleTemplateController extends TemplateController {

	public SampleTemplateController(String path) {
		super(path);
	}

	@Override
	public Map<String, Object> getContext(HttpRequest request) {
		Map<String, Object> context = new HashMap<>();
		context.put("greeting", "hello world");
		return context;
	}

}
