package com.connections.ibp;

import com.components.CEPBus;
import com.components.interfaces.CEPBusManagementCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CEPBusInboundPort extends AbstractInboundPort implements CEPBusManagementCI {

	private static final long serialVersionUID = 1L;

	/**
	 * Creating the inbound port instance with the URI
	 * 
	 * @param uri   the unique identifier of the port
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public CEPBusInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CEPBusManagementCI.class, owner);
		assert uri != null && !uri.isEmpty();
	}

	/**
	 * Creating the inbound port instance
	 * 
	 * @param owner the owner of the component owning the port
	 * @throws Exception
	 * 
	 */
	public CEPBusInboundPort(ComponentI owner) throws Exception {
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
		return (String) this.getOwner().handleRequest(o -> ((CEPBus) o).registerEmitter(uri));
	}

	/**
	 * Unregister the emitted event
	 * 
	 * @param uri emitted event uri
	 * @throws Exception
	 */
	@Override
	public void unregisterEmitter(String uri) throws Exception {
		this.getOwner().handleRequest(o -> {
			((CEPBus) o).unregisterEmitter(uri);
			return null;
		});
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
		return (String) this.getOwner().handleRequest(o -> ((CEPBus) o).registerCorrelator(uri, inboundPortURI));
	}

	/**
	 * Unregister the correlator
	 * 
	 * @param uri correlator uri
	 * @throws Exception
	 */
	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		this.getOwner().handleRequest(o -> {
			((CEPBus) o).unregisterCorrelator(uri);
			return null;
		});
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
		this.getOwner().handleRequest(o -> {
			((CEPBus) o).registerExecutor(uri, inboundPortURI);
			return null;
		});
	}

	/**
	 * Get the uri of the inbound port of the action executor
	 * 
	 * @param uri action executor uri
	 * @return inbound port uri of the action executor
	 */
	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return (String) this.getOwner().handleRequest(o -> ((CEPBus) o).getExecutorInboundPortURI(uri));
	}

	/**
	 * Unregisters the action executor
	 * 
	 * @param uri action executor uri
	 */
	@Override
	public void unregisterExecutor(String uri) throws Exception {
		this.getOwner().handleRequest(o -> {
			((CEPBus) o).unregisterExecutor(uri);
			return null;
		});
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
		this.getOwner().handleRequest(o -> {
			((CEPBus) o).subscribe(subscriberURI, emitterURI);
			return null;
		});
	}

	/**
	 * Unsubscribe the correlator
	 * 
	 * @param subscriberURI subscriber uri
	 * @param emitterURI    emitter uri
	 */
	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		this.getOwner().handleRequest(o -> {
			((CEPBus) o).unsubscribe(subscriberURI, emitterURI);
			return null;
		});
	}

}
