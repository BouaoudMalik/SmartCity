package com.connections.obp;

import com.components.interfaces.EventEmissionCI;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class EventEmissionOutboundPort extends AbstractOutboundPort implements EventEmissionCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the outbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventEmissionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, EventEmissionCI.class, owner);
	}

	/**
	 * Creating the outbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventEmissionOutboundPort(ComponentI owner) throws Exception {
		super(EventEmissionCI.class, owner);
	}

	/**
	 * Send an event to propagate to the event subscriber
	 * 
	 * @param emitterURI emitted event uri
	 * @param event      the event
	 * @throws Exception
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI) this.getConnector()).sendEvent(emitterURI, event);
	}

	/**
	 * Send an array of events to propagated to the event subscriber
	 * 
	 * @param emitterURI emitted event uri
	 * @param events     the array of events
	 * @throws Exception
	 */
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionCI) this.getConnector()).sendEvents(emitterURI, events);
	}

}
