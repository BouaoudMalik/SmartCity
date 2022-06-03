package com.rule.samu;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.event.samu.ManualSignal;
import com.rule.correlator.HealthCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;

public class RuleS7 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("type")) {
				if (event.getPropertyValue("type").equals(TypeOfHealthAlarm.TRACKING) && event instanceof HealthEvent) {
					matchedEvents.add(event);
				}
				if (event.getPropertyValue("type").equals("Manual signal") && event instanceof ManualSignal) {
					matchedEvents.add(event);
				}
			}
		}
		return matchedEvents;
	}

	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		LocalTime eventTime;
		LocalTime nextEventTime;

		if (matchedEvents.size() <= 1) {
			return false;
		}
		if (matchedEvents.get(0).hasProperty("personId") && matchedEvents.get(1).hasProperty("personId")) {
			if (matchedEvents.get(0).getPropertyValue("personId")
					.equals(matchedEvents.get(1).getPropertyValue("personId"))) {
				eventTime = matchedEvents.get(0).getTimeStamp();
				nextEventTime = matchedEvents.get(1).getTimeStamp();

				if (eventTime.isBefore(nextEventTime)
						&& eventTime.plus(Duration.of(10, ChronoUnit.MINUTES)).isAfter(nextEventTime)) {
					return true;
				}

			}
		}
		return false;
	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);
		return state.isDoctorAvailable();
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorState state = HealthCorrelatorState.initCorrelator(c);

		Serializable[] params = new Serializable[] { matchedEvents.get(0).getPropertyValue("position"),
				matchedEvents.get(1).getPropertyValue("personId"), TypeOfSAMURessources.TELEMEDIC };

		try {
			state.triggerDoctorCall(state, params);
			System.out.println("RuleS7");
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
