package com.connections.ibp;

import com.components.Correlator;
import com.components.interfaces.EventReceptionCI;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class EventReceptionInboundPort extends AbstractInboundPort implements EventReceptionCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the inbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventReceptionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, EventReceptionCI.class, owner);
		assert uri != null && !uri.isEmpty();
	}

	/**
	 * Creating the inbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public EventReceptionInboundPort(ComponentI owner) throws Exception {
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
		this.getOwner().runTask(o -> {
			try {
				((Correlator) o).receiveEvent(emitterURI, e);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});
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
		this.getOwner().runTask(o -> {
			try {
				((Correlator) o).receiveEvents(emitterURI, events);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
