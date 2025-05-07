# MVCly

This is a small Java Web framework around the Netty library; it starts a web server, and it's ready for requests in around 3 seconds on a Raspberry Pi-like server.

Extending different base controller classes, you can publish:

- Static resources (StaticController)
- Dynamic pages using templates (TemplateController)
- Process basic HTML forms (FormController)
- Publish REST services (JsonController)
- Create redirects to handle routing (RedirectController)

I created this framework because I needed something basic and lightweight for my web applications, without complex dependencies or front-end frameworks.

In future releases, I will include:

- SSL Support (this is currently supported with an NGINX server)
- File uploads in forms
- Authentication
