package net.aschemann.tomcat.lifecycle;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleState;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Apache Tomcat LifecycleListener which shuts down Tomcat after a failed WAR deployment.
 * Set "force" to just perform a "System.exit" of the server JVM
 * Otherwise enable call to shutdown port, optionally
 * set port (default: 8005) and shutdown command (default: SHUTDOWN) as in server.xml
 * set waitForStart (default 30s) to make the shut down command wait until TC is completely up and running
 *
 * @author Gerd Aschemann &lt;gerd@aschemann.net&gt;
 */
public class FailstopLifecycleListener implements LifecycleListener {

    private static Logger LOG = Logger.getLogger(FailstopLifecycleListener.class.getName());

    /**
     * The port of the shutdown listener of Tomcat - see server.xml
     */
    private int port = 8005;

    /**
     * The command string to send for shutdown - see server.xml
     */
    private String shutdown = "SHUTDOWN";

    /**
     * Force the shutdown via System.exit
     */
    private boolean force;

    /**
     * How long should we wait to start the server before the shutdown port might be open!
     */
    private int waitForStart = 30;

    public void lifecycleEvent(LifecycleEvent event) {
        String type = event.getType();
        Lifecycle lifecycle = event.getLifecycle();
        LifecycleState state = lifecycle.getState();

        String data = event.getData() != null ? event.getData().toString() : "";
        LOG.finer("Lifecycle Event '" + lifecycle.toString() +
                "', State = '" + state +
                "', Type = '" +  type +
                "', Data = '" + data +
                "' occured");

        if (LifecycleState.FAILED == state) {
            if (force) {
                LOG.severe("Deployment of '" + lifecycle + "' failed: Exiting Tomcat now");
                System.exit(1);
            } else {
                LOG.severe("Deployment of '" + lifecycle + "' failed: Shutting down Tomcat gracefully");
                new ShutdownTask().start();
            }
        }
    }

    class ShutdownTask extends Thread {

        @Override
        public void run() {
            try {
                LOG.info ("Waiting #" + waitForStart + "s until shutdown port might be open");
                Thread.sleep(waitForStart * 1000);
                LOG.info ("Shutting down server via port '" + port + "' and command '" + shutdown + "'");
                Socket s = new Socket("127.0.0.1", Integer.valueOf(port));
                PrintStream os = new PrintStream(s.getOutputStream());
                os.println(shutdown);
                s.close();
            } catch (Exception e) {
                LOG.severe ("Could not shut down server: " + e);
            }
        }
    }

    public String getPort() {
        return String.valueOf(port);
    }

    public void setPort(String port) {
        LOG.fine("Set Tomcat shutdown port to '" + port + "'");
        this.port = Integer.valueOf(port);
    }

    public String getShutdown() {
        return shutdown;
    }

    public void setShutdown(String shutdown) {
        LOG.fine("Set Tomcat shutdown command to '" + shutdown + "'");
        this.shutdown = shutdown;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        LOG.fine("Set Tomcat force exit to '" + force + "'");
        this.force = force;
    }

    public String getWaitForStart() {
        return String.valueOf(waitForStart);
    }

    public void setWaitForStart(String waitForStart) {
        LOG.fine("Set Tomcat waitForStart to '" + waitForStart + "'");
        this.waitForStart = Integer.valueOf(waitForStart);
    }
}
