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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static onl.andres.mvcly.core.MvclyParameters.ENABLE_CACHE;
import static onl.andres.mvcly.core.MvclyParameters.TEMPLATES_PATH;

public abstract class BaseTemplateCtrl implements BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseTemplateCtrl.class);

    private static final String CURRENT_PATH = "current_path";

    private Map<String, byte[]> templateMap;
    private String templatePath;

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
        byte[] template = getTemplate(this.templatePath);
        if(context == null) {
            return template;
        }
        if (FileSystemUtils.isClasspath(this.templatePath)) {
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        }
        StringWriter writer = new StringWriter();
        VelocityContext velocityContext = new VelocityContext(context);
        velocityContext.put(CURRENT_PATH, FileSystemUtils.getDirectory(this.templatePath));
        velocityEngine.evaluate(velocityContext, writer, "", new String(template, StandardCharsets.UTF_8));
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getTemplate(String path) {
        if (Boolean.parseBoolean(ENABLE_CACHE.get())) {
            if(this.templateMap != null) {
                templateMap.computeIfAbsent(path, FileSystemUtils::getContent);
                return templateMap.get(path);
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

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath.startsWith("file://") || templatePath.startsWith("classpath://") ? templatePath : TEMPLATES_PATH.get() + "/" + templatePath;
    }
}
