package net.jpkg.mvcly.sample;

import java.util.Map;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import net.jpkg.mvcly.ctrl.FormController;

public class SampleFormController extends FormController {

  public SampleFormController() {
  }

  @Override
  public void execute(HttpHeaders responseheaders, HttpRequest request, Map<String, String> formData) {
  }

}
