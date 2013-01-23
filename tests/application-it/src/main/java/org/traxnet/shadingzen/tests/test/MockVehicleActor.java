package org.traxnet.shadingzen.tests.test;

import org.traxnet.shadingzen.core.CollisionInfo;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;

/**
 */
public class MockVehicleActor extends VehicleActor {
    public void init(){
        this.currentVelocity = 0.f;
        this.maxSteerVelocity = 1.f;
        this.maxVelocity = 10.f;
        this.currentAcceleration = 1.f;
        this._position.set(0.f, 0.f, 0.f);
        this.currentSteeringAcceleration = 5.f;
    }



    @Override
    protected void setupBehaviours() {

    }

    @Override
    public BBox getBoundingBox() {
        return new BBox();
    }


    @Override
    public void onDraw(RenderService renderer) throws Exception {

    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onUnload() {
    }

    @Override
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);

        //Log.i("ShadingZen", "TestVehicle position: " + _position.x + "," + _position.y + "," + _position.z);
    }

    @Override
    public float getBoundingRadius() {
        return 0;
    }

    @Override
    public CollisionInfo checkForRayIntersection(CollisionInfo info, float [] orig, float [] dir, float radius, float length) {
        return null;
    }

    @Override
    public boolean onTouch(CollisionInfo info) {
        return false;
    }
}
