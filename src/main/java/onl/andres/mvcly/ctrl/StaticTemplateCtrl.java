package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

public class StaticTemplateCtrl extends BaseTemplateCtrl {

    @Override
    public Map<String, Object> getContext(HttpRequest request) {
        return null;
    }

}
