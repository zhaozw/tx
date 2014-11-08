package com.example.tx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ResizeLayout extends FrameLayout {
	private OnResizeListener mListener;

	public ResizeLayout(Context context, AttributeSet attrs) {
	super(context, attrs);
	}

	public interface OnResizeListener {
	void OnResize(int w, int h, int oldw, int oldh);
	}

	public void setOnResizeListener(OnResizeListener l) {
	mListener = l;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);

	if (mListener != null) {
	mListener.OnResize(w, h, oldw, oldh);
	}
	}
}
