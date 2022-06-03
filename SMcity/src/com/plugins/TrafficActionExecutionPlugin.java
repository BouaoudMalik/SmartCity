package com.plugins;

import java.io.Serializable;

import com.components.interfaces.ActionExecutionCI;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;
import com.plugins.ports.ActionExecutionInboundPortForPluginTraffic;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;

public class TrafficActionExecutionPlugin extends AbstractPlugin implements ActionExecutionCI {

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected ActionExecutionInboundPortForPluginTraffic actionExecutionIBP;
	private String actionPlugin_URI = AbstractPort.generatePortURI();

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public TrafficActionExecutionPlugin() {
		super();
	}

	// -------------------------------------------------------------------------
	// Plugin life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();

		this.addOfferedInterface(ActionExecutionCI.class);

		this.actionExecutionIBP = new ActionExecutionInboundPortForPluginTraffic(this.actionPlugin_URI, this.getOwner(),
				this.getPluginURI());
		this.actionExecutionIBP.publishPort();
	}

	@Override
	public void uninstall() throws Exception {
		this.actionExecutionIBP.unpublishPort();
		this.actionExecutionIBP.destroyPort();
		this.removeOfferedInterface(ActionExecutionCI.class);
	}

	// -------------------------------------------------------------------------
	// Plugin services implementation
	// -------------------------------------------------------------------------

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
		return ((ActionExecutionCI) this.getOwner()).executeAction(a, params);
	}

	/**
	 * Method getting the uri of the action plugin
	 * 
	 * @return action plugin uri
	 */
	public String getActionPluginURI() {
		return this.actionPlugin_URI;
	}

}
