package org.traxnet.shadingzen.simulation;

import org.traxnet.shadingzen.core.Actor;

/**
 * ActionsDrivenBehaviouralState represent an actor state that controls or drives the actor during the time the behaviour is
 * active. An actor may have more than one available behaviour and each one should decide if it should take
 * over the control of the actor if the takeOver method returns true.
 */
public interface BehaviouralState {
    /**
     * Called once the BehaviouralState has been attached to an Actor
     * @param target the target actor receiving this behaviour
     */
    public void register(Actor target);

    /**
     * Called for each engine tick to run the simulation
     * @param deltaTime the tick time
     */
    public void step(float deltaTime);

    /**
     * If returns true, the behaviour state wants to take control of the simulation
     * @return whenever this BehaviouralState want to take control
     */
    public boolean takeOver();

    /**
     * If suppress is called by the arbitrator, all pending actions should be cancelled and the
     * behaviour state will go on hold.
     */
    public void suppress();
}
