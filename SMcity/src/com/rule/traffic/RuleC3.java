package com.rule.traffic;

import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.traffic.PassageVehicle;
import com.event.traffic.WaitPassageVehicle;
import com.rule.correlator.TrafficLightCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.Direction;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public class RuleC3 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event instanceof WaitPassageVehicle) {
				matchedEvents.add(event);
			}
			if (event.hasProperty("direction") && event instanceof PassageVehicle) {
				matchedEvents.add(event);
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
		Direction direction = (Direction) matchedEvents.get(0).getPropertyValue("direction");
		AbsolutePosition destination = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("destination");

		IntersectionPosition current = (IntersectionPosition) matchedEvents.get(0).getPropertyValue("position");
		IntersectionPosition nextIntersection = current.next(direction);

		return nextIntersection.equals(destination);
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		TrafficLightCorrelatorState state = TrafficLightCorrelatorState.initCorrelator(c);
		state.setIntersectionMode(TypeOfTrafficLightPriority.NORTH_SOUTH);

		System.out.println("RuleC3");
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
