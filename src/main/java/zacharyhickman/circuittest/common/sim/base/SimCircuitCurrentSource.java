package zacharyhickman.circuittest.common.sim.base;

import zacharyhickman.circuittest.common.sim.SimCircuit;

/**
 * Object that represents a source object in a circuit.
 * @author zhick
 *
 */
public abstract class SimCircuitCurrentSource
{
	// The string id of this current source.
	String sourceName = "";
	
	// Constructor for the source.
	public SimCircuitCurrentSource(String sourceName)
	{
		this.sourceName = sourceName;
	}
	
	/**
	 * Returns the source string id of this current source.
	 * @return
	 */
	public String getSourceName()
	{
		return this.sourceName;
	}
	
	// Get the current through this source (from terminal A to terminal B).
	public abstract float getCurrentValue();
	
	// Simulate some behavior that the source needs to exhibit.
	public void simulateBehavior(SimCircuit circuit)
	{
		
	}
}
