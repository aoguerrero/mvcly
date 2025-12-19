package onl.andres.mvcly.ctrl;

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

	protected JsonController() {
		this.gson = new Gson();
	}

	public abstract O execute(HttpRequest request, I input);

	@Override
	public Response execute(HttpRequest request, byte[] body) {
        if(this.inputType == null) {
            throw new IllegalStateException("Input Type not defined");
        }
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
		if (body.length == 0)
			throw new ServiceException.BadRequest();
		I input = this.gson.fromJson(new String(body, StandardCharsets.UTF_8), this.inputType);
		String output = this.gson.toJson(execute(request, input));
		return new Response(HttpResponseStatus.OK, headers, output.getBytes());
	}

    public void setInputType(Class<I> inputType) {
        this.inputType = inputType;
    }
}
