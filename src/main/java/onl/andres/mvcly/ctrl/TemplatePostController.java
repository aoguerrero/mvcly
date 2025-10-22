package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.HttpUtils;

import java.util.Map;

public abstract class TemplatePostController extends BaseTemplateCtrl {

    protected TemplatePostController(String path) {
        super(path);
    }

	protected TemplatePostController(String path, Map<String, byte[]> templatesMap) {
        super(path, templatesMap);
	}

	public abstract Map<String, Object> getContext(HttpRequest request, Map<String, String> formData);

	public Response execute(HttpRequest request, byte[] body) {
		return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request, HttpUtils.bodyToForm(body))));
	}
}
