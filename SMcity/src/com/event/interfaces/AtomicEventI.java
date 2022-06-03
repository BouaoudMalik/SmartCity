package com.event.interfaces;

import java.io.Serializable;

public interface AtomicEventI extends EventI {

	public Serializable putProperty(String name, Serializable value);

	public void removeProperty(String name);

}
