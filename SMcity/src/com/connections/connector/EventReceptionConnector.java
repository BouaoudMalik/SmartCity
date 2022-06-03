package com.connections.connector;

import com.components.interfaces.EventReceptionCI;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class EventReceptionConnector extends AbstractConnector implements EventReceptionCI {

	/**
	 * Receive an event
	 * 
	 * @param emitterURI emitted event uri
	 * @param e          the event
	 * @throws Exception
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		((EventReceptionCI) this.offering).receiveEvent(emitterURI, e);
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
		((EventReceptionCI) this.offering).receiveEvents(emitterURI, events);
	}

}
