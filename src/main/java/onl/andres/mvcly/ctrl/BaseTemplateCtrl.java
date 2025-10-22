package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.utl.ContentType;
import onl.andres.mvcly.utl.FileSystemUtils;
import onl.andres.mvcly.utl.HttpUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static onl.andres.mvcly.ThinmvcParameters.ENABLE_CACHE;
import static onl.andres.mvcly.ThinmvcParameters.TEMPLATES_PATH;

public abstract class BaseTemplateCtrl implements BaseController {

    public static final String CURRENT_PATH = "current_path";

    private final Map<String, byte[]> templatesMap;
    private final String path;
    private final VelocityEngine velocityEngine;

    public BaseTemplateCtrl(String path) {
        this(path, null);
    }

    public BaseTemplateCtrl(String path, Map<String, byte[]> templatesMap) {
        this.path = path.startsWith("files://") || path.startsWith("classpath://") ? path : TEMPLATES_PATH.get() + "/" + path;
        velocityEngine = new VelocityEngine();
        if (FileSystemUtils.isClasspath(path)) {
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        }
        velocityEngine.init();
        this.templatesMap = new HashMap<>();
    }

    public abstract Map<String, Object> getContext(HttpRequest request);

    protected HttpHeaders getHeaders() {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpUtils.CONTENT_TYPE, ContentType.HTML.getStr());
        headers.add(HttpUtils.CACHE_CONTROL, HttpUtils.CACHE_CONTROL_NO_STORE);
        return headers;
    }

    protected byte[] evaluateTemplate(Map<String, Object> context) {
        byte[] template = getTemplate(path);
        StringWriter writer = new StringWriter();
        VelocityContext velocityContext = new VelocityContext(context);
        velocityContext.put(CURRENT_PATH, FileSystemUtils.getDirectory(getPath()));
        velocityEngine.evaluate(velocityContext, writer, "", new String(template, StandardCharsets.UTF_8));
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getTemplate(String path) {
        if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
            templatesMap.computeIfAbsent(path, FileSystemUtils::getContent);
            return templatesMap.get(path);
        }
        return FileSystemUtils.getContent(path);
    }

    protected String getPath() {
        return this.path;
    }
}
