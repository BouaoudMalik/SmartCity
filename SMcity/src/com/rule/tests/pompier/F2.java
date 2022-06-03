package com.rule.tests.pompier;

import java.util.ArrayList;

import com.event.fire.FireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class F2 extends F1 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			// if(eb.getEvent(i).getPropertyValue("type")==null) {
			// System.out.println("je suis null à la valeur : "+i);
			// }
			if (eb.getEvent(i).getPropertyValue("type").equals("maison") && eb.getEvent(i) instanceof FireEvent) {
				// System.out.println("j'entre dans le if pour i = "+i);
				matchedEvents.add(eb.getEvent(i));
			}
		}
		return matchedEvents;
	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorStateMock state = (FireCorrelatorStateMock) c;
		return state.isNearFireStation(state.getEventPosition(), state.getFireStationPosition())
				&& state.isTruckAvailable();
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorStateMock state = (FireCorrelatorStateMock) c;
		state.triggerHouseAlarm();
	}

}
