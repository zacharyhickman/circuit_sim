package zacharyhickman.circuittest.common.sim.base;

import zacharyhickman.circuittest.common.sim.SimCircuit;

/**
 * Object that represents a source object in a circuit.
 * @author zhick
 *
 */
public abstract class SimCircuitVoltageSource
{
	// The string id of this voltage source.
	String sourceName = "";
	
	// The branch id of this voltage source.
	int branchId = 0;
	
	// Constructor for the source.
	public SimCircuitVoltageSource(String sourceName)
	{
		this.sourceName = sourceName;
	}
	
	/**
	 * Returns the source string id of this voltage source.
	 * @return
	 */
	public String getSourceName()
	{
		return this.sourceName;
	}
	
	/**
	 * Set the branch id of this source.
	 * @return
	 */
	public void setBranchId(int branchId)
	{
		this.branchId = branchId;
	}
	
	/**
	 * Get the branch id of this source.
	 * @return
	 */
	public int getBranchId()
	{
		return this.branchId;
	}
	
	// Simulate some behavior that the source needs to exhibit.
	public void simulateBehavior(SimCircuit circuit)
	{
		
	}
}
