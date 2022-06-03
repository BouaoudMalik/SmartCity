package com.rule.samu;

import java.io.Serializable;
import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.ConsciousFall;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;

public class RuleS13 implements RuleI {

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
		return state.isDoctorAvailable();
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		Serializable[] params = new Serializable[] { matchedEvents.get(0).getPropertyValue("position"),
				matchedEvents.get(0).getPropertyValue("personId"), TypeOfSAMURessources.TELEMEDIC };

		try {
			state.triggerDoctorCall(state, params);
			System.out.println("RuleS13");
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
