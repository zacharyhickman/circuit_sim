package zacharyhickman.circuittest.common;

import java.io.IOException;

import zacharyhickman.circuittest.common.sim.SimCircuit;
import zacharyhickman.circuittest.common.sim.base.SimCircuitNode;
import zacharyhickman.circuittest.common.sim.component.SimComponentCapacitor;
import zacharyhickman.circuittest.common.sim.component.SimComponentResistor;
import zacharyhickman.circuittest.common.sim.source.SimSourceDCCurrent;
import zacharyhickman.circuittest.common.sim.source.SimSourceDCVoltage;

public class MainProj
{
	public static void main(String[] args)
	{
		// Test circuit with start time of 0S and step of 0.001.
		SimCircuit circuit = new SimCircuit(0f, 0.0001f, true);
		
		// Node declaration.
		SimCircuitNode nodeGnd = new SimCircuitNode("GND", true);
		SimCircuitNode nodeA = new SimCircuitNode("NA");
		SimCircuitNode nodeB = new SimCircuitNode("NB");
		
		// Component declaration.
		SimComponentResistor resistor1 = new SimComponentResistor("R1", nodeA, nodeB, 500);
		SimComponentResistor resistor2 = new SimComponentResistor("R2", nodeB, nodeGnd, 1000);
		SimComponentCapacitor capacitor1 = new SimComponentCapacitor("C1", nodeB, nodeGnd, 0.0000001f);
		
		// Source declaration.
		SimSourceDCVoltage voltageA = new SimSourceDCVoltage("V1", nodeA, nodeGnd, 1f);
		
		// Construct the circuit.
		circuit.addNewCircuitNode(nodeGnd);
		circuit.addNewCircuitNode(nodeA);
		circuit.addNewCircuitNode(nodeB);
		circuit.addResistorComponent(resistor1);
		circuit.addResistorComponent(resistor2);
		circuit.addCapacitorComponent(capacitor1);
		circuit.addDCVoltageSource(voltageA);
		
		// Simulate the circuit multiple times.
		for (int x = 0; x < 10; x++)
		{
			// Step the simulation.
			circuit.stepSimulation();
			
			// Print the results.
			System.out.println("SIM STEP: " + circuit.getLastTime());
			
			for (SimCircuitNode node : circuit.circuitNodes.values())
			{
				System.out.println("NN: " + node.getNodeName());
				System.out.println("cV: " + node.getNodeVoltage());
				System.out.println("lV: " + node.getLastNodeVoltage());
			}
		}
		
		// Pause output.
		try
		{
			System.in.read();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
