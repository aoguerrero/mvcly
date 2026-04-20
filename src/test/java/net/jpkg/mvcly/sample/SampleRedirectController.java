package net.jpkg.mvcly.sample;


import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import net.jpkg.mvcly.ctrl.RedirectController;

public class SampleRedirectController extends RedirectController {

  public SampleRedirectController() {
  }

  @Override
  public void execute(HttpHeaders responseHeaders, HttpRequest request) {
  }
}
