package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.math.Vector3;

/**
 *
 */
public class PursuitBehaviourAction extends ApproachBehaviourAction {
    VehicleActor _pursuedActor;
    Vector3 to_target = new Vector3();

    public PursuitBehaviourAction(VehicleActor navpoint_actor, float meet_distance, boolean flee){
        super(navpoint_actor.getPosition(), meet_distance, flee);

        _pursuedActor = navpoint_actor;
    }

    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {
        if(!_cancelled && !_done){
            to_target.set(_pursuedActor.getPosition());
            to_target.subNoCopy(_targetActor.getPosition());

            float t = calculateTimeToIntercept(to_target);

            Vector3 displacement = _pursuedActor.getLocalFrontAxis().mul(_pursuedActor.getCurrentVelocity()*t);
            currentDesiredVelocityVector = _pursuedActor.getPosition().add(displacement);


            if(!fleeFromTarget && to_target.lengthSqrt() <= _meetDistance){
                _done = true;
                return;
            }

            if(calculateTargetVelocityVector()){
                calculateCurrentSteering(deltaTime);
                setMinimumVelocityIfTargetIsBehind();
            }

            _vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
            _vehicle.setSteeringAngles(currentSteerVelocityX, currentSteerVelocityY);
            _vehicle.setTargetFrontVelocity(currentTargetFrontVelocity);
        }
    }

    private float calculateTimeToIntercept(Vector3 to_target) {
        float a = _pursuedActor.getCurrentVelocity()* _pursuedActor.getCurrentVelocity() -
                _vehicle.getCurrentVelocity()*_vehicle.getCurrentVelocity();
        float b = 2* _pursuedActor.getLocalFrontAxis().mul(_pursuedActor.getCurrentVelocity()).dot(to_target);
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
