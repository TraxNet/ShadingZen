package org.traxnet.shadingzen.core.queries;

import android.opengl.Matrix;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.Collider;
import org.traxnet.shadingzen.core.CollisionInfo;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 6/02/13
 * Time: 7:40
 */
public class ActorQueryAlongRay extends ActorQuery {
    float ray_length;
    float[] ray_origin;
    float[] ray_dir;
    float ray_radius;

    float [] _temp_ray_origin_targetspace = new float[4];
    float [] _temp_dir_as_array_targetspace = new float[4];

    Vector3 _temp_ray_origin_vec = new Vector3();
    Vector3 _temp_distance_vec = new Vector3();

    CollisionInfo info = new CollisionInfo();

    public void setRay(float[] origin, float[] dir, float radius, float length){
        ray_dir = dir;
        ray_origin = origin;
        ray_length = length;
        ray_radius = radius;

        ray_origin[3] = 1.f;
        ray_dir[3] = 0.f;
    }


    @Override
    public boolean checkIntersectsAABBox(BBox box) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean testActor(Actor actor) {
        float [] target_inv_model_matrix = actor.getInverseWorldModelMatrix().getAsArray();

        Matrix.multiplyMV(_temp_ray_origin_targetspace, 0, target_inv_model_matrix, 0, ray_origin, 0);
        _temp_ray_origin_vec.x = _temp_ray_origin_targetspace[0];
        _temp_ray_origin_vec.y = _temp_ray_origin_targetspace[1];
        _temp_ray_origin_vec.z = _temp_ray_origin_targetspace[2];

        BBox target_bbox = actor.getBoundingBox();

        float distance_from_target = _temp_ray_origin_vec.lengthSqrt();
        if(distance_from_target > target_bbox.radius() + ray_radius + ray_length)
            return false;


        Matrix.multiplyMV(_temp_dir_as_array_targetspace, 0, target_inv_model_matrix, 0, ray_dir, 0);

        if(ray_length >= 0.f){
            if(target_bbox.testRay(_temp_ray_origin_targetspace, _temp_dir_as_array_targetspace, ray_radius, info.hitPoint)){
                _temp_distance_vec.set(info.hitPoint);
                _temp_distance_vec.sub(_temp_ray_origin_vec);


                float distance_from_hit = _temp_distance_vec.lengthSqrt();
                if(ray_length >= distance_from_hit){

                    if(Collider.class.isInstance(actor)){
                        Collider collider = (Collider)actor;
                        info = collider.checkForRayIntersection(info, _temp_ray_origin_targetspace, _temp_dir_as_array_targetspace, ray_radius, ray_length);
                        if(null == info)
                            return false;

                        info.hitActor = collider;
                        info.hitLength = distance_from_hit;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void initializeFromPool() {

    }

    @Override
    public void finalizeFromPool() {

    }
}
