package com.rule.tests.samu;

import java.util.ArrayList;

import com.event.interfaces.EventI;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class S4 extends S3 implements RuleI {

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorStateMock state = (HealthCorrelatorStateMock) c;
		return state.isNearCenter(state.getEventPosition(), state.getSamuPosition()) && !state.isMedicAvailable()
				&& state.isAnotherSamuAvailableNear();
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorStateMock state = (HealthCorrelatorStateMock) c;
		state.propagateComplexEvent();
	}

}
