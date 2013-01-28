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

        float up_projection = actor.getLocalFrontAxis().dot(tangent_to_obstacle);
        float right_projection = actor.getLocalFrontAxis().dot(bitangent_to_obstacle);

        Vector3 escape_dir;

        if(Math.abs(up_projection) > Math.abs(right_projection)){
            escape_dir = bitangent_to_obstacle;
        } else{
            escape_dir = tangent_to_obstacle;
        }

        if(escape_dir.length() == 0)
            escape_dir.set(actor.getLocalRightAxis());

        float escape_radius = _lastObstacleInfo.hitActor.getBoundingRadius();
        escape_radius +=  actor.getBoundingRadius() + _radiusOffset;


        _navpoint.set(escape_dir);
        _navpoint.mulInplace(escape_radius);
        _navpoint.addNoCopy(_lastObstacleInfo.hitActor.getPosition());

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
        recalculateNavPoint();
    }

    @Override
    public boolean takeOver() {
        if(isObstacleInMovementTrajectory())
            return true;

        return false;
    }

    @Override
    public void start() {
       if(null == _lastObstacleInfo)
           return;

       VehicleActor actor = (VehicleActor)_targetActor;
       float meet_distance = actor.getBoundingRadius() + _radiusOffset;

       recalculateNavPoint();

       ApproachBehaviourAction approach_action = new ApproachBehaviourAction(_navpoint ,meet_distance, false);
       runAction(approach_action);
    }
}
