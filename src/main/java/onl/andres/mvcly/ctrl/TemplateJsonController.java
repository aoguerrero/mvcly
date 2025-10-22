package onl.andres.mvcly.ctrl;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class TemplateJsonController<I, O> extends BaseTemplateCtrl {

    private Class<I> inputType;
    private Gson gson;

    public static final String CURRENT_PATH = "current_path";

    protected TemplateJsonController(String path, Class<I> type) {
        super(path);
        this.inputType = type;
        this.gson = new Gson();
    }

	protected TemplateJsonController(String path, Map<String, byte[]> templatesMap, Class<I> type) {
        super(path, templatesMap);
        this.inputType = type;
        this.gson = new Gson();
	}

	public abstract Map<String, Object> getContext(HttpRequest request, I input);

	public Response execute(HttpRequest request, byte[] body) {
        if (body.length == 0)
            throw new ServiceException.BadRequest();
        I input = gson.fromJson(new String(body, StandardCharsets.UTF_8), inputType);
		return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request, input)));
	}
}
