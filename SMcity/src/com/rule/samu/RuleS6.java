package com.rule.samu;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.event.ComplexEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.event.samu.InterventionEvent;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

public class RuleS6 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("position") && event.hasProperty("type")) {
				if (event.getPropertyValue("type").equals(TypeOfHealthAlarm.TRACKING)
						&& event.getPropertyValue("position") != null && event instanceof HealthEvent) {
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

		LocalTime currentTime = TimeManager.get().getCurrentLocalTime();
		LocalTime eventTime;
		LocalTime elapsedTime;

		if (!matchedEvents.isEmpty()) {
			eventTime = matchedEvents.get(0).getTimeStamp();
			elapsedTime = currentTime.minus(Duration.of(10, ChronoUnit.MINUTES));
			String id = (String) matchedEvents.get(0).getPropertyValue("id");

			if (eventTime.isBefore(elapsedTime)) {
				if (!state.isDoctorAvailable() && id != null) {
					if (state.isAnotherSamuAvailableNear()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		AbsolutePosition position = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
		TypeOfHealthAlarm type = (TypeOfHealthAlarm) matchedEvents.get(0).getPropertyValue("type");
		LocalTime t = matchedEvents.get(0).getTimeStamp();
		String id = (String) matchedEvents.get(0).getPropertyValue("id");

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
				state.getEventEmissionOutboundPort().sendEvent(state.getNearestSamu(id), propagatedEvent.getCorrelatedEvents().get(i));
				// System.out.println("Propagate intervention request to " + state.getNearestSamu(id));
				System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleS6 at " + position);
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
