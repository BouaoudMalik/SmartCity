package com.rule.samu;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.event.abstracts.AtomicEvent;
import com.event.interfaces.AtomicEventI;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

public class RuleS5 implements RuleI {

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
			
			if (eventTime.isBefore(elapsedTime)) {
				if (state.isDoctorAvailable()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		String id = (String) matchedEvents.get(0).getPropertyValue("id");
		TypeOfHealthAlarm type = (TypeOfHealthAlarm) matchedEvents.get(0).getPropertyValue("type");
		LocalTime t = matchedEvents.get(0).getTimeStamp();

		System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleS5");
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		if (matchedEvents.get(0).getPropertyValue("type").equals(TypeOfHealthAlarm.TRACKING)) {
			((AtomicEventI) matchedEvents.get(0)).removeProperty("type");
			((AtomicEvent) matchedEvents.get(0)).putProperty("type", TypeOfHealthAlarm.MEDICAL);
		}
	}

}
