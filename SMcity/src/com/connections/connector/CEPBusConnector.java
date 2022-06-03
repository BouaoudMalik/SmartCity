package com.connections.connector;

import com.components.interfaces.CEPBusManagementCI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class CEPBusConnector extends AbstractConnector implements CEPBusManagementCI {

	/**
	 * Register the emitted event
	 * 
	 * @param uri emitted event uri
	 * @return event emission uri
	 * @throws Exception
	 */
	@Override
	public String registerEmitter(String uri) throws Exception {
		return ((CEPBusManagementCI) this.offering).registerEmitter(uri);
	}

	/**
	 * Unregister the emitted event
	 * 
	 * @param uri emitted event uri
	 * @throws Exception
	 */
	@Override
	public void unregisterEmitter(String uri) throws Exception {
		((CEPBusManagementCI) this.offering).unregisterEmitter(uri);
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
		return ((CEPBusManagementCI) this.offering).registerCorrelator(uri, inboundPortURI);
	}

	/**
	 * Unregister the correlator
	 * 
	 * @param uri correlator uri
	 * @throws Exception
	 */
	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		((CEPBusManagementCI) this.offering).unregisterCorrelator(uri);
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
		((CEPBusManagementCI) this.offering).registerExecutor(uri, inboundPortURI);
	}

	/**
	 * Get the uri of the inbound port of the action executor
	 * 
	 * @param uri action executor uri
	 * @return inbound port uri of the action executor
	 */
	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return ((CEPBusManagementCI) this.offering).getExecutorInboundPortURI(uri);
	}

	/**
	 * Unregisters the action executor
	 * 
	 * @param uri action executor uri
	 */
	@Override
	public void unregisterExecutor(String uri) throws Exception {
		((CEPBusManagementCI) this.offering).unregisterExecutor(uri);
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
		((CEPBusManagementCI) this.offering).subscribe(subscriberURI, emitterURI);
	}

	/**
	 * Unsubscribe the correlator
	 * 
	 * @param subscriberURI subscriber uri
	 * @param emitterURI    emitter uri
	 */
	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		((CEPBusManagementCI) this.offering).unsubscribe(subscriberURI, emitterURI);
	}

}
