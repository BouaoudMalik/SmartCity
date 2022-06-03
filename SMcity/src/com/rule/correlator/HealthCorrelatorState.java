package com.rule.correlator;

import java.io.Serializable;
import java.util.Iterator;

import com.actions.SAMUActions;
import com.connections.obp.ActionExecutionOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.rule.correlator.interfaces.HealthCorrelatorStateI;
import com.rule.interfaces.CorrelatorStateI;

import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;

public class HealthCorrelatorState extends CorrelatorState implements HealthCorrelatorStateI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static boolean ambulanceAvailable = true;
	public static boolean doctorAvailable = true;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public HealthCorrelatorState(ActionExecutionOutboundPort actionExecutionOutboundPort,
			EventEmissionOutboundPort eventEmissionOutboundPort) {
		super(actionExecutionOutboundPort, eventEmissionOutboundPort);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Method that return true if an ambulance is available
	 * 
	 * @return boolean value
	 */
	@Override
	public boolean isAmbulanceAvailable() {
		return HealthCorrelatorState.ambulanceAvailable;
	}

	/**
	 * Method that return true if a doctor is available
	 * 
	 * @return boolean value
	 */
	@Override
	public boolean isDoctorAvailable() {
		return HealthCorrelatorState.doctorAvailable;
	}

	/**
	 * Method that tells if a samu is available near the current samu center
	 * 
	 * @return boolean value
	 */
	@Override
	public boolean isAnotherSamuAvailableNear() {
		return true;
	}

	/**
	 * Method that get the nearest samu position
	 * 
	 * @return the nearest samu id
	 */
	@Override
	public String getNearestSamu(String samuId) {
		Iterator<String> samuStationsIditerator = SmartCityDescriptor.createSAMUStationIdIterator();
		String res = null;
		double min = Double.MAX_VALUE;
		double dist;

		while (samuStationsIditerator.hasNext()) {
			String samuStationNextId = samuStationsIditerator.next();
			if (samuId != samuStationNextId) {
				dist = SmartCityDescriptor.distance(samuId, samuStationNextId);
				if (min > dist) {
					min = dist;
					res = samuStationNextId;
				}
			}
		}

		return res;
	}

	/**
	 * The ambulance is available or not depending on the boolean b
	 *
	 * @param b boolean value
	 */
	@Override
	public void setAmbulanceAvailable(boolean b) {
		HealthCorrelatorState.ambulanceAvailable = b;
	}

	/**
	 * The doctor is available or not depending on the boolean b
	 *
	 * @param b boolean value
	 */
	@Override
	public void setDoctorAvailable(boolean b) {
		HealthCorrelatorState.doctorAvailable = b;
	}

	/**
	 * Method to trigger an ambulance intervention
	 * 
	 * @param state  health correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerAmbulanceIntervention(HealthCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(SAMUActions.triggerIntervention, params);

	}

	/**
	 * Method to trigger a doctor intervention
	 * 
	 * @param state  health correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerDoctorIntervention(HealthCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(SAMUActions.triggerIntervention, params);
	}

	/**
	 * Method to trigger an ambulance call
	 * 
	 * @param state  health correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerAmbulanceCall(HealthCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(SAMUActions.triggerIntervention, params);
	}

	/**
	 * Method to trigger a doctor call
	 * 
	 * @param state  health correlator state
	 * @param params serializable parameters
	 */
	@Override
	public void triggerDoctorCall(HealthCorrelatorState state, Serializable[] params) throws Exception {
		state.getActionExecutionOBP().executeAction(SAMUActions.triggerIntervention, params);
	}

	/**
	 * Method that return a health correlator state
	 * 
	 * @param c correlator state
	 * @return HealthCorrelatorState
	 */
	public static HealthCorrelatorState initCorrelator(CorrelatorStateI c) {
		CorrelatorState correlatorTemp = (CorrelatorState) c;
		ActionExecutionOutboundPort actionExecutionOBP = correlatorTemp.getActionExecutionOBP();
		EventEmissionOutboundPort eventEmissionOBP = correlatorTemp.getEventEmissionOutboundPort();

		return new HealthCorrelatorState(actionExecutionOBP, eventEmissionOBP);
	}

}