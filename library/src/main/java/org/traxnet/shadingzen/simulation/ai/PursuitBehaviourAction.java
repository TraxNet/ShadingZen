package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.math.Vector3;

/**
 *
 */
public class PursuitBehaviourAction extends ApproachBehaviourAction {
    VehicleActor _pursedActor;

    public PursuitBehaviourAction(VehicleActor navpoint_actor, float meet_distance, boolean flee){
        super(navpoint_actor, meet_distance, flee);

        _pursedActor = navpoint_actor;
    }

    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {
        if(!_cancelled && !_done){
            Vector3 to_target = _navpoint.getPosition().sub(_targetActor.getPosition());
            float t = calculateTimeToIntercept(to_target);

            Vector3 displacement = _pursedActor.getLocalFrontAxis().mul(_pursedActor.getCurrentVelocity()*t);
            currentDesiredVelocityVector = _pursedActor.getPosition().add(displacement);


            if(!fleeFromTarget && to_target.lengthSqrt() <= _meetDistance){
                _done = true;
                return;
            }

            if(calculateTargetVelocityVector()){
                calculateCurrentSteering(deltaTime);
                setZeroVelocityIfTargetIsBehind();
            }

            _vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
            _vehicle.setSteeringAngles(currentSteerVelocityX, currentSteerVelocityY);
            _vehicle.setTargetFrontVelocity(currentTargetFrontVelocity);
        }
    }

    private float calculateTimeToIntercept(Vector3 to_target) {
        float a = _pursedActor.getCurrentVelocity()*_pursedActor.getCurrentVelocity() -
                _vehicle.getCurrentVelocity()*_vehicle.getCurrentVelocity();
        float b = 2*_pursedActor.getLocalFrontAxis().mul(_pursedActor.getCurrentVelocity()).dot(to_target);
        float c = to_target.length();

        float p = -b/(2*a);
        float q = (float)Math.sqrt((b*b)-4*a*c)/(2*a);

        float t1 = p - q;
        float t2 = p + q;
        float t;

        if (t1 > t2 && t2 > 0)
        {
            t = t2;
        }
        else
        {
            t = t1;
        }
        return t;
    }
}
