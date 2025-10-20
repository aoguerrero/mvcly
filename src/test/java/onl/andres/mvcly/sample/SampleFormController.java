package onl.andres.mvcly.sample;

import java.util.Map;
import java.util.Optional;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.ctrl.FormController;

public class SampleFormController extends FormController {

	public SampleFormController(String path) {
		super(path);
	}

	@Override
	public Optional<String> execute(HttpRequest request, Map<String, String> formData) {
		String id = formData.get("id");
		return Optional.of(id);
	}

}
