# MVCly

This is a small Java Web framework around the Netty library; it starts a web server, and it's ready for requests in around 3 seconds on a Raspberry Pi-like server.

Extending different base controller classes, you can publish:

- Static resources ([StaticController](https://github.com/aoguerrero/mvcly/blob/main/src/main/java/onl/andres/mvcly/cntr/StaticController.java))
- Dynamic pages using templates ([TemplateController](https://github.com/aoguerrero/mvcly/blob/main/src/main/java/onl/andres/mvcly/cntr/TemplateController.java))
- Process basic HTML forms ([FormController](https://github.com/aoguerrero/mvcly/blob/main/src/main/java/onl/andres/mvcly/cntr/FormController.java))
- Publish REST services ([JsonController](https://github.com/aoguerrero/mvcly/blob/main/src/main/java/onl/andres/mvcly/cntr/JsonController.java))
- Create redirects to handle routing ([RedirectController](https://github.com/aoguerrero/mvcly/blob/main/src/main/java/onl/andres/mvcly/cntr/RedirectController.java))

I created this framework because I needed something basic and lightweight for my web applications, without complex dependencies or front-end frameworks.

In future releases, I will include:

- SSL Support (this is currently supported with a NGINX server).
- File uploads in forms.
- Authentication.
