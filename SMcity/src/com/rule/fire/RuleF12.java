package com.rule.fire;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.event.ComplexEvent;
import com.event.fire.FireEvent;
import com.event.fire.FirstFireEvent;
import com.event.fire.SecondFireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class RuleF12 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);

			if (event.hasProperty("type") && event.hasProperty("position")) {
				if (event.getPropertyValue("position") != null
						&& event.getPropertyValue("type").equals(TypeOfFire.House)) {
					if (event instanceof FirstFireEvent) {
						matchedEvents.add(event);
					}
					if (event instanceof FireEvent) {
						matchedEvents.add(event);
					}
				}

			}
		}
		return matchedEvents;
	}

	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		LocalTime eventTime;
		LocalTime nextEventTime;
		
		if (matchedEvents.isEmpty()) {
			return false;
		} else {
			if (matchedEvents.get(0).hasProperty("position") && matchedEvents.get(1).hasProperty("position")) {
				if (matchedEvents.get(0).getPropertyValue("position")
						.equals(matchedEvents.get(1).getPropertyValue("position"))) {
					eventTime = matchedEvents.get(0).getTimeStamp();
					nextEventTime = matchedEvents.get(1).getTimeStamp();
	
					if (eventTime.isBefore(nextEventTime)
							&& eventTime.plus(Duration.of(15, ChronoUnit.MINUTES)).isAfter(nextEventTime)) {
						return true;
					}
	
				}
			}
		}
		return false;
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

		AbsolutePosition position = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
		String id = (String) matchedEvents.get(0).getPropertyValue("id");
		TypeOfFire type = (TypeOfFire) matchedEvents.get(0).getPropertyValue("type");
		LocalTime t = matchedEvents.get(0).getTimeStamp();

		// Propagate a complex event
		ArrayList<EventI> arrayListEvent = new ArrayList<>();
		SecondFireEvent secondEvent = new SecondFireEvent();
		secondEvent.putProperty("id", id);
		secondEvent.putProperty("position", position);
		secondEvent.putProperty("type", type);
		arrayListEvent.add(secondEvent);
		ComplexEvent propagatedEvent = new ComplexEvent(arrayListEvent);

		for(int i = 0; i < propagatedEvent.getCorrelatedEvents().size(); i++) {
			try {
				state.getEventEmissionOutboundPort().sendEvent(state.getNearestFire(id), propagatedEvent.getCorrelatedEvents().get(i));
				System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleF12");
			} catch (Exception e) {
	
			}
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			if (matchedEvents.get(i) instanceof FirstFireEvent) {
				eb.removeEvent(matchedEvents.get(i));
				SecondFireEvent secondFireEvent = new SecondFireEvent();
				eb.addEvent(secondFireEvent);
			}
		}
	}

}
