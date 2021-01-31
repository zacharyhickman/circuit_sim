package zacharyhickman.circuittest.common.sim.base;

/**
 * An object that represents a voltage node in a circuit.
 * @author zhick
 *
 */
public class SimCircuitNode
{
	// Constructor for the node object.
	public SimCircuitNode(String nodeName)
	{
		this.nodeName = nodeName;
	}
	
	// Ground node constructor for the node object.
	public SimCircuitNode(String nodeName, boolean isReference)
	{
		this.nodeName = nodeName;
		this.isReference = isReference;
	}
	
	/**
	 * Get the string id of this node.
	 * @return
	 */
	public String getNodeName()
	{
		return this.nodeName;
	}
	
	/**
	 * Get the integer id of this node.
	 * @return
	 */
	public int getNodeId()
	{
		return this.nodeId;
	}
	
	/**
	 * Returns the voltage of this node.
	 * @return
	 */
	public float getNodeVoltage()
	{
		return this.voltage;
	}
	
	/**
	 * Returns the last voltage of this node.
	 * @return
	 */
	public float getLastNodeVoltage()
	{
		return this.lastVoltage;
	}
	
	/**
	 * Returns true if this node is considered the reference node of the circuit.
	 * @return
	 */
	public boolean isReferenceNode()
	{
		return this.isReference;
	}
	
	/**
	 * Set the voltage of this node. Typically used by voltage sources.
	 */
	public void setNodeVoltage(float voltage)
	{
		// Save the previous voltage of this node.
		this.lastVoltage = this.voltage;
		this.voltage = voltage;
	}
	
	/**
	 * Set the last voltage of this node. Typically used by initial start condition setting.
	 */
	public void setLastNodeVoltage(float lastVoltage)
	{
		this.lastVoltage = lastVoltage;
	}
	
	/**
	 * Set the integer node ID of this node.
	 * @param nodeId
	 */
	public void setNodeId(int nodeId)
	{
		this.nodeId = nodeId;
	}
	
	// The string id of this node.
	String nodeName = "";
	
	// The integer id of this node.
	int nodeId = -1;
	
	// The voltage of the node.
	float voltage = 0.0f;
	
	// The last voltage of this node.
	float lastVoltage = this.voltage;
	
	// Whether or not this node is a reference node.
	boolean isReference = false;
}
