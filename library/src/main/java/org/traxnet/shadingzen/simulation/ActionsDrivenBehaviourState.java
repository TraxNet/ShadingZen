package org.traxnet.shadingzen.simulation;

import org.traxnet.shadingzen.core.Actor;

import java.util.LinkedList;

/**
 * ActionsDrivenBehaviourState run BehaviourActions to accomplish its behaviour. This further allows reusing parts of code
 * between BehaviourStates.
 */
public abstract class ActionsDrivenBehaviourState {
    protected LinkedList<BehaviourAction> _currentActions;
    protected Actor _targetActor;

    public ActionsDrivenBehaviourState(){
        _currentActions = new LinkedList<BehaviourAction>();
    }

    public void register(Actor target){
        _targetActor = target;
    }


    public void suppress(){
        for(BehaviourAction action : _currentActions){
            action.cancel();
        }
    }



}
