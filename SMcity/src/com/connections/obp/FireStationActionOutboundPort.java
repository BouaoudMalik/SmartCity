package com.connections.obp;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;

public class FireStationActionOutboundPort extends AbstractOutboundPort implements FireStationActionCI {

	private static final long serialVersionUID = 1L;

	public FireStationActionOutboundPort(ComponentI owner) throws Exception {
		super(FireStationActionCI.class, owner);
	}

	public FireStationActionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, FireStationActionCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI#triggerFirstAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource)
	 */
	@Override
	public void triggerFirstAlarm(AbsolutePosition p, TypeOfFirefightingResource r) throws Exception {
		((FireStationActionCI) this.getConnector()).triggerFirstAlarm(p, r);
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI#triggerSecondAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition)
	 */
	@Override
	public void triggerSecondAlarm(AbsolutePosition p) throws Exception {
		((FireStationActionCI) this.getConnector()).triggerSecondAlarm(p);
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI#triggerGeneralAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition)
	 */
	@Override
	public void triggerGeneralAlarm(AbsolutePosition p) throws Exception {
		((FireStationActionCI) this.getConnector()).triggerGeneralAlarm(p);
	}

}
