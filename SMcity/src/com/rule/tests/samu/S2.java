package com.rule.tests.samu;

import java.util.ArrayList;

import com.event.interfaces.EventI;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class S2 extends S1 implements RuleI {

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorStateMock samuState = (HealthCorrelatorStateMock) c;
		return samuState.isNearCenter(samuState.getEventPosition(), samuState.getSamuPosition())
				&& !samuState.isMedicAvailable() && samuState.isAnotherSamuAvailableNear();
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorStateMock state = (HealthCorrelatorStateMock) c;
		state.propagateComplexEvent();
	}

}
