package onl.andres.mvcly.cntr;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.HttpUtils;

public abstract class JsonController<I, O> implements BaseController {

	private Class<I> inputType;
	private Gson gson;

	protected JsonController(Class<I> type) {
		this.inputType = type;
		this.gson = new Gson();
	}

	public abstract O execute(HttpRequest request, I input);

	@Override
	public Response execute(HttpRequest request, byte[] body) {
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
		if (body.length == 0)
			throw new ServiceException.BadRequest();
		I input = gson.fromJson(new String(body, StandardCharsets.UTF_8), inputType);
		String output = gson.toJson(execute(request, input));
		return new Response(HttpResponseStatus.OK, headers, output.getBytes());
	}
}
