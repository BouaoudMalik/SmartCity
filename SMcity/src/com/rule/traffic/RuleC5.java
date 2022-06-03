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

public class RuleC5 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		ArrayList<EventI> matchedEvents = new ArrayList<>();
		for (int i = 0; i < eb.numberOfEvents(); i++) {
			EventI event = eb.getEvent(i);
			if (event.hasProperty("type")) {
				if (!event.getPropertyValue("type").equals(TypeOfTrafficLightPriority.EMERGENCY)
						&& event instanceof WaitPassageVehicle) {
					matchedEvents.add(event);
				}
				if (event.getPropertyValue("type").equals(TypeOfTrafficLightPriority.EMERGENCY)
						&& event instanceof PriorityEvent) {
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
		return true;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		TrafficLightCorrelatorState state = TrafficLightCorrelatorState.initCorrelator(c);
		state.setIntersectionMode(TypeOfTrafficLightPriority.EMERGENCY);

		System.out.println("RuleC5");
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		for (int i = 0; i < matchedEvents.size(); i++) {
			EventI event = matchedEvents.get(i);
			if (event instanceof WaitPassageVehicle) {
				eb.removeEvent(event);
				PriorityEvent priorityEvent = new PriorityEvent();
				eb.addEvent(priorityEvent);
			}
			if (event instanceof PriorityEvent
					&& event.getPropertyValue("type").equals(TypeOfTrafficLightPriority.EMERGENCY)) {
				eb.removeEvent(event);
				WaitPassageVehicle waitEvent = new WaitPassageVehicle();
				eb.removeEvent(waitEvent);
			}
		}
	}

}
