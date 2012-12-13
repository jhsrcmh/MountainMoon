package com.simjava;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;

public class Disk extends Sim_entity {
	 private Sim_port in;
	  private double delay;

	  Disk(String name, double delay) {
	    super(name);
	    this.delay = delay;
	    in = new Sim_port("In");           // Port for receiving events from the processor
	    add_port(in);
	  }

	  
	  public void body() {
	    while (Sim_system.running()) {
	      Sim_event e = new Sim_event();
	      sim_get_next(e);                 // Get the next event
	      sim_process(delay);              // Process the event
	      sim_completed(e);                // The event has completed service
	    }
	  }
}
