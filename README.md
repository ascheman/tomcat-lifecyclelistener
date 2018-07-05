# Tomcat Failstop Lifecycle Listener

[![Build Status](https://travis-ci.org/ascheman/tomcat-lifecyclelistener.svg?branch=master)](https://travis-ci.org/ascheman/tomcat-lifecyclelistener)

This component contains a Java class implementing the [Tomcat Lifecycle Listener](https://tomcat.apache.org/tomcat-7.0-doc/config/context.html#Lifecycle_Listeners) to 
shut down or exit Tomcat on a failed deployment.

Add it to Tomcat:
 
* Copy the JAR to the ```${catalina.base}/lib``` directory;
* Extend the configuration within the ```context.xml``` as

```xml
    <!-- Stop on failed deployments:
         - Set "force" to just perform a "System.exit" of the server JVM
         - Otherwise enable call to shutdown port, optionally
           - set port (default: 8005) and shutdown command (default: SHUTDOWN) as in server.xml
           - set waitForStart (default 30s) to make the shut down command wait until TC is completely up and running
    -->
    <Listener className="net.aschemann.tomcat.lifecycle.FailstopLifecycleListener" force="true" />
    <!-- port="29821" shutdown="bye" waitForStart="20" /> -->
```