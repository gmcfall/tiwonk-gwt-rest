tiwonk-gwt-rest
===============

Provides a generator useful for accessing REST services from a GWT client application.

Here's the approach in a nutshell:

1. Define an interface for accessing REST services in accordance with JAX-RS (JSR 311).
2. Define classes for your REST resources using JSNI.
3. Use GWT.create(YourInterface.class) to create a GWT client for your REST service.

The [example project] (https://github.com/gmcfall/tiwonk-gwt-rest/tree/master/tiwonk-gwt-rest-example) 
shows how this works in practice.

See [PersonClientResource.java] (https://github.com/gmcfall/tiwonk-gwt-rest/tree/master/tiwonk-gwt-rest-example/src/main/java/org/tiwonk/rest/example/person/client)
for an example that shows how to define an interface for a REST service. 
Notice that the interface must extend ClientResource.

See [Person.java] (https://github.com/gmcfall/tiwonk-gwt-rest/blob/master/tiwonk-gwt-rest-example/src/main/java/org/tiwonk/rest/example/person/client/Person.java)
for an example of a REST resource implemented with JSNI.

See [PersonEntryPoint.java} (https://github.com/gmcfall/tiwonk-gwt-rest/blob/master/tiwonk-gwt-rest-example/src/main/java/org/tiwonk/rest/example/person/client/PersonEntryPoint.java)
for an example that shows how to use the REST API in GWT.