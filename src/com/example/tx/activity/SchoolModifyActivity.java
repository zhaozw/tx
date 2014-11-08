package com.example.tx.activity;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.R.id;
import com.example.tx.R.layout;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class SchoolModifyActivity extends BaseActivity implements OnClickListener
{
	private String school_modify_campus="";
	private String school_modify_school="";
	private String school_modify_major="";
	private int school_modify_grade=-1;
	
	Spinner sp_school,sp_major,sp_grade;
	
	private EditText location;
	
	String[] school=null;
	
	boolean loaded_school = false;
	boolean loaded_grade = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_school_modify);
		
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("学校信息");
		
		
		//返回键
		ImageButton ret=(ImageButton) findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		TextView complete=(TextView) findViewById(R.id.common_title_bar_login);
		complete.setText("完成");
		complete.setVisibility(View.VISIBLE);
		complete.setOnClickListener(this);

		((TextView)findViewById(R.id.school_modify_campus)).setText(C.myDetail.getCampus());
		
		sp_school=(Spinner)findViewById(R.id.school_modify_school);
		sp_major=(Spinner)findViewById(R.id.school_modify_major);
		sp_grade=(Spinner)findViewById(R.id.school_modify_grade);
		location = (EditText) findViewById(R.id.school_modify_location);
		
		location.setText(C.location);
		
		HashMap map=new HashMap();
		map.put("college", C.college);
		new PostTask(C.URLget_school,map)
		{
			@Override
			protected void onPostExecute(JSONObject result) {
				try
				{
					if(result.getInt("status")==0)
					{
						int selected=0;
						JSONArray ja=result.getJSONArray("schools");
						school=new String[ja.length()];
						for(int i=0;i<ja.length();i++)
						{
							school[i]=ja.get(i).toString();
							if(school[i].equals(C.myDetail.getSchool()))
								selected=i;
						}
						sp_school.setAdapter(new ArrayAdapter(SchoolModifyActivity.this,android.R.layout.simple_list_item_1,school));
						sp_school.setSelection(selected);
						loaded_school = true;
					}
				}
				catch(Exception e)
				{
					makeToast("后台错误");
					e.printStackTrace();
				}
			}
		}.execute();
		sp_school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String school=((TextView)view).getText().toString();
				HashMap map=new HashMap();
				map.put("college", C.college);
				map.put("school", school);
				new PostTask(C.URLget_major,map)
				{
					@Override
					protected void onPostExecute(JSONObject result) {
						try
						{
							if(result.getInt("status")==0)
							{
								int selected=0;
								JSONArray ja=result.getJSONArray("majors");
								String[] majors=new String[ja.length()];
								for(int i=0;i<ja.length();i++)
								{
									majors[i]=ja.get(i).toString();
									if(majors[i].equals(C.myDetail.getMajor()))
										selected=i;
								}
								sp_major.setAdapter(new ArrayAdapter(SchoolModifyActivity.this,android.R.layout.simple_list_item_1,majors));
								sp_major.setSelection(selected);
							}
						}
						catch(Exception e)
						{
							makeToast("后台错误");
						}
					}
				}.execute();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		
		new PostTask(C.URLget_grade,new HashMap())
		{
			@Override
			protected void onPostExecute(JSONObject result) {
				try
				{
					if(result.getInt("status")==0)
					{
						JSONArray ja=result.getJSONArray("grades");
						int selected=0;
						String[] grades=new String[ja.length()];
						for(int i=0;i<ja.length();i++)
						{
							grades[i]=ja.get(i).toString();
							if(Integer.valueOf(grades[i].trim())==C.myDetail.getGrade())
								selected=i;
						}
						sp_grade.setAdapter(new ArrayAdapter(SchoolModifyActivity.this,android.R.layout.simple_list_item_1,grades));
						sp_grade.setSelection(selected);
						loaded_grade = true;
					}
				}
				catch(Exception e)
				{
					makeToast("后台错误");
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		if(KeyEvent.KEYCODE_BACK==keyCode&&event.getRepeatCount()==0)
		{			
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) 
	{
		if(!(loaded_school && loaded_grade)){
			makeToast("努力加载信息中...");
			return;
		}
		school_modify_campus=((TextView)findViewById(R.id.school_modify_campus)).getText().toString();
		school_modify_school=sp_school.getSelectedItem().toString();
		school_modify_major=sp_major.getSelectedItem().toString();
		school_modify_grade=Integer.valueOf(sp_grade.getSelectedItem().toString());
		
		C.myDetail.setCampus(school_modify_campus);
		C.myDetail.setSchool(school_modify_school);
		C.myDetail.setMajor(school_modify_major);
		C.myDetail.setGrade(school_modify_grade);
		C.location = location.getText().toString();
		
		Intent it=new Intent();
		it.putExtra("school_modify_campus", school_modify_campus);
		it.putExtra("school_modify_school", school_modify_school);
		it.putExtra("school_modify_major", school_modify_major);
		it.putExtra("school_modify_grade", school_modify_grade);
		setResult(RESULT_OK, it);
		
		finish();
	}
}
