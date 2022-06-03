package com.rule.traffic;

import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.event.traffic.PriorityEvent;
import com.event.traffic.WaitPassageVehicle;
import com.rule.correlator.TrafficLightCorrelatorState;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public class RuleC1 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event instanceof PriorityEvent) {
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
		return true;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		TrafficLightCorrelatorState state = TrafficLightCorrelatorState.initCorrelator(c);
		state.setIntersectionMode(TypeOfTrafficLightPriority.EAST_WEST);

		System.out.println("RuleC1");
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			eb.removeEvent(matchedEvents.get(i));
			WaitPassageVehicle waitPassageVehicle = new WaitPassageVehicle();
			eb.addEvent(waitPassageVehicle);
		}
	}

}
