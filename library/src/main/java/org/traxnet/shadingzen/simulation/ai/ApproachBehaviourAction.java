package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.BehaviourAction;

/**
 * Approaches a given Actor until a minimum distance is met.
 */
public class ApproachBehaviourAction extends BehaviourAction {
    protected Vector3 _navpoint;
    protected VehicleActor _vehicle;
    protected boolean _done = false;
    protected float _meetDistance;

    protected Vector3 currentDesiredVelocityVector = new Vector3();
    protected Vector3 currentDesiredVelocityVectorNormalize = new Vector3();
    protected float currentTargetFrontVelocity = 0.f;
    protected float currentTargetSteerVelocityX = 0.f;
    protected float currentTargetSteerVelocityY = 0.f;
    protected float currentSteerVelocityX = 0.f;
    protected float currentSteerVelocityY = 0.f;
    protected boolean fleeFromTarget = false;
    protected float _minimunVelocityFactorWhileSteering = 0.f;

    public ApproachBehaviourAction(Vector3 navpoint, float meet_distance, boolean flee){
        _navpoint = navpoint;
        _meetDistance = meet_distance;
        fleeFromTarget = flee;
    }

    /** A positive number between 0.0 and 1.0 with the minimum velocity during steering
     * The default is to 0.0 which makes the ship stop and rotate before accelerating again once
     * the vehicle is heading the correct direction.
     *
     * @param value
     */
    public void setMinimunVelocityFactorWhileSteering(float value){
        _minimunVelocityFactorWhileSteering = Math.max(0.f,Math.min(1.f,value));
    }

    public void setNavpoint(Vector3 navpoint){
        _navpoint = navpoint;
    }

    protected boolean calculateTargetVelocityVector(){
        float lengthSqrt = currentDesiredVelocityVector.lengthSqrt();
        if(lengthSqrt < 0.0001f)
            return false;
        currentDesiredVelocityVectorNormalize.set(currentDesiredVelocityVector);
        currentDesiredVelocityVectorNormalize.mulInplace(1.f/lengthSqrt);
        //currentDesiredVelocityVectorNormalize = currentDesiredVelocityVector.mul(1.f/lengthSqrt);
        if(fleeFromTarget)
            currentDesiredVelocityVectorNormalize.negateNoCopy();

        float desiredVelocity = lengthSqrt;

        float x = _vehicle.getLocalRightAxis().dot(currentDesiredVelocityVectorNormalize);
        float y = _vehicle.getLocalUpAxis().dot(currentDesiredVelocityVectorNormalize);
        float z = _vehicle.getLocalFrontAxis().dot(currentDesiredVelocityVectorNormalize);

        currentTargetSteerVelocityX = Math.signum(x) *
                Math.min(Math.abs(x*desiredVelocity), _vehicle.getMaxSteerVelocity());
        currentTargetSteerVelocityY = Math.signum(y) *
                Math.min(Math.abs(y*desiredVelocity), _vehicle.getMaxSteerVelocity());
        currentTargetFrontVelocity = Math.signum(z) *
                Math.min(Math.abs(z*desiredVelocity), _vehicle.getMaxVelocity());



        return true;
    }

    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {
        if(!_cancelled && !_done){
            currentDesiredVelocityVector.set(_navpoint);
            currentDesiredVelocityVector.subNoCopy(_targetActor.getPosition());

            if(!fleeFromTarget && currentDesiredVelocityVector.lengthSqrt() <= _meetDistance){
                _vehicle.setSteeringAngles(0, 0);
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

    protected void calculateCurrentSteering(float deltaTime) {
        if(currentTargetSteerVelocityX > currentSteerVelocityX){
            currentSteerVelocityX += Math.abs(_vehicle.getCurrentSteeringAcceleration()*deltaTime*currentTargetSteerVelocityX/_vehicle.getMaxSteerVelocity());
        } else  if(currentTargetSteerVelocityX < currentSteerVelocityX){
            currentSteerVelocityX -= Math.abs(_vehicle.getCurrentSteeringAcceleration()*deltaTime*currentTargetSteerVelocityX/_vehicle.getMaxSteerVelocity());
        }

        if(currentTargetSteerVelocityY > currentSteerVelocityY){
            currentSteerVelocityY += Math.abs(_vehicle.getCurrentSteeringAcceleration()*deltaTime*currentTargetSteerVelocityY/_vehicle.getMaxSteerVelocity());
        } else if(currentTargetSteerVelocityY < currentSteerVelocityY){
            currentSteerVelocityY -= Math.abs(_vehicle.getCurrentSteeringAcceleration()*deltaTime*currentTargetSteerVelocityY/_vehicle.getMaxSteerVelocity());
        }
    }

    protected void setMinimumVelocityIfTargetIsBehind() {
        float cosine = _vehicle.getLocalFrontAxis().dot(currentDesiredVelocityVectorNormalize);
        if(cosine <= 0.f){
            currentTargetFrontVelocity *= _minimunVelocityFactorWhileSteering;
        }
    }

    @Override
    protected void onRegisterTarget() {
        _vehicle = (VehicleActor) _targetActor;
        _vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
    }

    @Override
    public boolean isDone() {
        return _done;
    }
}
