package onl.andres.mvcly.cntr;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.mdl.Response;

public interface BaseController {
	
	public Response execute(HttpRequest request, byte[] body);

}
