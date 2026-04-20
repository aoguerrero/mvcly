package net.jpkg.mvcly.ctrl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.mdl.Response;

/**
 * Receives a POST body with JSON content that is parsed into the input class and produces a
 * response with HTML content based on the template defined in the field {@code templatePath}.
 *
 * @param <I> Input Class
 * @see BaseTemplateCtrl
 */
public abstract class JsonTemplateCtrl<I> extends BaseTemplateCtrl {

  private Class<I> inputType;
  private Gson gson;

  protected JsonTemplateCtrl() {
    this.gson = new Gson();
  }

  public abstract Map<String, Object> getContext(HttpRequest request, I input);

  public Response execute(HttpRequest request, byte[] body) {
    if (body.length == 0) {
      throw new ServiceException.BadRequest();
    }
    I input;
    try {
      input = gson.fromJson(new String(body, StandardCharsets.UTF_8), inputType);
    } catch (JsonSyntaxException e) {
      throw new ServiceException.BadRequest(e);
    }
    return new Response(HttpResponseStatus.OK, getHeaders(),
        evaluateTemplate(getContext(request, input)));
  }

  public void setInputType(Class<I> inputType) {
    this.inputType = inputType;
  }
}
