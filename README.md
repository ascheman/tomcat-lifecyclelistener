# Tomcat Failstop Lifecycle Listener

[![Build Status](https://travis-ci.org/ascheman/tomcat-lifecyclelistener.svg?branch=master)](https://travis-ci.org/ascheman/tomcat-lifecyclelistener)

This component contains a Java class implementing the [Tomcat Lifecycle
Listener](https://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Lifecycle_Listeners) to shut down or exit Tomcat
on a failed deployment.

Add it to Tomcat:
 
* Download [tomcat-lifecyclelistener.jar](https://repo1.maven.org/maven2/net/aschemann/tomcat/tomcat-lifecyclelistener/1.0.1/tomcat-lifecyclelistener-1.0.1.jar) and put it into the `${catalina.base}/lib` directory
* Extend the configuration within the `context.xml` as

```xml
    <!-- Stop on failed deployments:
         - Set "force" to just perform a "System.exit" of the server JVM
         - Otherwise enable call to shutdown port, optionally
           - set port (default: 8005) and shutdown password (default: SHUTDOWN) as in server.xml
           - set waitForStart (default 30s) to make the shut down command wait until TC is completely up and running
    -->
    <Listener className="net.aschemann.tomcat.lifecycle.FailstopLifecycleListener" />
```

The listener will send a `SHUTDOWN` password to Tomcat's 8005 port and Tomcat will try to make a gracefully shutdown.
The port and password are configured in `${catalina.base}/conf/server.xml` file like this:
```xml
    <Server port="8005" shutdown="SHUTDOWN">
```

For security reasons port and the shutdown password may be changed so you can override it as well as other defaults:
```xml
    <Listener className="net.aschemann.tomcat.lifecycle.FailstopLifecycleListener" port="29821" 
        shutdown="Hasta la vista, baby!" waitForStart="20" />
```

Or if you need to exit without gracefully shutdown you can use the `force` attribute:
```xml
    <Listener className="net.aschemann.tomcat.lifecycle.FailstopLifecycleListener" force="true" />
```

Optionally you can also add the library to your app's `pom.xml`:
```xml
<dependency>
    <groupId>net.aschemann.tomcat</groupId>
    <artifactId>tomcat-lifecyclelistener</artifactId>
    <version>1.0.1</version>
    <scope>provided</scope>
</dependency>
```

Thus you'll be able to debug the `FailstopLifecycleListener` class.
