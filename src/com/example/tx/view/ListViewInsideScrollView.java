package com.example.tx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * ר������ScrollView���ListView
 * @author liu
 *
 */

public class ListViewInsideScrollView extends ListView {

	public ListViewInsideScrollView(Context context) {
		super(context);
	}

	public ListViewInsideScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ListViewInsideScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	
    public void setListViewHeightBasedOnChildren() {   
        ListAdapter listAdapter = getAdapter();   
        if (listAdapter == null) {   
            return;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, this);  
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();    
        }
   
        ViewGroup.LayoutParams params = getLayoutParams();   
        params.height = totalHeight+ (getDividerHeight() * (listAdapter.getCount() - 1));
        setLayoutParams(params);   
    }   
}
