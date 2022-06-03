package com.connections.ibp;

import java.io.Serializable;

import com.components.interfaces.ActionExecutionCI;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ActionExecutionInboundPort extends AbstractInboundPort implements ActionExecutionCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the inbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public ActionExecutionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ActionExecutionCI.class, owner);
		assert uri != null && !uri.isEmpty();
	}

	/**
	 * Creating the inbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public ActionExecutionInboundPort(ComponentI owner) throws Exception {
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
		return this.getOwner().handleRequest(o -> {
			((ActionExecutionCI) o).executeAction(a, params);
			return null;
		});
	}
}
