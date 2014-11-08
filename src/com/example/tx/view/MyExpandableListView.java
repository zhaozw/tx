package com.example.tx.view;

import java.util.ArrayList;
import java.util.List;




import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;

public class MyExpandableListView extends ExpandableListView {  
	  
    public MyExpandableListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
    }  
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        // TODO Auto-generated method stub  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
  
        MeasureSpec.AT_MOST);  
  
        super.onMeasure(widthMeasureSpec, expandSpec);  
    }

	@Override
	public boolean collapseGroup(int groupPos) {
		return super.collapseGroup(groupPos);
	}

	@Override
	public boolean expandGroup(int groupPos) {
		return super.expandGroup(groupPos);
	}  
    
    
} 
