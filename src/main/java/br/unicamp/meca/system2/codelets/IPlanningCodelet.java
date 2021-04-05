/**
 * 
 */
package br.unicamp.meca.system2.codelets;

import java.util.List;

import br.unicamp.cst.core.entities.Memory;

/**
 * @author andre
 *
 */
public interface IPlanningCodelet {
	
	/**
	 * Returns the id of this Planning Codelet.
	 * 
	 * @return the id
	 */
	String getId();
	
	/**
	 * Gets the list of output memories.
	 * 
	 * @return the outputs.
	 */
	List<Memory> getOutputs();
	
	/**
	 * Add a memory to the output list.
	 * 
	 * @param memory
	 *            one output to set.
	 */
	void addOutput(Memory memory);
	
	/**
	 * Gets the input memories list.
	 * 
	 * @return the inputs.
	 */
	List<Memory> getInputs();
	
	/**
	 * Add one memory to the input list.
	 * 
	 * @param memory
	 *            one input to set.
	 */
	void addInput(Memory memory);

}
