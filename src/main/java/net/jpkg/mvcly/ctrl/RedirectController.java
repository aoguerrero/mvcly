package net.jpkg.mvcly.ctrl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.jpkg.mvcly.mdl.Response;
import net.jpkg.mvcly.utl.HttpUtils;

/**
 * Redirects to another page based on the field {@code redirectPath}.
 * <p>
 * Override method {@code execute(HttpRequest)} to return a different target
 * path.
 */
public abstract class RedirectController extends BaseController {

  public abstract void execute(HttpHeaders responseheaders, HttpRequest request);

  @Override
  public Response execute(HttpRequest request, byte[] body) {
    this.setTarget(this.getInitialTarget());
    HttpHeaders responseheaders = new DefaultHttpHeaders();
    this.execute(responseheaders, request);
    responseheaders.add(HttpUtils.LOCATION, getTarget());
    return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseheaders, new byte[] {});
  }

}
