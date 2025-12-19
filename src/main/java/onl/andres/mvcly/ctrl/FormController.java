package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class FormController implements BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    private String redirectPath;
    private HttpHeaders responseHeaders;

    public abstract void execute(HttpRequest request, Map<String, String> formData);

    @Override
    public Response execute(HttpRequest request, byte[] body) {
        if (this.redirectPath == null) {
            throw new IllegalStateException("Redirect Path not defined");
        }
        this.responseHeaders = new DefaultHttpHeaders();
        execute(request, HttpUtils.bodyToForm(body));
        this.responseHeaders.add(HttpUtils.LOCATION, this.redirectPath);
        return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, this.responseHeaders, new byte[]{});
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }
}
