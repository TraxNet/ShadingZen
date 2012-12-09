package org.traxnet.shadingzen.examples;

import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.core2d.*;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.math.Vector4;
import org.traxnet.shadingzen.core2d.SpriteAnimationAction;

/**
 * Extend from GameInfo to design you own game logic controller
 */
public class AnimationsGameInfo extends GameInfo implements InputController {
    Scene _gameScene;
    Layer _animsLayer;

    @Override
    public void onGameStart() {
        // Generate a new scene and push it into the stack to make it the current one
        _gameScene = new Scene();
        Engine.getSharedInstance().pushScene(_gameScene);

        // Add a new camera and set it as current
        _currentCamera = new Camera();
        _currentCamera.setValues(new Vector3(0.0f, 0.0f, -8.0f), 1.5f, 4.0f/3.0f, 1.f, 200.0f);

        _currentCamera.setViewportSize(Engine.getSharedInstance().getViewWidth(), Engine.getSharedInstance().getViewHeight());

        Engine.getSharedInstance().setCurrentCamera(_currentCamera);
        Engine.getSharedInstance().getRenderService().setClearColor(new Vector4(0.0f, 0.0f, 0.0f, 0.f));

        int screen_height = Engine.getSharedInstance().getViewHeight();

        // Create a layer (empty Node2D) which will keep all the 2d items in scene
        _animsLayer = (Layer) _gameScene.spawn(Layer.class, "animslayer");

        // Add a simple label with the default font (shipped with ShadingZen)
        Label label = (Label) _animsLayer.spawn(Label.class, "mylabel");
        label.initWithStringAndFont("Enjoy 2D nodes!", R.raw.courier02, R.raw.courier02_0);
        label.setPositionInPixels(280, screen_height-30);

        // Create the maid face
        Sprite maid = Sprite.spriteWithTexture(_animsLayer, R.raw.maidface01, (short)0, (short)0);
        maid.setPositionInPixels(48, screen_height-48);

        // Create an animated (walking) maid sprite
        AnimatedSprite maid_char = AnimatedSprite.animatedSpriteWithTexture(_animsLayer,  R.raw.maidchar01, 32, 48);
        SpriteAnimationAction anim = buildMaidenAnim(maid_char);
        maid_char.centerInScreen();
        maid_char.runAction(anim);


    }

    public SpriteAnimationAction buildMaidenAnim(AnimatedSprite maid_char){
        SpriteAnimationDefinition definition = new SpriteAnimationDefinition();

        definition.addFrame(0.f, 0.f);
        definition.addFrame(32.f, 0.f);
        definition.addFrame(64.f, 0.f);
        definition.addFrame(96.f, 0.f);

        SpriteAnimationAction anim =  new SpriteAnimationAction(definition, 4, true);

        return anim;
    }

    @Override
    public void onGameEnd() {

    }

    /**
     * Called each frame to keep the actor updated. Must be overriden by child classes
     *
     * @param deltaTime the time in seconds elapsed since the last onUpdate call.
     */
    @Override
    protected void onUpdate(float deltaTime) {

    }

    /**
     * Draw this entity
     *
     * @throws Exception
     */
    @Override
    public void onDraw(RenderService renderer) throws Exception {

    }

    /**
     * Called when loading entity's data
     */
    @Override
    public void onLoad() {

    }

    /**
     * Called when unloading entity's data
     */
    @Override
    public void onUnload() {

    }



    @Override
    public void onTouchDrag(float posx, float posy, float deltax, float deltay) {

    }

    @Override
    public void onTouchUp(float posx, float posy) {

    }

    @Override
    public boolean onScaleGesture(float scale_factor) {
        return false;
    }
}
