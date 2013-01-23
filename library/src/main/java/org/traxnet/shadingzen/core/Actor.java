package org.traxnet.shadingzen.core;

import android.opengl.Matrix;
import android.util.Log;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.math.Quaternion;
import org.traxnet.shadingzen.math.Vector3;

import java.util.HashMap;
import java.util.Vector;

/** 
 * Actor is the base class for all objects within the scene that have position and rotation.
 * It also keeps a hierarchy of child Actors.
 * 
 * Action enable actor to do simple actions without having to track and handle updates for those actions
 * within the Actor's code. Actions are scheduled and executed by the engine for each frame enabling
 * easy animations and transitions. @see Action for more info.
 * 
 * To create your own Actor subclass, extend from it and implement onUpdate.
 */
public abstract class Actor extends Entity {
	protected Vector3 _position, _previousPosition;
	protected Quaternion _rotation;
	protected float _scale;
	protected Matrix4 _worldMatrix, _localMatrix, _scalingMatrix, _inverseWorldMatrix;
	protected HashMap<String, Actor> _childrenActors;
	protected Vector<Action> _activeActions;
    protected Object [] _cachedChildren;
	
	protected Shape _mesh;
	protected ShadersProgram _program;
	protected BitmapTexture _texture;
	protected Actor _parent;

    protected BBox _boundingBox;
	
	public Actor(){
		_rotation = new Quaternion();
		_position = new Vector3();
        _previousPosition = new Vector3();
		_childrenActors = new HashMap<String, Actor>();
		_worldMatrix = Matrix4.identity();
		_localMatrix = Matrix4.identity();
		_scalingMatrix = Matrix4.identity();
        _inverseWorldMatrix = Matrix4.identity();
		_activeActions = new Vector<Action>();
		_scale = 1.f;
        _boundingBox = new BBox();
	}
	
	@Override
	public void register(String name){
		super.register(name);
		
		_nameId = name;
	}
	
	@Override
	public void unregister(){
		for(int index = 0; index < _cachedChildren.length; index++){
            ((Actor)_cachedChildren[index]).unregister();
		}

        _cachedChildren = null;
		_childrenActors.clear();
		
		for(Action action: _activeActions){
			action.cancel();
		}
		
		super.unregister();
	}
	
	/** Finds an actor given its nameId
	 * 
	 * This implementation is very slow but we don't expect a
	 * deep hierarchy
	 * 
	 * @param id nameId to look for
	 * @return An Actor or null if not found
	 */
	public Actor find(String id){
		if(_childrenActors.containsKey(id))
			return _childrenActors.get(id);
		
		for(Actor child : _childrenActors.values()){
			Actor found = child.find(id);
			if(null != found)
				return found;
		}
		
		return null;
	}
	
	public void setParent(Actor parent){
		_parent = parent;
	}
	
	public Actor getParent(){
		return _parent;
	}


    public int getNumChildren(){
        return _childrenActors.size();
    }
	
	/** 
	 * Adds a new children actor to the hierarchy 
	 *
	 * @param child the child actor to be added.
	 */
	public void addChildren(Actor child){
		_childrenActors.put(child._nameId, child);
        _cachedChildren = _childrenActors.values().toArray();
	}
	
	/**
	 * Removes a child actor from the list of children. 
	 * If child actor is not found no error is reported.
	 * 
	 * @param child child actor to be removed.
	 */
	public void removeChildren(Actor child){
		removeChildren(child.getNameId());
	}
	
	/**
	 * Remos a child actor given its ID. 
	 * If child actor is not found no error is reported.
	 * 
	 * @param childId
	 */
	public void removeChildren(String childId){
		Actor child = _childrenActors.get(childId);
        child._childrenActors.remove(childId);
		//child.setParent(null);
		child.markForDestroy();
        _cachedChildren = _childrenActors.values().toArray();
	}
	
	/**
	 * Retuns the collection of children actors. May be empty.
	 * @return the children collection.
	 */
	public Object [] getChildren(){
		return _cachedChildren;
	}
	
	/*
	 * Called before an actor is destroyed (freed) by the engine.
	 * Derived classes MUST call super.onDestroy()
	 * 
	 * @see org.traxnet.shadingzen.core.Entity#onDestroy()
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();

		if(null != _cachedChildren){
            for(int index=0; index < _cachedChildren.length; index++){
                removeChildren((Actor)_cachedChildren[index]);
            }
        }

        _cachedChildren =  null;


        removeAllActions();
		
	}
	
	/** Get the underlying rotation quaternion 
	 * 
	 * @return A quaternon representing this shape's local rotation 
	 */
	public Quaternion getRotation(){ return _rotation; }
	
	/** Returns a totation matrix generated using the internal quaternion 
	 * @return A Matrix4 representing this shape's rotation
	 */
	public Matrix4 getRotationMatrix(){
		return _rotation.toMatrix();
	}
	
	public void setRotation(Quaternion rot){
		_rotation = rot;
	}
	public void applyRotation(Quaternion rot){
		_rotation = _rotation.mul(rot);
	}
	
	/** 
	 * Get local actor position relative to parent actor 
	 *
	 * @return the actor position.
	 */
	public Vector3 getPosition(){
		return _position;
	}
	/** Set local position relative to parent actor */
	public void setPosition(Vector3 v){
		_position = v;
	}

    public Vector3 getPreviousPosition(){
        return _previousPosition;
    }

    /** Sets thee local position relative to parent actor */
    public void setPosition(float x, float y, float z){
        _position.set(x, y, z);
    }
	
	public float getScale(){
		return _scale;
	}
	
	public void setScale(float scale){
		_scale = scale;
	}
	
	public void setWorldModelMatrix(Matrix4 matrix){
		_worldMatrix = matrix;
	}
	public Matrix4 getWorldModelMatrix(){
		return _worldMatrix;
	}
    public Matrix4 getInverseWorldModelMatrix(){
        return _inverseWorldMatrix;
    }

    Matrix4 local_matrix = Matrix4.identity();
	
	public Matrix4 getLocalModelMatrix(){
        _rotation.toMatrix(local_matrix);
		//local_matrix = getRotationMatrix();
		local_matrix.setTranslation(getPosition());
		
		_scalingMatrix.setScalingRow(getScale());
		
		Matrix.multiplyMM(_localMatrix.getAsArray(), 0, local_matrix.getAsArray(), 0, _scalingMatrix.getAsArray(), 0);
		return _localMatrix;
	}

    /**
     * Returns an object oriented bounding box which can be used to quickly discard
     * out of view-frustum actors and for collision purposes. Note: The bounding box must be
     * in object space, not in world space.
     *
     * NOTE: Actor class implementation returns a zero size oobb centered at object origin. If other
     * behaviour is required, this method should be overriden.
     *
     * @return an Object Oriented Bounding Box (OOBB)
     */
    public BBox getBoundingBox(){
       return _boundingBox;
    }

	
	@Override
	public void onTick(float delta){
        _previousPosition.set(_position);
		runActiveActions(delta);
		
		onUpdate(delta);
	}
	
	/** 
	 * Called each frame to keep the actor updated. Must be overriden by child classes 
	 *
	 * @param deltaTime the time in seconds elapsed since the last onUpdate call.
	 */ 
	protected abstract void onUpdate(float deltaTime);
	
	
	// ACTIONS ////////
	
	/** Steps one delta-time for each action registered for this actor */
	private void runActiveActions(float deltaTime){
		//@SuppressWarnings("unchecked")
		//Vector<Action> list = (Vector<Action>) _activeActions.clone();
        int num_toremove = 0;
		for(int index=0; index < _activeActions.size(); index++){
            Action action = _activeActions.get(index);
			try{
                if(action.isDone())
                    num_toremove++;
                else
				    action.step(deltaTime);
			} catch(InvalidTargetActorException e){
				Log.e("ShadingZen", "Registered action cannot be executed for the current type of actor", e);
			}
		}

        while(num_toremove > 0){
            for(int index=0; index < _activeActions.size(); index++){
                Action action = _activeActions.get(index);

                if(action.isDone()){
                    _activeActions.remove(action);
                    num_toremove--;
                    break;
                }
            }
        }
	}
	
	/** Schedules the action to be executed at the next frame */
	public void runAction(Action action){
		action.setTarget(this);
		_activeActions.add(action);
	}
	
	/** Cancel and remove the given action if present */
	public void removeAction(Action action){
		if(_activeActions.contains(action)){
			_activeActions.remove(action);
			action.cancel();
		}
	}

    private void removeAllActions(){
        for(int index=0; index < _activeActions.size(); index++){
            _activeActions.get(index).cancel();
        }

        _activeActions.clear();
    }
}
