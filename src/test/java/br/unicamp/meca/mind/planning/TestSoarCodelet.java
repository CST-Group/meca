/**
 * 
 */
package br.unicamp.meca.mind.planning;

import java.io.File;

import br.unicamp.meca.system2.codelets.SoarCodelet;

/**
 * @author andre
 *
 */
public class TestSoarCodelet extends SoarCodelet {

	/**
	 * @param id
	 */
	public TestSoarCodelet(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param path_to_commands
	 * @param _agentName
	 * @param _productionPath
	 * @param startSOARDebugger
	 */
	public TestSoarCodelet(String id, String path_to_commands, String _agentName, File _productionPath,
			Boolean startSOARDebugger) {
		super(id, path_to_commands, _agentName, _productionPath, startSOARDebugger);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void fromPlanToAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void calculateActivation() {
		// TODO Auto-generated method stub

	}

}
