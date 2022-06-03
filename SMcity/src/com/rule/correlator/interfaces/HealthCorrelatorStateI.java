package com.rule.correlator.interfaces;

import java.io.Serializable;

import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;

public interface HealthCorrelatorStateI extends CorrelatorStateI {

	public boolean isAmbulanceAvailable();

	public boolean isDoctorAvailable();

	public void triggerAmbulanceIntervention(HealthCorrelatorState state, Serializable[] params) throws Exception;

	public void triggerDoctorIntervention(HealthCorrelatorState state, Serializable[] params) throws Exception;

	public void triggerAmbulanceCall(HealthCorrelatorState state, Serializable[] params) throws Exception;

	public void triggerDoctorCall(HealthCorrelatorState state, Serializable[] params) throws Exception;

	public boolean isAnotherSamuAvailableNear();

	public void setAmbulanceAvailable(boolean b);

	public void setDoctorAvailable(boolean b);

	public String getNearestSamu(String samuId);

}
