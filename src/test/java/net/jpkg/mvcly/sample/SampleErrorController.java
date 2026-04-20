package net.jpkg.mvcly.sample;

import io.netty.handler.codec.http.HttpRequest;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.mdl.Response;

public class SampleErrorController extends net.jpkg.mvcly.ctrl.BaseController {

  private final RuntimeException exception;

  public SampleErrorController(RuntimeException exception) {
    this.exception = exception;
  }

  @Override
  public Response execute(HttpRequest request, byte[] body) {
    throw exception;
  }
}
