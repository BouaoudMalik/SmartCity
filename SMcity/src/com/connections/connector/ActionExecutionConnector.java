package com.connections.connector;

import java.io.Serializable;

import com.components.interfaces.ActionExecutionCI;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ActionExecutionConnector extends AbstractConnector implements ActionExecutionCI {

	/**
	 * Executing the action a
	 * 
	 * @param a      the action
	 * @param params the parameters of the action
	 * @return a response
	 * @throws Exception
	 */
	@Override
	public ResponseI executeAction(ActionI a, Serializable[] params) throws Exception {
		return ((ActionExecutionCI) this.offering).executeAction(a, params);
	}

}
