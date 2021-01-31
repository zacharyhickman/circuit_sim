package zacharyhickman.circuittest.common.sim.component;

import org.ejml.simple.SimpleMatrix;

import zacharyhickman.circuittest.common.sim.SimCircuit;
import zacharyhickman.circuittest.common.sim.base.SimCircuitComponent;
import zacharyhickman.circuittest.common.sim.base.SimCircuitNode;

public class SimComponentCapacitor extends SimCircuitComponent
{
	// The capacitance of this capacitor.
	float capacitance = 0.0f;
	
	// The node connected to the first terminal of this two-terminal capacitor.
	SimCircuitNode terminalA;
	
	// The node connected to the second terminal of this two-terminal capacitor.
	SimCircuitNode terminalB;
	
	public SimComponentCapacitor(String deviceName, SimCircuitNode terminalA, SimCircuitNode terminalB, float capacitance)
	{
		super(deviceName);
		this.capacitance = capacitance;
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
		SimpleMatrix capacitorLHS = new SimpleMatrix(circuit.getRowCountLHSMatrix(), circuit.getColumnCountLHSMatrix());
		
		/*
		 * From the top left, calculate the location of the stamp elements based on what nodes this capacitor is connected to.
		 * Note that we use nodeId-1, because the reference node is not included in the matrix. We also check if one of the nodes is ground node, and alter accordingly.
		 * We don't include a stamp element if one of the terminals is connected to ground.
		 */
		if (this.terminalA.getNodeId() != 0) 
			capacitorLHS.set(this.terminalA.getNodeId() - 1, this.terminalA.getNodeId() - 1, this.capacitance/circuit.getTimeStep());
		if ((this.terminalA.getNodeId() != 0) && (this.terminalB.getNodeId() != 0)) 
			capacitorLHS.set(this.terminalB.getNodeId() - 1, this.terminalA.getNodeId() - 1, -this.capacitance/circuit.getTimeStep());
		if ((this.terminalA.getNodeId() != 0) && (this.terminalB.getNodeId() != 0)) 
			capacitorLHS.set(this.terminalA.getNodeId() - 1, this.terminalB.getNodeId() - 1, -this.capacitance/circuit.getTimeStep());
		if (this.terminalB.getNodeId() != 0) 
			capacitorLHS.set(this.terminalB.getNodeId() - 1, this.terminalB.getNodeId() - 1, this.capacitance/circuit.getTimeStep());
		
		// Return the matrix that contains the conductance values of the capacitor.
		return capacitorLHS;		
	}
	
	/**
	 * Apply to the main circuit matrix the conductance contribution of this element (RHS).
	 * @param circuit
	 */
	@Override
	public SimpleMatrix getVariableStampRHS(SimCircuit circuit)
	{
		// Create a new matrix, sized based on nodes in the circuit, to populate with current values from this capacitor.
		SimpleMatrix capacitorRHS = new SimpleMatrix(circuit.getRowCountLHSMatrix(), 1);
		
		/*
		 * From the top left, calculate the location of the stamp elements based on what nodes this capacitor is connected to. 
		 * Note that we use nodeId-1, because the reference node is not included in the matrix. We also check if one of the nodes is ground node, and alter accordingly.
		 * We don't include a stamp element if one of the terminals is connected to ground.
		 */
		float deltaV = (this.terminalA.getLastNodeVoltage() - this.terminalB.getLastNodeVoltage());
		if (this.terminalA.getNodeId() != 0)
			capacitorRHS.set((this.terminalA.getNodeId() - 1), 0, (this.capacitance/circuit.getTimeStep())*(this.terminalA.getLastNodeVoltage() - this.terminalB.getLastNodeVoltage()));
		if (this.terminalB.getNodeId() != 0)
			capacitorRHS.set((this.terminalB.getNodeId() - 1), 0, -(this.capacitance/circuit.getTimeStep())*(this.terminalA.getLastNodeVoltage() - this.terminalB.getLastNodeVoltage()));
		
		// Return the matrix that contains the current values through this capacitor based on last voltage across it.
		return capacitorRHS;		
	}
}
