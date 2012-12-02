package org.traxnet.shadingzen.core;

import android.content.Context;
import android.media.AudioManager;

public class SoundResource extends Resource {
	int _soundId = -1;
	Context _context;

	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id,
			Object params) {
		
		_soundId = ResourcesManager.getSharedInstance().getSoundPool().load(context, resource_id, 0);
		_context = context;
		
		return false;
	}
	
	public void play(float left_volume, float right_volume, int priority, int loop){
		ResourcesManager.getSharedInstance().getSoundPool().play(_soundId, left_volume, right_volume, priority, loop, 1.f);
	}
	
	public void play(){
		AudioManager audioManager = (AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
		float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		  
		float volume = curVolume/maxVolume;
		play(volume, volume, 0, 0);
	}

	@Override
	public boolean onDriverLoad(Context context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onResumed(Context context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPaused(Context context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onRelease() {
		ResourcesManager.getSharedInstance().getSoundPool().unload(_soundId);
	}

	@Override
	public boolean isDriverDataDirty() {
		// TODO Auto-generated method stub
		return false;
	}

}
