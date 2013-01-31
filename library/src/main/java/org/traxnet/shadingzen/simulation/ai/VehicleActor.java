package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.core.Collider;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.math.Quaternion;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.BehaviourArbitrator;

/**
 *
 */
public abstract class VehicleActor extends Collider {
    protected BehaviourArbitrator _behaviouralArbitrator;
    protected float maxSteerVelocity = 0.1f; // Pitch and Yaw angles
    protected float maxVelocity = 1.f;
    protected float currentAcceleration = 0.5f;  // Probably constant in all applications
    protected float currentSteeringAcceleration = 0.5f;
    protected Vector3 currentLocalRightAxis = new Vector3(1.f, 0.f, 0.f);
    protected Vector3 currentLocalUpAxis = new Vector3(0.f, 1.f, 0.f);
    protected Vector3 currentLocalFrontAxis = new Vector3(0.f, 0.f, 1.f);
    private float [] _vec_currentLocalRightAxis = new float[4];
    private float [] _vec_currentLocalUpAxis = new float[4];
    private float [] _vec_currentLocalFrontAxis = new float[4];

    protected float currentTargetFrontVelocity = 0.f;
    protected float currentSteerVelocityX = 0.f;
    protected float currentSteerVelocityY = 0.f;
    protected float currentVelocity = 0.f;
    protected float currentRollVelocity = 0.f;
    protected Matrix4 _currentMatrixRotation = new Matrix4();
    protected Quaternion _currentStepAngularRotation = new Quaternion();

    private Quaternion _right_axis_quaternion = new Quaternion();
    private Quaternion _front_axis_quaternion = new Quaternion();

    public enum AccelerateState{
        MAINTAIN_VELOCITY,
        ACCELERATE,
        DECELERATE,
        AUTO
    }

    private AccelerateState _accelerateState = AccelerateState.MAINTAIN_VELOCITY;

    public void setAccelerateState(AccelerateState state){ _accelerateState = state; }

    public float getCurrentSteeringAcceleration(){ return currentSteeringAcceleration; }
    public float getCurrentAcceleration(){ return currentAcceleration; }
    public float getCurrentVelocity(){ return currentVelocity; }
    public float getMaxVelocity(){ return maxVelocity; }
    public float getMaxSteerVelocity(){ return maxSteerVelocity; }
    public Vector3 getLocalUpAxis(){ return currentLocalUpAxis; }
    public Vector3 getLocalRightAxis(){ return currentLocalRightAxis; }
    public Vector3 getLocalFrontAxis(){ return currentLocalFrontAxis; }
    public void setSteeringAngles(float yaw_angle, float pitch_angle){
        currentSteerVelocityX = yaw_angle;
        currentSteerVelocityY = pitch_angle;
    }
    public void setTargetFrontVelocity(float velocity){ currentTargetFrontVelocity = velocity; }





    private void rotateUsingCurrentSteerVelocity(float deltatime){
        _right_axis_quaternion.setRotation(currentLocalUpAxis, -currentSteerVelocityX *deltatime);
        _front_axis_quaternion.setRotation(currentLocalFrontAxis, currentRollVelocity*deltatime);
        _currentStepAngularRotation.setRotation(currentLocalRightAxis, currentSteerVelocityY *deltatime);
        _currentStepAngularRotation.mulInplace(_right_axis_quaternion);
        _currentStepAngularRotation.mulInplace(_front_axis_quaternion);
       /* Log.i("ShadingZen", "currentSteerVelocityX=" + currentSteerVelocityX);
        Log.i("ShadingZen", "currentSteerVelocityY=" + currentSteerVelocityY);
        Log.i("ShadingZen", "currentTargetFrontVelocity=" + currentTargetFrontVelocity);
        //Log.i("ShadingZen", "rotate_right_axis=("+rotate_right_axis.x + "," + rotate_right_axis.y + "," + rotate_right_axis.z + ")");
        Log.i("ShadingZen", "currentFrontAxis=("+currentLocalFrontAxis.x + "," + currentLocalFrontAxis.y + "," + currentLocalFrontAxis.z + ")");
        Log.i("ShadingZen", "_position=("+_position.x + "," + _position.y + "," + _position.z + ")");
          */
    }


    @Override
    protected void onUpdate(float deltaTime) {
        rotateUsingCurrentSteerVelocity(deltaTime);

        _rotation.mulInplace(_currentStepAngularRotation);
        _rotation.toMatrix(_currentMatrixRotation);

        _currentMatrixRotation.mul(_vec_currentLocalRightAxis, Vector3.vectorRightArray);
        _currentMatrixRotation.mul(_vec_currentLocalUpAxis, Vector3.vectorUpArray);
        _currentMatrixRotation.mul(_vec_currentLocalFrontAxis, Vector3.vectorFrontArray);
        /*currentLocalRightAxis = _currentMatrixRotation.mul(Vector3.vectorRight);
        currentLocalUpAxis = _currentMatrixRotation.mul(Vector3.vectorUp);
        currentLocalFrontAxis = _currentMatrixRotation.mul(Vector3.vectorFront);  */
        currentLocalRightAxis.set(_vec_currentLocalRightAxis);
        currentLocalUpAxis.set(_vec_currentLocalUpAxis);
        currentLocalFrontAxis.set(_vec_currentLocalFrontAxis);

        updateVelocity(deltaTime);

        _position.x += currentLocalFrontAxis.x*currentVelocity*deltaTime;
        _position.y += currentLocalFrontAxis.y*currentVelocity*deltaTime;
        _position.z += currentLocalFrontAxis.z*currentVelocity*deltaTime;
    }

    private void updateVelocity(float deltaTime) {
        //float cosine = navigation_vector.dot(currentLocalFrontAxis);

        if(_accelerateState == AccelerateState.ACCELERATE){
            currentVelocity = Math.min(maxVelocity, currentVelocity + currentAcceleration * deltaTime);
        } else if(_accelerateState == AccelerateState.DECELERATE){
            currentVelocity = Math.max(0.f, currentVelocity-currentAcceleration*deltaTime);
        } else if(_accelerateState == AccelerateState.AUTO){
            if(currentTargetFrontVelocity < currentVelocity){
                currentVelocity = Math.max(currentTargetFrontVelocity, currentVelocity-currentAcceleration*deltaTime);
            } else{
                currentVelocity = Math.min(currentTargetFrontVelocity, currentVelocity + currentAcceleration * deltaTime);
            }
        }

    }

    /** Initializes the actor
     * The bonus params is used to give a priority bonus to the running BehaviouralState over
     * the non-running BehaviouralStates waiting to take over.
     *
     * @param bonus a prioirity bonus
     */
    public void initWithArbitratorBonus(int bonus){
        _behaviouralArbitrator = new BehaviourArbitrator(bonus);

        // Start running simulation just after being spawned
        runAction(_behaviouralArbitrator);

        // Let subclasses add states
        setupBehaviours();
    }

    /** Child classes should add their BehaviouralState inside this method */
    protected abstract void setupBehaviours();


}
