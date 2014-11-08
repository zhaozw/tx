package com.example.tx.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.R.id;
import com.example.tx.R.layout;
import com.example.tx.dto.Item;
import com.example.tx.dto.UserDetails;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MineDetailActivity extends BaseActivity implements OnClickListener
{
	private final String imgLoaction="file://"+Environment.getExternalStorageDirectory().getPath() + "/temp.png";
	private final String imgFileLocation=Environment.getExternalStorageDirectory().getPath() + "/temp.png";
	private final Uri imageUri=Uri.parse(imgLoaction);
	
	private static final int aspectX = 1;
	private static final int aspectY = aspectX;
	private static final int outputX = 320;
	private static final int outputY = outputX;
	
	private String school_modify_campus="";
	private String school_modify_school="";
	private String school_modify_major="";
	private String school_modify_grade="";
	
	private Bitmap picBitmap = null;
	
	private ImageView avatar=null;
	
	private AlertDialog a;
	
	private boolean imgChanged=false;
	
	private TextView tv_college,tv_campus,tv_school,tv_major,tv_grade,tv_name;
	private ImageView im_avatar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mine_detail);
		
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("个人信息");
		
		tv_college=((TextView)findViewById(R.id.mine_detail_college));
		tv_campus=((TextView)findViewById(R.id.mine_detail_campus));
		tv_school=((TextView)findViewById(R.id.mine_detail_school));
		tv_major=((TextView)findViewById(R.id.mine_detail_major));
		tv_grade=((TextView)findViewById(R.id.mine_detail_grade));
		
		//设置页面默认值
		setDefaultValue();
		
		//返回键
		ImageButton ret=(ImageButton) findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(this);
		
		//学校信息修改
		ImageButton modify=(ImageButton) findViewById(R.id.btn_mine_detail_school_mdf);
		modify.setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) {
				if(C.myDetail == null){
					makeToast("努力加载信息中...");
					return;
				}
				Intent it=new Intent(MineDetailActivity.this,SchoolModifyActivity.class);
				startActivityForResult(it, 0);
			}
		});
				
		//点击照片页面
		avatar=(ImageView) findViewById(R.id.img_mine_detail_avatar);
		((LinearLayout)findViewById(R.id.mine_detail_avatar_bar)).setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				a=new AlertDialog.Builder(MineDetailActivity.this).setTitle("选择上传方法").setSingleChoiceItems(new String[]{"拍照","从照片中选择"}, 0, new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which)
						{
						case 0:
							Intent intent0 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent0.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(intent0, 1);
							a.cancel();
							break;
						case 1:
							Intent intent=new Intent(Intent.ACTION_PICK,null);
							intent.setType("image/*");
							intent.putExtra("crop", "true");
							intent.putExtra("aspectX", aspectX);
							intent.putExtra("aspectY", aspectY);
							intent.putExtra("outputX", outputX);
							intent.putExtra("outputY", outputY);
							intent.putExtra("scale", true);
							intent.putExtra("return-data", false);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
							intent.putExtra("noFaceDetection", false);
							startActivityForResult(intent, 2);
							a.cancel();
							break;
						}
					}
				}).setNegativeButton("取消", null).show();
			}
		});
	}

	//设置键盘返回键保存
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		if(KeyEvent.KEYCODE_BACK==keyCode&&event.getRepeatCount()==0)
		{
			try 
			{
				save();
				finish();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//返回键
	@Override
	public void onClick(View v) 
	{
		try 
		{
			save();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finish();
	}
	//保存
	private void save() throws JSONException, IOException 
	{
		if(C.myDetail==null)
			return;
		String userId=C.userId;
		String college=C.college;
		String campus=C.myDetail.getCampus();
		String school=C.myDetail.getSchool();
		String major=C.myDetail.getMajor();
		String location=C.location;
		int grade=C.myDetail.getGrade();
		
		File avatarfile;
		if(imgChanged)
			avatarfile=new File(imgFileLocation);
		else
		{
			avatarfile=new File(imgFileLocation);
			if(avatarfile.exists())
				avatarfile.delete();
			avatarfile.createNewFile();
			try
			{
				FileOutputStream out=new FileOutputStream(avatarfile);
				C.getBitmapFromMemCache(C.userId).compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
			}
			catch(Exception eee)
			{eee.printStackTrace();}
		}
		
		HashMap jsonObject=new HashMap();
		jsonObject.put("userId", userId);
		jsonObject.put("campus", campus);
		jsonObject.put("school", school);
		jsonObject.put("major", major);
		jsonObject.put("grade", grade);
		jsonObject.put("college", college);
		jsonObject.put("location", location);
		jsonObject.put("images", avatarfile);
		
		new PostTask(C.URLmodify_my_profile,jsonObject)
		{
			@Override
			protected void onPostExecute(JSONObject result) {
				try
				{
					if(result.getInt("status")==0)
						MineActivity.that.mHandler.sendEmptyMessage(0);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					MineActivity.that.mHandler.sendToast("更新个人信息错误");
				}
			}
			
		}.execute();		
	}
	
	//从学校信息返回值
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		switch(requestCode)
		{
		case 0://修改学校信息
			switch(resultCode)
			{
			case RESULT_OK:
				((TextView)findViewById(R.id.mine_detail_campus)).setText(C.myDetail.getCampus());
				((TextView)findViewById(R.id.mine_detail_school)).setText(C.myDetail.getSchool());
				((TextView)findViewById(R.id.mine_detail_major)).setText(C.myDetail.getMajor());
				((TextView)findViewById(R.id.mine_detail_grade)).setText(String.valueOf(C.myDetail.getGrade())+"级");
				((TextView)findViewById(R.id.mine_detail_location)).setText(C.location);
				break;
			case RESULT_CANCELED:
				break;
			default:
				break;	
			}
			break;
		case 1://照相
			if (resultCode != RESULT_OK) {
				return;
			}
			cropImageUri(imageUri, 2);
			break;
		case 2://本地照片
			if(resultCode==RESULT_CANCELED)
				return;
			if (picBitmap != null && !picBitmap.isRecycled()) {
				picBitmap.recycle();
			}
			picBitmap = decodeUriAsBitmap(imageUri);
			C.addBitmapToMemoryCache(C.userId, picBitmap);
			avatar.setImageBitmap(picBitmap);
			imgChanged=true;
			break;
		default:
			break;
		}
	}
	private void setDefaultValue()
	{
		HashMap req=new HashMap();
		req.put("userId", C.userId);
		new PostTask(C.URLget_my_profile,req)
		{
			@Override
			protected void onPostExecute(JSONObject res) {
				try
				{
					JSONObject result=res.getJSONObject("userDetails");
					List<Item> items=new ArrayList<Item>();
					JSONArray ja=result.getJSONArray("items");
					for(int i=0;i<ja.length();i++)
					{
						JSONObject o=ja.getJSONObject(i);
						items.add(new Item(o.getString("itemId")
								, o.getString("itemName")
								,(float) o.getDouble("price")
								, o.getString("details") 
								, o.getString("releaseTime")
								, o.getString("unshelveTime")
								, o.getString("categoryId")
								, null//Image[]
								, null));//seller
					}
					C.myDetail=new UserDetails(result.getString("detailId")
							,result.getString("campus")
							,result.getString("school")
							,result.getString("major")
							,result.getInt("grade")
							,result.getString("userId")
							,null//User
							,items);
					tv_campus.setText(C.myDetail.getCampus());
					tv_school.setText(C.myDetail.getSchool());
					tv_major.setText(C.myDetail.getMajor());
					tv_grade.setText(String.valueOf(C.myDetail.getGrade())+"级");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}.execute();
		
		((TextView)findViewById(R.id.mine_detail_name)).setText(C.userName);
		((TextView)findViewById(R.id.mine_detail_college)).setText(C.college);
		((TextView)findViewById(R.id.mine_detail_location)).setText(C.location);
		
		im_avatar=((ImageView)findViewById(R.id.img_mine_detail_avatar));
		
		if(C.getBitmapFromMemCache(C.userId)==null)
		{
			new Request4Image(C.avatar)
			{
				@Override
				protected void onPostExecute(Bitmap result) {
					if(result!=null)
					{
						((ImageView)findViewById(R.id.img_mine_detail_avatar)).setImageBitmap(result);
						C.addBitmapToMemoryCache(C.userId, result);
					}
				}
			}.execute();
		}
		else
			((ImageView)findViewById(R.id.img_mine_detail_avatar)).setImageBitmap(C.getBitmapFromMemCache(C.userId));
	}
	private void cropImageUri(Uri uri, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try 
		{
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
