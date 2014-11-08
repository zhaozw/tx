package com.example.tx.util;

import org.json.JSONObject;

import android.os.AsyncTask;

public abstract class MsgValidation extends AsyncTask<Void, Void, JSONObject> {

	private String mobile;
	
	public MsgValidation(String m)
	{
		this.mobile=m;
	}
	
	@Override
	protected JSONObject doInBackground(Void... params) {
		return C.MsgValidation(mobile);
	}

	@Override
	abstract protected void onPostExecute(JSONObject result) ;
}
