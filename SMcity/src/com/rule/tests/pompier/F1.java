package com.rule.tests.pompier;

import java.util.ArrayList;

import com.event.fire.FireEvent;
import com.event.fire.FirstFireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class F1 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			if (eb.getEvent(i).getPropertyValue("type").equals("immeuble") && eb.getEvent(i) instanceof FireEvent) {
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
		FireCorrelatorStateMock fireState = (FireCorrelatorStateMock) c;
		return fireState.isNearFireStation(fireState.getEventPosition(), fireState.getFireStationPosition())
				&& fireState.isLadderAvailable();
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorStateMock state = (FireCorrelatorStateMock) c;
		state.triggerAlarm();
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			if (eb.appearsIn(matchedEvents.get(i))) {
				eb.removeEvent(matchedEvents.get(i));
			}
		}
		FirstFireEvent firstFireEvent = new FirstFireEvent();
		eb.addEvent(firstFireEvent);
	}

}
