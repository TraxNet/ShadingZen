package org.traxnet.shadingzen.core.queries;

import android.opengl.Matrix;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 7/02/13
 * Time: 6:39
 */
public class ActorQueryDistanceRange extends ActorQuery {
    float min_range, max_range;
    Vector3 position = new Vector3();

    float [] _temp_position_array = new float[4];
    float [] _temp_origin_targetspace = new float[4];

    Vector3 _temp_origin_vec = new Vector3();

    public void initWithRange(float x, float y, float z, float min, float max) {
        position.set(x, y, z);
        min_range = min;
        max_range = max;

        _temp_position_array[0] = x;
        _temp_position_array[1] = y;
        _temp_position_array[2] = z;
        _temp_position_array[3] = 1.f;
    }

    @Override
    public boolean checkIntersectsAABBox(BBox box) {
        return box.isPointInside(position, max_range);
    }

    @Override
    public boolean testActor(Actor actor) {
        float [] target_inv_model_matrix = actor.getInverseWorldModelMatrix().getAsArray();

        Matrix.multiplyMV(_temp_origin_targetspace, 0, target_inv_model_matrix, 0, _temp_position_array, 0);
        _temp_origin_vec.x = _temp_origin_targetspace[0];
        _temp_origin_vec.y = _temp_origin_targetspace[1];
        _temp_origin_vec.z = _temp_origin_targetspace[2];

        BBox target_bbox = actor.getBoundingBox();

        float distance_from_target = _temp_origin_vec.lengthSqrt();
        if(distance_from_target > target_bbox.radius() + max_range)
            return false;
        if(distance_from_target + target_bbox.radius() < min_range)
            return false;

        return true;
    }

    @Override
    public void initializeFromPool() {

    }

    @Override
    public void finalizeFromPool() {

    }


}
