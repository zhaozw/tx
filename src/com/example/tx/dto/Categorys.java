package com.example.tx.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.example.tx.util.C;

public class Categorys {
	static public List<Category> categorys = new ArrayList<Category>();
	
	public static synchronized List<Category> getCategorys() {
		if(categorys==null)
		{
			JSONObject res = C.asyncPost(C.URLget_categories, new HashMap());
			try 
			{
				if( !(res.getInt("status") == 0) ) {
					return null;
				}
				JSONArray categories = res.getJSONArray("categories");
				for(int i=0;i<categories.length();i++) {
					JSONObject category = categories.getJSONObject(i);
					Categorys.categorys.add(new Category(
							category.getString("itemCount"),
							category.getString("id"),
							category.getString("name"),
							category.getString("image")
							));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return categorys;
	}

	public static void setCategorys(List<Category> categorys) {
		Categorys.categorys = categorys;
	}
	
}
