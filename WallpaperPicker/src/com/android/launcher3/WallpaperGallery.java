package com.android.launcher3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class WallpaperGallery extends Gallery {

	public WallpaperGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public WallpaperGallery(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public WallpaperGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		int keyEvent;
		if(isScrollingLeft(e1, e2)){
			keyEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyEvent, null);
		return false;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return super.onScroll(e1, e2, distanceX, distanceY);
	}
	
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){
		return e2.getX() > e1.getX();
	}
}
