package com.rule.correlator.interfaces;

import com.rule.interfaces.CorrelatorStateI;

import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.Direction;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public interface TrafficLightCorrelatorStateI extends CorrelatorStateI {

	public AbsolutePosition getNextIntersection(Direction direction);

	public void setIntersectionMode(TypeOfTrafficLightPriority priority);

}
