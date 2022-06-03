package com.connections.connector;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;

public class SAMUActionConnector extends AbstractConnector implements SAMUActionCI {

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUActionCI#triggerIntervention(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.lang.String,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources)
	 */
	@Override
	public void triggerIntervention(AbsolutePosition position, String personId, TypeOfSAMURessources type)
			throws Exception {
		((SAMUActionCI) this.offering).triggerIntervention(position, personId, type);
	}

}
