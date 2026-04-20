package net.jpkg.mvcly.ctrl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import net.jpkg.mvcly.sample.SampleFormController;
import net.jpkg.mvcly.sample.SampleJsonController;
import net.jpkg.mvcly.sample.SampleInput;
import org.junit.Test;

public class ControllerFactoryTest {

  @Test
  public void testCreateWithNoDependencies() throws Exception {
    ControllerFactory factory = new ControllerFactory();
    BaseController controller = factory.getController(SampleFormController.class);
    assertNotNull(controller);
    assertTrue(controller instanceof SampleFormController);
  }

  @Test
  public void testCreateWithBaseDependencies() throws Exception {
    Map<String, Object> baseDeps = new HashMap<>();
    baseDeps.put("path", "/test-path");
    ControllerFactory factory = new ControllerFactory(baseDeps);

    BaseController controller = factory.getController(SampleFormController.class);
    assertEquals("/test-path", controller.getPath());
  }

  @Test
  public void testCreateWithAdditionalDependencies() throws Exception {
    ControllerFactory factory = new ControllerFactory();
    BaseController controller = factory.getController(SampleJsonController.class, "inputType",
        SampleInput.class);
    assertNotNull(controller);
    assertTrue(controller instanceof SampleJsonController);
  }

  @Test
  public void testBaseAndAdditionalDependenciesMerged() throws Exception {
    Map<String, Object> baseDeps = new HashMap<>();
    baseDeps.put("path", "/base-path");
    ControllerFactory factory = new ControllerFactory(baseDeps);

    BaseController controller = factory.getController(SampleFormController.class, "target",
        "/target-path");
    assertEquals("/base-path", controller.getPath());
    assertEquals("/target-path", controller.getTarget());
  }

  @Test
  public void testAdditionalDependencyOverridesBase() throws Exception {
    Map<String, Object> baseDeps = new HashMap<>();
    baseDeps.put("path", "/base-path");
    ControllerFactory factory = new ControllerFactory(baseDeps);

    BaseController controller = factory.getController(SampleFormController.class, "path",
        "/override-path");
    assertEquals("/override-path", controller.getPath());
  }

  @Test
  public void testEmptyBaseDependencies() throws Exception {
    ControllerFactory factory = new ControllerFactory();
    BaseController controller = factory.getController(SampleFormController.class);
    assertNotNull(controller);
  }

  @Test
  public void testUnknownDependencyIgnored() throws Exception {
    Map<String, Object> baseDeps = new HashMap<>();
    baseDeps.put("nonExistentDependency", "value");
    ControllerFactory factory = new ControllerFactory(baseDeps);

    BaseController controller = factory.getController(SampleFormController.class);
    assertNotNull(controller);
  }
}
