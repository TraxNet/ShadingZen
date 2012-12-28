package org.traxnet.shadingzen.tests.test;

import org.traxnet.shadingzen.core.Collider;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;

/**
 * Created with IntelliJ IDEA.
 * User: Oscar
 * Date: 23/12/12
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onTouch(Collider other) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDraw(RenderService renderer) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLoad() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onUnload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);    //To change body of overridden methods use File | Settings | File Templates.

        //Log.i("ShadingZen", "TestVehicle position: " + _position.x + "," + _position.y + "," + _position.z);
    }
}
