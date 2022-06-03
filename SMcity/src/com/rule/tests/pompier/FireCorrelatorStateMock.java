package com.rule.tests.pompier;

import java.awt.Point;

public class FireCorrelatorStateMock implements FireCorrelatorStateMockI {

	/**
	 * Testing the availability of the ladder
	 * 
	 * @return boolean value
	 */
	public boolean isLadderAvailable() {
		return true;
	}

	/**
	 * Testing the availability of the truck
	 * 
	 * @return boolean value
	 */
	public boolean isTruckAvailable() {
		return true;
	}

	/**
	 * Method that trigger an alarm for a building
	 */
	public void triggerAlarm() {
		System.out.println("The alarm for the building has been triggered");
	}

	/**
	 * Method that trigger a house alarm
	 */
	public void triggerHouseAlarm() {
		System.out.println("The alarm for the house has been triggered");
	}

	/**
	 * Testing if the position eventPosition is near the position
	 * fireStationPosition
	 * 
	 * @param eventPosition       the position of the alarm
	 * @param fireStationPosition the position of the fire station
	 * 
	 * @return boolean value
	 */
	public boolean isNearFireStation(Point eventPosition, Point fireStationPosition) {
		return true;
	}

	/**
	 * Method that get the event position
	 * 
	 * @return the event position
	 */
	public Point getEventPosition() {
		return new Point(0, 0);
	}

	/**
	 * Method that get the fire station center position
	 * 
	 * @return the fire station center position
	 */
	public Point getFireStationPosition() {
		return new Point(0, 0);
	}

	/**
	 * Testing the next nearest position of a fire station at the position
	 * eventPosition
	 * 
	 * @param eventPosition       the position of the alarm
	 * @param fireStationPosition the next nearest fire station from the alarm
	 * 
	 * @return boolean value
	 */
	public boolean isNextNearFireStation(Point eventPosition, Point fireStationPosition) {
		return true;
	}

	/**
	 * Get the next near fire station
	 * 
	 * @return the next near fire position
	 */
	public Point getNextNearFireStationPosition() {
		return new Point(1, 0);
	}

	/**
	 * Method that propagate a complex from a fire station to another one
	 */
	public void propagateComplexEvent() {
		System.out.println("The event is propagate to the nearest fire station for intervention");
	}

	/**
	 * Testing the availability of the nearest fire station from the position
	 * firestation
	 * 
	 * @return boolean value
	 */
	public boolean availableFireStationisNear() {
		return true;
	}

}
