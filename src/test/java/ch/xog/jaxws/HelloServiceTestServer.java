package ch.xog.jaxws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

import org.junit.rules.ExternalResource;

import ch.xog.jaxws.handler.SOAPLoggingHandler;
import ch.xog.jaxws.hello.v1.HelloService;

public class HelloServiceTestServer extends ExternalResource {

	HelloServiceImpl testService = new HelloServiceImpl();

	private Endpoint endpoint;
	private int port;
	private String publishUrl;
	private SOAPLoggingHandler handler;

	@Override
	protected void before() throws Exception {
		endpoint = Endpoint.create(testService);
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = endpoint.getBinding().getHandlerChain();
		handler = new SOAPLoggingHandler();
		handlerChain.add(handler);

		endpoint.getBinding().setHandlerChain(handlerChain);
		port = findFreePort();
		publishUrl = "http://localhost:" + port + "/ws/hello";
		endpoint.publish(publishUrl);
	}

	protected List<Handler<?>> createServerHandlerChain() {
		return Arrays.asList(new SOAPLoggingHandler());
	}

	@Override
	protected void after() {
		endpoint.stop();
	}

	private static int findFreePort() throws IOException {
		ServerSocket server = new ServerSocket(0);
		int port = server.getLocalPort();
		server.close();
		return port;
	}
	
	public SOAPLoggingHandler getHandler() {
		return handler;
	}

	public Service createService() {
		URL wsdlUrl;
		try {
			wsdlUrl = new URL("http://localhost:" + port + "/ws/hello");
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Configuration problem", e);
		}
		return new HelloService(wsdlUrl);
	}
}
