package onl.andres.mvcly.ctrl;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Receives a POST body with JSON content that is parsed into the input class and produces a response with HTML content
 * based on the template defined in the field {@code templatePath}.
 *
 * <p>
 * Implement the {@code Map<String, Object> getContext(HttpRequest, I)} method to define the logic and produce the
 * contents of the page.
 *
 * @param <I> Input Class
 * @see onl.andres.mvcly.ctrl.BaseTemplateCtrl
 */
public abstract class JsonTemplateCtrl<I> extends BaseTemplateCtrl {

    private Class<I> inputType;
    private Gson gson;

    protected JsonTemplateCtrl() {
        this.gson = new Gson();
    }

    public abstract Map<String, Object> getContext(HttpRequest request, I input);

    public Response execute(HttpRequest request, byte[] body) {
        if (body.length == 0)
            throw new ServiceException.BadRequest();
        I input = gson.fromJson(new String(body, StandardCharsets.UTF_8), inputType);
        return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request, input)));
    }

    public void setInputType(Class<I> inputType) {
        this.inputType = inputType;
    }
}
