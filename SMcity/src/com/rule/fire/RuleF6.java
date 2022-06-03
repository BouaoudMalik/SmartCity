package com.rule.fire;

import java.time.LocalTime;
import java.util.ArrayList;

import com.event.fire.BuildingIntervention;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class RuleF6 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("position") && event instanceof BuildingIntervention) {
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
			AbsolutePosition position = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");

			if (!state.isLadderAvailable()) {
				if (state.isAnotherFireStationAvailableNear(position)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorState state = FireCorrelatorState.initCorrelator(c);

		String id = (String) matchedEvents.get(0).getPropertyValue("id");
		AbsolutePosition position = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
		TypeOfFire type = (TypeOfFire) matchedEvents.get(0).getPropertyValue("type");
		LocalTime t = matchedEvents.get(0).getTimeStamp();

		try {
			state.getEventEmissionOutboundPort().sendEvent(state.getNearestFire(id), matchedEvents.get(0));
			System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleF6 at " + position);
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
