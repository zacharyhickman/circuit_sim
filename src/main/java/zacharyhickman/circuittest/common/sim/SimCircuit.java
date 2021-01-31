package zacharyhickman.circuittest.common.sim;

import java.util.HashMap;

import org.ejml.LinearSolverSafe;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.simple.SimpleMatrix;

import zacharyhickman.circuittest.common.sim.base.SimCircuitNode;
import zacharyhickman.circuittest.common.sim.component.SimComponentCapacitor;
import zacharyhickman.circuittest.common.sim.component.SimComponentInductor;
import zacharyhickman.circuittest.common.sim.component.SimComponentResistor;
import zacharyhickman.circuittest.common.sim.source.SimSourceDCCurrent;
import zacharyhickman.circuittest.common.sim.source.SimSourceDCVoltage;
import zacharyhickman.circuittest.common.sim.util.MatrixHelper;

/**
 * An object that represents a simulatable circuit.
 * @author zhick
 *
 */
public class SimCircuit
{	
	// Constructor of the circuit simulator.
	public SimCircuit(float startTime, float timeStep)
	{
		this.currentTime = startTime;
		this.lastTime = this.currentTime;
		this.timeStep = timeStep;
	}
	
	// Constructor of the circuit simulator which has an option of Vi = 0V.
	public SimCircuit(float startTime, float timeStep, boolean startUp)
	{
		this.currentTime = startTime;
		this.lastTime = this.currentTime;
		this.timeStep = timeStep;
		this.startUp = startUp;
	}
	
	/**
	 * Adds a circuit node to the circuit of a given name. Does nothing if a node of the same name already exists.
	 * @param node
	 */
	public void addNewCircuitNode(String nodeName)
	{
		if (this.circuitNodes.containsKey(nodeName))
		{
			// Do nothing.
		}
		else
		{
			// Add new node instance to circuit nodes.
			this.circuitNodes.put(nodeName, new SimCircuitNode(nodeName));
			rebuildSimMatricies();
		}
	}
	
	/**
	 * Adds a circuit node to the circuit. Does nothing if a node of the same name already exists.
	 * @param node
	 */
	public void addNewCircuitNode(SimCircuitNode node)
	{
		if (this.circuitNodes.containsKey(node.getNodeName()))
		{
			// Do nothing.
		}
		else
		{
			// Add new node instance to circuit nodes.
			this.circuitNodes.put(node.getNodeName(), node);
			rebuildSimMatricies();
		}
	}
	
	/**
	 * Removes a circuit node of a given name. Does nothing if the node of the given name does not exist.
	 * @param nodeName
	 */
	public void removeCircuitNode(String nodeName)
	{
		if (this.circuitNodes.containsKey(nodeName))
		{
			// Remove the stored node.
			this.circuitNodes.remove(nodeName);
			// Rebuild the conductance matrix.
			rebuildSimMatricies();
		}
		else
		{
			// Do nothing.
		}
	}
	
	/**
	 * Removes a circuit node. Does nothing if the node of the given name does not exist.
	 * @param nodeName
	 */
	public void removeCircuitNode(SimCircuitNode node)
	{
		if (this.circuitNodes.containsKey(node.getNodeName()))
		{
			// Remove the stored node.
			this.circuitNodes.remove(node.getNodeName());
			// Rebuild the conductance matrix.
			rebuildSimMatricies();
		}
		else
		{
			// Do nothing.
		}
	}
	
	/**
	 * Adds a resistor to the circuit. Does nothing if a resistor of the same name already exists.
	 * @param node
	 */
	public void addResistorComponent(SimComponentResistor resistor)
	{
		if (this.resistors.containsKey(resistor.getComponentName()))
		{
			// Do nothing.
		}
		else
		{
			// Add new node instance to circuit nodes.
			this.resistors.put(resistor.getComponentName(), resistor);
			rebuildSimMatricies();
		}
	}
	
	/**
	 * Adds a capacitor to the circuit. Does nothing if a capacitor of the same name already exists.
	 * @param node
	 */
	public void addCapacitorComponent(SimComponentCapacitor capacitor)
	{
		if (this.capacitors.containsKey(capacitor.getComponentName()))
		{
			// Do nothing.
		}
		else
		{
			// Add new node instance to circuit nodes.
			this.capacitors.put(capacitor.getComponentName(), capacitor);
			rebuildSimMatricies();
		}
	}
	
	/**
	 * Adds a DC current source to the circuit. Does nothing if the circuit contains a dc current source of the same name.
	 * @param dcCurrent
	 */
	public void addDCCurrentSource(SimSourceDCCurrent dcCurrent)
	{
		if (this.currentSourcesDC.containsKey(dcCurrent.getSourceName()))
		{
			// Do nothing.
		}
		else
		{
			// Add new node instance to circuit nodes.
			this.currentSourcesDC.put(dcCurrent.getSourceName(), dcCurrent);
			rebuildSimMatricies();
		}
	}
	
	/**
	 * Adds a DC voltage source to the circuit. Does nothing if the circuit contains a dc voltage source of the same name.
	 * @param dcCurrent
	 */
	public void addDCVoltageSource(SimSourceDCVoltage dcVoltage)
	{
		if (this.voltageSourcesDC.containsKey(dcVoltage.getSourceName()))
		{
			// Do nothing.
		}
		else
		{
			// Add new node instance to circuit nodes.
			this.voltageSourcesDC.put(dcVoltage.getSourceName(), dcVoltage);
			rebuildSimMatricies();
		}
	}
	
	/**
	 * Clears and recreates the conductance matrix and the variable vector based on present nodes and components.
	 */
	public void rebuildSimMatricies()
	{
		// Remap existing nodes and branches to an integer.
		remapCircuitNodes();
		remapBranches();
				
		// Calculate the size needed for the conductance matrix.
		// rows = #(non-reference nodes) + #(inductor branches) + #(capacitor branches) + #(voltage source branches)
		int rowsG = (this.circuitNodes.size() - 1) + (this.inductors.size()) + (this.capacitors.size()) + this.voltageSourcesDC.size();
		int columnsG = rowsG;
		
		// Calculate the size needed for the variable vector.
		// rows = #(non-reference nodes) + #(inductor branches) + #(capacitor branches) + #(voltage source branches)
		int rowsV = (this.circuitNodes.size() - 1) + (this.inductors.size()) + (this.capacitors.size()) + this.voltageSourcesDC.size();
		int columnsV = 1;
		
		// Create/rewrite the matrices based on calculated size.
		this.matLHS = new SimpleMatrix(rowsG, columnsG);
		this.matRHS = new SimpleMatrix(rowsV, columnsV);
		
		// Apply stamps from the resistors in the circuit.
		for (SimComponentResistor resistor : this.resistors.values())
		{
			// Get contribution matrix from resistor and add to the LHS matrix.
			this.matLHS = MatrixHelper.addMatrix(this.matLHS, resistor.getConductanceStampLHS(this));
		}
		
		// Apply stamps from the capacitors in the circuit.
		for (SimComponentCapacitor capacitor : this.capacitors.values())
		{
			// Get contribution matrix from capactior and add to the LHS matrix.
			this.matLHS = MatrixHelper.addMatrix(this.matLHS, capacitor.getConductanceStampLHS(this));
			
			// Because the capacitor is a reactive component, it also affects the rhs matrix as well.
			this.matRHS = MatrixHelper.addMatrix(this.matRHS, capacitor.getVariableStampRHS(this));
		}
		
		// Apply stamps from the dc current sources in the circuit.
		for (SimSourceDCCurrent currentSource : this.currentSourcesDC.values())
		{
			// Get contribution matrix from current source and add to the RHS matrix.
			this.matRHS = MatrixHelper.addMatrix(this.matRHS, currentSource.getVariableStampRHS(this));
		}
		
		// Apply stamps from the dc voltage sources in the circuit.
		for (SimSourceDCVoltage voltageSource : this.voltageSourcesDC.values())
		{
			// Get contribution matrix from voltage source and add to the LHS matrix.
			this.matLHS = MatrixHelper.addMatrix(this.matLHS, voltageSource.getConductanceStampLHS(this));
			
			// Get contribution matrix from voltage source and add to the RHS matrix.
			this.matRHS = MatrixHelper.addMatrix(this.matRHS, voltageSource.getVariableStampRHS(this));
		}
	}
	
	/**
	 * Associates existing nodes with an integer identifier. Returns true if the mapping was successful.
	 * @return
	 */
	public boolean remapCircuitNodes()
	{
		// Declare an iterator variable that will be used to keep track of the current assigned index.
		int currentNodeIndex = 0;
		
		// Boolean pass variable for reference node assignment.
		boolean hasReference = false;
		
		// Clear old node mapping.
		this.circuitNodeIndexMap.clear();
		
		// First, find the ground node and assign it an index of zero.
		for (SimCircuitNode node : this.circuitNodes.values())
		{
			if (node.isReferenceNode())
			{
				// Found the reference node, so set its index as zero.
				node.setNodeId(currentNodeIndex);
				this.circuitNodeIndexMap.put(node.getNodeName(), currentNodeIndex);
				// Increment index variable.
				currentNodeIndex++;
				hasReference = true;
				break;
			}
		}
		
		// Check if we found a reference node. If not, stop remapping.
		if (!hasReference) return false;
		
		// Assign the rest of the nodes an index.
		for (SimCircuitNode node : this.circuitNodes.values())
		{
			if (!node.isReferenceNode())
			{
				// Assign index to non-reference nodes.
				node.setNodeId(currentNodeIndex);
				this.circuitNodeIndexMap.put(node.getNodeName(), currentNodeIndex);
				// Increment index variable.
				currentNodeIndex++;
			}
		}
		
		return true;
	}
	
	/**
	 * Associates existing branches with an integer identifier. Returns true if the mapping was successful.
	 * @return
	 */
	public boolean remapBranches()
	{
		// Declare an iterator variable that will be used to keep track of the current assigned index.
		int currentBranchIndex = 0;
		
		// Clear old branch mapping.
		this.circuitBranchIndexMap.clear();
		
		// Find all branches for dc voltage sources and assign them an index.
		for (SimSourceDCVoltage dcVSource : this.voltageSourcesDC.values())
		{
			dcVSource.setBranchId(currentBranchIndex);
			this.circuitBranchIndexMap.put(dcVSource.getSourceName() + "_i", currentBranchIndex);
			// Increment index variable.
			currentBranchIndex++;
		}
		
		return true;
	}
	
	/**
	 * Resets all node voltages to zero and resets the current time.
	 * @return
	 */
	public void resetSim()
	{
		// Reset the current time to zero.
		this.currentTime = 0f;
		
		// Set start flag to true, for the next sim step to set false.
		this.firstSimStep = true;
		
		// Reset all node voltages to zero.
		for (SimCircuitNode node : this.circuitNodes.values())
		{
			node.setNodeVoltage(0f);
		}
	}
	
	/**
	 * Populate the results matrix of this circuit.
	 * @return
	 */
	public void stepSimulation()
	{
		// Remap the stamp matricies and nodes for a new time instance.
		remapCircuitNodes();
		rebuildSimMatricies();
		
		// Create/rewrite the result circuit based on the row length of the conductance matrix.
		this.matSol = new SimpleMatrix(this.matLHS.numRows(), 1);
		
		// Solve the matrix.
		// Create solver.
		LinearSolverSafe<DMatrixRMaj> solver = new LinearSolverSafe<DMatrixRMaj>(LinearSolverFactory_DDRM.leastSquaresQrPivot(true, false));
		// Declare empty solution matrix, A matrix, and B matrix.
		DMatrixRMaj solutionMat = new DMatrixRMaj(this.matLHS.numRows(), this.matLHS.numCols());
		DMatrixRMaj bMat = MatrixHelper.convertSimpleToDMatrixRMaj(this.matRHS);
		DMatrixRMaj aMat = MatrixHelper.convertSimpleToDMatrixRMaj(this.matLHS);
		// Set A matrix of solver, converting to complex mat object in the process.
		solver.setA(aMat);
		// Solve the matrix and store in solution.
		solver.solve(bMat, solutionMat);
		// Set solution to circuit storage object, converting to simple mat object in the process.
		this.matSol = MatrixHelper.convertDMatrixRMajToSimple(solutionMat);
		
		// If this is the first simulation step, go ahead and set the last voltage of the nodes to whatever voltage was calclated initially.
		if (this.firstSimStep)
		{
			// Reset flag.
			this.firstSimStep = false;
			
			// Set node voltages to previous.
			for (SimCircuitNode node : this.circuitNodes.values())
			{
				// Skip the ground reference node.
				if (getIndexOfNode(node.getNodeName()) == 0) continue;
				
				// Search the integer mapping of the nodes and get its index.
				int nodeIndex = getIndexOfNode(node.getNodeName());
				
				// Set the voltage based on the position/element of the solution matrix. This also saves the previous voltage of the node which helps with reactive components.
				// If startUp is true, all voltage nodes should start with Vi = 0.
				if (this.startUp)
				{
					node.setNodeVoltage(0f);
				}
				else
				{
					node.setNodeVoltage((float) this.matSol.get(nodeIndex - 1, 0));
				}
				node.setLastNodeVoltage(node.getNodeVoltage());
			}
		}
		else
		{
			// Set node voltages to new voltages solved.
			for (SimCircuitNode node : this.circuitNodes.values())
			{
				// Skip the ground reference node.
				if (getIndexOfNode(node.getNodeName()) == 0) continue;
				
				// Search the integer mapping of the nodes and get its index.
				int nodeIndex = getIndexOfNode(node.getNodeName());
				
				// Set the voltage based on the position/element of the solution matrix. This also saves the previous voltage of the node which helps with reactive components.
				node.setNodeVoltage((float) this.matSol.get(nodeIndex - 1, 0));
			}
		}
		
		// Advance the current time based on the current step.
		this.lastTime = this.currentTime;
		this.currentTime += this.timeStep;
	}
	
	/**
	 * Return the solution matrix of this circuit, based on the last run simulation.
	 * @return
	 */
	public SimpleMatrix getSolutionMatrix()
	{
		return this.matSol;
	}
	
	/**
	 * Get the total number of voltage nodes in the circuit that are not the reference node.
	 * @return
	 */
	public int getNumberOfNonReferenceNodes()
	{
		return this.circuitNodes.size() - 1;
	}
	
	/**
	 * Get the row count of the LHS matrix of this circuit.
	 * @return
	 */
	public int getRowCountLHSMatrix()
	{
		return this.matLHS.numRows();
	}
	
	/**
	 * Get the row count of the LHS matrix of this circuit.
	 * @return
	 */
	public int getColumnCountLHSMatrix()
	{
		return this.matLHS.numCols();
	}
	
	/**
	 * Get the row count of the RHS matrix of this circuit.
	 * @return
	 */
	public int getColumnCountRHSMatrix()
	{
		return this.matRHS.numCols();
	}
	
	/**
	 * Get the row count of the RHS matrix of this circuit.
	 * @return
	 */
	public int getRowCountRHSMatrix()
	{
		return this.matRHS.numRows();
	}
	
	/**
	 * Returns the index of the node of the given name.
	 * @param nodeName
	 * @return
	 */
	public int getIndexOfNode(String nodeName)
	{
		return this.circuitNodeIndexMap.get(nodeName);
	}
	
	/**
	 * Returns the index of the branch of the given source name.
	 * @param nodeName
	 * @return
	 */
	public int getIndexOfBranch(String sourceName)
	{
		return this.circuitBranchIndexMap.get(sourceName + "_i");
	}
	
	/**
	 * Get the current sim time.
	 * @return
	 */
	public float getTime()
	{
		return this.currentTime;
	}
	
	/**
	 * Get the last sim time.
	 * @return
	 */
	public float getLastTime()
	{
		return this.lastTime;
	}
	
	/**
	 * Get the difference in time between the last simulation iteration and the current one.
	 * @return
	 */
	public float getDeltaTime()
	{
		return (this.currentTime - this.lastTime);
	}

	/**
	 * Get the time step used by the circuit simulation.
	 * @return
	 */
	public float getTimeStep()
	{
		return this.timeStep;
	}
	
	// The current time of the circuit.
	float currentTime = 0f;
	
	// The last time instance of the circuit.
	float lastTime = 0f;
	
	// The time step of the circuit.
	float timeStep = 1f;
	
	// Whether or not the next step of the sim is the first. Affects starting voltages across elements.
	boolean firstSimStep = true;
	
	// Whether or not voltage levels should start out @ 0V.
	boolean startUp = false;
	
	// The nodes present in this circuit.
	public HashMap<String, SimCircuitNode> circuitNodes = new HashMap<String, SimCircuitNode>();
	
	// The dc voltage sources present in this circuit.
	public HashMap<String, SimSourceDCVoltage> voltageSourcesDC = new HashMap<String, SimSourceDCVoltage>();
	
	// The dc current sources present in this circuit.
	public HashMap<String, SimSourceDCCurrent> currentSourcesDC = new HashMap<String, SimSourceDCCurrent>();
	
	// The resistors present in this circuit.
	public HashMap<String, SimComponentResistor> resistors = new HashMap<String, SimComponentResistor>();
	
	// The capacitors present in this circuit.
	public HashMap<String, SimComponentCapacitor> capacitors = new HashMap<String, SimComponentCapacitor>();
	
	// The inductors present in this circuit.
	public HashMap<String, SimComponentInductor> inductors = new HashMap<String, SimComponentInductor>();
	
	// The mapping of nodes to an index.
	public HashMap<String, Integer> circuitNodeIndexMap = new HashMap<String, Integer>();
	
	// The mapping of branches to an index.
	public HashMap<String, Integer> circuitBranchIndexMap = new HashMap<String, Integer>();
	
	// The condunctance interconnection matrix of the circuit. Rebuilt on circuit element add/remove.
	public SimpleMatrix matLHS;
	
	// The variable vector matrix of the circuit. Rebuilt on circuit element add/remove.
	public SimpleMatrix matRHS;
	
	// The results of this circuit after simulation.
	public SimpleMatrix matSol;
	
	
}
