package ch.xog.jaxws.handler;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	private boolean logFault = false;

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		logSOAPMessageContext(context, false);
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		logSOAPMessageContext(context, true);
		return true;
	}

	@Override
	public void close(MessageContext context) {
		// Nothing to close here
	}

	public void setLogFault(boolean logFault) {
		this.logFault = logFault;
	}

	private void logSOAPMessageContext(SOAPMessageContext smc, boolean isFault) {
		Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		StringBuilder builder = new StringBuilder();
		if (outboundProperty.booleanValue()) {
			builder.append("SOAPLoggingHandler - Server outbound message: \n");
		} else {
			builder.append("SOAPLoggingHandler - Server inbound message: \n");
		}

		// Just add "SOAPFault" if fault logging is disabled
		if (isFault && !logFault) {
			builder.append("SOAPFault");
		} else {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				// The problem happens here, when smc.getMessage() on a SOAPFault is called. This causes Metro to mix up the namespaces on the fault.
				smc.getMessage().writeTo(bout);
				builder.append(bout.toString("utf-8"));
			} catch (Exception e) {
				builder.append("Exception in handler: " + e);
			}
		}
		builder.append("\n");

		// For demo purposes, we do a simple print here
		System.out.println(builder.toString());
	}

	@Override
	public Set<QName> getHeaders() {
		return new HashSet<>();
	}

}
