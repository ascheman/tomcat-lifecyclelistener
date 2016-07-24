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
 *
 * @author Gerd Aschemann &lt;gerd@aschemann.net&gt;
 */
public class FailstopLifecycleListener implements LifecycleListener {

    private static Logger LOG = Logger.getLogger(FailstopLifecycleListener.class.getName());

    /**
     * The port of the shutdown listener of Tomcat - see server.xml
     */
    private String port = "8015";

    /**
     * The command string to send for shutdown - see server.xml
     */
    private String shutdown = "shutdown";

    /**
     * Force the shutdown via System.exit
     */
    private boolean force;

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        String type = event.getType();
        Lifecycle lifecycle = event.getLifecycle();
        LifecycleState state = lifecycle.getState();

        String data = event.getData() != null ? event.getData().toString() : "";
        LOG.info ("Lifecycle Event '" + lifecycle.toString() +
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
                LOG.info ("Shutting down server via port '" + port + "' and command '" + shutdown + "'");
                Socket s = new Socket("127.0.0.1", Integer.valueOf(port));
                PrintStream os = new PrintStream(s.getOutputStream());
                os.println(shutdown);
                s.close();
            } catch (IOException e) {
                LOG.severe ("Could not shut down server: " + e);
            }
        }
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        LOG.info ("Set Tomcat shutdown port to '" + port + "'");
        this.port = port;
    }

    public String getShutdown() {
        return shutdown;
    }

    public void setShutdown(String shutdown) {
        LOG.info ("Set Tomcat shutdown command to '" + shutdown + "'");
        this.shutdown = shutdown;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        LOG.info ("Set Tomcat force exit to '" + force + "'");
        this.force = force;
    }
}