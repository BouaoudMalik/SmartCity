package com.rule.fire;

import java.time.LocalTime;
import java.util.ArrayList;

import com.event.fire.AllTrucksInIntervention;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class RuleF16 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event instanceof AllTrucksInIntervention) {
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
		return true;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			LocalTime t = matchedEvents.get(i).getTimeStamp();
			String id = (String) matchedEvents.get(i).getPropertyValue("id");

			FireCorrelatorState state = FireCorrelatorState.initCorrelator(c);
			state.setTruckAvailable(false);

			System.out.println("[" + t + "] " + id + " RuleF16");
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
