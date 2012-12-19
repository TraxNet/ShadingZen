package org.traxnet.shadingzen.simulation;

import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.core.Action;

import java.util.LinkedList;

/**
 *
 */
public class BehaviourArbitrator extends Action {
    LinkedList<BehaviourStatePrioritizer> _behaviours;

    public BehaviourArbitrator(){
        _behaviours = new  LinkedList<BehaviourStatePrioritizer>();
    }

    public void registerBehaviour(BehaviourState state, int priority){
        _behaviours.add(new BehaviourStatePrioritizer(state, priority));
    }

    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {

    }

    @Override
    protected void onRegisterTarget() {

    }

    @Override
    public boolean isDone() {
        return false;
    }
}
