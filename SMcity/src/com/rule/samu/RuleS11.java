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

public class RuleS11 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("type") && event.getPropertyValue("type").equals(TypeOfHealthAlarm.MEDICAL)
					&& event instanceof InterventionEvent) {
				matchedEvents.add(event);
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
		return state.isDoctorAvailable();
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		if (!matchedEvents.isEmpty()) {
			String id = (String) matchedEvents.get(0).getPropertyValue("id");
			TypeOfHealthAlarm type = (TypeOfHealthAlarm) matchedEvents.get(0).getPropertyValue("type");
			LocalTime t = matchedEvents.get(0).getTimeStamp();

			Serializable[] params = new Serializable[] { matchedEvents.get(0).getPropertyValue("position"),
					matchedEvents.get(0).getPropertyValue("personId"), TypeOfSAMURessources.MEDIC };

			try {
				state.triggerDoctorIntervention(state, params);
				System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleS11");
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
