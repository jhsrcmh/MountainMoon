package com.simjava;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;
public class Sink extends Sim_entity {
	  private Sim_port in;
	  private Sim_port out1, out2;
	  private double delay;

	  Sink(String name, double delay) {
	    super(name);
	    this.delay = delay;
	    in = new Sim_port("In");           // Port for receiving events from the source
	    out1 = new Sim_port("Out1");       // Port for sending events to disk 1
	    out2 = new Sim_port("Out2");       // Port for sending events to disk 2
	    add_port(in);
	    add_port(out1);
	    add_port(out2);
	  }

	  public void body() {
	    int i = 0;
	    while (Sim_system.running()) {
	      Sim_event e = new Sim_event();
	      sim_get_next(e);                 // Get the next event
	      sim_process(delay);              // Process the event
	      sim_completed(e);                // The event has completed service
	      if ((i % 2) == 0) {
	        sim_schedule(out1, 0.0, 1);    // Even I/O jobs go to disk 1
	      } else {
	        sim_schedule(out2, 0.0, 1);    // Odd I/O jobs go to disk 2
	      }
	      i++;
	    }
	  }

}
