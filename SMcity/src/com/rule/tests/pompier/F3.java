package com.rule.tests.pompier;

import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class F3 extends F1 implements RuleI {

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorStateMock fireState = (FireCorrelatorStateMock) c;
		return fireState.isNearFireStation(fireState.getEventPosition(), fireState.getFireStationPosition())
				&& !fireState.isLadderAvailable() && fireState.isNextNearFireStation(fireState.getEventPosition(),
						fireState.getNextNearFireStationPosition());
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorStateMock state = (FireCorrelatorStateMock) c;
		state.propagateComplexEvent();
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			if (eb.appearsIn(matchedEvents.get(i))) {
				eb.removeEvent(matchedEvents.get(i));
			}
		}
	}

}
