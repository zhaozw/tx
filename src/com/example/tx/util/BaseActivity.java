package com.example.tx.util;

import cn.jpush.android.api.InstrumentedActivity;
import android.widget.Toast;

public class BaseActivity extends InstrumentedActivity
{
	public void makeToast(String msg)
	{
		Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
	}
}
