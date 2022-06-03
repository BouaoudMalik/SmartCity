package com.rule.samu;

import java.time.LocalTime;
import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.AllAmbulancesInIntervention;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class RuleS16 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			if (eb.getEvent(i) instanceof AllAmbulancesInIntervention) {
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
		return true;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			LocalTime t = matchedEvents.get(0).getTimeStamp();
			String id = (String) matchedEvents.get(i).getPropertyValue("id");

			HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);
			state.setAmbulanceAvailable(false);

			System.out.println("[" + t + "] " + id + " RuleS16");
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			//if (eb.appearsIn(matchedEvents.get(i))) {
				eb.removeEvent(matchedEvents.get(i));
			//}
		}
	}

}
