package com.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.components.interfaces.CEPBusManagementCI;
import com.components.interfaces.EventEmissionCI;
import com.components.interfaces.EventReceptionCI;
import com.connections.connector.EventReceptionConnector;
import com.connections.ibp.CEPBusInboundPort;
import com.connections.ibp.EventEmissionInboundPort;
import com.connections.ibp.EventReceptionInboundPort;
import com.connections.obp.EventReceptionOutboundPort;
import com.event.interfaces.EventI;

import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@OfferedInterfaces(offered = { CEPBusManagementCI.class, EventEmissionCI.class, EventReceptionCI.class })
@RequiredInterfaces(required = { EventReceptionCI.class })
public class CEPBus extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static String cepBus_URI;
	protected String eventReceptionIBP_URI;
	protected String eventReceptionOBP_URI;

	public static String eventEmissionIBP_URI;
	protected static String cepBusIBP_URI = AbstractPort.generatePortURI();

	protected CEPBusInboundPort cepBusInboundPort;
	protected EventEmissionInboundPort eventEmissionInboundPort;
	protected EventReceptionInboundPort eventReceptionInboundPort;
	protected EventReceptionOutboundPort eventReceptionOutboundPort;

	protected Vector<EventReceptionOutboundPort> listReceptionObp = new Vector<EventReceptionOutboundPort>();

	/**
	 * URI des émetteurs d'événement
	 */
	private List<String> urisEmitter = Collections.synchronizedList(new ArrayList<String>());
	/**
	 * Map pour les URI des corrélateurs associés au port entrant offrant
	 * EventReceptionCI
	 */
	private ConcurrentHashMap<String, String> urisMapCorrelator = new ConcurrentHashMap<>();
	/**
	 * Map pour les URI des exécuteurs d'actions associés au port entrant offrant
	 * ActionExecutionCI
	 */
	private ConcurrentHashMap<String, String> urisMapExecutor = new ConcurrentHashMap<>();
	/**
	 * Map pour abonner un corrélateur aux événements émis
	 */
	private ConcurrentHashMap<String, Vector<String>> subscribeMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Vector<EventReceptionOutboundPort>> mapOutBoundPorts = new ConcurrentHashMap<>();

	/**
	 * Pool thread creation
	 */
	protected static final String EMISSION_POOL_URI = "emission_pool";
	protected static final int EMISSION_NTHREADS = 3;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected CEPBus(String cepBus_URI, String eventEmissionIBP_URI, String eventReceptionIBP_URI,
			String eventReceptionOBP_URI) throws Exception {
		super(1, 0);

		this.cepBus_URI = cepBus_URI;
		this.eventEmissionIBP_URI = eventEmissionIBP_URI;
		this.eventReceptionIBP_URI = eventReceptionIBP_URI;
		this.eventReceptionOBP_URI = eventReceptionOBP_URI;

		this.cepBusInboundPort = new CEPBusInboundPort(cepBusIBP_URI, this);
		this.cepBusInboundPort.publishPort();

		this.eventReceptionInboundPort = new EventReceptionInboundPort(this.eventReceptionIBP_URI, this);
		this.eventReceptionInboundPort.publishPort();

		this.eventEmissionInboundPort = new EventEmissionInboundPort(eventEmissionIBP_URI, this);
		this.eventEmissionInboundPort.publishPort();

		this.eventReceptionOutboundPort = new EventReceptionOutboundPort(this);
		this.eventReceptionOutboundPort.publishPort();

		this.initialise();
	}

	/**
	 * Creation of a new executor service
	 * 
	 * @throws Exception
	 */
	public void initialise() throws Exception {
		this.createNewExecutorService(EMISSION_POOL_URI, EMISSION_NTHREADS, false);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
		
		try {
			this.doPortConnection(this.eventReceptionOutboundPort.getPortURI(), this.eventReceptionIBP_URI,
					EventReceptionConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
	}

	@Override
	public synchronized void finalise() throws Exception {
		for (int i = 0; i < this.listReceptionObp.size(); i++) {
			this.doPortDisconnection(this.listReceptionObp.get(i).getPortURI());
		}
		
		this.doPortDisconnection(this.eventReceptionOutboundPort.getPortURI());
		
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.cepBusInboundPort.unpublishPort();
			this.eventEmissionInboundPort.unpublishPort();
			this.eventReceptionInboundPort.unpublishPort();

			for (int i = 0; i < this.listReceptionObp.size(); i++) {
				this.listReceptionObp.get(i).unpublishPort();
			}
			
			this.eventReceptionOutboundPort.unpublishPort();

		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * Send an event to propagate to the event subscriber
	 * 
	 * @param emitterURI emitted event uri
	 * @param event      the event
	 * @throws Exception
	 */
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		assert this.mapOutBoundPorts.size() > 0;
		for (EventReceptionOutboundPort receptionOBP : this.mapOutBoundPorts.get(emitterURI)) {

			this.runTask(o -> {
				try {
					receptionOBP.receiveEvent(emitterURI, event);
					// transférer vers l'autre bus
					// if emitter not  contained -> eventReceptionOBP.receiveEvent(emitterURI, event);

				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		}
	}

	/**
	 * Send an array of events to propagated to the event subscriber
	 * 
	 * @param emitterURI emitted event uri
	 * @param events     the array of events
	 * @throws Exception
	 */
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		assert this.mapOutBoundPorts.size() > 0;
		for (EventReceptionOutboundPort receptionOBP : this.mapOutBoundPorts.get(emitterURI)) {

			this.runTask(o -> {
				try {
					receptionOBP.receiveEvents(emitterURI, events);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		}
	}

	/**
	 * Register the emitted event
	 * 
	 * @param uri emitted event uri
	 * @return event emission uri
	 * @throws Exception
	 */
	public String registerEmitter(String uri) throws Exception {
		urisEmitter.add(uri);
		return eventEmissionIBP_URI;
	}

	/**
	 * Unregister the emitted event
	 * 
	 * @param uri emitted event uri
	 * @throws Exception
	 */
	public void unregisterEmitter(String uri) throws Exception {
		urisEmitter.remove(uri);
	}

	/**
	 * Register the correlator
	 * 
	 * @param uri            correlator uri
	 * @param inboundPortURI incoming port
	 * @return uri of the inbound port offering EventEmissionCI interface
	 * @throws Exception
	 */
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		urisMapCorrelator.put(uri, inboundPortURI);
		return eventEmissionIBP_URI;
	}

	/**
	 * Unregister the correlator
	 * 
	 * @param uri correlator uri
	 * @throws Exception
	 */
	public void unregisterCorrelator(String uri) throws Exception {
		urisMapCorrelator.remove(uri);
	}

	/**
	 * Register the action executor
	 * 
	 * @param uri            action executor uri
	 * @param inboundPortURI uri of the inbound port offering ActionExcutionCI
	 *                       interface
	 */
	public void registerExecutor(String uri, String inboundPortURI) {
		urisMapExecutor.put(uri, inboundPortURI);
	}

	/**
	 * Get the uri of the inbound port of the action executor
	 * 
	 * @param uri action executor uri
	 * @return inbound port uri of the action executor
	 */
	public String getExecutorInboundPortURI(String uri) {
		return this.urisMapExecutor.get(uri);
	}

	/**
	 * Unregisters the action executor
	 * 
	 * @param uri action executor uri
	 */
	public void unregisterExecutor(String uri) {
		urisMapExecutor.remove(uri);
	}

	/**
	 * Subscribing the correlator to the emitted event
	 * 
	 * @param subscriberURI subscriber uri
	 * @param emitterURI    emitter uri
	 * @throws Exception
	 */
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		EventReceptionOutboundPort receptionOBP = new EventReceptionOutboundPort(this);
		receptionOBP.publishPort();

		doPortConnection(receptionOBP.getPortURI(), urisMapCorrelator.get(subscriberURI),
				EventReceptionConnector.class.getCanonicalName());

		if (subscribeMap.get(emitterURI) != null) {
			subscribeMap.get(emitterURI).add(subscriberURI);
			mapOutBoundPorts.get(emitterURI).add(receptionOBP);
		} else {
			Vector<String> l = new Vector<String>();
			l.add(subscriberURI);
			subscribeMap.put(emitterURI, l);
			listReceptionObp.add(receptionOBP);
			mapOutBoundPorts.put(emitterURI, listReceptionObp);
		}

	}

	/**
	 * Unsubscribe the correlator
	 * 
	 * @param subscriberURI subscriber uri
	 * @param emitterURI    emitter uri
	 */
	public void unsubscribe(String subscriberURI, String emitterURI) {
		Vector<String> save = (Vector<String>) subscribeMap.get(emitterURI);
		save.remove(subscriberURI);
		subscribeMap.remove(emitterURI);
		subscribeMap.put(emitterURI, save);
	}

}
