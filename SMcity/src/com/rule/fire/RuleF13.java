package com.rule.fire;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

import com.event.fire.SecondFireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class RuleF13 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("position") && event instanceof SecondFireEvent) {
				if (event.getPropertyValue("position") != null) {
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
		FireCorrelatorState state = FireCorrelatorState.initCorrelator(c);

		if (matchedEvents.isEmpty()) {
			return false;
		} else {
			if (state.isTruckAvailable()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorState state = FireCorrelatorState.initCorrelator(c);

		String id = (String) matchedEvents.get(0).getPropertyValue("id");
		TypeOfFire type = (TypeOfFire) matchedEvents.get(0).getPropertyValue("type");
		LocalTime t = matchedEvents.get(0).getTimeStamp();

		Serializable[] params = new Serializable[] { matchedEvents.get(0).getPropertyValue("position") };

		try {
			state.triggerSecondAlarm(state, params);
			System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleF13");
		} catch (Exception e) {

		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {

	}

}
