package onl.andres.mvcly.sample;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.ctrl.RedirectController;

public class SampleRedirectController extends RedirectController {

	public SampleRedirectController() {
	}

	@Override
	public String execute(HttpRequest request) {
		return super.execute(request);
	}
}
