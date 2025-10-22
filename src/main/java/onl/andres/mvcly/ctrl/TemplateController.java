package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.mdl.Response;

import java.util.Map;

public abstract class TemplateController extends BaseTemplateCtrl {

    protected TemplateController(String path) {
        super(path);
    }

	protected TemplateController(String path, Map<String, byte[]> templatesMap) {
        super(path, templatesMap);
	}

	public Response execute(HttpRequest request, byte[] body) {
		return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request)));
	}
}
