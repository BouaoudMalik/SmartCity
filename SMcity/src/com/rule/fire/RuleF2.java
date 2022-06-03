package com.rule.fire;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

import com.event.fire.FireEvent;
import com.event.fire.FirstFireEvent;
import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.correlator.FireCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;

public class RuleF2 implements RuleI {

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
			String id = (String) matchedEvents.get(0).getPropertyValue("id");

			if (SmartCityDescriptor.dependsUpon(position, id) && state.isTruckAvailable()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireCorrelatorState state = FireCorrelatorState.initCorrelator(c);

		AbsolutePosition position = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
		String id = (String) matchedEvents.get(0).getPropertyValue("id");
		LocalTime t = matchedEvents.get(0).getTimeStamp();
		TypeOfFire type = (TypeOfFire) matchedEvents.get(0).getPropertyValue("type");

		Serializable[] params = new Serializable[] { matchedEvents.get(0).getPropertyValue("position"),
				TypeOfFirefightingResource.StandardTruck };

		try {
			state.triggerFirstHouseAlarm(state, params);
			System.out.println("[" + t + "] " + id + " " + type.toString() + " RuleF2 at " + position);
		} catch (Exception e) {

		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			eb.removeEvent(matchedEvents.get(i));
			FirstFireEvent firstFireEvent = new FirstFireEvent();
			eb.addEvent(firstFireEvent);
		}
	}

}
