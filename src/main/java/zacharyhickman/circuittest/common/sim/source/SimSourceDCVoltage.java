package zacharyhickman.circuittest.common.sim.source;

import org.ejml.simple.SimpleMatrix;

import zacharyhickman.circuittest.common.sim.SimCircuit;
import zacharyhickman.circuittest.common.sim.base.SimCircuitNode;
import zacharyhickman.circuittest.common.sim.base.SimCircuitVoltageSource;

public class SimSourceDCVoltage extends SimCircuitVoltageSource
{
	// The DC voltage level of this source.
	float dcVoltage = 1f;
		
	// The node connected to the first terminal of this two-terminal voltage source.
	SimCircuitNode terminalA;
	
	// The node connected to the second terminal of this two-terminal voltage source.
	SimCircuitNode terminalB;
	
	// Constructor of this DC voltage source.
	public SimSourceDCVoltage(String sourceName, SimCircuitNode terminalA, SimCircuitNode terminalB, float dcVoltage)
	{
		super(sourceName);
		this.dcVoltage = dcVoltage;
		this.terminalA = terminalA;
		this.terminalB = terminalB;
	}

	/**
	 * Return the source contribution matrix of this voltage source (LHS).
	 * @param circuit
	 */
	public SimpleMatrix getConductanceStampLHS(SimCircuit circuit)
	{
		// Create a new matrix, sized based on nodes in the circuit, to populate with 1s or -1s.
		SimpleMatrix dcVoltageLHS = new SimpleMatrix(circuit.getRowCountLHSMatrix(), circuit.getColumnCountLHSMatrix());
		
		/*
		 * For a dc voltage source, we need to place a 1 or a -1 on the LHS matrix of the circuit, depending on what nodes are connected to this source.
		 * These 1s or -1s reside on their own row and column which represents a branch. We use branchStartingIndex to skip past the stamp slots and
		 * move straight to the branch section of the matrix.
		 */
		int branchStartingIndex = circuit.getNumberOfNonReferenceNodes();
		if (this.terminalA.getNodeId() != 0) 
			dcVoltageLHS.set((this.terminalA.getNodeId() - 1), branchStartingIndex + this.getBranchId(), 1);
		if (this.terminalA.getNodeId() != 0) 
			dcVoltageLHS.set(branchStartingIndex + this.getBranchId(), (this.terminalA.getNodeId() - 1), 1);
		if (this.terminalB.getNodeId() != 0) 
			dcVoltageLHS.set((this.terminalB.getNodeId() - 1), branchStartingIndex + this.getBranchId(), -1);
		if (this.terminalB.getNodeId() != 0) 
			dcVoltageLHS.set(branchStartingIndex + this.getBranchId(), (this.terminalB.getNodeId() - 1), -1);
		
		// Return the matrix that contains the conductance values of the capacitor.
		return dcVoltageLHS;		
	}
	
	/**
	 * Return the source contribution vector of this dc voltage source (RHS).
	 * @param circuit
	 */
	public SimpleMatrix getVariableStampRHS(SimCircuit circuit)
	{
		// Create a new matrix, sized based on nodes in the circuit, to populate with current values from this capacitor.
		SimpleMatrix dcVoltageRHS = new SimpleMatrix(circuit.getRowCountLHSMatrix(), 1);
		
		/*
		 * For a dc voltage source, we need to place a 1 or a -1 on the LHS matrix of the circuit, depending on what nodes are connected to this source.
		 * These 1s or -1s reside on their own row and column which represents a branch. For the RHS, we need to set the coresponding node voltages based on the voltage
		 * level of this dc voltage source. We use branchStartingIndex to skip past the stamp slots and move straight to the branch section of the matrix.
		 */
		int branchStartingIndex = circuit.getNumberOfNonReferenceNodes();
		dcVoltageRHS.set(branchStartingIndex + this.getBranchId(), 0, this.dcVoltage);
		
		// Return the matrix that contains the current values through this capacitor based on last voltage across it.
		return dcVoltageRHS;		
	}
}
