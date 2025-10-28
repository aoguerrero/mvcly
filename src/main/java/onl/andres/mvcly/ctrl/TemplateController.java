package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.core.AppCtx;
import onl.andres.mvcly.mdl.Response;

public abstract class TemplateController extends BaseTemplateCtrl {

    protected TemplateController(String path, AppCtx ctx) {
        super(path, ctx);
    }

	public Response execute(HttpRequest request, byte[] body) {
		return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request)));
	}
}
