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
package br.unicamp.meca.mind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.meca.memory.WorkingMemory;
import br.unicamp.meca.system1.codelets.S1To2AttentionCodelet;
import br.unicamp.meca.system1.codelets.MotivationalBehavioralCodelet;
import br.unicamp.meca.system1.codelets.MotivationalCodelet;
import br.unicamp.meca.system1.codelets.MotorCodelet;
import br.unicamp.meca.system1.codelets.PerceptualCodelet;
import br.unicamp.meca.system1.codelets.RandomBehavioralCodelet;
import br.unicamp.meca.system1.codelets.ReactiveBehavioralCodelet;
import br.unicamp.meca.system1.codelets.SensoryCodelet;
import br.unicamp.meca.system2.codelets.AppraisalCodelet;
import br.unicamp.meca.system2.codelets.AttentionCodelet;
import br.unicamp.meca.system2.codelets.ConsciousnessCodelet;
import br.unicamp.meca.system2.codelets.EpisodicAttentionCodelet;
import br.unicamp.meca.system2.codelets.EpisodicLearningCodelet;
import br.unicamp.meca.system2.codelets.EpisodicRetrievalCodelet;
import br.unicamp.meca.system2.codelets.ExpectationCodelet;
import br.unicamp.meca.system2.codelets.GoalCodelet;
import br.unicamp.meca.system2.codelets.LearningCodelet;
import br.unicamp.meca.system2.codelets.PerceptualBufferAttentionCodelet;
import br.unicamp.meca.system2.codelets.PlannerCodelet;
import br.unicamp.meca.system2.codelets.SelectionCodelet;

/**
 * This class represents th MECA's agent mind.This is the main class to be used
 * by any MECA user.
 * 
 * @author A. L. O. Paraense
 * @author E. Froes
 * @see Mind
 * 
 * Author of the new fields, methods and modifications of this version:
 * 
 * @author W. Gibaut
 * 
 */
public class MecaMind extends Mind {

	/*
	 * System 1
	 */

	private List<SensoryCodelet> sensoryCodelets;

	private List<PerceptualCodelet> perceptualCodelets;

	private List<MotivationalCodelet> motivationalCodelets;

	private S1To2AttentionCodelet attentionCodeletSystem1;

	private List<RandomBehavioralCodelet> randomBehavioralCodelets;

	private List<ReactiveBehavioralCodelet> reactiveBehavioralCodelets;

	private List<MotivationalBehavioralCodelet> motivationalBehavioralCodelets;

	private List<MotorCodelet> motorCodelets;

	/*
	 * System 2
	 */

	private List<br.unicamp.meca.system2.codelets.AttentionCodelet> attentionCodeletsSystem2;

	private EpisodicLearningCodelet episodicLearningCodelet;

	private EpisodicRetrievalCodelet episodicRetrievalCodelet;
        
        private PerceptualBufferAttentionCodelet perceptualBufferAttentionCodelet;
        
        private EpisodicAttentionCodelet episodicAttentionCodelet;

	private ExpectationCodelet expectationCodelet;
        
        private SelectionCodelet selectionCodelet;
        
        private LearningCodelet learningCodelet;

	private ConsciousnessCodelet consciousnessCodelet;

	private PlannerCodelet plannerCodelet;

	private GoalCodelet goalCodelet;

	private AppraisalCodelet appraisalCodelet;

	private WorkingMemory workingMemory;

	private String id;

	/**
	 * Creates the MECA Mind.
	 */
	public MecaMind() {
		setId(UUID.randomUUID().toString());
		setWorkingMemory(new WorkingMemory(getId()/*, attentionCodeletSystem1.getId(), episodicRetrievalCodelet.getId()*/));
	}

	/**
	 * Creates the MECA Mind.
	 * 
	 * @param id
	 *            the id of the MECA mind. Must be unique per MECA mind.
	 */
	public MecaMind(String id) {
		setId(id);
		setWorkingMemory(new WorkingMemory(getId()/*, attentionCodeletSystem1.getId(), episodicRetrievalCodelet.getId()*/));
	}

	/**
	 * Mounts the MECA Mind. After creating the MECA Mind's instance and setting
	 * all the codelets inside it, this method is responsible for binding
	 * together all codelets inside the mind, creating memories (objects and
	 * containers) and setting them either as inputs or outputs of each codelet,
	 * according to MECA's reference architecture, as shown in the picture in
	 * https://github.com/CST-Group/meca/blob/master/README.md.
	 * <p>
	 * This method must be called before running the MECA Agent.
	 */
	public void mountMecaMind() {

		mountSensoryCodelets();

		mountPerceptualCodelets();

		mountMotorCodelets();

		mountAttentionCodelets();

		mountWorkingMemory();

		mountPlannerCodelet();

		mountRandomBehavioralCodelets();

		mountReactiveBehavioralCodelets();

		mountMotivationalCodelets();

		mountMotivationalBehavioralCodelets();

                mountEpisodicCodelets();
                
		mountExpectationCodelet();
                
                mountSelectionCodelet();
                
                mountLearningCodelet();
                
                mountModules();
                
	}

	private void mountModules() {

		if (getMotivationalCodelets() != null) {
			if (getMotivationalCodelets().size() > 0) {
				List<? extends br.unicamp.cst.motivational.MotivationalCodelet> mtcodelets = getMotivationalCodelets();
				getMotivationalSubsystemModule()
						.setMotivationalCodelets((List<br.unicamp.cst.motivational.MotivationalCodelet>) mtcodelets);
			}

		}

		if (getPlannerCodelet() != null) {
			//getPlansSubsystemModule().setjSoarCodelet(getSoarCodelet());
		}

	}

	private void mountPerceptualCodelets() {
		if (perceptualCodelets != null) {
			for (PerceptualCodelet perceptualCodelet : perceptualCodelets) {
				if (perceptualCodelet != null && perceptualCodelet.getId() != null) {

					insertCodelet(perceptualCodelet);
					/*
					 * Inputs
					 */
					if (sensoryCodelets != null) {
						for (SensoryCodelet sensoryCodelet : sensoryCodelets) {
							if (sensoryCodelet != null && sensoryCodelet.getId() != null) {
								ArrayList<String> sensoryCodeletsIds = perceptualCodelet.getSensoryCodeletsIds();
								if (sensoryCodeletsIds != null) {
									for (String sensoryCodeletId : sensoryCodeletsIds) {
										if (sensoryCodeletId != null
												&& sensoryCodeletId.equalsIgnoreCase(sensoryCodelet.getId())) {
											perceptualCodelet.addInputs(sensoryCodelet.getOutputs());
										}
									}
								}
							}
						}
					}
					/*
					 * Output
					 */
					MemoryObject perceptualMemory = createMemoryObject(perceptualCodelet.getId());
					perceptualCodelet.addOutput(perceptualMemory);

				}
			}
		}
	}

	private void mountSensoryCodelets() {
		if (sensoryCodelets != null) {

			for (SensoryCodelet sensoryCodelet : sensoryCodelets) {
				if (sensoryCodelet != null && sensoryCodelet.getId() != null) {

					insertCodelet(sensoryCodelet);
					/*
					 * Input
					 */
					MemoryObject sensoryMemory = createMemoryObject(sensoryCodelet.getId());
					sensoryCodelet.addOutput(sensoryMemory);

				}
			}
		}
	}

	private void mountMotivationalCodelets() {
		if (getMotivationalCodelets() != null) {
			for (MotivationalCodelet motivationalCodelet : getMotivationalCodelets()) {

				/*
				 * Input Sensors
				 */
				if (motivationalCodelet.getSensoryCodeletsIds() != null) {
					List<String> sensoryIds = motivationalCodelet.getSensoryCodeletsIds();
					for (String sensoryId : sensoryIds) {
						if (sensoryCodelets != null) {
							for (SensoryCodelet sensoryCodelet : sensoryCodelets) {
								if (sensoryCodelet.getId().equals(sensoryId)) {
									motivationalCodelet.addInputs(sensoryCodelet.getOutputs());

								}
							}
						}
					}
				}

				/*
				 * Input Drives
				 */

				if (motivationalCodelet.getMotivationalCodeletsIds() != null) {
					HashMap<String, Double> motivationalCodeletsIds = motivationalCodelet.getMotivationalCodeletsIds();
					for (Map.Entry<String, Double> motivationalCodeletId : motivationalCodeletsIds.entrySet()) {

						for (MotivationalCodelet motivationalCodeletInput : getMotivationalCodelets()) {
							if (motivationalCodeletInput.getId().equals(motivationalCodeletId.getKey())) {

								HashMap<Memory, Double> driveRelevance = new HashMap<>();
								driveRelevance.put(motivationalCodeletInput.getOutputDriveMO(),
										motivationalCodeletId.getValue());

								motivationalCodelet.addInput(this.createMemoryObject(
										motivationalCodeletInput.getOutputDriveMO().getName(), driveRelevance));
							}
						}
					}
				}

				/*
				 * Output Drives
				 */
				MemoryObject outputDrive = this.createMemoryObject(motivationalCodelet.getId() + "_DRIVE_MO");
				motivationalCodelet.addOutput(outputDrive);

				insertCodelet(motivationalCodelet);
			}
		}
	}

	private void mountMotivationalBehavioralCodelets() {
		if (getMotivationalBehavioralCodelets() != null) {
			for (MotivationalBehavioralCodelet motivationalBehavioralCodelet : getMotivationalBehavioralCodelets()) {
				if (motivationalBehavioralCodelet != null && motivationalBehavioralCodelet.getId() != null
						&& motivationalBehavioralCodelet.getMotivationalCodeletsIds() != null
						&& motivationalBehavioralCodelet.getMotorCodeletId() != null
						&& motivationalBehavioralCodelet.getPlannerCodeletId() != null) {

					/*
					 * Outputs
					 */
					if (motorCodelets != null) {
						for (MotorCodelet motorCodelet : motorCodelets) {
							if (motorCodelet != null && motorCodelet.getId() != null) {
								if (motorCodelet.getId()
										.equalsIgnoreCase(motivationalBehavioralCodelet.getMotorCodeletId())) {
									motivationalBehavioralCodelet.addOutputs(motorCodelet.getInputs());
								}
							}
						}
					}
					/*
					 * Inputs
					 */
					if (getMotivationalCodelets() != null) {
						for (MotivationalCodelet motivationalCodelet : getMotivationalCodelets()) {
							if (motivationalCodelet != null && motivationalCodelet.getId() != null) {
								ArrayList<String> motivationalCodeletsIds = motivationalBehavioralCodelet
										.getMotivationalCodeletsIds();
								if (motivationalCodeletsIds != null) {
									for (String motivationalCodeletId : motivationalCodeletsIds) {
										if (motivationalCodeletId != null && motivationalCodelet.getId()
												.equalsIgnoreCase(motivationalCodeletId)) {
											motivationalBehavioralCodelet.addInputs(motivationalCodelet.getOutputs());
										}
									}
								}
							}
						}
					}

					if (plannerCodelet != null && plannerCodelet.getId() != null) {
						if (plannerCodelet.getId().equalsIgnoreCase(motivationalBehavioralCodelet.getPlannerCodeletId())) {
							motivationalBehavioralCodelet.addBroadcasts(plannerCodelet.getOutputs());
						}

					}

					insertCodelet(motivationalBehavioralCodelet);

				}
			}
		}
	}

	private void mountReactiveBehavioralCodelets() {
		if (getReactiveBehavioralCodelets() != null) {
			for (ReactiveBehavioralCodelet reactiveBehavioralCodelet : getReactiveBehavioralCodelets()) {
				if (reactiveBehavioralCodelet != null && reactiveBehavioralCodelet.getId() != null
						&& reactiveBehavioralCodelet.getPerceptualCodeletsIds() != null
						&& reactiveBehavioralCodelet.getMotorCodeletIds() != null
						&& reactiveBehavioralCodelet.getPlannerCodeletId() != null) {

					insertCodelet(reactiveBehavioralCodelet);
					/*
					 * Outputs
					 */
					if (motorCodelets != null) {
						for (MotorCodelet motorCodelet : motorCodelets) {
							if (motorCodelet != null && motorCodelet.getId() != null) {
                                                            for(int i=0; i < reactiveBehavioralCodelet.getMotorCodeletIds().size(); i++){
                                                                if (motorCodelet.getId()
										.equalsIgnoreCase(reactiveBehavioralCodelet.getMotorCodeletIds().get(i))) {
									reactiveBehavioralCodelet.addOutputs(motorCodelet.getInputs());
								}
                                                        }
							}
						}
					}
					/*
					 * Inputs
					 */
					if (perceptualCodelets != null) {
						for (PerceptualCodelet perceptualCodelet : perceptualCodelets) {
							if (perceptualCodelet != null && perceptualCodelet.getId() != null) {
								ArrayList<String> perceptualCodeletsIds = reactiveBehavioralCodelet
										.getPerceptualCodeletsIds();
								if (perceptualCodeletsIds != null) {
									for (String perceptualCodeletId : perceptualCodeletsIds) {
										if (perceptualCodeletId != null
												&& perceptualCodelet.getId().equalsIgnoreCase(perceptualCodeletId)) {
											reactiveBehavioralCodelet.addInputs(perceptualCodelet.getOutputs());
										}
									}
								}
							}
						}
					}

					if (plannerCodelet != null && plannerCodelet.getId() != null) {
						if (plannerCodelet.getId().equalsIgnoreCase(reactiveBehavioralCodelet.getPlannerCodeletId())) {
							reactiveBehavioralCodelet.addBroadcasts(plannerCodelet.getOutputs());
						}

					}

				}
			}
		}
	}

	private void mountRandomBehavioralCodelets() {
		if (getRandomBehavioralCodelets() != null) {
			for (RandomBehavioralCodelet randomBehavioralCodelet : getRandomBehavioralCodelets()) {
				if (randomBehavioralCodelet != null && randomBehavioralCodelet.getId() != null
						&& randomBehavioralCodelet.getMotorCodeletId() != null
						&& randomBehavioralCodelet.getPlannerCodeletId() != null) {
					insertCodelet(randomBehavioralCodelet);
					/*
					 * Outputs
					 */
					if (motorCodelets != null) {
						for (MotorCodelet motorCodelet : motorCodelets) {
							if (motorCodelet != null && motorCodelet.getId() != null) {
								if ((motorCodelet.getId())
										.equalsIgnoreCase(randomBehavioralCodelet.getMotorCodeletId())) {
									randomBehavioralCodelet.addOutputs(motorCodelet.getInputs());
								}
							}
						}
					}
					/*
					 * Inputs
					 */
					if (plannerCodelet != null && plannerCodelet.getId() != null) {
						if (plannerCodelet.getId().equalsIgnoreCase(randomBehavioralCodelet.getPlannerCodeletId())) {
							randomBehavioralCodelet.addBroadcasts(plannerCodelet.getOutputs());
						}

					}
				}
			}
		}
	}

	private void mountMotorCodelets() {
		if (motorCodelets != null) {
			for (MotorCodelet motorCodelet : motorCodelets) {
				if (motorCodelet != null && motorCodelet.getId() != null) {
					insertCodelet(motorCodelet);
					/*
					 * Input
					 */
					Memory motorMemoryContainer = createMemoryContainer(motorCodelet.getId());
					motorCodelet.addInput(motorMemoryContainer);
				}
			}
		}
	}

	private void mountAttentionCodelets() {
		if (attentionCodeletSystem1 != null) {

			if (perceptualCodelets != null) {
				for (String inputPerceptualId : attentionCodeletSystem1.getPerceptualCodeletsIds()) {
					for (PerceptualCodelet perceptualCodelet : perceptualCodelets) {

						if (inputPerceptualId.equals(perceptualCodelet.getId())) {
							attentionCodeletSystem1.addInputs(perceptualCodelet.getOutputs());
						}
					}
				}
			}
			Memory attentionMemoryOutput = createMemoryObject(attentionCodeletSystem1.getId());
			attentionCodeletSystem1.addOutput(attentionMemoryOutput);
			attentionCodeletSystem1.setOutputFilteredPerceptsMO(attentionMemoryOutput);
			insertCodelet(attentionCodeletSystem1);
		}
                if(!attentionCodeletsSystem2.isEmpty()){
                    for(AttentionCodelet attention : attentionCodeletsSystem2){
                        //String[] className = attention.getClass().getName().split("\\.");
                        
                        //olhar isso aqui
                        if(attention instanceof  PerceptualBufferAttentionCodelet){
                        //if(className[4].equals("PerceptualBufferAttentionCodelet")){
                            attention.addOutput(createMemoryObject(attention.getId())); //cria um output com o nome do perceptual
                            attention.addInput(attentionCodeletSystem1.getOutputFilteredPerceptsMO());
                            setPerceptualBufferAttentionCodelet((PerceptualBufferAttentionCodelet)attention);
                            insertCodelet(attention);
                        }
                        else if (attention instanceof EpisodicAttentionCodelet){
                            attention.addOutput(createMemoryObject(attention.getId())); //cria um output com o nome do perceptual
                            attention.addInputs(getPerceptualBufferAttentionCodelet().getOutputs());
                            setEpisodicAttentionCodelet((EpisodicAttentionCodelet)attention);
                            insertCodelet(attention);
                        }
                    }
                }
	}
        
        private void mountEpisodicCodelets(){
            if(episodicLearningCodelet != null){
                episodicLearningCodelet.addInputs(episodicAttentionCodelet.getOutputs());
                episodicLearningCodelet.addOutput(createMemoryObject(episodicLearningCodelet.getId()));
                insertCodelet(episodicLearningCodelet);
            }
            
            if(episodicRetrievalCodelet != null){
                episodicRetrievalCodelet.addInputs(episodicLearningCodelet.getOutputs());
                //episodicRetrievalCodelet.addInput(getWorkingMemory().getCueMemory());
                episodicRetrievalCodelet.addInputs(attentionCodeletSystem1.getOutputs());
                episodicRetrievalCodelet.addOutput(createMemoryObject(episodicRetrievalCodelet.getId()));
                insertCodelet(episodicRetrievalCodelet);
            }
        }

	private void mountPlannerCodelet() {
		if (plannerCodelet != null) {
			plannerCodelet.addInput(createMemoryObject(WorkingMemory.WORKING_MEMORY_INPUT, getWorkingMemory()));
			plannerCodelet.addOutput(createMemoryObject(plannerCodelet.getId()));
			insertCodelet(plannerCodelet);
		}

	}

        private void mountExpectationCodelet(){
                if(expectationCodelet != null){
                        expectationCodelet.addInput(getWorkingMemory().getCurrentPerceptionMemory());
                        expectationCodelet.addInput(createMemoryObject(learningCodelet.getId()));
                        expectationCodelet.addInputs(episodicRetrievalCodelet.getOutputs());
                        expectationCodelet.addOutput(createMemoryObject(selectionCodelet.getId()));
                        expectationCodelet.addOutput(createMemoryObject(expectationCodelet.getId()));
                        insertCodelet(expectationCodelet);
                }
        }
        
        //private void mountExpectationCodeletWithPlanner(){}
        
        private void mountSelectionCodelet(){
                if(selectionCodelet != null){
                        selectionCodelet.addInputs(expectationCodelet.getOutputs());
                        selectionCodelet.addOutput(getWorkingMemory().getNextActionMemory());
                        insertCodelet(selectionCodelet);
                }
        }
        
        private void mountLearningCodelet(){
                if(learningCodelet != null){
                    //weights output
                        learningCodelet.addInput(getWorkingMemory().getCurrentPerceptionMemory());
                        learningCodelet.addInput(createMemoryObject(expectationCodelet.getId()));
                        learningCodelet.addOutput(createMemoryObject(learningCodelet.getId()));
                        insertCodelet(learningCodelet);
                }
        }
        
        
	private void mountWorkingMemory() {
		if (getWorkingMemory() != null) {

			if (attentionCodeletSystem1 != null) {
				getWorkingMemory().setCurrentPerceptionMemory(attentionCodeletSystem1.getOutputFilteredPerceptsMO());
                                getWorkingMemory().getCueMemory().setType(attentionCodeletSystem1.getId());
			}
                        if(episodicRetrievalCodelet != null){
                                getWorkingMemory().getEpisodicRecallMemory().setType(episodicRetrievalCodelet.getId());
                        }

		}
	}

	/**
	 * Sets the Sensory Codelets.
	 * 
	 * @param sensoryCodelets
	 *            the sensoryCodelets to set
	 */
	public void setSensoryCodelets(List<SensoryCodelet> sensoryCodelets) {
		this.sensoryCodelets = sensoryCodelets;
	}

	/**
	 * Sets the Perceptual Codelets.
	 * 
	 * @param perceptualCodelets
	 *            the perceptualCodelets to set
	 */
	public void setPerceptualCodelets(List<PerceptualCodelet> perceptualCodelets) {
		this.perceptualCodelets = perceptualCodelets;
	}

	/**
	 * Sets the Motivational Codelets.
	 * 
	 * @param motivationalCodelets
	 *            the motivationalCodelets to set
	 */
	public void setMotivationalCodelets(List<MotivationalCodelet> motivationalCodelets) {
		this.motivationalCodelets = motivationalCodelets;
	}

	/**
	 * Sets the System 1 Attention Codelet.
	 * 
	 * @param attentionCodeletSystem1
	 *            the attentionCodeletSystem1 to set
	 */
	public void setAttentionCodeletSystem1(S1To2AttentionCodelet attentionCodeletSystem1) {
		this.attentionCodeletSystem1 = attentionCodeletSystem1;
	}

	/**
	 * Sets the Random Behavioral Codelets.
	 * 
	 * @param randomBehavioralCodelets
	 *            the randomBehavioralCodelets to set
	 */
	public void setRandomBehavioralCodelets(List<RandomBehavioralCodelet> randomBehavioralCodelets) {
		this.randomBehavioralCodelets = randomBehavioralCodelets;
	}

	/**
	 * Sets the Reactive Behavioral Codelets.
	 * 
	 * @param reactiveBehavioralCodelets
	 *            the reactiveBehavioralCodelets to set
	 */
	public void setReactiveBehavioralCodelets(List<ReactiveBehavioralCodelet> reactiveBehavioralCodelets) {
		this.reactiveBehavioralCodelets = reactiveBehavioralCodelets;
	}

	/**
	 * Sets the Motovational Behavioral Codelets.
	 * 
	 * @param motivationalBehavioralCodelets
	 *            the motivationalBehavioralCodelets to set
	 */
	public void setMotivationalBehavioralCodelets(List<MotivationalBehavioralCodelet> motivationalBehavioralCodelets) {
		this.motivationalBehavioralCodelets = motivationalBehavioralCodelets;
	}

	/**
	 * Sets the Motor Codelets.
	 * 
	 * @param motorCodelets
	 *            the motorCodelets to set
	 */
	public void setMotorCodelets(List<MotorCodelet> motorCodelets) {
		this.motorCodelets = motorCodelets;
	}

	/**
	 * Sets the System 2 Attention Codelets
	 * 
	 * @param attentionCodeletsSystem2
	 *            the attentionCodeletsSystem2 to set
	 */
	public void setAttentionCodeletsSystem2(
			List<br.unicamp.meca.system2.codelets.AttentionCodelet> attentionCodeletsSystem2) {
		this.attentionCodeletsSystem2 = attentionCodeletsSystem2;
	}

	/**
	 * Sets the Episodic Learning Codelet.
	 * 
	 * @param episodicLearningCodelet
	 *            the episodicLearningCodelet to set
	 */
	public void setEpisodicLearningCodelet(EpisodicLearningCodelet episodicLearningCodelet) {
		this.episodicLearningCodelet = episodicLearningCodelet;
	}

	/**
	 * Sets the Episodic Retrieval Codelet.
	 * 
	 * @param episodicRetrievalCodelet
	 *            the episodicRetrievalCodelet to set
	 */
	public void setEpisodicRetrievalCodelet(EpisodicRetrievalCodelet episodicRetrievalCodelet) {
		this.episodicRetrievalCodelet = episodicRetrievalCodelet;
	}

	/**
	 * Sets the Expectation Codelet.
	 * 
	 * @param expectationCodelet
	 *            the expectationCodelet to set
	 */
	public void setExpectationCodelet(ExpectationCodelet expectationCodelet) {
		this.expectationCodelet = expectationCodelet;
	}
        
        /**
	 * Sets the Selection Codelet.
	 * 
	 * @param selectionCodelet
	 *            the selectionCodelet to set
	 */
        public void setSelectionCodelet(SelectionCodelet selectionCodelet){
            this.selectionCodelet = selectionCodelet;
        }
        
        /**
	 * Sets the Learning Codelet.
	 * 
	 * @param learningCodelet
	 *            the learningCodelet to set
	 */
        public void setLearningCodelet(LearningCodelet learningCodelet){
            this.learningCodelet = learningCodelet;
        }

	/**
	 * Sets the Consciousness Codelet.
	 * 
	 * @param consciousnessCodelet
	 *            the consciousnessCodelet to set
	 */
	public void setConsciousnessCodelet(ConsciousnessCodelet consciousnessCodelet) {
		this.consciousnessCodelet = consciousnessCodelet;
	}

	/**
	 * Sets the Planner Codelet.
	 * 
	 * @param plannerCodelet
	 *            the plannerCodelet to set
	 */
	public void setPlannerCodelet(PlannerCodelet plannerCodelet) {
		this.plannerCodelet = plannerCodelet;
	}

	/**
	 * Sets the Goal Codelet.
	 * 
	 * @param goalCodelet
	 *            the goalCodelet to set
	 */
	public void setGoalCodelet(GoalCodelet goalCodelet) {
		this.goalCodelet = goalCodelet;
	}

	/**
	 * Sets the Appraisal Codelet.
	 * 
	 * @param appraisalCodelet
	 *            the appraisalCodelet to set
	 */
	public void setAppraisalCodelet(AppraisalCodelet appraisalCodelet) {
		this.appraisalCodelet = appraisalCodelet;
	}
        
	/**
	 * Gets the MECA Mind id
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the MECA Mind id
	 * 
	 * @param id
	 *            the id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the Motivational Codelets.
	 * 
	 * @return the Motivational Codelets.
	 */
	public List<MotivationalCodelet> getMotivationalCodelets() {
		return motivationalCodelets;
	}

	/**
	 * Gets the Goal Codelets.
	 * 
	 * @return the Goal Codelet.
	 */
	public GoalCodelet getGoalCodelet() {
		return goalCodelet;
	}

	/**
	 * Gets the Appraisal Codelet.
	 * 
	 * @return the Appraisal Codelet.
	 */
	public AppraisalCodelet getAppraisalCodelet() {
		return appraisalCodelet;
	}

	/**
	 * Gets the Working Memory.
	 * 
	 * @return the Working Memory.
	 */
	public WorkingMemory getWorkingMemory() {
		return workingMemory;
	}

	/**
	 * Sets the Working Memory.
	 * 
	 * @param workingMemory
	 *            the working memory to set.
	 */
	public void setWorkingMemory(WorkingMemory workingMemory) {
		this.workingMemory = workingMemory;
	}

	/**
	 * Gets the Sensory Codelets.
	 * 
	 * @return the sensoryCodelets.
	 */
	public List<SensoryCodelet> getSensoryCodelets() {
		return sensoryCodelets;
	}

	/**
	 * Gets the Perceptual Codelets.
	 * 
	 * @return the perceptualCodelets.
	 */
	public List<PerceptualCodelet> getPerceptualCodelets() {
		return perceptualCodelets;
	}

	/**
	 * Gets the Attention Codelet from System 1.
	 * 
	 * @return the attentionCodeletSystem1.
	 */
	public S1To2AttentionCodelet getAttentionCodeletSystem1() {
		return attentionCodeletSystem1;
	}

	/**
	 * Gets the Random Behavioral Codelets.
	 * 
	 * @return the randomBehavioralCodelets.
	 */
	public List<RandomBehavioralCodelet> getRandomBehavioralCodelets() {
		return randomBehavioralCodelets;
	}

	/**
	 * Gets the Reactive Behavioral Codelets.
	 * 
	 * @return the reactiveBehavioralCodelets.
	 */
	public List<ReactiveBehavioralCodelet> getReactiveBehavioralCodelets() {
		return reactiveBehavioralCodelets;
	}

	/**
	 * Gets the Motivational Behavioral Codelets.
	 * 
	 * @return the motivationalBehavioralCodelets.
	 */
	public List<MotivationalBehavioralCodelet> getMotivationalBehavioralCodelets() {
		return motivationalBehavioralCodelets;
	}

	/**
	 * Gets the Motor Codelets.
	 * 
	 * @return the motorCodelets.
	 */
	public List<MotorCodelet> getMotorCodelets() {
		return motorCodelets;
	}

	/**
	 * Gets the Attention Codelets from System 2.
	 * 
	 * @return the attentionCodeletsSystem2.
	 */
	public List<br.unicamp.meca.system2.codelets.AttentionCodelet> getAttentionCodeletsSystem2() {
		return attentionCodeletsSystem2;
	}

	/**
	 * Gets the Episodic Learning Codelet.
	 * 
	 * @return the episodicLearningCodelet.
	 */
	public EpisodicLearningCodelet getEpisodicLearningCodelet() {
		return episodicLearningCodelet;
	}

	/**
	 * Gets the Episodic Retrieval Codelet.
	 * 
	 * @return the episodicRetrievalCodelet.
	 */
	public EpisodicRetrievalCodelet getEpisodicRetrievalCodelet() {
		return episodicRetrievalCodelet;
	}

	/**
	 * Gets the Expectation Codelet.
	 * 
	 * @return the expectationCodelet.
	 */
	public ExpectationCodelet getExpectationCodelet() {
		return expectationCodelet;
	}

	/**
	 * Gets the Consciousness Codelet.
	 * 
	 * @return the consciousnessCodelet.
	 */
	public ConsciousnessCodelet getConsciousnessCodelet() {
		return consciousnessCodelet;
	}

	/**
	 * Gets the Planner Codelet.
	 * 
	 * @return the plannerCodelet.
	 */
	public PlannerCodelet getPlannerCodelet() {
		return plannerCodelet;
	}
        
        public PerceptualBufferAttentionCodelet getPerceptualBufferAttentionCodelet(){
            return perceptualBufferAttentionCodelet;
        }
        
        public void setPerceptualBufferAttentionCodelet(PerceptualBufferAttentionCodelet perceptualBufferAttentionCodelet){
            this.perceptualBufferAttentionCodelet = perceptualBufferAttentionCodelet;
        }
        
        public EpisodicAttentionCodelet getEpisodicAttentionCodelet(){
            return this.episodicAttentionCodelet;
        }
        
        public void setEpisodicAttentionCodelet(EpisodicAttentionCodelet attention){
            this.episodicAttentionCodelet = attention;
        }
}
