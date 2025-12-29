package onl.andres.mvcly.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a lightweight (cheap) dependency injector, it takes the keys and values from {@code baseDependencies} and
 * execute the set(Key) method of the controller if is available with the corresponding value.
 * <p>
 * Additional dependencies are injected using the {@code getController} methods, receiving a pair or pairs of
 * key and values.
 * <p>
 * For example, if {@code baseDependencies} contains a key "dataSource" with an Object {@code DataSource} the method
 * {@code setDataSource} will be executed in each controller, setting the same Object.
 * <p>
 * This class doesn't check types, a {@code RuntimeException} can be produced if the set(Key) method expects a different
 * type.
 * <p>
 * Use this class to create instances of controllers instead of calling the constructors.
 */
public class ControllerFactory {

    private static Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

    private Map<String, Object> baseDependencies;

    public ControllerFactory(Map<String, Object> baseDependencies) {
        this.baseDependencies = baseDependencies;
    }

    public ControllerFactory() {
        this.baseDependencies = Map.of();
    }

    public BaseController getController(Class<? extends BaseController> clazz) throws Exception {
        return getController(clazz, Map.of());
    }

    public BaseController getController(Class<? extends BaseController> clazz, String field, Object value)
            throws Exception {
        return getController(clazz, Map.of(field, value));
    }

    public BaseController getController(Class<? extends BaseController> clazz, String field1, Object value1,
                                        String field2, Object value2) throws Exception {
        return getController(clazz, Map.of(field1, value1, field2, value2));
    }

    public BaseController getController(Class<? extends BaseController> clazz, String field1, Object value1,
                                        String field2, Object value2, String field3, Object value3)
            throws Exception {
        return getController(clazz, Map.of(field1, value1, field2, value2, field3, value3));
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

    private String fieldName(String methodName) {
        if (methodName.startsWith("set")) {
            return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        }
        throw new IllegalArgumentException();
    }
}
