package onl.andres.mvcly.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {

    private static Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

    private Map<String, Object> dependencies;
    private Map<String, Object> otherDependencies;

    public ControllerFactory(Map<String, Object> dependencies) {
        this.dependencies = dependencies;
    }

    public BaseController getController(Class<? extends BaseController> clazz) throws Exception {
        return getController(clazz, Map.of());
    }

    public BaseController getController(Class<? extends BaseController> clazz, String methodName, Object value)
            throws Exception {
        return getController(clazz, Map.of(methodName, value));
    }

    public BaseController getController(Class<? extends BaseController> clazz, String methodName1, Object value1,
                                        String methodName2, Object value2) throws Exception {
        return getController(clazz, Map.of(methodName1, value1, methodName2, value2));
    }

    public BaseController getController(Class<? extends BaseController> clazz, String methodName1, Object value1,
                                        String methodName2, Object value2, String methodName3, Object value3)
            throws Exception {
        return getController(clazz, Map.of(methodName1, value1, methodName2, value2, methodName3, value3));
    }

    public BaseController getController(Class<? extends BaseController> clazz, Map<String, Object> otherDependencies)
            throws Exception {
        BaseController controller = clazz.getDeclaredConstructor().newInstance();
        if ((dependencies == null || dependencies.size() == 0) && (otherDependencies == null || otherDependencies.size() == 0)) {
            return controller;
        }
        HashMap<String, Object> dependenciesCopy = new HashMap<>(dependencies);
        dependenciesCopy.putAll(otherDependencies);
        for (Method method : clazz.getMethods()) {
            String clazzName = clazz.getName();
            String clazzMethodName = method.getName();
            for (Map.Entry<String, Object> entry : dependenciesCopy.entrySet()) {
                String setterName = getSetterName(entry.getKey());
                if (clazzMethodName.equals(setterName)) {
                    logger.info("Invoking method {}.{}()", clazzName, clazzMethodName);
                    method.invoke(controller, entry.getValue());
                }
            }
        }
        return controller;
    }

    private String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
