package com.rule.tests.pompier;

import java.awt.Point;

import com.rule.interfaces.CorrelatorStateI;

public interface FireCorrelatorStateMockI extends CorrelatorStateI {

	public boolean isLadderAvailable();

	public boolean isTruckAvailable();

	public void triggerAlarm();

	public void triggerHouseAlarm();

	public boolean isNearFireStation(Point eventPosition, Point fireStationPosition);

	public Point getEventPosition();

	public Point getFireStationPosition();

	public boolean isNextNearFireStation(Point eventPosition, Point fireStationPosition);

	public Point getNextNearFireStationPosition();

	public void propagateComplexEvent();

	public boolean availableFireStationisNear();

}
