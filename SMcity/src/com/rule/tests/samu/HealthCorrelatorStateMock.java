package com.rule.tests.samu;

import java.awt.Point;
import java.io.Serializable;

public class HealthCorrelatorStateMock implements HealthCorrelatorStateMockI {

	/**
	 * Method that return true if a medic is available
	 * 
	 * @return boolean value
	 */
	public boolean isMedicAvailable() {
		return true;
	}

	/**
	 * Method that return true if a doctor is position
	 * 
	 * @return boolean value
	 */
	public boolean isDoctorAvailable() {
		return true;
	}

	/**
	 * Method that trigger a medic call
	 * 
	 * @param personId value for the concerned person
	 */
	public void triggerMedicCall(Serializable personId) {
		System.out.println("A medical call has been triggered");
	}

	/**
	 * Method that trigger a doctor call
	 * 
	 * @param personId value for the concerned person
	 */
	public void triggerDoctorCall(Serializable personId) {
		System.out.println("A doctor call has been triggered for the person" + personId);
	}

	/**
	 * Method that tells if a center is near the event position
	 * 
	 * @param eventPosition the correlated event position
	 * @param samuPosition  the samu position
	 * @return boolean value
	 */
	public boolean isNearCenter(Point eventPosition, Point samuPosition) {
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
	 * Method that get the samu center position
	 * 
	 * @return the samu center position
	 */
	public Point getSamuPosition() {
		return new Point(0, 0);
	}

	/**
	 * Method that tells if a samu is available near the current samu center
	 * 
	 * @return boolean value
	 */
	public boolean isAnotherSamuAvailableNear() {
		return true;
	}

	/**
	 * Method that get the nearest samu position
	 * 
	 * @return the nearest samu position
	 */
	public Point getNearestSamuPosition() {
		return new Point(0, 0);
	}

	/**
	 * Method that propagate an event to another samu as a complex event
	 */
	public void propagateComplexEvent() {
		System.out.println("A complex event has been created and propagated to the nearest SAMU available");
	}

	/**
	 * Method that propagate an intervention request event to another samu as a
	 * complex eventn
	 */
	public void propagateInterventionEvent() {
		System.out.println("A intervention request has been created and propagated to the nearest SAMU available");
	}

	/**
	 * Method that propagate an conscious fall event to another samu as a complex
	 * event
	 */
	public void propagateConsciousFallEvent() {
		System.out.println("A conscious fall event has been created and propagated to the nearest SAMU available");
	}

	/**
	 * There is no more center accessible
	 * 
	 * @return boolean value
	 */
	public boolean noOtherSamuAccessible() {
		return true;
	}

}
