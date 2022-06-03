package com.rule.fire;

import java.time.LocalTime;
import java.util.ArrayList;

import com.event.ComplexEvent;
import com.event.fire.FireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.InterventionEvent;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class RuleF4 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("type") && event.hasProperty("position") && event instanceof FireEvent) {
				if (event.getPropertyValue("type").equals(TypeOfFire.House)
						&& event.getPropertyValue("position") != null) {
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

			if (!state.isTruckAvailable()) {
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

		// Propagate a complex event
		ArrayList<EventI> arrayListEvent = new ArrayList<>();
		InterventionEvent interventionRequest = new InterventionEvent();
		interventionRequest.putProperty("id", id);
		interventionRequest.putProperty("position", position);
		interventionRequest.putProperty("type", type);
		arrayListEvent.add(interventionRequest);
		ComplexEvent propagatedEvent = new ComplexEvent(arrayListEvent);

		for(int i = 0; i < propagatedEvent.getCorrelatedEvents().size(); i++) {
			try {
				state.getEventEmissionOutboundPort().sendEvent(state.getNearestFire(id), propagatedEvent.getCorrelatedEvents().get(i));
				System.out.println("Propagate intervention request to " + state.getNearestFire(id));
				System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleF4 at " + position);
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
