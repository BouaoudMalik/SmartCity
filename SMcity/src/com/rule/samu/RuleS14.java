package com.rule.samu;

import java.time.LocalTime;
import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.ConsciousFall;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;

public class RuleS14 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			if (eb.getEvent(i) instanceof ConsciousFall) {
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
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		if (matchedEvents.isEmpty()) {
			return false;
		} else {
			if (!state.isDoctorAvailable()) {
				if (state.isAnotherSamuAvailableNear()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		String id = (String) matchedEvents.get(0).getPropertyValue("id");
		TypeOfHealthAlarm type = (TypeOfHealthAlarm) matchedEvents.get(0).getPropertyValue("type");
		LocalTime t = matchedEvents.get(0).getTimeStamp();

		try {
			state.getEventEmissionOutboundPort().sendEvent(state.getNearestSamu(id), matchedEvents.get(0));
			System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleS14");
		} catch (Exception e) {

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
