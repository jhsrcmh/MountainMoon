package com.simjava;
import eduni.simjava.Sim_system;
public class ProcessorSubsystem {

	public static void main(String[] args) { // The main method
		Sim_system.initialise(); // Initialise Sim_system
		Source source = new Source("Source", 50); // Add the source
		Sink processor = new Sink("Processor", 30); // Add the processor
		Disk disk1 = new Disk("Disk1", 60); // Add disk 1
		Disk disk2 = new Disk("Disk2", 110); // Add disk 2
		// Link the entities' ports
		Sim_system.link_ports("Source", "Out", "Processor", "In");
		Sim_system.link_ports("Processor", "Out1", "Disk1", "In");
		Sim_system.link_ports("Processor", "Out2", "Disk2", "In");
		Sim_system.run(); // Run the simulation
	}
}
