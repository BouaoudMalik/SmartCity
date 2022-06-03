package com.rule.fire;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.event.fire.FireEvent;
import com.event.fire.FirstFireEvent;
import com.event.fire.SecondFireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class RuleF11 implements RuleI {

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

		if (matchedEvents.size() > 1) {
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

			if (state.isTruckAvailable()) {
				return true;
			} else {
				return false;
			}
		}
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
			System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleF11");
		} catch (Exception e) {

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
