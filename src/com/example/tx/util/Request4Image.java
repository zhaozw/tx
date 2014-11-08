package com.example.tx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
/*
 * 输入图像的url
 * 输出图像的Bitmap
 */
public abstract class Request4Image extends AsyncTask<Void, Void, Bitmap>
{	
	private URL url;
	public Request4Image(String url)
	{
		try {
			this.url=new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap=null;
		try 
		{
			InputStream is=url.openStream();
			bitmap=BitmapFactory.decodeStream(is); 
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	@Override
	protected abstract void onPostExecute(Bitmap result); 
}
