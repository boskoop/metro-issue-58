package ch.xog.jaxws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

import ch.xog.jaxws.hello.v1.HelloRequest;
import ch.xog.jaxws.hello.v1.HelloResponse;
import ch.xog.jaxws.hello.v1.HelloServiceException;
import ch.xog.jaxws.hello.v1.HelloServiceException_Exception;

@WebService(name = "HelloServicePortType", targetNamespace = "http://xog.ch/jaxws/hello/v1", serviceName = "HelloService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class HelloServiceImpl {

	@WebMethod(action = "helloRequest")
	@WebResult(name = "HelloResponse", targetNamespace = "http://xog.ch/jaxws/hello/v1", partName = "parameters")
	public HelloResponse helloRequest(@WebParam(name = "HelloRequest", targetNamespace = "http://xog.ch/jaxws/hello/v1", partName = "parameters") HelloRequest parameters) throws HelloServiceException_Exception {
		String firstName = parameters.getFirstName();
		if ("Arya".equals(firstName)) {
			HelloServiceException fault = new HelloServiceException();
			fault.setErrorText("Valar morghulis");
			throw new HelloServiceException_Exception(fault.getErrorText(), fault);
		}
		HelloResponse response = new HelloResponse();
		response.setGreeting("Hello " + firstName);
		return response;
	}

}
