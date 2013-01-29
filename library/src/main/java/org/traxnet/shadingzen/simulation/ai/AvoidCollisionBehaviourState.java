package org.traxnet.shadingzen.simulation.ai;

import org.traxnet.shadingzen.core.CollisionInfo;
import org.traxnet.shadingzen.core.Engine;
import org.traxnet.shadingzen.core.Scene;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.ActionsDrivenBehaviouralState;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 25/01/13
 * Time: 7:47
 */
public class AvoidCollisionBehaviourState extends ActionsDrivenBehaviouralState {
    CollisionInfo _lastObstacleInfo;
    Vector3 _navpoint = new Vector3();
    float [] trajectory_dir = new float[4], trajectory_orig = new float[4];
    float _frontalCheckDistance, _radiusOffset;
    float _timeToNextRecalculation = 0.f;
    ApproachBehaviourAction approach_action;
    boolean _isStateActive = false;
    private boolean obstacleInMovementTrajectory;

    private boolean isObstacleInMovementTrajectory() {
        Scene scene = Engine.getSharedInstance().getCurrentScene();

        VehicleActor actor = (VehicleActor)_targetActor;
        actor.getLocalFrontAxis().toArray(trajectory_dir);
        actor.getPosition().toArray(trajectory_orig);
        float radius = actor.getBoundingRadius() + _radiusOffset;

        _lastObstacleInfo = scene.getNearestColliderAlongRay(actor, trajectory_orig, trajectory_dir,
                _frontalCheckDistance, radius);

        if(null == _lastObstacleInfo)
            return false;

        return true;
    }

    Vector3 dir_to_obstacle = new Vector3();
    Vector3 tangent_to_obstacle = new Vector3();
    Vector3 bitangent_to_obstacle = new Vector3();

    private void recalculateNavPoint() {
        VehicleActor actor = (VehicleActor)_targetActor;

        calculateTrajectoryToObstacleCoordinates(actor);

        float right_projection = actor.getLocalFrontAxis().dot(tangent_to_obstacle);
        float up_projection = actor.getLocalFrontAxis().dot(bitangent_to_obstacle);

        Vector3 escape_dir;

        if(Math.abs(up_projection) > Math.abs(right_projection)){
            escape_dir = bitangent_to_obstacle;
        } else{
            escape_dir = tangent_to_obstacle;
        }

        if(escape_dir.length() == 0)
            escape_dir.set(actor.getLocalRightAxis());
        escape_dir.normalizeNoCopy();

        float escape_radius = _lastObstacleInfo.hitActor.getBoundingRadius();
        escape_radius +=  actor.getBoundingRadius() + _radiusOffset;


        _navpoint.set(escape_dir);
        _navpoint.mulInplace(escape_radius);
        _navpoint.addNoCopy(_lastObstacleInfo.hitActor.getPosition());

        if(null == approach_action){
            float meet_distance = actor.getBoundingRadius() + _radiusOffset;
            approach_action = new ApproachBehaviourAction(_navpoint ,meet_distance, false);
            runAction(approach_action);
        }
        approach_action.setNavpoint(_navpoint);

        _timeToNextRecalculation = 1.f;
    }

    private void calculateTrajectoryToObstacleCoordinates(VehicleActor actor) {
        dir_to_obstacle.set(_lastObstacleInfo.hitActor.getPosition());
        dir_to_obstacle.subNoCopy(actor.getPosition());
        dir_to_obstacle.normalizeNoCopy();

        tangent_to_obstacle.set(actor.getLocalUpAxis());
        tangent_to_obstacle.crossNoCopy(dir_to_obstacle);
        tangent_to_obstacle.normalizeNoCopy();

        bitangent_to_obstacle.set(dir_to_obstacle);
        bitangent_to_obstacle.crossNoCopy(tangent_to_obstacle);
        bitangent_to_obstacle.normalizeNoCopy();
    }

    public AvoidCollisionBehaviourState(float frontal_check_distance, float radius_offset){
       _frontalCheckDistance = frontal_check_distance;
        _radiusOffset = radius_offset;
    }

    @Override
    public void step(float deltaTime) {

        if(isTimeForNavpointRecalculation()){
            if(obstacleInMovementTrajectory){
                recalculateNavPoint();
            } else {
                disableApproachActionAndResetSteering();
            }
        } else{
            continueWithCurrentAction(deltaTime);
        }
    }

    private void continueWithCurrentAction(float deltaTime) {
        _timeToNextRecalculation -= deltaTime;
    }

    private void disableApproachActionAndResetSteering() {
        if(null != approach_action ){
            approach_action.cancel();
            approach_action = null;
        }
        VehicleActor actor = (VehicleActor)_targetActor;
        actor.setSteeringAngles(0.f, 0.f);
    }

    private boolean isTimeForNavpointRecalculation() {
        return _timeToNextRecalculation <= 0.f;
    }

    @Override
    public boolean takeOver() {
        obstacleInMovementTrajectory = isObstacleInMovementTrajectory();

        if(obstacleInMovementTrajectory)
            return true;

        return false;
    }

    @Override
    public void start() {
       if(null == _lastObstacleInfo)
           return;

       _timeToNextRecalculation = 0.f;




       //recalculateNavPoint();


    }
}
