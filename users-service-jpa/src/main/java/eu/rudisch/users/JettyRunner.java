package eu.rudisch.users;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.persistance.SqlServiceImpl;

public class JettyRunner {
	private static final Logger LOG = LogManager.getLogger(JettyRunner.class);

	public void startServer() {
		Server server = new Server(8081);

		HandlerCollection handlers = new HandlerCollection();

		ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);
		servletContextHandler.setServer(server);

		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.packages("eu.rudisch.users.rest.resources");
		AbstractBinder binder = new AbstractBinder() {
			@Override
			protected void configure() {
				bind(SqlServiceImpl.class).to(SqlService.class);
			}
		};
		resourceConfig.register(binder);
		ServletHolder servlet = new ServletHolder("resources", new ServletContainer(resourceConfig));
		servletContextHandler.addServlet(servlet, "/*");

		handlers.addHandler(servletContextHandler);

		server.setHandler(handlers);

		try {
			server.start();
			server.join();
		} catch (Exception ex) {
			LOG.error("Error occurred while starting Jetty", ex);
			System.exit(1);
		} finally {
			server.destroy();
		}
	}

}
