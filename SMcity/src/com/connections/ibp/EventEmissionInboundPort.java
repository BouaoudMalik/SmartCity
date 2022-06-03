package com.connections.ibp;

import com.components.CEPBus;
import com.components.interfaces.EventEmissionCI;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class EventEmissionInboundPort extends AbstractInboundPort implements EventEmissionCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the inbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventEmissionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, EventEmissionCI.class, owner);
		assert uri != null && !uri.isEmpty();
	}

	/**
	 * Creating the inbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventEmissionInboundPort(ComponentI owner) throws Exception {
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
		this.getOwner().runTask(o -> {
			try {
				((CEPBus) o).sendEvent(emitterURI, event);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});
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
		this.getOwner().runTask(o -> {
			try {
				((CEPBus) o).sendEvents(emitterURI, events);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});
	}

}
