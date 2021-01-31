package zacharyhickman.circuittest.common.sim.source;

import org.ejml.simple.SimpleMatrix;

import zacharyhickman.circuittest.common.sim.SimCircuit;
import zacharyhickman.circuittest.common.sim.base.SimCircuitCurrentSource;
import zacharyhickman.circuittest.common.sim.base.SimCircuitNode;

public class SimSourceDCCurrent extends SimCircuitCurrentSource
{
	// The DC current level of this source.
	float dcCurrent = 1f;
		
	// The node connected to the first terminal of this two-terminal current source.
	SimCircuitNode terminalA;
	
	// The node connected to the second terminal of this two-terminal current source.
	SimCircuitNode terminalB;
	
	// Constructor of this DC current source.
	public SimSourceDCCurrent(String sourceName, SimCircuitNode terminalA, SimCircuitNode terminalB, float dcCurrent)
	{
		super(sourceName);
		this.dcCurrent = dcCurrent;
		this.terminalA = terminalA;
		this.terminalB = terminalB;
	}

	/**
	 * Get the current value of this current source.
	 */
	@Override
	public float getCurrentValue()
	{
		// DC current sources do not change with time unless dependent.
		return this.dcCurrent;
	}
	
	/**
	 * Apply to the main circuit matrix the conductance contribution of this element (RHS).
	 * @param circuit
	 */
	public SimpleMatrix getVariableStampRHS(SimCircuit circuit)
	{
		// Create a new matrix, sized based on nodes in the circuit, to populate with current values.
		SimpleMatrix currentSourceRHS = new SimpleMatrix(circuit.getRowCountLHSMatrix(), 1);
		
		/*
		 * From the top left, calculate the location of the stamp elements based on what nodes this current source is connected to. 
		 * Note that we use nodeId-1, because the reference node is not included in the matrix. We also check if one of the nodes is ground node, and alter accordingly.
		 * We don't include a stamp element if one of the terminals is connected to ground.
		 */
		if (this.terminalA.getNodeId() != 0)
			currentSourceRHS.set((this.terminalA.getNodeId() - 1), 0, this.dcCurrent);
		if (this.terminalB.getNodeId() != 0)
			currentSourceRHS.set((this.terminalB.getNodeId() - 1), 0, -this.dcCurrent);
		
		// Return the matrix that contains the current values of this source.
		return currentSourceRHS;		
	}
}
