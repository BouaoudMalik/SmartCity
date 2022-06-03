package com.rule.correlator;

import java.io.Serializable;
import java.util.Iterator;

import com.actions.FireActions;
import com.connections.obp.ActionExecutionOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.rule.correlator.interfaces.FireCorrelatorStateI;
import com.rule.interfaces.CorrelatorStateI;

import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public class FireCorrelatorState extends CorrelatorState implements FireCorrelatorStateI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static boolean ladderAvailable = true;
	public static boolean truckAvailable = true;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public FireCorrelatorState(ActionExecutionOutboundPort actionExecutionOutboundPort,
			EventEmissionOutboundPort eventEmissionOutboundPort) {
		super(actionExecutionOutboundPort, eventEmissionOutboundPort);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Testing the availability of the ladder
	 * 
	 * @return boolean value
	 */
	@Override
	public boolean isLadderAvailable() {
		return FireCorrelatorState.ladderAvailable;
	}

	/**
	 * Testing the availability of the truck
	 * 
	 * @return boolean value
	 */
	@Override
	public boolean isTruckAvailable() {
		return FireCorrelatorState.truckAvailable;
	}

	/**
	 * Method to trigger a first building alarm
	 * 
	 * @param state  fire correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerFirstBuildingAlarm(FireCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(FireActions.triggerFirstAlarm, params);
	}

	/**
	 * Method to trigger a first house alarm
	 * 
	 * @param state  fire correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerFirstHouseAlarm(FireCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(FireActions.triggerFirstAlarm, params);
	}

	/**
	 * Method to trigger a general fire alarm
	 * 
	 * @param state  fire correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerGeneralAlarm(FireCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(FireActions.triggerGeneralAlarm, params);
	}

	/**
	 * Method to trigger a second fire alarm
	 * 
	 * @param state  fire correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerSecondAlarm(FireCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(FireActions.triggerSecondAlarm, params);
	}

	// il existe une caserne C’ non encore sollicitée qui est la suivante la plus
	// près de C
	/**
	 * Method that tells if a fire station is available near the current station
	 * 
	 * @param position the station position
	 * @return boolean value
	 */
	@Override
	public boolean isAnotherFireStationAvailableNear(AbsolutePosition position) {
		return true;
	}

	// caserne C’ est la suivante la plus près de p
	/**
	 * Testing the next nearest position of a fire station
	 * 
	 * @param fireStationPosition the station position
	 * @return boolean value
	 */
	public boolean isNextNearFireStation(AbsolutePosition fireStationPosition) {
		return true;
	}

	/**
	 * Method that get the nearest fire position
	 * 
	 * @return the nearest fire id
	 */
	@Override
	public String getNearestFire(String fireId) {
		Iterator<String> fireStationsIditerator = SmartCityDescriptor.createFireStationIdIterator();
		String res = null;
		double min = Double.MAX_VALUE;
		double dist;

		while (fireStationsIditerator.hasNext()) {
			String fireStationNextId = fireStationsIditerator.next();
			if (fireId != fireStationNextId) {
				dist = SmartCityDescriptor.distance(fireId, fireStationNextId);
				if (min > dist) {
					min = dist;
					res = fireStationNextId;
				}
			}
		}
		return res;
	}

	/**
	 * The ladder is available or not depending on the boolean b
	 *
	 * @param b boolean value
	 */
	@Override
	public void setLadderAvailable(boolean b) {
		FireCorrelatorState.ladderAvailable = b;
	}

	/**
	 * The standard truck is available or not depending on the boolean b
	 *
	 * @param b boolean value
	 */
	@Override
	public void setTruckAvailable(boolean b) {
		FireCorrelatorState.truckAvailable = b;
	}

	/**
	 * Method that return a fire correlator state
	 * 
	 * @param c correlator state
	 * @return FireCorrelatorState
	 */
	public static FireCorrelatorState initCorrelator(CorrelatorStateI c) {
		CorrelatorState correlatorTemp = (CorrelatorState) c;
		ActionExecutionOutboundPort actionExecutionOBP = correlatorTemp.getActionExecutionOBP();
		EventEmissionOutboundPort eventEmissionOBP = correlatorTemp.getEventEmissionOutboundPort();

		return new FireCorrelatorState(actionExecutionOBP, eventEmissionOBP);
	}

}
