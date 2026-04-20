package net.jpkg.mvcly.ctrl;

import java.util.Map;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.jpkg.mvcly.mdl.Response;
import net.jpkg.mvcly.utl.HttpUtils;

/**
 * Receives a POST request with form data and redirects the client to the path
 * specified in the redirectPath field.
 * <p>
 * Implement method {@code execute(HttpRequest, Map<String, String>} to process
 * the contents of the source form.
 * <p>
 */
public abstract class FormController extends BaseController {

  public abstract void execute(HttpHeaders responseHeaders, HttpRequest request, Map<String, String> formData);

  @Override
  public Response execute(HttpRequest request, byte[] body) {
    this.setTarget(this.getInitialTarget());
    HttpHeaders responseHeaders = new DefaultHttpHeaders();
    execute(responseHeaders, request, HttpUtils.bodyToForm(body));
    responseHeaders.add(HttpUtils.LOCATION, getTarget());
    return new Response(HttpResponseStatus.TEMPORARY_REDIRECT, responseHeaders, new byte[] {});
  }

}
