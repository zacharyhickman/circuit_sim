package zacharyhickman.circuittest.common.sim.component;

import org.ejml.simple.SimpleMatrix;

import zacharyhickman.circuittest.common.sim.SimCircuit;
import zacharyhickman.circuittest.common.sim.base.SimCircuitComponent;
import zacharyhickman.circuittest.common.sim.base.SimCircuitNode;

public class SimComponentResistor extends SimCircuitComponent
{
	// The resistance of this resistor.
	private float resistance = 0.0f;
	
	// The node connected to the first terminal of this two-terminal resistor.
	SimCircuitNode terminalA;
	
	// The node connected to the second terminal of this two-terminal resistor.
	SimCircuitNode terminalB;
	
	public SimComponentResistor(String deviceName, SimCircuitNode terminalA, SimCircuitNode terminalB, float resistance)
	{
		super(deviceName);
		this.resistance = resistance;
		this.terminalA = terminalA;
		this.terminalB = terminalB;
	}
	
	/**
	 * Return the conductance contribution matrix of this element (LHS).
	 * @param circuit
	 */
	@Override
	public SimpleMatrix getConductanceStampLHS(SimCircuit circuit)
	{
		// Create a new matrix, sized based on nodes in the circuit, to populate with conductance values.
		SimpleMatrix resistorLHS = new SimpleMatrix(circuit.getRowCountLHSMatrix(), circuit.getColumnCountLHSMatrix());
		
		/*
		 * From the top left, calculate the location of the stamp elements based on what nodes this resistor is connected to.
		 * Note that we use nodeId-1, because the reference node is not included in the matrix. We also check if one of the nodes is ground node, and alter accordingly.
		 * We don't include a stamp element if one of the terminals is connected to ground.
		 */
		if (this.terminalA.getNodeId() != 0) 
			resistorLHS.set(this.terminalA.getNodeId() - 1, this.terminalA.getNodeId() - 1, 1/this.resistance);
		if ((this.terminalA.getNodeId() != 0) && (this.terminalB.getNodeId() != 0)) 
			resistorLHS.set(this.terminalB.getNodeId() - 1, this.terminalA.getNodeId() - 1, -1/this.resistance);
		if ((this.terminalA.getNodeId() != 0) && (this.terminalB.getNodeId() != 0)) 
			resistorLHS.set(this.terminalA.getNodeId() - 1, this.terminalB.getNodeId() - 1, -1/this.resistance);
		if (this.terminalB.getNodeId() != 0) 
			resistorLHS.set(this.terminalB.getNodeId() - 1, this.terminalB.getNodeId() - 1, 1/this.resistance);
		
		// Return the matrix that contains the conductance values of the resistor.
		return resistorLHS;		
	}
}
