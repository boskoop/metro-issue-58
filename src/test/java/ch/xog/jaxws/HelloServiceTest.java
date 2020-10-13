package ch.xog.jaxws;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.xog.jaxws.hello.v1.HelloRequest;
import ch.xog.jaxws.hello.v1.HelloResponse;
import ch.xog.jaxws.hello.v1.HelloServiceException_Exception;
import ch.xog.jaxws.hello.v1.HelloServicePortType;

public class HelloServiceTest {

	@Rule
	public HelloServiceTestServer server = new HelloServiceTestServer();

	@BeforeClass
	public static void setUp() {
		// We set a system property in order to print the soap messages on the client side
		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
	}

	/**
	 * This is a happy case test to see if actual calls on the service work.
	 */
	@Test
	public void helloJon() throws Exception {
		HelloServicePortType port = createServicePort();
		HelloRequest request = new HelloRequest();
		request.setFirstName("Jon");

		// Execute Test
		HelloResponse response = port.helloRequest(request);

		assertThat(response.getGreeting(), equalTo("Hello Jon"));
	}

	/**
	 * This test case checks for a working SOAPFault.
	 */
	@Test(expected = HelloServiceException_Exception.class)
	public void dontAskForArya() throws Exception {
		HelloServicePortType port = createServicePort();
		HelloRequest request = new HelloRequest();
		request.setFirstName("Arya");

		// Execute Test
		try {
			port.helloRequest(request);
		} catch (HelloServiceException_Exception e) {
			assertThat(e.getFaultInfo().getErrorText(), equalTo("Valar morghulis"));
			throw e;
		}
	}

	/**
	 * This test case manipulates the SOAPLoggingHandler such that it calls {@link SOAPMessageContext#getMessage()} when a fault is processed.
	 */
	@Test(expected = HelloServiceException_Exception.class)
	public void dontAskForAryaWithFaultLogging() throws Exception {
		server.getHandler().setLogFault(true);

		dontAskForArya();
	}

	private HelloServicePortType createServicePort() {
		Service service = server.createService();
		HelloServicePortType port = service.getPort(HelloServicePortType.class);
		return port;
	}
}
