package onl.andres.mvcly.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {

    private static Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

    private Map<String, Object> baseDependencies;
    private Map<String, Object> otherDependencies;

    public ControllerFactory(Map<String, Object> baseDependencies) {
        this.baseDependencies = baseDependencies;
    }

    public ControllerFactory() {
        this.baseDependencies = Map.of();
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

        HashMap<String, Object> allDependencies = new HashMap<>();
        allDependencies.putAll(baseDependencies);
        allDependencies.putAll(otherDependencies);

        if (allDependencies.size() == 0) {
            return controller;
        }

        String clazzName = clazz.getName();
        for (Method method : clazz.getMethods()) {
            String clazzMethodName = method.getName();
            if (clazzMethodName.startsWith("set")) {
                Object dependency = allDependencies.get(fieldName(clazzMethodName));
                if (dependency != null) {
                    logger.info("Invoking method {}.{}()", clazzName, clazzMethodName);
                    method.invoke(controller, dependency);
                }
            }
        }
        return controller;
    }

    private String fieldName(String setterName) {
        if(setterName.startsWith("set")) {
            return setterName.substring(3, 4).toLowerCase() + setterName.substring(4);
        }
        throw new IllegalArgumentException();
    }
}
