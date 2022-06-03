package com.connections.obp;

import java.io.Serializable;

import com.components.interfaces.ActionExecutionCI;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ActionExecutionOutboundPort extends AbstractOutboundPort implements ActionExecutionCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the outbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public ActionExecutionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ActionExecutionCI.class, owner);
	}

	/**
	 * Creating the outbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public ActionExecutionOutboundPort(ComponentI owner) throws Exception {
		super(ActionExecutionCI.class, owner);
	}

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
		return ((ActionExecutionCI) this.getConnector()).executeAction(a, params);
	}

}
