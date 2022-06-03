package com.rule;

import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;
import com.rule.interfaces.CorrelatorStateI;
import com.rule.interfaces.RuleI;

public class RuleBase {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private ArrayList<RuleI> rules;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public RuleBase() {
		this.rules = new ArrayList<>();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Method adding a rule r to the array list of rules
	 * 
	 * @param r a rule
	 */
	public void addRule(RuleI r) {
		this.rules.add(r);
	}

	/**
	 * Method checking if a rule (match/correlate/filter/update/act) is correctly
	 * executed
	 * 
	 * @param eb the event base
	 * @param c  the correlator state
	 * @return boolean value
	 */
	public boolean fireFirstOn(EventBaseI eb, CorrelatorStateI c) {
		ArrayList<EventI> matchedEvents;
		for (int i = 0; i < this.rules.size(); i++) {
			matchedEvents = this.rules.get(i).match(eb);
			if (matchedEvents != null) {
				if (this.rules.get(i).correlate(matchedEvents)) {
					if (this.rules.get(i).filter(matchedEvents, c)) {
						this.rules.get(i).act(matchedEvents, c);
						this.rules.get(i).update(matchedEvents, eb);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * This method calls fireFirstOn as long as a rule is triggered and only stop
	 * when there are no more rules to triggered
	 * 
	 * @param eb the event base
	 * @param c  the correlator state
	 * @return boolean value
	 */
	public boolean fireAllOn(EventBaseI eb, CorrelatorStateI c) {
		ArrayList<EventI> matchedEvents;
		Boolean response = false;
		for (int i = 0; i < this.rules.size(); i++) {
			matchedEvents = this.rules.get(i).match(eb);
			if (matchedEvents != null) {
				if (this.rules.get(i).correlate(matchedEvents)) {
					if (this.rules.get(i).filter(matchedEvents, c)) {
						this.rules.get(i).act(matchedEvents, c);
						this.rules.get(i).update(matchedEvents, eb);
						response = true;
					}
				}
			}
		}
		return response;
	}

}
