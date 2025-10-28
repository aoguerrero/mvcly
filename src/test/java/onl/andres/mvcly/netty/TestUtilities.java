package onl.andres.mvcly.netty;

import java.util.HashMap;
import java.util.Map;

import onl.andres.mvcly.ctrl.BaseController;

public class TestUtilities {

	public static ControllerHandler getControllerHandler(String path, BaseController baseController) {
		Map<String, BaseController> controllers = new HashMap<>();
		controllers.put(path, baseController);
		return new ControllerHandler(controllers);
	}
}
