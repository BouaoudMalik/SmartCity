package com.rule.tests.samu;

import java.awt.Point;
import java.io.Serializable;

import com.rule.interfaces.CorrelatorStateI;

public interface HealthCorrelatorStateMockI extends CorrelatorStateI {

	public boolean isMedicAvailable();

	public boolean isDoctorAvailable();

	public void triggerMedicCall(Serializable personId);

	public void triggerDoctorCall(Serializable personId);

	public boolean isNearCenter(Point eventPosition, Point samuPosition);

	public Point getEventPosition();

	public Point getSamuPosition();

	public boolean isAnotherSamuAvailableNear();

	public Point getNearestSamuPosition();

	public void propagateComplexEvent();

	public void propagateInterventionEvent();

	public void propagateConsciousFallEvent();

	public boolean noOtherSamuAccessible();
	
}
