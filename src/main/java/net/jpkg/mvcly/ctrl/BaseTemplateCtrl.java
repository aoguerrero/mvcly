package net.jpkg.mvcly.ctrl;

import static net.jpkg.mvcly.core.MvclyParameters.ENABLE_CACHE;
import static net.jpkg.mvcly.core.MvclyParameters.TEMPLATES_PATH;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.jpkg.mvcly.mdl.Response;
import net.jpkg.mvcly.utl.ContentType;
import net.jpkg.mvcly.utl.FileSystemUtils;
import net.jpkg.mvcly.utl.HttpUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns an HTML response from a template defined in the field {@code templatePath}.
 * <p>
 * To define the contents (context) of the velocity template, the method
 * {@code Map<String, Object> getContext(HttpRequest)} should be implemented, the key-values will be
 * passed to the velocity engine to render the template.
 * <p>
 * The value of {@code templatePath} can start with:
 * <ul>
 * <li>{@code files://} the template is taken as an absolute path in the filesystem.</li>
 * <li>{@code classpath://} the template is taken as a classpath resource.</li>
 * <li>Any other path, the path is concatenated to the value of the parameter {@code templates_path}.</li>
 * </ul>
 * <p>
 * The templates can be cached on-demand, storing every new file in the {@code Map} passed in the field
 * {@code Map<String, byte[]> templateMap}, also the parameter {@code enable_cache} needs to be set to {@code true}.
 *
 * @see <a href="https://velocity.apache.org/engine/2.4.1/vtl-reference.html">Apache Velocity
 * Documentation</a>
 */
public abstract class BaseTemplateCtrl extends BaseController {

  private static final String CURRENT_PATH = "current_path";
  private static final Logger logger = LoggerFactory.getLogger(BaseTemplateCtrl.class);
  private final VelocityEngine velocityEngine;
  private Map<String, byte[]> templateMap;

  public BaseTemplateCtrl() {
    velocityEngine = new VelocityEngine();
    velocityEngine.init();
  }

  public abstract Map<String, Object> getContext(HttpRequest request);

  protected HttpHeaders getHeaders() {
    HttpHeaders headers = new DefaultHttpHeaders();
    headers.add(HttpUtils.CONTENT_TYPE, ContentType.HTML.getStr());
    headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_NO_STORE);
    return headers;
  }

  protected byte[] evaluateTemplate(Map<String, Object> context) {
    byte[] template = getTemplate(getTarget());
    if (context == null) {
      return template;
    }
    if (FileSystemUtils.isClasspath(getTarget())) {
      velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
    }
    StringWriter writer = new StringWriter();
    VelocityContext velocityContext = new VelocityContext(context);
    velocityContext.put(CURRENT_PATH, FileSystemUtils.getDirectory(getTarget()));
    velocityEngine.evaluate(velocityContext, writer, "",
        new String(template, StandardCharsets.UTF_8));
    return writer.toString().getBytes(StandardCharsets.UTF_8);
  }

  private byte[] getTemplate(String path) {
    if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
      if (this.templateMap != null) {
        return templateMap.computeIfAbsent(path, FileSystemUtils::getContent);
      }
      logger.warn("Cached enabled but no Map provided, getting contents from disk.");
    }
    return FileSystemUtils.getContent(path);
  }

  public Response execute(HttpRequest request, byte[] body) {
    return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request)));
  }

  public void setTemplateMap(Map<String, byte[]> templateMap) {
    this.templateMap = templateMap;
  }

  @Override
  public void setTarget(String target) {
    super.setTarget(target.startsWith("file://") || target.startsWith("classpath://") ? target
        : TEMPLATES_PATH.get() + "/" + target);
  }
}
