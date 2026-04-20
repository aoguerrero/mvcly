package net.jpkg.mvcly.ctrl;

import static net.jpkg.mvcly.core.MvclyParameters.ENABLE_CACHE;
import static net.jpkg.mvcly.core.MvclyParameters.FILES_PATH;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.Map;
import net.jpkg.mvcly.excp.ServiceException;
import net.jpkg.mvcly.mdl.Response;
import net.jpkg.mvcly.utl.FileSystemUtils;
import net.jpkg.mvcly.utl.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns a static resource based on the field {@code resourcePath}. If the defined path starts
 * with:
 * <ul>
 * <li>{@code file://} the resource is taken as an absolute path in the filesystem.</li>
 * <li>{@code classpath://} the resource is taken as a classpath resource.</li>
 * <li>Any other path, the path is concatenated to the value of the parameter {@code files_path} with a {@code /}.</li>
 * </ul>
 * <p>
 * To allow caching, set the field {@code Map<String, byte[]> staticMap} with a {@code Map} that will store the cached
 * values, and set {@code true} the parameter {@code enable_cache}.
 */
public class StaticController extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(StaticController.class);

  private Map<String, byte[]> staticMap;

  @Override
  public Response execute(HttpRequest request, byte[] body) {
    String uri = request.uri();
    var params = HttpUtils.getUrlParams(uri);

    HttpHeaders headers = new DefaultHttpHeaders();
    if (getTarget() == null) {
      setTarget("");
    }
    String filePath = getTarget();

    if (getTarget().endsWith("/")) {
      String resPath = params.get("path");
      if (resPath == null || resPath.isEmpty() || resPath.contains("..") || resPath.contains(":")
          || resPath.contains("//") || resPath.contains("\\") || resPath.startsWith("/")) {
        throw new ServiceException.BadRequest();
      }

      filePath = getTarget() + resPath;
      headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.getContentType(filePath));
    }
    headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_3_MONTH);
    return new Response(HttpResponseStatus.OK, headers, getContent(filePath));
  }

  private byte[] getContent(String path) {
    if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
      if (this.staticMap != null) {
        return this.staticMap.computeIfAbsent(path, FileSystemUtils::getContent);
      }
      logger.warn("Cached enabled but no Map provided, getting contents from disk.");
    }
    return FileSystemUtils.getContent(path);
  }

  public void setStaticMap(Map<String, byte[]> staticMap) {
    this.staticMap = staticMap;
  }

  @Override
  public void setTarget(String target) {
    super.setTarget(target.startsWith("file://") || target.startsWith("classpath://") ? target
        : FILES_PATH.get() + "/" + target);
  }
}
