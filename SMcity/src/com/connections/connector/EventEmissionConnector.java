package com.connections.connector;

import com.components.interfaces.EventEmissionCI;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class EventEmissionConnector extends AbstractConnector implements EventEmissionCI {

	/**
	 * Send an event to propagate to the event subscriber
	 * 
	 * @param emitterURI emitted event uri
	 * @param event      the event
	 * @throws Exception
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI) this.offering).sendEvent(emitterURI, event);
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
		((EventEmissionCI) this.offering).sendEvents(emitterURI, events);
	}

}
