package onl.andres.mvcly.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppCtx {

    private Map<String, byte[]> templatesMap;

    public AppCtx() {
        this.templatesMap = new ConcurrentHashMap<>();
    }

    public Map<String, byte[]> getTemplatesMap() {
        return this.templatesMap;
    }
}
