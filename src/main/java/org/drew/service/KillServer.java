package org.drew.service;

import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jamesdrew on 22/07/2015.
 */
@Slf4j
public class KillServer {

    private Server mServer;
    private LifecycleEnvironment lifeCycleEnvironment;
    private ServletEnvironment servletEnvironment;

    public KillServer(LifecycleEnvironment lifeCycleEnvironment, ServletEnvironment servletEnvironment){
        this.lifeCycleEnvironment = lifeCycleEnvironment;
        this.servletEnvironment = servletEnvironment;
    }

    public void addKillPathToApplication(String path){
        // Pick up Server instance, so I can shut him down
        this.lifeCycleEnvironment.addServerLifecycleListener(
                new ServerLifecycleListener() {
                    @Override
                    public void serverStarted(Server server) {
                        mServer = server;
                    }
                }
        );

        this.servletEnvironment.addServlet("Terminator", new ServerTerminator(mServer)).addMapping(path);
    }

    private class ServerTerminator extends HttpServlet {
        private Server server;

        private ServerTerminator(Server server){
            this.server = server;
        }
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            log.info("Terminator");
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            resp.getWriter().println();
            // Must initiate shut-down in separate thread to not deadlock
            new Thread() {
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (Exception ex) {
                        log.warn("Terminator failed", ex);
                    }
                }
            }.start();
        }
    }
}
