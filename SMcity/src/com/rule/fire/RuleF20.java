package com.rule.fire;

import java.time.LocalTime;
import java.util.ArrayList;

import com.event.fire.EndOfFire;
import com.event.fire.SecondFireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class RuleF20 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);

			if (event.hasProperty("position") && event.getPropertyValue("position") != null) {
				if (event instanceof SecondFireEvent) {
					matchedEvents.add(event);
				}
				if (event instanceof EndOfFire) {
					matchedEvents.add(event);
				}
			}
		}
		return matchedEvents;
	}

	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		if (matchedEvents.get(0).hasProperty("position") && matchedEvents.get(1).hasProperty("position")) {
			if (matchedEvents.get(0).getPropertyValue("position").equals(matchedEvents.get(1).getPropertyValue("position"))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		return true;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorState state = FireCorrelatorState.initCorrelator(c);

		String id = (String) matchedEvents.get(1).getPropertyValue("id");
		TypeOfFire type = (TypeOfFire) matchedEvents.get(1).getPropertyValue("type");
		LocalTime t = matchedEvents.get(1).getTimeStamp();

		try {
			state.getEventEmissionOutboundPort().sendEvent(id, matchedEvents.get(1));
			System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleF20");
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
