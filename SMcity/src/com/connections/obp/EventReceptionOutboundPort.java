package com.connections.obp;

import com.components.interfaces.EventReceptionCI;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class EventReceptionOutboundPort extends AbstractOutboundPort implements EventReceptionCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the outbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventReceptionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, EventReceptionCI.class, owner);
		assert uri != null;
	}

	/**
	 * Creating the outbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventReceptionOutboundPort(ComponentI owner) throws Exception {
		super(EventReceptionCI.class, owner);
	}

	/**
	 * Receive an event
	 * 
	 * @param emitterURI emitted event uri
	 * @param e          the event
	 * @throws Exception
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		((EventReceptionCI) this.getConnector()).receiveEvent(emitterURI, e);
	}

	/**
	 * Receive an array of events
	 * 
	 * @param emitterURI emitted event uri
	 * @param events     the array of events
	 * @throws Exception
	 */
	@Override
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		((EventReceptionCI) this.getConnector()).receiveEvents(emitterURI, events);
	}

}
