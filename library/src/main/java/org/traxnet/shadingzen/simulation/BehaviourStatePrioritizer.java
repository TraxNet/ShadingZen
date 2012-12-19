package org.traxnet.shadingzen.simulation;

/**
 * Manages a BehaviourState priority which is set by the user
 */
public final class BehaviourStatePrioritizer {
    private BehaviourState _state;
    private int _priority;

    public BehaviourStatePrioritizer(BehaviourState state, int priority){
        _state = state;
        _priority = priority;
    }

    public  BehaviourState getState(){ return _state; }

    public int getPriority(){ return _priority; }

}
