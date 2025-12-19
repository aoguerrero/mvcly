package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.mdl.Response;

public interface BaseController {
	
	Response execute(HttpRequest request, byte[] body);

}
