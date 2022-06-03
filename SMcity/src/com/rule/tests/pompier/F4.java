package com.rule.tests.pompier;

import java.util.ArrayList;

import com.event.fire.FireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class F4 extends F3 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			if (eb.getEvent(i).getPropertyValue("type").equals("maison") && eb.getEvent(i) instanceof FireEvent) {
				matchedEvents.add(eb.getEvent(i));
			}
		}
		return matchedEvents;
	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorStateMock fireState = (FireCorrelatorStateMock) c;
		return fireState.isNearFireStation(fireState.getEventPosition(), fireState.getFireStationPosition())
				&& !fireState.isTruckAvailable() && fireState.availableFireStationisNear();
	}

}
