# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

MVCly is a lightweight MVC web framework with a standalone HTTP server built on Netty. It uses regex-based path routing to map requests to controllers, includes a simple DI mechanism (`ControllerFactory`), and supports Velocity templates. Designed for fast startup (~3s on Raspberry Pi) and low memory footprint.

## Build & Test

- Build: `mvn clean package`
- Run all tests: `mvn test`
- Run a single test: `mvn test -Dtest=ControllerHandlerTest`
- Java 21 (`maven.compiler.release=21`)

## Architecture

Base package: `net.jpkg.mvcly`

### Request Flow

1. Netty pipeline (`HttpRequestDecoder` → `HttpResponseEncoder` → `ControllerHandler`)
2. `ControllerHandler` accumulates `HttpContent` chunks into the request body
3. On `LastHttpContent`, the URI is matched against pre-compiled regex patterns
4. The matching controller's `execute(HttpRequest, byte[])` is called
5. Controller returns a `Response` record (`HttpResponseStatus`, `HttpHeaders`, `byte[]`)
6. Response is written back to the client; `ServiceException` subtypes are mapped to appropriate HTTP error codes (400, 401, 404, 500) with custom HTML error pages

### Packages

| Package | Purpose |
|---------|---------|
| `core` | `Application` (server bootstrap), `MvclyParameters` (VM arg config) |
| `ctrl` | All controller types and `ControllerFactory` (DI) |
| `netty` | `ControllerHandler` (routing + request dispatch) |
| `mdl` | `Response` record |
| `excp` | `ServiceException` with HTTP status variants (`BadRequest`, `NotFound`, `Unauthorized`, `InternalServer`) |
| `utl` | `HttpUtils`, `ContentType`, `FileSystemUtils` |

### Controller Hierarchy

All controllers extend `BaseController`. Concrete types:

- **`JsonController<I,O>`** — Parses JSON request body into type `I`, returns type `O` serialized as JSON. Throws `BadRequest` on malformed JSON or empty body.
- **`FormController`** — Processes HTML form data, supports redirect after processing. Thread-safe (uses local variables for per-request state).
- **`StaticController`** — Serves static files from filesystem (`file://`), classpath (`classpath://`), or configurable files path. Includes path traversal protection and optional file caching.
- **`BaseTemplateCtrl`** — Base for Velocity template controllers with optional template caching
- **`JsonTemplateCtrl<I>`** — Combines JSON input parsing with Velocity template rendering
- **`StaticTemplateCtrl`** — Serves static HTML from Velocity template (no context)
- **`RedirectController`** — Simple HTTP redirect. Thread-safe.

### Routing & DI

- Controllers are registered via `ControllersConfig.add(controller, path, target)` where `path` is the regex pattern and `target` can rewrite the URL for downstream processing
- Regex patterns are pre-compiled at construction time for performance; routing order follows insertion order
- `ControllerFactory` provides setter-based DI: methods named `setDependency()` are detected and injected via reflection
- Configuration via VM arguments: `-Dport=8080`, `-Denable_cache=false`, `-Dtemplates_path=templates`, `-Dfiles_path=files`

### Path Prefix Conventions

| Prefix | Meaning |
|--------|---------|
| `classpath://` | Load resource from classpath |
| `file://` | Load resource from filesystem (used by `StaticController` and `BaseTemplateCtrl`) |

## Code Style

- Java 21, no external dependencies beyond Netty, Velocity, Gson, SLF4J, and JUnit 4
- Minimal abstraction — prefer concrete types over speculative generics
- Loggers: `private static final`

