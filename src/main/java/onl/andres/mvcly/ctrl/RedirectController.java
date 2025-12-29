package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Redirects to another page based on the field {@code redirectPath}.
 * <p>
 * Override method {@code String execute(HttpRequest)} to return a different final target path, based on your needs.
 */
public class RedirectController implements BaseController {

    private static Logger logger = LoggerFactory.getLogger(RedirectController.class);

    private String redirectPath;
    private HttpHeaders responseHeaders;

    public String execute(HttpRequest request) {
        return redirectPath;
    }

    @Override
    public Response execute(HttpRequest request, byte[] body) {
        this.responseHeaders = new DefaultHttpHeaders();
        String newPath = execute(request);
        responseHeaders.add(HttpUtils.LOCATION, newPath);
        return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[]{});
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }
}
