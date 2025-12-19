package onl.andres.mvcly.sample;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.ctrl.RedirectController;

public class SampleRedirectController extends RedirectController {

	public SampleRedirectController() {
	}

	@Override
	public int execute(HttpRequest request) {
		return 0;
	}
}
