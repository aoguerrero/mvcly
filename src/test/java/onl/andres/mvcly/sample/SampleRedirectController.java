package onl.andres.mvcly.sample;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.cntr.RedirectController;

public class SampleRedirectController extends RedirectController {

	public SampleRedirectController(String path) {
		super(path);
	}

	@Override
	public String execute(HttpRequest request) {
		return "";
	}

}
