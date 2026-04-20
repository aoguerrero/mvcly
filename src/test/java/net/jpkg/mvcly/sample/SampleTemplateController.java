package net.jpkg.mvcly.sample;

import io.netty.handler.codec.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import net.jpkg.mvcly.ctrl.BaseTemplateCtrl;

public class SampleTemplateController extends BaseTemplateCtrl {

  public SampleTemplateController() {
  }

  @Override
  public Map<String, Object> getContext(HttpRequest request) {
    Map<String, Object> context = new HashMap<>();
    context.put("greeting", "hello world");
    return context;
  }

}
