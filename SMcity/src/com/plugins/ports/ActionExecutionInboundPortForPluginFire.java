package com.plugins.ports;

import java.io.Serializable;

import com.components.FireStationFacade;
import com.components.interfaces.ActionExecutionCI;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ActionExecutionInboundPortForPluginFire extends AbstractInboundPort implements ActionExecutionCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the inbound port instance with the URI
	 * 
	 * @param uri       the unique identifier of the port
	 * @param owner     the owner of the component owning the port
	 * @param pluginURI the unique identifier of the port with plugin
	 * @throws Exception
	 * 
	 */
	public ActionExecutionInboundPortForPluginFire(String uri, ComponentI owner, String pluginURI) throws Exception {
		super(uri, ActionExecutionCI.class, owner, pluginURI, null);
	}

	/**
	 * Creating the inbound port instance
	 * 
	 * @param owner     the owner of the component owning the port
	 * @param pluginURI the unique identifier of the port with plugin
	 * @throws Exception
	 * 
	 */
	public ActionExecutionInboundPortForPluginFire(ComponentI owner, String pluginURI) throws Exception {
		super(ActionExecutionCI.class, owner, pluginURI, null);
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
		return ((FireStationFacade) this.getOwner()).executeAction(a, params);
	}

}
