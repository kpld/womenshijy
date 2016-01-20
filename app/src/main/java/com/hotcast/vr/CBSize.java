package com.hotcast.vr;


public class CBSize {
	
	public float width;
	public float height;
	
	public CBSize(float w, float h) {
		
		width = w;
		height = h;
	}
	
	public CBSize(int w, int h) {
		
		width = (float)w;
		height = (float)h;
	}
	
	public static CBSize make(float w, float h) {
        return new CBSize(w, h);
    }
}
