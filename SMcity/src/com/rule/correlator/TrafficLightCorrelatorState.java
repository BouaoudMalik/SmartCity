package com.rule.correlator;

import com.connections.obp.ActionExecutionOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.rule.correlator.interfaces.TrafficLightCorrelatorStateI;
import com.rule.interfaces.CorrelatorStateI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.Direction;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public class TrafficLightCorrelatorState extends CorrelatorState implements TrafficLightCorrelatorStateI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private TypeOfTrafficLightPriority priority = TypeOfTrafficLightPriority.NORTH_SOUTH;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public TrafficLightCorrelatorState(ActionExecutionOutboundPort actionExecutionOutboundPort,
			EventEmissionOutboundPort eventEmissionOutboundPort) {
		super(actionExecutionOutboundPort, eventEmissionOutboundPort);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Method to set a new mode of priority
	 * 
	 * @param priority the type of traffic light priority
	 */
	@Override
	public void setIntersectionMode(TypeOfTrafficLightPriority priority) {
		this.priority = priority;
	}

	/**
	 * Method to get the next intersection
	 * 
	 * @param direction the intersection direction
	 * @return AbsolutePosition
	 */
	@Override
	public AbsolutePosition getNextIntersection(Direction direction) {

		return null;
	}

	/**
	 * Method that return a traffic correlator state
	 * 
	 * @param c correlator state
	 * @return TrafficLightCorrelatorState
	 */
	public static TrafficLightCorrelatorState initCorrelator(CorrelatorStateI c) {
		CorrelatorState correlatorTemp = (CorrelatorState) c;
		ActionExecutionOutboundPort actionExecutionOBP = correlatorTemp.getActionExecutionOBP();
		EventEmissionOutboundPort eventEmissionOBP = correlatorTemp.getEventEmissionOutboundPort();

		return new TrafficLightCorrelatorState(actionExecutionOBP, eventEmissionOBP);
	}

}
