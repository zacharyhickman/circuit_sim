package zacharyhickman.circuittest.common.sim.base;

import org.ejml.simple.SimpleMatrix;

import zacharyhickman.circuittest.common.sim.SimCircuit;

/**
 * An object that represents a component in a circuit.
 * @author zhick
 *
 */
public abstract class SimCircuitComponent
{
	// The string id of this component.
	String deviceName = "";
	
	// Constructor for the component.
	public SimCircuitComponent(String deviceName)
	{
		this.deviceName = deviceName;
	}
	
	/**
	 * Get the string id of this device.
	 * @return
	 */
	public String getComponentName()
	{
		return this.deviceName;
	}
	
	/**
	 * Apply to the main circuit matrix the conductance contribution of this element (LHS).
	 * @param circuit
	 */
	public SimpleMatrix getConductanceStampLHS(SimCircuit circuit)
	{
		return null;
	}
	
	/**
	 * Apply to the main circuit matrix the conductance contribution of this element (RHS).
	 * @param circuit
	 */
	public SimpleMatrix getVariableStampRHS(SimCircuit circuit)
	{
		return null;
	}
}
