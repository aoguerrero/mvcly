package net.jpkg.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import net.jpkg.mvcly.mdl.Response;

/**
 * This is the super interface for all controllers, the {@code execute(HttpRequest, byte[])} is
 * called to get the HTTP response for the client.
 */
public abstract class BaseController {

  private String path;
  private String target;
  private String initialTarget;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
    if (initialTarget == null) {
      initialTarget = target;
    }
  }

  public String getInitialTarget() {
    return initialTarget;
  }

  public abstract Response execute(HttpRequest request, byte[] body);
}
