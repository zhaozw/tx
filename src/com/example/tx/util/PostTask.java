package com.example.tx.util;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.AsyncTask;

public abstract class PostTask extends AsyncTask<Void, Void, JSONObject> {
	
	private String toUrl;
	private HashMap content;
	
	public PostTask(String toUrl, HashMap content) {
		this.toUrl = toUrl;
		this.content = content;
	}

	@Override
	protected JSONObject doInBackground(Void... params) {
		return C.post(toUrl, content);
	}

	@Override
	abstract protected void onPostExecute(JSONObject result) ;
	
}
