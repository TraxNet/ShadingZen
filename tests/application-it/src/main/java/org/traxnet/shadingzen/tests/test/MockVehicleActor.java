package org.traxnet.shadingzen.tests.test;

import android.util.Log;
import org.traxnet.shadingzen.core.CollisionInfo;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;

/**
 */
public class MockVehicleActor extends VehicleActor {
    private boolean _isColliding;
    private boolean _positionLogging = false;

    public void init(){
        this.currentVelocity = 0.f;
        this.maxSteerVelocity = 1.f;
        this.maxVelocity = 10.f;
        this.currentAcceleration = 1.f;
        this._position.set(0.f, 0.f, 0.f);
        this.currentSteeringAcceleration = 5.f;
        _boundingBox.setFromRadius(Vector3.zero, 1.f);
    }

    public int getNumCollisions() {
        return numCollisions;
    }

    public boolean isCurrentlyColliding(){
        return _isColliding;
    }

    int numCollisions = 0;

    public void setMaxVelocity(float value){
        maxVelocity = value;
    }

    public void setCollisionRadius(float radius){
        _boundingBox.setFromRadius(Vector3.zero, radius);
    }

    @Override
    protected void setupBehaviours() {

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
        _isColliding = false;
        super.onUpdate(deltaTime);

        if(_positionLogging){
            Log.i("ShadingZen", "TestVehicle position: " + _position.x + "," + _position.y + "," + _position.z);
            Log.i("ShadingZen", "TestVehicle front dir: " + currentLocalFrontAxis.x + "," + currentLocalFrontAxis.y + "," + currentLocalFrontAxis.z);
        }
    }

    public void enablePositionLogging(){
        _positionLogging = true;
    }

    @Override
    public float getBoundingRadius() {
        return _boundingBox.radius();
    }

    @Override
    public CollisionInfo checkForRayIntersection(CollisionInfo info, float [] orig, float [] dir, float radius, float length) {
        return info;
    }

    @Override
    public boolean onTouch(CollisionInfo info) {
        numCollisions++;
        _isColliding = true;
        return false;
    }

    public void setMockPreviousPosition(float x, float y, float z) {
        _previousPosition.set(x, y, z);
    }
}
