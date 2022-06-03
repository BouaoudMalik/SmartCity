package com.event.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.event.samu.HealthEvent;

/**
 * JUnit test class for an AtomicEvent
 *
 */
class AtomicEventTest {

	/**
	 * Method that should return true if the property is added to healthAlarm
	 * 
	 */
	@Test
	void testIfPropertyIsAdded() {
		HealthEvent healthAlarm = new HealthEvent();
		healthAlarm.putProperty("type", "tracking");
		assertTrue(healthAlarm.getPropertyValue("type").equals("tracking"));
	}

	/**
	 * Method that should return false if the property "type" has been removed
	 * 
	 */
	@Test
	void testIfPropertyIsRemoved() {
		HealthEvent healthAlarm = new HealthEvent();
		healthAlarm.putProperty("type", "tracking");
		healthAlarm.removeProperty("type");
		assertFalse(healthAlarm.hasProperty("type"));
	}

	/**
	 * A method testing if the time of the creation is before the current time
	 * 
	 */
	@Test
	void testifTimeStamp() {
		HealthEvent healthAlarm = new HealthEvent();
		healthAlarm.putProperty("type", "tracking");
		assertTrue(healthAlarm.getTimeStamp().compareTo(LocalTime.now()) < 0);
	}

}
