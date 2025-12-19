package onl.andres.mvcly.ctrl;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class JsonTemplateCtrl<I, O> extends BaseTemplateCtrl {

    private Class<I> inputType;
    private Gson gson;

	protected JsonTemplateCtrl() {
        this.gson = new Gson();
	}

	public abstract Map<String, Object> getContext(HttpRequest request, I input);

	public Response execute(HttpRequest request, byte[] body) {
        if(inputType == null) {
            throw new IllegalStateException("Input Type not defined");
        }
        if (body.length == 0)
            throw new ServiceException.BadRequest();
        I input = gson.fromJson(new String(body, StandardCharsets.UTF_8), inputType);
		return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request, input)));
	}

    public void setInputType(Class<I> inputType) {
        this.inputType = inputType;
    }
}
