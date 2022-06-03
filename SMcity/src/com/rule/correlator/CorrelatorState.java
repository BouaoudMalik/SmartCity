package com.rule.correlator;

import com.connections.obp.ActionExecutionOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.rule.interfaces.CorrelatorStateI;

public class CorrelatorState implements CorrelatorStateI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public ActionExecutionOutboundPort actionExecutionOutboundPort;
	public EventEmissionOutboundPort eventEmissionOutboundPort;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public CorrelatorState(ActionExecutionOutboundPort actionExecutionOutboundPort,
			EventEmissionOutboundPort eventEmissionOutboundPort) {
		this.actionExecutionOutboundPort = actionExecutionOutboundPort;
		this.eventEmissionOutboundPort = eventEmissionOutboundPort;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Method getting the action execution outbound port
	 * 
	 * @return the action execution outbound port
	 */
	public ActionExecutionOutboundPort getActionExecutionOBP() {
		return this.actionExecutionOutboundPort;
	}

	/**
	 * Method getting the event emission outbound port
	 * 
	 * @return the event emission outbound port
	 */
	public EventEmissionOutboundPort getEventEmissionOutboundPort() {
		return this.eventEmissionOutboundPort;
	}

}
