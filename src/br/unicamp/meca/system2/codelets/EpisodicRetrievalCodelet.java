/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.meca.system2.codelets;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import br.unicamp.meca.util.AbstractObjectPair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wander
 */
public abstract class EpisodicRetrievalCodelet extends Codelet{
    
    protected String id;
    
    protected String episodicLearningCodeletId;
    protected String s1To2AttentionCodeletId;
    
    private Memory cueMemoryMO;
    private Memory episodicMemory; 
    private Memory episodicRecallMemory;
    
    
    private List<Memory> episodicMemoryMOs; 
    //soh mesmo pra ter o metodo checkStd correto
    //private S1To2AttentionCodelet_apagar attention;
    
    //private double lastPerceptionReceivedID = 0;
    
    public EpisodicRetrievalCodelet(String id, String episodicLearningCodeletId, String s1To2AttentionCodeletId){
        this.id = id;
        this.episodicLearningCodeletId = episodicLearningCodeletId;
        this.s1To2AttentionCodeletId = s1To2AttentionCodeletId;
        setName(id);
        
        
    }
    
    @Override
    public void accessMemoryObjects() {

        
        
        if(episodicMemory==null && episodicLearningCodeletId!=null)
			this.episodicMemory = this.getInput(episodicLearningCodeletId);
        
        if(cueMemoryMO==null)
			this.cueMemoryMO = this.getInput(s1To2AttentionCodeletId);
                
        if(episodicRecallMemory==null && id!=null)
                	this.episodicRecallMemory = this.getOutput(id);
    }

    @Override
    public void proc() {
        List<AbstractObjectPair> recallEpisodes = new ArrayList<>();
        //episodicMemory.
        episodicMemoryMOs = (List<Memory>)episodicMemory.getI();
        
        AbstractObject currentPerceptionToSearch = (AbstractObject)cueMemoryMO.getI();
        if(currentPerceptionToSearch != null && episodicMemoryMOs != null && !episodicMemoryMOs.isEmpty()){
            
                
                for(int i=0; i<episodicMemoryMOs.size(); i++){
                    Memory epMem = episodicMemoryMOs.get(i);
                    AbstractObjectPair episode = (AbstractObjectPair)epMem.getI();
                    if(equals(currentPerceptionToSearch, episode)){
                    //if(equals(currentPerceptionToSearch, episode.getBefore())){
                        recallEpisodes.add(episode);
                    }
                }
            episodicRecallMemory.setI(recallEpisodes);

        }
    
    }
    
    public abstract boolean equals(AbstractObject currentPerceptionToSearch, AbstractObjectPair episode);
    
    public abstract boolean equals(AbstractObject cueMemoryConf, AbstractObject epMemBefore);
    
    public boolean similiarDispersion(double actual, double comparative, double std){
        boolean similar = false;
        
        if((actual >= comparative - std) && (actual <= comparative + std)){
            similar = true;
        }
        return similar;
    }
    
    //funcao q diz o q eh um "desvio padrao" pra cada tipo de valor. obviamente depende da aplicação
    
    public boolean containsName(final List<AbstractObject> list, final String name){
        return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }
    
    public AbstractObject getAbstractObjectStruct(final AbstractObject list, final String name){
        return list.getCompositeParts().stream().filter(o -> o.getName().equals(name)).findFirst().get();
    }
    public Property getPropertyStruct(final AbstractObject list, final String name){
        return list.getProperties().stream().filter(o -> o.getName().equals(name)).findFirst().get();
    }
    public boolean containsPropertyStruct(final AbstractObject list, final String name){
        return list.getProperties().stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }
    
    public QualityDimension getQDStruct(final Property list, final String name){
        return list.getQualityDimensions().stream().filter(o -> o.getName().equals(name)).findFirst().get();
    }
    
    
        public String getId() {
		return id;
	}

	/**
	 * Sets the id of this Motor Codelet.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}