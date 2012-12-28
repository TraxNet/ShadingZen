package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.BehaviourAction;

/**
 *
 */
public class OrbitBehaviourAction extends BehaviourAction {
    Actor _navpoint;
    VehicleActor _vehicle;
    float _orbitDistance;
    float _targetSteering;

    public OrbitBehaviourAction(Actor navpoint_actor){
        _navpoint = navpoint_actor;

    }

    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {
        Vector3 dir = _targetActor.getPosition().sub(_navpoint.getPosition()).normalize();


    }

    @Override
    protected void onRegisterTarget() {
        _vehicle = (VehicleActor) _targetActor;
        _vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);

        _orbitDistance = _targetActor.getPosition().sub(_navpoint.getPosition()).lengthSqrt();
    }

    @Override
    public boolean isDone() {
        return false;   // Orbit never ends
    }
}
