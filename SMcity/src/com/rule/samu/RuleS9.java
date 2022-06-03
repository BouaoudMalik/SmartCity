package com.rule.samu;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.InterventionEvent;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;

public class RuleS9 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event instanceof InterventionEvent) {
				if (event.getPropertyValue("type").equals(TypeOfHealthAlarm.EMERGENCY)) {
					matchedEvents.add(event);
				}
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
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		if (matchedEvents.isEmpty()) {
			return false;
		} else {
			if (state.isAmbulanceAvailable()) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		if (!matchedEvents.isEmpty()) {
			HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

			String id = (String) matchedEvents.get(0).getPropertyValue("id");
			TypeOfHealthAlarm type = (TypeOfHealthAlarm) matchedEvents.get(0).getPropertyValue("type");
			LocalTime t = matchedEvents.get(0).getTimeStamp();

			Serializable[] params = new Serializable[] { matchedEvents.get(0).getPropertyValue("position"),
					matchedEvents.get(0).getPropertyValue("personId"), TypeOfSAMURessources.AMBULANCE };

			try {
				state.triggerAmbulanceIntervention(state, params);
				System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleS9");
			} catch (Exception e) {

			}
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
