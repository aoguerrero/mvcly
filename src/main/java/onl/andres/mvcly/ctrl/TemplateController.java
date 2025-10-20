package onl.andres.mvcly.ctrl;

import static onl.andres.mvcly.ThinmvcParameters.*;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.ContentType;
import onl.andres.mvcly.utl.FileSystemUtils;
import onl.andres.mvcly.utl.HttpUtils;

public abstract class TemplateController implements BaseController {

	protected final String path;
	private Map<String, byte[]> templatesCache;
	private VelocityEngine velocityEngine;

	public static final String CURRENT_PATH = "current_path";

	protected TemplateController(String path) {
        this.path = path.startsWith("files://") || path.startsWith("classpath://") ? path : TEMPLATES_PATH.get() + "/" + path;
		templatesCache = new HashMap<>();

		velocityEngine = new VelocityEngine();
		if (FileSystemUtils.isClasspath(path)) {
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
			velocityEngine.init();
		}
	}

	public abstract Map<String, Object> getContext(HttpRequest request);

	public Response execute(HttpRequest request, byte[] body) {
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.add(HttpUtils.CONTENT_TYPE, ContentType.HTML.getStr());
		headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_NO_STORE);
		Map<String, Object> context = getContext(request);
		VelocityContext velocityContext = new VelocityContext(context);

		byte[] template = getTemplate(path);
		StringWriter writer = new StringWriter();

		velocityContext.put(CURRENT_PATH, FileSystemUtils.getDirectory(path));

		velocityEngine.evaluate(velocityContext, writer, "", new String(template, StandardCharsets.UTF_8));
		return new Response(HttpResponseStatus.OK, headers, writer.toString().getBytes(StandardCharsets.UTF_8));
	}

	private byte[] getTemplate(String path) {
		if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
			templatesCache.computeIfAbsent(path, FileSystemUtils::getContent);
			return templatesCache.get(path);
		}
		return FileSystemUtils.getContent(path);
	}
}
