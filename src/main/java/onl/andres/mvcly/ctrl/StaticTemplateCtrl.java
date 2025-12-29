package onl.andres.mvcly.ctrl;

import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

/**
 * Returns a static HTML response from a template defined in the field {@code templatePath}.
 * The template is not processed by the VelocityEngine.
 *
 * @see onl.andres.mvcly.ctrl.BaseTemplateCtrl
 */
public class StaticTemplateCtrl extends BaseTemplateCtrl {

    @Override
    public Map<String, Object> getContext(HttpRequest request) {
        return null;
    }
}
