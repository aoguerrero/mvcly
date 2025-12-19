package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import onl.andres.mvcly.mdl.Response;
import onl.andres.mvcly.utl.ContentType;
import onl.andres.mvcly.utl.FileSystemUtils;
import onl.andres.mvcly.utl.HttpUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static onl.andres.mvcly.core.MvclyParameters.ENABLE_CACHE;
import static onl.andres.mvcly.core.MvclyParameters.TEMPLATES_PATH;

public abstract class BaseTemplateCtrl implements BaseController {

    private static final String CURRENT_PATH = "current_path";

    private Map<String, byte[]> templateMap;
    private String path;

    private final VelocityEngine velocityEngine;

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
        Objects.requireNonNull(this.path, "Path not defined");
        Objects.requireNonNull(this.templateMap, "Template Map missing");

        byte[] template = getTemplate(this.path);
        if(context == null) {
            return template;
        }
        if (FileSystemUtils.isClasspath(this.path)) {
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        }
        StringWriter writer = new StringWriter();
        VelocityContext velocityContext = new VelocityContext(context);
        velocityContext.put(CURRENT_PATH, FileSystemUtils.getDirectory(this.path));
        velocityEngine.evaluate(velocityContext, writer, "", new String(template, StandardCharsets.UTF_8));
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getTemplate(String path) {
        if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
            templateMap.computeIfAbsent(path, FileSystemUtils::getContent);
            return templateMap.get(path);
        }
        return FileSystemUtils.getContent(path);
    }

    public Response execute(HttpRequest request, byte[] body) {
        return new Response(HttpResponseStatus.OK, getHeaders(), evaluateTemplate(getContext(request)));
    }

    public void setTemplateMap(Map<String, byte[]> templateMap) {
        this.templateMap = templateMap;
    }

    public void setPath(String path) {
        this.path = path.startsWith("file://") || path.startsWith("classpath://") ? path : TEMPLATES_PATH.get() + "/" + path;
    }
}
