package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.FileSystemUtils;
import onl.andres.mvcly.utl.HttpUtils;

import java.util.Map;
import java.util.Objects;

import static onl.andres.mvcly.core.MvclyParameters.ENABLE_CACHE;
import static onl.andres.mvcly.core.MvclyParameters.FILES_PATH;

public class StaticController implements BaseController {

	private String path;
	private Map<String, byte[]> staticMap;

	public Response execute(HttpRequest request, byte[] body) {
        Objects.requireNonNull(this.path, "Path not defined");
        Objects.requireNonNull(this.staticMap, "Static Map missing");

		String uri = request.uri();
		var params = HttpUtils.getUrlParams(uri);

		HttpHeaders headers = new DefaultHttpHeaders();
		String filePath = path;

		if (path.endsWith("/")) {
			String resPath = params.get("path");
			if (resPath == null || resPath.isEmpty() || resPath.contains("..") || resPath.contains(":")
					|| resPath.contains("//") || resPath.contains("\\") || resPath.startsWith("/"))
				throw new ServiceException.BadRequest();

			filePath = path + resPath;
			headers.add(HttpUtils.CONTENT_TYPE, HttpUtils.getContentType(filePath));
		}
		headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_3_MONTH);
		return new Response(HttpResponseStatus.OK, headers, getContent(filePath));
	}

	private byte[] getContent(String path) {
		if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
			this.staticMap.computeIfAbsent(path, FileSystemUtils::getContent);
			return this.staticMap.get(path);
		}
		return FileSystemUtils.getContent(path);
	}

    public void setStaticMap(Map<String, byte[]> staticMap) {
        this.staticMap = staticMap;
    }

    public void setPath(String path) {
        this.path = path.startsWith("files://") || path.startsWith("classpath://") ? path : FILES_PATH.get() + "/" + path;
    }
}
