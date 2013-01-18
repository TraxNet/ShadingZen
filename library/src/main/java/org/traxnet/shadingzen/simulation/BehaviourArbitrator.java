package org.traxnet.shadingzen.simulation;

import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;

import java.util.LinkedList;

/**
 *
 */
public class BehaviourArbitrator extends Action {
    LinkedList<BehaviourStatePrioritizer> _behaviours;
    BehaviourStatePrioritizer _currentRunningState = null;
    int _runninStatePriorityBonus = 0;


    public BehaviourArbitrator(int running_prioirity_bonus){
        _behaviours = new  LinkedList<BehaviourStatePrioritizer>();
        _runninStatePriorityBonus = running_prioirity_bonus;
    }

    /**
     * Registers a new BehaviouralState with a priority. Must be called before adding the arbitrator to an Actor
     * The priority is used to device if BehaviouralState should takeOver another BehaviouralState.
     *
     * @param state the BehaviouralState to add
     * @param priority a decisor priority
     */
    public void registerBehaviour(BehaviouralState state, int priority){
        _behaviours.add(new BehaviourStatePrioritizer(state, priority));
    }

    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {
        computeCurrentRunningBehaviourState();

        if(null == _currentRunningState)
            return;

        _currentRunningState.getState().step(deltaTime);
    }

    private void computeCurrentRunningBehaviourState() {
        BehaviourStatePrioritizer new_state = findNewCurrentBehaviour();

        if(new_state != _currentRunningState){
            if(null != _currentRunningState)
                _currentRunningState.getState().suppress();

            _currentRunningState = new_state;
            _currentRunningState.getState().start();
        }
    }

    private BehaviourStatePrioritizer findNewCurrentBehaviour() {
        BehaviourStatePrioritizer new_state = _currentRunningState;
        int new_state_priority = 0;

        if(null != _currentRunningState)
            new_state_priority += _runninStatePriorityBonus;

        int size = _behaviours.size();
        for(int i=0; i < size; i++){
            BehaviourStatePrioritizer state = _behaviours.get(i);
            if(!state.getState().takeOver())
                continue;

            if(null == new_state){
                new_state = state;
                continue;
            }

            int priority = state.getPriority();

            if(priority > new_state_priority){
                new_state_priority = priority;
                new_state = state;
            }
        }
        return new_state;
    }

    @Override
    public void cancel() {
        super.cancel();

        for(BehaviourStatePrioritizer holder : _behaviours){
            holder.getState().suppress();
        }
    }

    @Override
    protected void onRegisterTarget() {
        for(BehaviourStatePrioritizer holder : _behaviours){
            holder.getState().register((VehicleActor)_targetActor);
        }
    }

    @Override
    public boolean isDone() {
        return false;    // The arbitrator never ends
    }
}
