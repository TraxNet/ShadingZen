package org.traxnet.shadingzen.simulation;

import org.traxnet.shadingzen.core.Actor;

import java.util.LinkedList;

/**
 * ActionsDrivenBehaviouralState run BehaviourActions to accomplish its behaviour. This further allows reusing parts of code
 * between BehaviourStates.
 */
public abstract class ActionsDrivenBehaviouralState implements BehaviouralState {
    protected LinkedList<BehaviourAction> _currentActions;
    protected Actor _targetActor;

    public ActionsDrivenBehaviouralState(){
        _currentActions = new LinkedList<BehaviourAction>();
    }

    public void register(Actor target){
        _targetActor = target;
    }

    /**
     * Runs a BehaviourAction and adds it to _currentActions list
     *
     * @param action the action to be executed
     */
    protected void runAction(BehaviourAction action){
        _targetActor.runAction(action);

        _currentActions.add(action);
    }

    protected void removeAction(BehaviourAction action){
        _targetActor.removeAction(action);

        _currentActions.remove(action);
    }
    /**
     * If overriden in child classes, super.suppress must be called.
     */
    public void suppress(){
        for(BehaviourAction action : _currentActions){
            _targetActor.removeAction(action);
        }

        _currentActions.clear();
    }



}
