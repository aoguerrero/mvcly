package onl.andres.mvcly.sample;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.ctrl.JsonRedirectController;

public class SampleJsonRedirectController extends JsonRedirectController<SampleInput, SampleOutput> {

	@Override
	public SampleOutput execute(HttpRequest request, SampleInput input) {
		return new SampleOutput(input.firstParam() + "-" + input.secondParam());
	}
}
