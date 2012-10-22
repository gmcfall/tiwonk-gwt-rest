tiwonk-gwt-rest
===============

Provides a generator useful for accessing REST services from a GWT client application.

Here's the approach in a nutshell:

1. Define an interface for accessing REST services in accordance with JAX-RS (JSR 311).
2. Define classes for your REST resources using JSNI.
3. Use GWT.create(YourInterface.class) to create a GWT client for your REST service.

The [example project] (../tiwonk-gwt-rest-example) shows how this works in practice.