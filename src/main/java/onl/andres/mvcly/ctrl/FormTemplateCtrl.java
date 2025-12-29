package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.HttpUtils;

import java.util.Map;

/**
 * Receives a POST request with form data and redirects the client to a page that is rendered using the template defined
 * in the {@code templatePath} field.
 * <p>
 * Implement method {@code Map<String, Object> getContext(HttpRequest, Map<String, String>} to process the contents of
 * the source form and populate the variables of the target template.
 *
 * @see onl.andres.mvcly.ctrl.BaseTemplateCtrl
 */
public abstract class FormTemplateCtrl extends BaseTemplateCtrl {

    public abstract Map<String, Object> getContext(HttpRequest request, Map<String, String> formData);

    public Response execute(HttpRequest request, byte[] body) {
        return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request,
                HttpUtils.bodyToForm(body))));
    }
}
