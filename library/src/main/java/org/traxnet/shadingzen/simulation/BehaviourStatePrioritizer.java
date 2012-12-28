package org.traxnet.shadingzen.simulation;

/**
 * Manages a BehaviouralState priority which is set by the user
 */
public final class BehaviourStatePrioritizer {
    private BehaviouralState _state;
    private int _priority;

    public BehaviourStatePrioritizer(BehaviouralState state, int priority){
        _state = state;
        _priority = priority;
    }

    public BehaviouralState getState(){ return _state; }

    public int getPriority(){ return _priority; }

}
