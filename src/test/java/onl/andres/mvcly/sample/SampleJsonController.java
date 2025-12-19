package onl.andres.mvcly.sample;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.ctrl.JsonController;

public class SampleJsonController extends JsonController<SampleInput, SampleOutput> {

	@Override
	public SampleOutput execute(HttpRequest request, SampleInput input) {
		return new SampleOutput(input.firstParam() + "-" + input.secondParam());
	}
}
