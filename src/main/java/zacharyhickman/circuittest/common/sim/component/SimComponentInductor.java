package zacharyhickman.circuittest.common.sim.component;

import zacharyhickman.circuittest.common.sim.base.SimCircuitComponent;
import zacharyhickman.circuittest.common.sim.base.SimCircuitNode;

public class SimComponentInductor extends SimCircuitComponent
{
	// The inductance of this inductor.
	float inductance = 0.0f;
	
	// The node connected to the first terminal of this two-terminal inductor.
	SimCircuitNode terminalA;
	
	// The node connected to the second terminal of this two-terminal inductor.
	SimCircuitNode terminalB;
	
	public SimComponentInductor(String deviceName, SimCircuitNode terminalA, SimCircuitNode terminalB, float inductance)
	{
		super(deviceName);
		this.inductance = inductance;
		this.terminalA = terminalA;
		this.terminalB = terminalB;
	}
}
