package com.rule.samu;

import java.time.LocalTime;
import java.util.ArrayList;

import com.event.ComplexEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.event.samu.InterventionEvent;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;

public class RuleS4 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("type") && event.hasProperty("position"))
				if (event.getPropertyValue("type").equals(TypeOfHealthAlarm.MEDICAL)
						&& event.getPropertyValue("position") != null && event instanceof HealthEvent) {
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

		/*if (matchedEvents.isEmpty()) {
			return false;
		} else {
			String id = (String) matchedEvents.get(0).getPropertyValue("id");
			String idSamu = state.getNearestSamu(id);
			// si pas de samu donc pas de médecins
			if (idSamu != null) {
				return true;
			} else {
				return false;
			}
		}*/
		
		if (matchedEvents.isEmpty()) {
			return false;
		} else {
			AbsolutePosition position = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
			String id = (String) matchedEvents.get(0).getPropertyValue("id");
	
			if (SmartCityDescriptor.dependsUpon(position, id) && !state.isDoctorAvailable()
					&& state.isAnotherSamuAvailableNear()) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		String id = (String) matchedEvents.get(0).getPropertyValue("id");
		AbsolutePosition position = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
		TypeOfHealthAlarm type = (TypeOfHealthAlarm) matchedEvents.get(0).getPropertyValue("type");
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
				System.out.println("Propagate intervention request to " + state.getNearestSamu(id));
				System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleS4 at " + position);
				state.getEventEmissionOutboundPort().sendEvent(state.getNearestSamu(id), propagatedEvent.getCorrelatedEvents().get(i));

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
