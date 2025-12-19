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

public abstract class RedirectController implements BaseController {

    private static Logger logger = LoggerFactory.getLogger(RedirectController.class);

    private String[] redirectPaths;
    private HttpHeaders responseHeaders;

    public abstract int execute(HttpRequest request);

    @Override
    public Response execute(HttpRequest request, byte[] body) {
        Objects.requireNonNull(redirectPaths, "Redirect Paths not defined");

        this.responseHeaders = new DefaultHttpHeaders();
        int index = execute(request);
        if (index < redirectPaths.length) {
            responseHeaders.add(HttpUtils.LOCATION, redirectPaths[index]);
            return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[]{});
        } else {
            logger.error("Invalid index of Redirect Path");
            throw new ServiceException.InternalServer();
        }
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setRedirectPaths(String... redirectPaths) {
        this.redirectPaths = redirectPaths;
    }
}
