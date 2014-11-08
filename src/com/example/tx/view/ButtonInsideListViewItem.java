package com.example.tx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * ר������ListView Item���Button
 * 
 * @author liu
 * 
 */

public class ButtonInsideListViewItem extends Button {

	public ButtonInsideListViewItem(Context context) {
		super(context);
	}

	public ButtonInsideListViewItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ButtonInsideListViewItem(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setPressed(boolean pressed) {
		if (pressed && getParent() instanceof View
				&& ((View) getParent()).isPressed()) {
			return;
		}
		super.setPressed(pressed);
	}

}