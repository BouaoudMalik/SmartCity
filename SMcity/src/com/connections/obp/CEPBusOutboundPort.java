package com.connections.obp;

import com.components.interfaces.CEPBusManagementCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class CEPBusOutboundPort extends AbstractOutboundPort implements CEPBusManagementCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the outbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public CEPBusOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CEPBusManagementCI.class, owner);
	}

	/**
	 * Creating the outbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public CEPBusOutboundPort(ComponentI owner) throws Exception {
		super(CEPBusManagementCI.class, owner);
	}

	/**
	 * Register the emitted event
	 * 
	 * @param uri emitted event uri
	 * @return event emission uri
	 * @throws Exception
	 */
	@Override
	public String registerEmitter(String uri) throws Exception {
		return ((CEPBusManagementCI) this.getConnector()).registerEmitter(uri);
	}

	/**
	 * Unregister the emitted event
	 * 
	 * @param uri emitted event uri
	 * @throws Exception
	 */
	@Override
	public void unregisterEmitter(String uri) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unregisterEmitter(uri);
	}

	/**
	 * Register the correlator
	 * 
	 * @param uri            correlator uri
	 * @param inboundPortURI incoming port
	 * @return uri of the inbound port offering EventEmissionCI interface
	 * @throws Exception
	 */
	@Override
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		return ((CEPBusManagementCI) this.getConnector()).registerCorrelator(uri, inboundPortURI);
	}

	/**
	 * Unregister the correlator
	 * 
	 * @param uri correlator uri
	 * @throws Exception
	 */
	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unregisterCorrelator(uri);
	}

	/**
	 * Register the action executor
	 * 
	 * @param uri            action executor uri
	 * @param inboundPortURI uri of the inbound port offering ActionExcutionCI
	 *                       interface
	 */
	@Override
	public void registerExecutor(String uri, String inboundPortURI) throws Exception {
		((CEPBusManagementCI) this.getConnector()).registerExecutor(uri, inboundPortURI);
	}

	/**
	 * Get the uri of the inbound port of the action executor
	 * 
	 * @param uri action executor uri
	 * @return inbound port uri of the action executor
	 */
	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return ((CEPBusManagementCI) this.getConnector()).getExecutorInboundPortURI(uri);
	}

	/**
	 * Unregisters the action executor
	 * 
	 * @param uri action executor uri
	 */
	@Override
	public void unregisterExecutor(String uri) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unregisterExecutor(uri);
	}

	/**
	 * Subscribing the correlator to the emitted event
	 * 
	 * @param subscriberURI subscriber uri
	 * @param emitterURI    emitter uri
	 * @throws Exception
	 */
	@Override
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		((CEPBusManagementCI) this.getConnector()).subscribe(subscriberURI, emitterURI);
	}

	/**
	 * Unsubscribe the correlator
	 * 
	 * @param subscriberURI subscriber uri
	 * @param emitterURI    emitter uri
	 */
	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		((CEPBusManagementCI) this.getConnector()).unsubscribe(subscriberURI, emitterURI);
	}

}
