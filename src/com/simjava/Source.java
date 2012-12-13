package com.simjava;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
public class Source extends Sim_entity {
	 private Sim_port out;
	  private double delay;

	  Source(String name, double delay) {
	    super(name);
	    this.delay = delay;
	    out = new Sim_port("Out");         // Port for sending events to the processor
	    add_port(out);
	  }

	  public void body() {
	    for (int i=0; i < 100; i++) {
	      sim_schedule(out, 0.0, 0);       // Send the processor a job
	      sim_pause(delay);                // Pause
	    }
	  }
}
