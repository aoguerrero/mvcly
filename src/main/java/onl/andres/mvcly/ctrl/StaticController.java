package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.FileSystemUtils;
import onl.andres.mvcly.utl.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static onl.andres.mvcly.core.MvclyParameters.ENABLE_CACHE;
import static onl.andres.mvcly.core.MvclyParameters.FILES_PATH;

/**
 * Returns a static resource based on the field {@code resourcePath}. If the defined path starts with:
 * <ul>
 * <li>{@code files://} the resource is taken as an absolute path in the filesystem.</li>
 * <li>{@code classpath://} the resource is taken as a classpath resource.</li>
 * <li>Any other path, the path is concatenated to the value of the parameter {@code files_path} with a {@code /}.</li>
 * </ul>
 * <p>
 * To allow caching, pass the {@code Map<String, byte[]> staticMap} and set {@code true} the parameter
 * {@code enable_cache}.
 */
public class StaticController implements BaseController {

    private static Logger logger = LoggerFactory.getLogger(StaticController.class);

    private String resourcePath;
    private Map<String, byte[]> staticMap;

    public Response execute(HttpRequest request, byte[] body) {
        String uri = request.uri();
        var params = HttpUtils.getUrlParams(uri);

        HttpHeaders headers = new DefaultHttpHeaders();
        if (resourcePath == null) {
            setResourcePath("");
        }
        String filePath = resourcePath;

        if (resourcePath.endsWith("/")) {
            String resPath = params.get("path");
            if (resPath == null || resPath.isEmpty() || resPath.contains("..") || resPath.contains(":")
                    || resPath.contains("//") || resPath.contains("\\") || resPath.startsWith("/"))
                throw new ServiceException.BadRequest();

            filePath = resourcePath + resPath;
            headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.getContentType(filePath));
        }
        headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_3_MONTH);
        return new Response(HttpResponseStatus.OK, headers, getContent(filePath));
    }

    private byte[] getContent(String path) {
        if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
            if (this.staticMap != null) {
                this.staticMap.computeIfAbsent(path, FileSystemUtils::getContent);
                return this.staticMap.get(path);
            }
            logger.warn("Cached enabled but no Map provided, getting contents from disk.");
        }
        return FileSystemUtils.getContent(path);
    }

    public void setStaticMap(Map<String, byte[]> staticMap) {
        this.staticMap = staticMap;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath.startsWith("files://") || resourcePath.startsWith("classpath://") ? resourcePath : FILES_PATH.get() + "/" + resourcePath;
    }
}
