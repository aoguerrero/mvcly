package net.jpkg.mvcly.ctrl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.charset.StandardCharsets;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.mdl.Response;
import net.jpkg.mvcly.utl.HttpUtils;

/**
 * Class to implement REST webservices, receives a POST body with JSON content that is parsed into
 * the input class and produces a response with JSON content from the output class.
 * <p>
 * Implement the {@code execute(HttpRequest, I)} method to define the webservice logic.
 *
 * @param <I> Input Class
 * @param <O> Output Class
 */
public abstract class JsonController<I, O> extends BaseController {

  private Class<I> inputType;
  private Gson gson;

  protected JsonController() {
    this.gson = new Gson();
  }

  public abstract O execute(HttpRequest request, I input);

  @Override
  public Response execute(HttpRequest request, byte[] body) {
    HttpHeaders headers = new DefaultHttpHeaders();
    headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
    if (body.length == 0) {
      throw new ServiceException.BadRequest();
    }
    I input;
    try {
      input = this.gson.fromJson(new String(body, StandardCharsets.UTF_8), this.inputType);
    } catch (JsonSyntaxException e) {
      throw new ServiceException.BadRequest(e);
    }
    String output = this.gson.toJson(execute(request, input));
    return new Response(HttpResponseStatus.OK, headers, output.getBytes());
  }

  public void setInputType(Class<I> inputType) {
    this.inputType = inputType;
  }
}
