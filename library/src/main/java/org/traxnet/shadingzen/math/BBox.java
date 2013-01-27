package org.traxnet.shadingzen.math;

import android.util.FloatMath;

public class BBox {
	Vector3 _mins, _maxs;
    float []_minsArray, _maxsArray;
	
	public BBox(){
		_mins = new Vector3();
		_maxs = new Vector3();

        updateCachedArrays();
	}
	
	public BBox(Vector3 mins, Vector3 maxs){
		_mins = new Vector3(mins);
		_maxs = new Vector3(maxs);

        updateCachedArrays();
	}
	
	public BBox(final BBox b){
		_mins = new Vector3(b._mins);
		_maxs = new Vector3(b._maxs);

        updateCachedArrays();
	}
	
	public Vector3 getMins(){
		return _mins;
	}
	
	public Vector3 getMaxs(){
		return _maxs;
	}

    public void setMinMax(float max_x, float max_y, float max_z, float min_x, float min_y, float min_z){
        _mins.set(min_x, min_y, min_z);
        _maxs.set(max_x, max_y, max_z);

        updateCachedArrays();
    }
	
	public void setFromRadius(Vector3 center, float radius){
		_mins = new Vector3();
		_maxs = new Vector3();
		
		_mins.setX(center.getX() - radius);
		_mins.setY(center.getY() - radius);
		_mins.setZ(center.getZ() - radius);
		_maxs.setX(center.getX() + radius);
		_maxs.setY(center.getY() + radius);
		_maxs.setZ(center.getZ() + radius);

        updateCachedArrays();
	}
	
	public void setCenterExtents(Vector3 center, Vector3 extents){
		_mins = center.sub(extents);
		_maxs = center.add(extents);

        updateCachedArrays();
	}

    public void updateCachedArrays(){
        _maxsArray = _maxs.getAsArray();
        _minsArray = _mins.getAsArray();
    }
	
	public Vector3 getExtents(){
		Vector3 v = new Vector3(_maxs.getX() - _mins.getX(), _maxs.getY() - _mins.getY(), _maxs.getZ() - _mins.getZ());
		return v.mul(0.5f);
	}
	
	public float radius(){
		float max = _maxs.getX();
		if(max < _maxs.getY())
			max = _maxs.getY();
		if(max < _maxs.getZ())
			max = _maxs.getZ();
		float min = _mins.getX();
		if(min > _mins.getY())
			min = _mins.getY();
		if(min > _mins.getZ())
			min = _mins.getZ();
		float d = max > min? max : min;
		return d;
	}
	
	public boolean isPointInside( Vector3 p ){
		if( _mins.getX() < p.getX() && _mins.getY() < p.getY() && _mins.getZ() < p.getZ() && 
				_maxs.getX() > p.getX() && _maxs.getY() > p.getY() && _maxs.getZ() > p.getZ() ) return true;
		return false;
	}

    public boolean isPointInside( Vector3 p, float radius ){
        if( _mins.x - radius < p.x && _mins.y - radius < p.y && _mins.z - radius < p.z &&
                _maxs.x + radius > p.x && _maxs.y + radius > p.y && _maxs.z + radius > p.z ) return true;
        return false;
    }
	
	public boolean overlap(BBox b ){
		if(_mins.getX()>=b._maxs.getX() || _maxs.getX()<=b._mins.getX() ) return false;
		if(_mins.getY()>=b._maxs.getY() || _maxs.getY()<=b._mins.getY() ) return false;
		if(_mins.getZ()>=b._maxs.getZ() || _maxs.getZ()<=b._mins.getZ() ) return false;
		return true;
	}

	
	public Vector3 getCenter(){
		return _mins.add(_maxs);
	}
	
	public boolean testRayWithSphereExtents(Vector3 orig, Vector3 dir){
		float A = dir.length();
		if(A == 0.0) return false;
		
		float radius = radius();
		Vector3 center = getCenter();
		Vector3 oc = orig.sub(center);
		Vector3 co = center.sub(orig);
		float B = 2*oc.dot(dir);
		float diam = getExtents().length();
		float C = oc.dot(oc)-radius*radius;
		float disc = B*B - 4*A*C;
		
		 if( disc < 0.0f )
			 return false;
		 float t = 0.f;
		 
		// compute q as described above
		    float distSqrt = FloatMath.sqrt(disc);
		    float q;
		    if (B < 0)
		        q = (-B - distSqrt)/2.f;
		    else
		        q = (-B + distSqrt)/2.f;

		    // compute t0 and t1
		    float t0 = q / A;
		    float t1 = C / q;

		    // make sure t0 is smaller than t1
		    if (t0 > t1)
		    {
		        // if t0 is bigger than t1 swap them around
		        float temp = t0;
		        t0 = t1;
		        t1 = temp;
		    }

		    // if t1 is less than zero, the object is in the ray's negative direction
		    // and consequently the ray misses the sphere
		    if (t1 < 0)
		        return false;

		    // if t0 is less than zero, the intersection point is at t1
		    if (t0 < 0)
		    {
		        t = t1;
		        return true;
		    }
		    // else the intersection point is at t0
		    else
		    {
		        t = t0;
		        return true;
		    }
	}

    float[] coord = new float[3];
	
	public boolean testRay(float[] origin, float[] dir, float radius, Vector3 coordinates){

		//float[] origin = orig.getAsArray();
		//float[] dir = d.getAsArray();
		float[] Mins = _minsArray;
		float[] Maxs = _maxsArray;
		int i = 0;
		
		boolean Inside = true;
		float[] MaxT = {-1, -1, -1};

		// Find candidate planes.
		for( i=0; i<3; i++ ) {
		    Mins[i] -= radius;
            Maxs[i] += radius;

			if( origin[i] < Mins[i] ) {
				coord[i] = Mins[i];
				Inside = false;

				// Calculate T distances to candidate planes
				MaxT[i] = (Mins[i] - origin[i]) / dir[i];
			}
			else if( origin[i] > Maxs[i] ) {
				coord[i] = Maxs[i];
				Inside = false;

				// Calculate T distances to candidate planes
				MaxT[i] = (Maxs[i] - origin[i]) / dir[i];
			}
		}

		// Ray origin inside bounding box
		if( Inside ) return true;

		// Get largest of the maxT's for final choice of intersection
		int WhichPlane = 0;
		if( MaxT[1] > MaxT[WhichPlane])	WhichPlane = 1;
		if( MaxT[2] > MaxT[WhichPlane])	WhichPlane = 2;

		// Check final candidate actually inside box
		//if( IR(MaxT[WhichPlane])&SIGN_BITMASK ) return false;
		if( MaxT[WhichPlane] < 0) return false;

		for( i=0; i<3; i++ ) {
			if( i!=WhichPlane ) {
				coord[i] = origin[i] + MaxT[WhichPlane] * dir[i];
				if( coord[i]<Mins[i] || coord[i]>Maxs[i] )	return false;
			}
		}
		
		coordinates.setX(coord[0]);
		coordinates.setY(coord[1]);
		coordinates.setZ(coord[2]);
		return true;	// ray hits box
	}
}
