# MVCly

A lightweight MVC web framework with a standalone HTTP server (Netty), template engine (Velocity), and basic IoC container. Starts in ~3 seconds on a Raspberry Pi.

## Requirements

- Java 21+
- Maven

## Build & Test

```bash
mvn clean package
mvn test
mvn test -Dtest=ControllerHandlerTest
```

## Dependencies

- Netty 4.2 (HTTP server)
- Velocity 2.4 (template engine)
- Gson 2.12 (JSON serialization)
- SLF4J 2.0 (logging)

## Quick Start

```java
import net.jpkg.mvcly.core.Application;
import net.jpkg.mvcly.ctrl.ControllersConfig;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ControllersConfig config = new ControllersConfig();

        // Register controllers with a regex path pattern and optional target
        config.add(new HelloController(), "/hello", "hello.vm");
        config.add(new ApiUserController(), "/api/users", null);
        config.add(new StaticController(), "/static/.*", "static/");

        new Application().start(config);
    }
}
```

Run with optional VM arguments:

```bash
java -Dport=8080 -Denable_cache=true -jar app.jar
```

## Configuration

MVCly is configured via JVM startup parameters:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `-Dport` | `8080` | Server port |
| `-Denable_cache` | `false` | Enable file/template caching |
| `-Dtemplates_path` | `templates` | Base directory for Velocity templates |
| `-Dfiles_path` | `files` | Base directory for static files |

## Routing

Routes are regex patterns matched against the request URI in registration order. The first matching controller handles the request. If no route matches, a 404 error is returned.

```java
// Exact match
config.add(controller, "/hello", null);

// Regex patterns for dynamic routes
config.add(userCtrl, "/users/\\d+", null);
config.add(postCtrl, "/posts/\\w+/\\d+", null);

// Catch-all prefix
config.add(staticCtrl, "/static/.*", "static/");
```

The `target` parameter in `add()` sets an initial value used by certain controller types (templates, redirects, static files). Pass `null` if not needed.

## Controllers

All controllers extend `BaseController` and implement the `execute(HttpRequest, byte[])` method. Choose a base class based on your use case:

### JsonController<I, O>

REST endpoint that deserializes a JSON request body into type `I` and serializes the return type `O` as JSON. Throws `BadRequest` on empty body or malformed JSON.

```java
public record UserRequest(String name, int age) {}
public record UserResponse(String message) {}

public class UserApi extends JsonController<UserRequest, UserResponse> {

    public UserApi() {
        setInputType(UserRequest.class);
    }

    @Override
    public UserResponse execute(HttpRequest request, UserRequest input) {
        return new UserResponse("Hello, " + input.name());
    }
}
```

Registration:

```java
config.add(new UserApi(), "/api/user", null);
```

### FormController

Processes HTML form submissions (`application/x-www-form-urlencoded`) and redirects the client. Thread-safe (per-request state is local).

```java
public class ContactForm extends FormController {

    @Override
    public void execute(HttpHeaders responseHeaders, HttpRequest request,
                        Map<String, String> formData) {
        String name = formData.get("name");
        String email = formData.get("email");
        // process form data...

        // Optionally change redirect target (defaults to initialTarget)
        setTarget("/thank-you");
    }
}
```

Registration — the `target` sets the default redirect path:

```java
config.add(new ContactForm(), "/contact", "/contact");
```

### RedirectController

Returns an HTTP 307 redirect. Override `execute()` to dynamically choose the redirect target.

```java
public class LoginRedirect extends RedirectController {

    @Override
    public void execute(HttpHeaders responseHeaders, HttpRequest request) {
        setTarget("/dashboard");
    }
}
```

Registration — the `target` sets the default redirect path:

```java
config.add(new LoginRedirect(), "/old-login", "/login");
```

### BaseTemplateCtrl

Renders HTML using Apache Velocity templates. Implement `getContext()` to provide template variables.

Path prefixes for the `target`:
- `file://` — absolute filesystem path
- `classpath://` — classpath resource
- Anything else — resolved relative to `templates_path`

```java
public class HomeController extends BaseTemplateCtrl {

    @Override
    public Map<String, Object> getContext(HttpRequest request) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("title", "Home");
        ctx.put("items", List.of("A", "B", "C"));
        return ctx;
    }
}
```

Registration — the `target` is the template file path:

```java
// Relative to templates_path
config.add(new HomeController(), "/", "home.vm");

// Absolute filesystem path
config.add(new HomeController(), "/", "file:///opt/templates/home.vm");

// Classpath resource
config.add(new HomeController(), "/", "classpath://templates/home.vm");
```

Template (`templates/home.vm`):

```html
<html>
<body>
    <h1>$title</h1>
    <ul>
    #foreach($item in $items)
        <li>$item</li>
    #end
    </ul>
</body>
</html>
```

### StaticTemplateCtrl

Serves a static HTML template without Velocity processing. Extends `BaseTemplateCtrl` — the `target` follows the same path prefix rules.

```java
config.add(new StaticTemplateCtrl(), "/about", "about.vm");
```

### JsonTemplateCtrl<I>

Combines JSON input parsing with Velocity template rendering. Implement `getContext(HttpRequest, I)` to build the template context from the parsed JSON input.

```java
public record SearchQuery(String term) {}

public class SearchPage extends JsonTemplateCtrl<SearchQuery> {

    public SearchPage() {
        setInputType(SearchQuery.class);
    }

    @Override
    public Map<String, Object> getContext(HttpRequest request, SearchQuery input) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("results", searchService.search(input.term()));
        return ctx;
    }
}
```

### StaticController

Serves static files from the filesystem or classpath. Includes path traversal protection (rejects `..`, `//`, `\`, `:`, and leading `/`).

When the `target` ends with `/`, the controller maps the request path into that directory. Path prefix rules:

- `file://` — absolute filesystem path
- `classpath://` — classpath resource
- Anything else — resolved relative to `files_path`

```java
// Serve files from files_path/static/
config.add(new StaticController(), "/static/.*", "static/");

// Serve from absolute path
config.add(new StaticController(), "/assets/.*", "file:///var/www/assets/");

// Serve a classpath resource
config.add(new StaticController(), "/favicon\\.ico", "classpath://favicon.ico");
```

Supported content types: CSS, HTML, JS, TXT, GIF, ICO, JPEG, JPG, PNG, SVG, PDF, JSON, ZIP. Unknown extensions default to `text/plain`.

## Dependency Injection

`ControllerFactory` provides setter-based injection via reflection. Methods named `setXxx()` are detected and invoked with matching dependency keys.

```java
// Shared dependencies injected into all controllers
Map<String, Object> baseDeps = new HashMap<>();
baseDeps.put("userService", new UserService());
baseDeps.put("templateMap", new ConcurrentHashMap<String, byte[]>());

ControllerFactory factory = new ControllerFactory(baseDeps);

// Controller with setUserService(UserService) and setTemplateMap(Map) will receive these
BaseController ctrl = factory.getController(MyController.class);

// Add per-controller overrides
BaseController ctrl = factory.getController(MyController.class, "customDep", someObject);

// Up to 3 additional dependencies
BaseController ctrl = factory.getController(
    MyController.class,
    "dep1", value1,
    "dep2", value2,
    "dep3", value3
);

// With a map of dependencies
BaseController ctrl = factory.getController(MyController.class, Map.of("dep", value));
```

Matching convention: a dependency key `"userService"` maps to the method `setUserService(...)`. Additional dependencies override base dependencies with the same key.

## Caching

File and template caching is disabled by default. To enable it:

1. Set the VM argument: `-Denable_cache=true`
2. Inject a `Map<String, byte[]>` into the controller via DI:

```java
Map<String, byte[]> cache = new ConcurrentHashMap<>();
baseDeps.put("staticMap", cache);       // for StaticController (method: setStaticMap)
baseDeps.put("templateMap", cache);     // for BaseTemplateCtrl (method: setTemplateMap)
```

## Error Handling

Throw `ServiceException` subtypes from any controller to return the corresponding HTTP error:

| Exception | HTTP Status |
|-----------|-------------|
| `ServiceException.BadRequest` | 400 |
| `ServiceException.Unauthorized` | 401 |
| `ServiceException.NotFound` | 404 |
| `ServiceException.InternalServer` | 500 |

```java
@Override
public UserResponse execute(HttpRequest request, UserRequest input) {
    if (input.name() == null) {
        throw new ServiceException.BadRequest();
    }
    if (!userExists(input.name())) {
        throw new ServiceException.NotFound();
    }
    return new UserResponse("Found: " + input.name());
}
```

Unmatched routes automatically throw `NotFound`. Unhandled exceptions produce a 500 response.

### Custom Error Pages

Place HTML files in `classpath:///error/` named by status code: `400.html`, `401.html`, `404.html`, `500.html`. If a template is missing, a plain text fallback with the status description is returned.

## Response

Controllers ultimately return a `Response` record:

```java
public record Response(HttpResponseStatus status, HttpHeaders headers, byte[] body) {}
```

The base controller classes construct this for you, but you can also return a custom `Response` directly by overriding `execute(HttpRequest, byte[])` in `BaseController`.

## Utility Methods

`HttpUtils` provides static helpers:

- `bodyToForm(byte[])` — parse form-encoded body into `Map<String, String>`
- `getUrlParams(String url)` — extract query parameters from a URL
- `getParams(String body)` — parse `key=value&key=value` string
- `cookiesToMap(String cookieStr)` — parse Cookie header into `Map<String, String>`
- `getContentType(String path)` — resolve content type from file extension
