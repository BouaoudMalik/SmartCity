package com.rule.tests.samu;

import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class S1 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			if (eb.getEvent(i).getPropertyValue("type").equals("urgence") && eb.getEvent(i) instanceof HealthEvent) {
				matchedEvents.add(eb.getEvent(i));
			}
		}
		return matchedEvents;
	}

	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		return true;
	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorStateMock state = (HealthCorrelatorStateMock) c;
		return state.isMedicAvailable() && state.isNearCenter(state.getEventPosition(), state.getSamuPosition());
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorStateMock samuState = (HealthCorrelatorStateMock) c;
		for (int i = 0; i < matchedEvents.size(); i++) {
			samuState.triggerMedicCall(matchedEvents.get(i));
		}
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
