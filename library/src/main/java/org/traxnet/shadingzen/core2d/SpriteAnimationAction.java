package org.traxnet.shadingzen.core2d;

import org.traxnet.shadingzen.core.actions.Action;
import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.math.Vector2;

/** SpriteAnimationAction works with an AnimatedSprite to created animated sprites that can be easily controlled
 * with an action.
 */
public class SpriteAnimationAction extends Action {
    private SpriteAnimationDefinition _animDefinition;
    private float _frameTime;
    private boolean  _loop;
    private float _currentTime;

    /**
     * Default constructor.
     * @param anim An animation (a list of frames) definition
     * @param frame_rate the number of frames within one second we want to play the animation
     * @param loop whenever we want to loop the animation or stop at the end
     */
    public SpriteAnimationAction(SpriteAnimationDefinition anim, float frame_rate, boolean loop){
        _animDefinition = anim;
        _frameTime = 1.f/frame_rate;
        _loop = loop;
    }

    @Override
    public void step(float deltaTime) throws InvalidTargetActorException {
        AnimatedSprite sprite = (AnimatedSprite) _targetActor;

        int frame = (int) (_currentTime / _frameTime);
        if(frame >= _animDefinition.frames.size())
            frame %=  _animDefinition.frames.size();

        //Log.e("ShadingZen", "Current SpriteAnimationAction frame=" + frame);

        Vector2 current_frame = _animDefinition.frames.get(frame);
        sprite._needsBufferUpdate = true;
        sprite.setCurrentFrame(current_frame.getX()/sprite._texture.getWidth(), current_frame.getY()/sprite._texture.getHeight());

        _currentTime += deltaTime;
    }

    @Override
    protected void onRegisterTarget() {

    }

    @Override
    public boolean isDone() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
