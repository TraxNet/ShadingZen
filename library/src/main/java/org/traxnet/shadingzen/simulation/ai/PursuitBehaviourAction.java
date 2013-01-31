package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.math.Vector3;

/**
 *
 */
public class PursuitBehaviourAction extends ApproachBehaviourAction {
    VehicleActor _quarry;
    Vector3 vector_to_quarry = new Vector3();
    Vector3 quarry_displacement = new Vector3();

    public PursuitBehaviourAction(VehicleActor quarry, float meet_distance, boolean flee){
        super(quarry.getPosition(), meet_distance, flee);

        _quarry = quarry;
    }


    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {
        if(!_cancelled && !_done){
            vector_to_quarry.set(_quarry.getPosition());
            vector_to_quarry.subNoCopy(_targetActor.getPosition());

            if(!fleeFromTarget && vector_to_quarry.lengthSqrt() <= _meetDistance){
                _done = true;
                _vehicle.setSteeringAngles(0, 0);
                return;
            }

            if(_quarry.getCurrentVelocity() < 0.1f || _vehicle.getCurrentVelocity() < 0.1f){
                navigation_vector.set(vector_to_quarry);
            } else{
                float estimated_time_intercept = calculateTimeToIntercept(vector_to_quarry);

                float estimated_quarry_travel_distance =  _quarry.getCurrentVelocity() * estimated_time_intercept;
                quarry_displacement.set(_quarry.getLocalFrontAxis());
                quarry_displacement.mulInplace(estimated_quarry_travel_distance);

                navigation_vector.set(_quarry.getPosition().add(quarry_displacement));
                navigation_vector.subNoCopy(_targetActor.getPosition());
            }


            /*
            Log.i("ShadingZen", "navigation_vector=(" +
                    navigation_vector.x + "," +
                    navigation_vector.y + "," +
                    navigation_vector.z + ")");
            Log.i("ShadingZen", "vector_to_quarry =(" +
                    vector_to_quarry.x + "," +
                    vector_to_quarry.y + "," +
                    vector_to_quarry.z + ")");
            */

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
        float a = _quarry.getCurrentVelocity()* _quarry.getCurrentVelocity() -
                _vehicle.getCurrentVelocity()*_vehicle.getCurrentVelocity();

        if(Math.abs(a) < 0.01f)
            return to_target.lengthSqrt()/_vehicle.getMaxVelocity();

        float b = 2* _quarry.getLocalFrontAxis().mul(_quarry.getCurrentVelocity()).dot(to_target);
        float c = to_target.length();

        float p = -b/(2*a);
        float r = (b*b)-4*a*c;
        if(r < 0.f)
            return to_target.lengthSqrt()/_vehicle.getMaxVelocity();
        float q = (float)Math.sqrt(r)/(2*a);

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
