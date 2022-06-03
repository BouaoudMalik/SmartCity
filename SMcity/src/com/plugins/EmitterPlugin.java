package com.plugins;

import com.components.CEPBus;
import com.components.interfaces.EventEmissionCI;
import com.connections.connector.EventEmissionConnector;
import com.connections.obp.EventEmissionOutboundPort;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;

public class EmitterPlugin extends AbstractPlugin implements EventEmissionCI {

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static String eventEmitterPluginURI = AbstractPort.generatePortURI();
	private EventEmissionOutboundPort eventEmissionOBP;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public EmitterPlugin() {
		super();
	}

	// -------------------------------------------------------------------------
	// Plugin life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();

		this.addRequiredInterface(EventEmissionCI.class);

		this.eventEmissionOBP = new EventEmissionOutboundPort(this.getOwner());
		this.eventEmissionOBP.publishPort();

		this.getOwner().doPortConnection(this.eventEmissionOBP.getPortURI(), CEPBus.eventEmissionIBP_URI,
				EventEmissionConnector.class.getCanonicalName());
	}

	@Override
	public void finalise() throws Exception {
		this.getOwner().doPortDisconnection(this.eventEmissionOBP.getPortURI());
	}

	@Override
	public void uninstall() throws Exception {
		super.uninstall();

		this.eventEmissionOBP.unpublishPort();
		this.eventEmissionOBP.destroyPort();
		this.removeRequiredInterface(EventEmissionCI.class);
	}

	// -------------------------------------------------------------------------
	// Plugin services implementation
	// -------------------------------------------------------------------------

	/**
	 * Send an event to propagate to the event subscriber
	 * 
	 * @param emitterURI emitted event uri
	 * @param event      the event
	 * @throws Exception
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		this.eventEmissionOBP.sendEvent(emitterURI, event);
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
		this.eventEmissionOBP.sendEvents(emitterURI, events);
	}

}