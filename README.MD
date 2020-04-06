## Dropwizard Sample App  

After migrating from Dropwizard 0.7.1 to 0.8.5, on application startup the list of registered paths printed in
the console did not include paths from sub-resources. Worth-mentioning that from version 0.7.x to 0.8.x, the
major upgrade is Jersey from 1.x to 2.x. 

```java
-- HelloResource.java (Main Resource)
---- HelloSubResource.java (Sub Resource)
------ HelloSubSubResource.java (Sub Resource)
```

When building this application with dropwizard 0.7.1 and running the application, in IntelliJ console the following
is printed


```shell script
...
INFO  [2020-04-06 18:20:27,364] io.dropwizard.jersey.DropwizardResourceConfig: The following paths were found for the configured resources:

    GET     /api/manager (test.dropwizard.resources.HelloResource)
    GET     /api/manager/greetings (test.dropwizard.resources.HelloSubResource)
    GET     /api/manager/greetings/{greetingId}/sub-greetings (test.dropwizard.resources.HelloSubResource)
...
```
When updating the dropwizard version to `<dropwizard.version>0.8.5</dropwizard.version>` we get the following output

```shell script
...
INFO  [2020-04-06 18:23:22,347] io.dropwizard.jersey.DropwizardResourceConfig: The following paths were found for the configured resources:

    GET     /api/manager (test.dropwizard.resources.HelloResource)

INFO  [2020-04-06 18:23:22,348] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@6a1ef65c{/,null,AVAILABLE}
...
```

The `/api/manager` endpoint is listed because the `HelloResource.java` has a `@Path` and the paths in the sub-resources
are not discovered. 

## Possibles Fixes
I did a ton of googling and I could not find an exisitng issue/solution on this and I wonder it is because, it's a minor 
issue and the sub-resource paths are still reachable. They are just not logged.

After some research I found out 2 solutions 

1. Fork the `DropwizardResourceConfig.java` of 0.8.5 with some additions (changes)
2. Simply replace the `DropwizardResourceConfig.java` of 0.8.5 with Dropwizard version 1.3.x which seem to work without any changes

I feel more comfortable with (1) instead of (2).

### Forking DropwizardResourceConfig
The issue seem to be in the `DropwizardResourceConfig.java` of Dropwizard 0.8.5 version and after some debugging it's 
possible fix this by forking the file and adding the following changes

- The private method `populateEndpoints(String basePath, Class<?> klass, boolean isLocator)` would change to do null check on `Resource.from(klass)`
- The private method `private void populateEndpoints(String basePath, Class<?> klass, boolean isLocator, Resource resource)` 
would change to recursively call the method if the Resource is a sub-resource

```java
// Replaces Line 151, See https://github.com/dropwizard/dropwizard/blob/release/0.8.x/dropwizard-jersey/src/main/java/io/dropwizard/jersey/DropwizardResourceConfig.java#L150
Resource resource;
if ((resource=Resource.from(klass)) != null) {
    this.populateEndpoints(basePath, klass, isLocator, resource);
}
``` 

and 
```java
// Added to line 171, See https://github.com/dropwizard/dropwizard/blob/release/0.8.x/dropwizard-jersey/src/main/java/io/dropwizard/jersey/DropwizardResourceConfig.java#L172
// Outside inner for loop and inside outer loop - for (Resource childResource : resource.getChildResources()) 
ResourceMethod method = childResource.getResourceLocator();
if (childResource.getResourceLocator() != null) {
    if (method.getType() == ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR) {
        final String path = this.normalizePath(basePath, childResource.getPath());
        this.populateEndpoints(path, method.getInvocable().getRawResponseType(), true);
    }
}
```
For a sub-resource `childResource.getResourceMethods()` is empty hence execution does not reach the following `else if`
block 
```java
else if (method.getType() == ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR) {
    final String path = normalizePath(basePath, childResource.getPath());
    populateEndpoints(path, method.getInvocable().getRawResponseType(), true);
}
```
That results in sub-resource paths not getting logged to the console on application start. 

