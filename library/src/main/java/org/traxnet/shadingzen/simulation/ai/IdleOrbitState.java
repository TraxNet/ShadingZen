package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.simulation.ActionsDrivenBehaviouralState;

/**
 * This is part of a set of predefined states for 3D objects (space simulator).
 *
 * IdleOrbitState manages an idle state that orbits around a target actor while the target
 * actor is active. If the target moves, the state tries to follow it or orbit as close to
 * the ideal orbit as possible.
 */
public class IdleOrbitState extends ActionsDrivenBehaviouralState {


    @Override
    public void step(float deltaTime) {

    }

    @Override
    public boolean takeOver() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
