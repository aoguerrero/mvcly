package net.jpkg.mvcly.ctrl;

import java.util.HashMap;
import java.util.Map;

public class ControllersConfig {

  private Map<String, BaseController> controllers;

  public ControllersConfig() {
    controllers = new HashMap<>();
  }

  public void add(BaseController controller, String path, String target) {
    controller.setPath(path);
    controller.setTarget(target);
    this.controllers.put(path, controller);
  }

  public Map<String, BaseController> getControllers() {
    return controllers;
  }
}
