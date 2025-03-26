/*******************************************************************************
 * Copyright (c) 2018  DCA-FEEC-UNICAMP and Ericsson Research                  *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the GNU Lesser Public License v3      *
 * which accompanies this distribution, and is available at                    *
 * http://www.gnu.org/licenses/lgpl.html                                       *
 *                                                                             *
 * Contributors:                                                               *
 *     R. R. Gudwin, A. L. O. Paraense, E. Froes, W. Gibaut, S. de Paula,      * 
 *     E. Castro, V. Figueredo and K. Raizer                                   *
 *                                                                             *
 ******************************************************************************/
package br.unicamp.meca.system2.codelets;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.representation.idea.Idea;
import br.unicamp.meca.memory.WorkingMemory;

/**
 * This class represents MECA's Soar Codelet. This is a binding for CST's JSoar
 * Codelet.
 * 
 * @author A. L. O. Paraense
 * @author E. Froes
 * @author W. Gibaut
 * @see br.unicamp.cst.bindings.soar.JSoarCodelet
 */
public abstract class SoarCodelet extends br.unicamp.cst.bindings.soar.JSoarCodelet implements IPlanningCodelet {

	private String id;

	private String pathToCommands;

	private WorkingMemory workingMemory;

	private List<Object> rawPlan = null;

	private Memory workingMemoryOutputMO;

	private Memory workingMemoryInputMO;

	private String _agentName;

	private File _productionPath;

	private boolean startSOARDebugger;

	/**
	 * Creates the MECA Soar Codelet.
	 * 
	 * @param id
	 *            the id of theSoar Codelet. Must be unique per Soar Codelet.
	 */
	public SoarCodelet(String id) {

		this.setId(id);
		setName(id);

		rawPlan = new ArrayList<>();
		setPathToCommands("");
	}

	/**
	 * Creates the MECA Soar Codelet.
	 * 
	 * @param id
	 *            the id of theSoar Codelet. Must be unique per Soar Codelet.
	 * @param path_to_commands
	 *            the path to commands
	 * @param _agentName
	 *            the agent name
	 * @param _productionPath
	 *            the production path
	 * @param startSOARDebugger
	 *            if should start SOAR debugger
	 */
	public SoarCodelet(String id, String path_to_commands, String _agentName, File _productionPath,
			Boolean startSOARDebugger) {

		this.setId(id);
		setName(id);
		this._agentName = _agentName;
		this._productionPath = _productionPath;
		this.startSOARDebugger = startSOARDebugger;

		rawPlan = new ArrayList<>();
		setPathToCommands(path_to_commands);
		initSoarPlugin(_agentName, _productionPath, startSOARDebugger);
	}

	@Override
	public void accessMemoryObjects() {

		if (workingMemoryInputMO == null) {
			workingMemoryInputMO = this.getInput(WorkingMemory.WORKING_MEMORY_INPUT);
			workingMemory = (WorkingMemory) workingMemoryInputMO.getI();
		}

		if (workingMemoryOutputMO == null)
			workingMemoryOutputMO = this.getOutput(id);
	}

	@Override
	public void proc() {

		Idea il = processWorkingMemoryInput();
		if (il.getL().size() == 0)
			return;

		this.setInputLinkIdea(il);

		if (getDebugState() == 0)
			getJsoar().step();

		ArrayList<Object> commandList = getOutputInObject(getPathToCommands());

		if (commandList != null) {
			Collections.addAll(rawPlan, commandList);

			//workingMemory.getPlansMemory().setI(rawPlan);
                        workingMemory.getInternalMemory("PlansMemory").setI(rawPlan);

			workingMemoryOutputMO.setI(workingMemory);

			fromPlanToAction();

		} else {

			System.out.println("Error in SoarCodelet proc() ... commandList is null ");
		}

	}

	/**
	 * Goes from plan to action
	 */
	public abstract void fromPlanToAction();

	/**
	 * Processes the working memory input.
	 * 
	 * @return an abstract object represent an Input link.
	 */
	public Idea processWorkingMemoryInput() {

		Idea il = Idea.createIdea("InputLink","",0);

		//AbstractObject currentPerceptionWO = (AbstractObject) workingMemory.getCurrentPerceptionMemory().getI() != null
                Idea currentPerceptionWO = (Idea) workingMemory.getInternalMemory("CurrentPerceptionMemory").getI() != null
				? convertToIdea((Idea) workingMemory.getInternalMemory("CurrentPerceptionMemory").getI(),
						"CURRENT_PERCEPTION")
				: null;

		//AbstractObject imaginationsWO = (List<AbstractObject>) workingMemory.getImaginationsMemory().getI() != null
                Idea imaginationsWO = (List<Idea>) workingMemory.getInternalMemory("ImaginationsMemory").getI() != null
				? convertToIdea((List<Idea>) workingMemory.getInternalMemory("ImaginationsMemory").getI(),
						"IMAGINATION")
				: null;

		//AbstractObject goalsWO = (List<Goal>) workingMemory.getGoalsMemory().getI() != null
                Idea goalsWO = (List<Idea>) workingMemory.getInternalMemory("GoalsMemory").getI() != null
				? goalToIdea((List<Idea>) workingMemory.getInternalMemory("GoalsMemory").getI()) : null;

		//AbstractObject globalWO = (List<Memory>) workingMemory.getGlobalWorkspaceMemory().getI() != null
                Idea globalWO = (List<Memory>) workingMemory.getInternalMemory("GlobalWorkspaceMemory").getI() != null
				? globalWorkspaceToIdea((List<Memory>) workingMemory.getInternalMemory("GlobalWorkspaceMemory").getI())
				: null;

		//AbstractObject epRecallWO = (List<Memory>) workingMemory.getEpisodicRecallMemory().getI() != null
                Idea epRecallWO = (List<Memory>) workingMemory.getInternalMemory("EpisodicRecallMemory").getI() != null
				? epRecallToIdea((List<Memory>) workingMemory.getInternalMemory("EpisodicRecallMemory").getI()) : null;

		if (currentPerceptionWO != null)
			il.add(currentPerceptionWO);

		if (imaginationsWO != null)
			il.add(imaginationsWO);

		if (goalsWO != null)
			il.add(goalsWO);

		if (globalWO != null)
			il.add(globalWO);

		if (epRecallWO != null)
			il.add(epRecallWO);

		return il;
	}

	/**
	 * Converts to abstract object.
	 * 
	 * @param abstractObject
	 *            the input abstract object.
	 * @param nodeName
	 *            the node name.
	 * @return the abstract object.
	 */
	public Idea convertToIdea(Idea abstractObject, String nodeName) {

		Idea abs = Idea.createIdea(nodeName,"",0);

		abs.add(abstractObject);

		return abs;
	}

	/**
	 * Converts to abstract object.
	 * 
	 * @param ideas
	 *            the list of input abstract objects.
	 * @param nodeNameTemplate
	 *            the node name template.
	 * @return the abstract object.
	 */
	public Idea convertToIdea(List<Idea> ideas, String nodeNameTemplate) {

		Idea configs = Idea.createIdea(ideas.toString(),"",0);

		for (Idea abs : ideas) {
			configs.add(convertToIdea(abs, nodeNameTemplate));
		}
		return configs;
	}

	/**
	 * Creates the Abstract Object representing the Goals.
	 * 
	 * @param goals
	 *            the list of goals.
	 * @return the Abstract Object representing the Goals.
	 */
	public Idea goalToIdea(List<Idea> goals) {

		Idea go = Idea.createIdea("Goals","",0);

		for (Idea goal : goals) {

			//Idea temp = convertToIdea(goal.getGoalIdeas(), "GOAL");
			//temp.add(new Idea(goal.getId()));
			//go.add(temp);
                        go.add(goal);
		}
		return go;
	}

	/**
	 * Creates an Abstract Object representing the global workspace.
	 * 
	 * @param global
	 *            the list of memories
	 * @return the abstract object representing global workspace
	 */
	public Idea globalWorkspaceToIdea(List<Memory> global) {

		List<Idea> globalIdeass = null;

		List<String> globalStrings = null;

		for (Memory mem : global) {

			if (isIdea(mem.getI())) {

				globalIdeass.add((Idea) mem.getI());
			}
		}

		for (Memory mem : global) {

			if (isString(mem.getI())) {

				globalStrings.add((String) mem.getI());
			}
		}

		Idea gAbs = convertToIdea(globalIdeass, "GLOBAL_WORKSPACE");

		for (String st : globalStrings) {

			gAbs.add(Idea.createIdea(st,"",0));
		}

		return gAbs;
	}

	/**
	 * Creates an Abstract Object representing an episodic recall.
	 * 
	 * @param episodicRecall
	 *            the list of memories representing the episodic recall.
	 * @return the Abstract Object representing an episodic recall.
	 */
	public Idea epRecallToIdea(List<Memory> episodicRecall) {

		List<Idea> epConfigurations = null;

		for (Memory mem : episodicRecall) {

			if (isIdea(mem.getI())) {

				epConfigurations.add((Idea) mem.getI());
			}
		}

		Idea gConf = convertToIdea(epConfigurations, "EPISODIC_RECALL_MEMORY");

		return gConf;
	}

	/**
	 * Tells if the object is an abstract object.
	 * 
	 * @param obj
	 *            the object to be tested
	 * @return true if the obj is an abstract object.
	 */
	public boolean isIdea(Object obj) {

		if (obj.getClass() == Idea.class)
			return true;
		else
			return false;
	}

	/**
	 * Tells if the object is a Java String.
	 * 
	 * @param obj
	 *            the object to be tested
	 * @return true if the obj is a Java String.
	 */
	public boolean isString(Object obj) {

		if (obj.getClass() == String.class)
			return true;
		else
			return false;
	}

	/**
	 * Gets the Soar Codelet id.
	 * 
	 * @return the Soar Codelet id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the Soar Codelet id.
	 * 
	 * @param id
	 *            the Soar codelet id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the Working memory
	 * 
	 * @return the working memoryy.
	 */
	public WorkingMemory getWorkingMemory() {
		return this.workingMemory;
	}

	/**
	 * Gets the path to commands.
	 * 
	 * @return the path to commands.
	 */
	public String getPathToCommands() {
		return this.pathToCommands;
	}

	/**
	 * Sets the path to commands.
	 * 
	 * @param path
	 *            the path to commands to set.
	 */
	public void setPathToCommands(String path) {
		this.pathToCommands = path;
	}
}
