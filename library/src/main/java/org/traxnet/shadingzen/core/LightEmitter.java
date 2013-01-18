package org.traxnet.shadingzen.core;

public interface LightEmitter {
	
	public enum ShadowCastingType {
		NONE(0x0000),
		SOFT_SHADOWMAP(0x0001),
		HARD_SHADOWMAP(0x0002);
		
		private int _value;
		
		private ShadowCastingType(int value){
			_value = value;
		}
	}
	
	
	public ShadowCastingType getShadowCastingType();
	public float computeContributionAtWorldPoint(float x, float y, float z);
    public float[] getLightColor();
    public float[] getLightPosition();
}
