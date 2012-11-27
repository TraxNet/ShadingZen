package org.traxnet.shadingzen.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;
import java.util.Vector;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core.EntityManager.EntityHolder;
import org.traxnet.shadingzen.core2d.Node2d;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.rendertask.BindFrameBufferRenderTask;
import org.traxnet.shadingzen.rendertask.BindTextureRenderTargetTask;
import org.traxnet.shadingzen.rendertask.RenderSceneRenderBatch;

import android.content.Context;
import android.content.res.Configuration;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public final class Engine implements Runnable {
	private Renderer _openglRenderer;
	private Stack<InputController> _inputControllerStack;
	private Context _context;
	private Handler _mainHandler;
	private Thread _logicThread;
	private float initialX;  
	private float initialY;  
    private float deltaX;  
    private float deltaY; 
    private long _frameTime = 0;
    private Stack<Scene> _sceneStack = new Stack<Scene>();
    private Stack<Scene> _toDestroyToScenes = new Stack<Scene>();
    private Scene _sceneToPushOnNextFrame = null;
    private int _viewWidth = 0;
    private int _viewHeight = 0;
    private Configuration _currentDeviceConfig;
    private int _currentFrameId = 0;  
    private ShadersProgram _debugWireframeProgram;  
    private GameInfo _currentGameInfo = null;
    private Vector<IShadowCaster> _cachedShadowCasters = null;
    private TextureRenderTarget _shadowMapRenderTarget = null;
    
    static Engine globalInstance = null;
    public static Engine getSharedInstance(){
    	return globalInstance;
    }
    
    public Engine(int viewWidth, int viewHeight){
    	_viewWidth = viewWidth;
    	_viewHeight = viewHeight;
    	initialX = 0;  
        initialY = 0;  
        deltaX = 0;  
        deltaY = 0;
        _logicThread = new Thread(this);
        _cachedShadowCasters = new Vector<IShadowCaster>();
        
        globalInstance = this;
        
        Log.i("ShadingZen", "Engine started with surface size: " + viewWidth + "x" + viewHeight);
    }
	
	public void Init(Context context, Renderer renderer){
		_openglRenderer = renderer;
		_context = context;
		
		try {
			ResourcesManager.setSharedInstance(new ResourcesManager());
			TaskManager.setSharedInstance(new TaskManager());
			//EntityManager.setSharedInstance(new EntityManager(_openglRenderer));
			_inputControllerStack = new Stack<InputController>();
		} catch (IllegalAccessException e) {
			Log.e("ShadingZen", "Error at initialization:" + e.getLocalizedMessage());
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
			Log.e("ShadingZen", sw.toString());
		}
		ResourcesManager.getSharedInstance().setContext(_context);
		
		_mainHandler = new Handler();
		TaskManager.getSharedInstance().registerHandler(_mainHandler);
		
		loadDebugShaders();
		//_logicThread.start();
		
		_currentDeviceConfig = this._context.getResources().getConfiguration();
	}
	
	public void setGameInfo(GameInfo game_info){
		_currentGameInfo = game_info;
	}
	
	public int getViewWidth(){
		return _viewWidth;
	}
	
	public int getViewHeight(){
		return _viewHeight;
	}
	
	public Texture getShadowMapTexture(){
		return _shadowMapRenderTarget;
	}
	
	public Scene getCurrentScene(){
		if(_sceneStack.isEmpty())
			return null;
		
		return _sceneStack.peek();
	}
	
	public void pushScene(Scene scene){
		//if(!_sceneStack.isEmpty()){
		//	getCurrentScene().onUnload();
		//}
		_sceneStack.push(scene);
	}
	
	public void popScene(){
		if(!_sceneStack.isEmpty()){
			Scene scene = _sceneStack.pop();
			//scene.onDestroy();
			_toDestroyToScenes.add(scene);
			
			//if(!_sceneStack.isEmpty())
			//	getCurrentScene().onLoad();
		}
	}
	
	public void replaceScene(SceneTransition transition){
		transition.setIsReplace(true);
		_sceneStack.pop();
		
		_sceneStack.push(transition);
	}
	
	public void updateTick(){
		//Log.i("ShadingZen", "updateTick");
		
		long nanotime = System.nanoTime();
		long delta_time = nanotime - _frameTime;
		
		if(delta_time > 1000000000)
			delta_time = 0;
		
		float deltaf = delta_time/1000000000.f;
		
		if(null != _currentGameInfo){
			_currentGameInfo.onTick(deltaf);
		}
		
		Scene scene = getCurrentScene();
		if(null == scene)
			return;
		
		scene.onTick(deltaf);
		
		// Destroy scene already removed from the scenes stack
		while(!_toDestroyToScenes.isEmpty()){
			scene = _toDestroyToScenes.pop();
			scene.onDestroy();
		}
		
		_frameTime = nanotime;
	}
	
	public void drawFrame(RenderService renderer){
		if(null != _currentGameInfo)
			_currentGameInfo.onPreDraw(renderer);
		
		Scene scene = getCurrentScene();
		if(null != scene){
			EntityManager entity_manager = scene.getEntityManager();
			
			renderSceneAsNormal(renderer, scene, entity_manager);
			
			// TODO: We don't need to update shadowmap each frame
			//renderSceneShadowMap(renderer);
		}
		
		if(null != _currentGameInfo)
			_currentGameInfo.onPostDraw(renderer);
	}
	
	private void renderSceneShadowMap(RenderService renderer){
		pushNewRenderSceneBatch(renderer);
		
		BindTextureRenderTargetTask bind_target = new BindTextureRenderTargetTask();
		bind_target.init(_shadowMapRenderTarget);
		renderer.addRenderTask(bind_target);
		
		if(null == _shadowMapRenderTarget){
			_shadowMapRenderTarget = new TextureRenderTarget();
			_shadowMapRenderTarget.init(256, 256);
		}
		
		for(IShadowCaster caster : _cachedShadowCasters){
			try{ 
				Entity entity = (Entity)caster;
				if(entity.isPendingDestroy() || !caster.castsShadow())
					continue;
				
				caster.onDepthMapDraw(renderer);
			} catch(Exception ex){
				Log.e("ShadingZen", "Exception found while rendering as shadow caster entity:" + ((Entity)caster).getNameId(), ex);
			}
		}
	}

	private void renderSceneAsNormal(RenderService renderer, Scene scene,
			EntityManager entity_manager) {
		pushNewRenderSceneBatch(renderer);
		renderer.addRenderTask(new BindFrameBufferRenderTask());
		scene.onDraw(renderer);
		float[] scene_model_matrix = scene.getLocalModelMatrix().getAsArray();		
		drawHierarchy(renderer, entity_manager, scene_model_matrix);
	}

	private void pushNewRenderSceneBatch(RenderService renderer) {
		try {
			RenderSceneRenderBatch render_scene_batch = RenderSceneRenderBatch.createFromPool();
			renderer.pushRenderBatch(render_scene_batch);
		} catch (InstantiationException e) {
			
		} catch (IllegalAccessException e) {
			
		}
	}
	
	public void drawHierarchy(RenderService renderer, EntityManager entity_manager, float[] scene_model_matrix){
		_currentFrameId += 1;
		
		Vector<EntityHolder> ordered_list = new Vector<EntityHolder>();
		Vector<EntityHolder> node2d_ordered_list = new Vector<EntityHolder>();
		Vector<EntityHolder> node2d_background_ordered_list = new Vector<EntityHolder>();
		
		Object[] holders = entity_manager.getCurrentEntityHolders();
		
		//synchronized(this._entities){
		
			// Create an ordered render list of entities or actors without parent. Actor with parent are rendered hierarchically below
			for(Object uncasted_holder : holders){
				EntityHolder holder = (EntityHolder)uncasted_holder;
				
				Entity entity = holder.getEntity();
				if(entity.isPendingDestroy())
					continue;
				
				if(Node2d.class.isInstance(entity) /*&& Layer.class.isInstance(entity)*/){
					Node2d node = (Node2d)entity;
					if(node.getNodeDrawInBackground())
						node2d_background_ordered_list.add(holder);
					else
						node2d_ordered_list.add(holder);
				} else if(Actor.class.isInstance(entity)){
					if(null == ((Actor)entity).getParent())
						ordered_list.add(0, holder);
					else
						ordered_list.add(holder);
					
					// Update cached actors 
					if(IShadowCaster.class.isInstance(entity))
						_cachedShadowCasters.add((IShadowCaster)entity);
				} else {
					ordered_list.add(holder);
				}
			}
			
			
			for(EntityHolder holder : node2d_background_ordered_list){
				drawActor(renderer, (Actor)holder.getEntity(), scene_model_matrix);
			}
			
			for(EntityHolder holder : ordered_list){
				drawEntity(renderer, holder, scene_model_matrix);
			}
			
			for(EntityHolder holder : node2d_ordered_list){
				drawActor(renderer, (Actor)holder.getEntity(), scene_model_matrix);
			}
		//}
	}

	private void drawEntity(RenderService renderer, EntityHolder holder, float[] parent_model_matrix) {
		if(holder.isMarked())
			return;

		Entity entity = holder.getEntity();
		
		
		if(Actor.class.isInstance(entity)){
				drawActor(renderer, (Actor)entity, parent_model_matrix);
			
		} else{
			if(entity.getFrameId() == _currentFrameId)
				return;
			
			try{
				entity.onDraw(renderer);
			} catch(Exception ex){
				Log.e("ShadingZen", "Exception found while rendering entity:" + entity.getNameId(), ex);
			}
		
		}
		
		entity.setFrameId(_currentFrameId);
	}

	private void drawActor(RenderService renderer, Actor actor, float[] parent_model_matrix) {
		try{
			if(actor.getFrameId() == _currentFrameId)
				return;
			
			
			/*float[] parent_model = null;
			if(null != actor.getParent()){
				parent_model = actor.getParent().getWorldModelMatrix().getAsArray();
			} else{
				//parent_model = Matrix4.identity().getAsArray();
				
				// The scene is the world itself so we call getLocalModelMatrix (convention)
				parent_model = _ownerScene.getLocalModelMatrix().getAsArray();
			}*/
			
			float [] model_matrix = new float[16];
			
			Matrix.multiplyMM(model_matrix, 0, parent_model_matrix, 0, actor.getLocalModelMatrix().getAsArray(), 0);
			actor.setWorldModelMatrix(new Matrix4(model_matrix));
			
			actor.onDraw(renderer);
				
			for(Actor child : actor.getChildren()){
				drawActor(renderer, child, model_matrix);
				child.setFrameId(_currentFrameId);
			}
		} catch(Exception ex){
			Log.e("ShadingZen", "Exception found while rendering actor:" + actor.getNameId(), ex);
		}
	}
	
	// Android input
	
	public void pushInputController(InputController controller){
		_inputControllerStack.push(controller);
	}
	
	public void popInputController(){
		_inputControllerStack.pop();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_DPAD_CENTER == keyCode) {
            
            return true;
        }
       return false;
    }
	
	public void setCurrentCamera(Camera camera){
		_openglRenderer.setCamera(camera);
	}
  
    public boolean onTouchEvent(MotionEvent event)  
    {  
    	if(_inputControllerStack.empty())
    		return false;
    	
    	InputController controller = _inputControllerStack.peek();
    	
        //when user touches the screen  
        if(event.getAction() == MotionEvent.ACTION_DOWN)  
        {  
            //reset deltaX and deltaY  
            deltaX = deltaY = 0;  
  
            //get initial positions  
            initialX = event.getRawX();  
            initialY = event.getRawY();  
        }  
       
        //when screen is released  
        if(event.getAction() == MotionEvent.ACTION_MOVE )  
        {  
         	deltaX = event.getRawX() - initialX;  
         	deltaY = event.getRawY() - initialY;  
  
         	//if(Math.abs(deltaY) < 15 && Math.abs(deltaX) < 15)
         	{
         		
         		if(null != controller){
         			controller.onTouchDrag(event.getRawX(), event.getRawY(), deltaX, deltaY);
         		}
         		
         	}
  
         	return true;  
        } else if(event.getAction() == MotionEvent.ACTION_UP){
     		
        	if(null != controller){
     			controller.onTouchUp(event.getRawX(), event.getRawY());
     		}
        }
        
        

		return true;  
		
    }
    
    public boolean onScaleGesture(float scale_factor){
    	if(_inputControllerStack.empty())
    		return false;
    	
    	InputController controller = _inputControllerStack.peek();
    	
    	return controller.onScaleGesture(scale_factor);
    }
    
    public void onConfigurationChanged(Configuration config){
    	if(null != this._currentGameInfo)
    		this._currentGameInfo.onConfigurationChanged(_currentDeviceConfig, config);
    }
    
    public boolean onBackPressed(){
    	if(null != this._currentGameInfo)
    		return this._currentGameInfo.onBackButtonPressed();
    	return false;
    }
    
    /*
     * Creates a vertex and fragment shaders for drawing wireframe meshes
     */
    void loadDebugShaders(){
    	_debugWireframeProgram = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, null, "WireShaders", 0); 
    	
    	if(!_debugWireframeProgram.isProgramDefined()){
    		_debugWireframeProgram.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_debugwire_vertex));
    		_debugWireframeProgram.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_debugwire_fragment));
    		_debugWireframeProgram.setProgramsDefined();
		}
    }
    
    public ShadersProgram getDebugWireframeProgram(){
    	return _debugWireframeProgram;
    }
    
    public RenderService getRenderService(){
    	return this._openglRenderer;
    }

    /** Main logic loop */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//this.UpdateTick();
	}
	
	public Context getContext(){
		return _context;
	}
}
