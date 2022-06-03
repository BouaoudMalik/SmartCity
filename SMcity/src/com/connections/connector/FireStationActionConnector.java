package com.connections.connector;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;

public class FireStationActionConnector extends AbstractConnector implements FireStationActionCI {

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI#triggerFirstAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource)
	 */
	@Override
	public void triggerFirstAlarm(AbsolutePosition p, TypeOfFirefightingResource r) throws Exception {
		((FireStationActionCI) this.offering).triggerFirstAlarm(p, r);
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI#triggerSecondAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition)
	 */
	@Override
	public void triggerSecondAlarm(AbsolutePosition p) throws Exception {
		((FireStationActionCI) this.offering).triggerSecondAlarm(p);
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI#triggerGeneralAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition)
	 */
	@Override
	public void triggerGeneralAlarm(AbsolutePosition p) throws Exception {
		((FireStationActionCI) this.offering).triggerGeneralAlarm(p);
	}

}
