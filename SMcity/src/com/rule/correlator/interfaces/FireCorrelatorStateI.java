package com.rule.correlator.interfaces;

import java.io.Serializable;

import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public interface FireCorrelatorStateI extends CorrelatorStateI {

	public boolean isLadderAvailable();

	public boolean isTruckAvailable();

	public void triggerFirstBuildingAlarm(FireCorrelatorState state, Serializable[] params) throws Exception;

	public void triggerFirstHouseAlarm(FireCorrelatorState state, Serializable[] params) throws Exception;

	public void triggerGeneralAlarm(FireCorrelatorState state, Serializable[] params) throws Exception;

	public void triggerSecondAlarm(FireCorrelatorState state, Serializable[] params) throws Exception;

	public boolean isAnotherFireStationAvailableNear(AbsolutePosition position);

	public boolean isNextNearFireStation(AbsolutePosition fireStationPosition);

	public String getNearestFire(String fireId);

	public void setLadderAvailable(boolean b);

	public void setTruckAvailable(boolean b);

}
