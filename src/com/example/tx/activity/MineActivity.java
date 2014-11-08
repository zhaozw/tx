package com.example.tx.activity;

import java.util.HashMap;



import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.example.tx.activity.ForgetActivity;

import com.example.tx.MineOrderActivity;

import com.example.tx.R;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ImageView;

public class MineActivity extends BaseActivity implements OnClickListener{
	
	private boolean check_remenber=false,check_autolog=false;
	private SharedPreferences sp;
	public static MineActivity that;
	private static HashMap result;
	private String account,password;
	private Bitmap bmAvatar;
	//旧版注册协议
//	private static String bargin="此App最终用户需遵从以下条款：\n"
//								+"1、不得在App中发布任何虚假商品信息。\n"
//								+"2、绝对不允许在App中发布任何不良信息（包含暴力、色情等信息）。\n"
//								+"3、不得发表任何不良言论（包含色情类）。\n"
//								+"4、不得在App中发布任何广告信息。\n"
//								+"5、用户只有同意以上条款，才能使用此App。\n"
//								+"6、如发现任何违反以上条款的行为的用户，将删除此用户的违规内容，严重者将可能导致封号。\n";
	
	private boolean refresh = false;  //自动登陆，刚开始时先设置页面，并未刷新信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		that=this;
		
		mHandler=new MyHandler();
		
		sp=getSharedPreferences("TaoxuePref", MODE_PRIVATE);
		
		check_remenber=sp.getBoolean("check_remenber",false);
		check_autolog=sp.getBoolean("check_autolog", false);
		
		if(C.logged)
		{
			refresh = false;
			setViewLogged();
			HashMap req=new HashMap();
			try
			{
				account=sp.getString("account", "");
				password=sp.getString("password", "");
				req.put("account", account);
				req.put("password",password);
			}
			catch(Exception e)
			{
				makeToast("后台错误");
				e.printStackTrace();
			}
			makeToast("正在刷新用户信息");
			new MyLogin(C.URLlogin, req).execute();
		}
		else
			setViewLogin();			
	}
	private void setViewLogin()
	{
		setContentView(R.layout.activity_mine_login);
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("登录");
		ImageButton ret=(ImageButton) findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.that.radio0.setChecked(true);
			}
		});
		TextView register=(TextView)findViewById(R.id.common_title_bar_login);
		register.setVisibility(View.VISIBLE);
		register.setText("注册");
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog
				.Builder(MineActivity.this)
				.setTitle("用户协议")
				.setMessage((String)(getResources().getString(R.string.bargin)))
				.setNegativeButton("拒绝",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.setPositiveButton("接受", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						Intent it=new Intent(MineActivity.this,RegisterActivity.class);
						startActivityForResult(it, 0);
					}
				}).show();
			}
		});
		
		final Switch swt_remember=(Switch) findViewById(R.id.sw_mine_remember);
		swt_remember.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked)
				{
					check_remenber=true;
					
					Editor editor=sp.edit();
					editor.putBoolean("check_remenber", true);
					editor.commit();
					
				}
				else
				{
					check_remenber=false;
					Editor editor=sp.edit();
					editor.putBoolean("check_remenber", false);
					editor.commit();
				}
			}
			
		});
		swt_remember.setChecked(check_remenber);
		
		Switch swt_autolog=(Switch) findViewById(R.id.sw_mine_autolog);
		swt_autolog.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked)
				{
					check_autolog=true;
					check_remenber=true;
					swt_remember.setChecked(true);
					swt_remember.setEnabled(false);
					
					
					Editor editor=sp.edit();
					editor.putBoolean("check_autolog", true);
					editor.commit();
				}
				else
				{
					check_autolog=false;
					swt_remember.setEnabled(true);
					
					Editor editor=sp.edit();
					editor.putBoolean("check_autolog", false);
					editor.commit();
				}
			}
			
		});
		swt_autolog.setChecked(check_autolog);
		
		TextView forgot = (TextView) findViewById(R.id.tv_mine_forgot);
		forgot.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MineActivity.this,ForgetActivity.class));
			}
		});
		
		//历史数据不为空，将历史数据赋值到输入框内
		if(sp!=null)
		{
			((TextView)findViewById(R.id.et_mine_telephone)).setText(sp.getString("account", ""));
			if(sp.getBoolean("check_remenber",false))
				((TextView)findViewById(R.id.et_mine_password)).setText(sp.getString("password", ""));
		}
		Button login = (Button) findViewById(R.id.btn_mine_login);
		login.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				account=((TextView)findViewById(R.id.et_mine_telephone)).getText().toString();
				password=((TextView)findViewById(R.id.et_mine_password)).getText().toString();
				
				HashMap o=new HashMap();
				try 
				{
					o.put("account",account);
					o.put("password", password);
					makeToast("正在登陆...");
					new MyLogin(C.URLlogin,o).execute();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});		
	}
	public class MyLogin extends PostTask
	{
		public MyLogin(String toUrl, HashMap content) {
			super(toUrl, content);
		}

		@Override
		protected void onPostExecute(JSONObject res) {
			try
			{
				if(res.getInt("status")==0)
				{
					JSONObject r=res.getJSONObject("user");
					result=new HashMap();
					result.put("userId", r.getString("userId"));
					result.put("account", r.getString("account"));
					result.put("userName", r.getString("userName"));
					result.put("college", r.getString("college"));
					result.put("location", r.getString("location"));
					result.put("avatar", r.getString("avatar"));					
					
					C.logged=true;
					C.account=(String) result.get("account");
					C.userId=(String) result.get("userId");
					C.userName=(String) result.get("userName");
					C.college=(String) result.get("college");
					C.location=(String) result.get("location");
					C.avatar=(String) result.get("avatar");
					
					//刷新消息
					//MessageActivity.mHandler.sendEmptyMessage(1);
					
					Editor editor=sp.edit();
					
					editor.putString("account",account);
					editor.putString("userId", C.userId);
					editor.putString("username", C.userName);
					editor.putString("locaion",C.location);
					editor.putBoolean("check_remenber", check_remenber);
					editor.putBoolean("check_autolog", check_autolog);
					if(check_remenber)
					{
						editor.putString("password", password);
					}
					else
					{
						editor.putString("password", "");
					}
					editor.commit();
					refresh = true;
					setViewLogged();
					
					//登陆后获取用户消息个数并传给mainactivity
					//MainActivity.that.mHandler.sendEmptyMessage(1);
					
					
				}
				else
				{
					makeToast(res.getString("description"));
					setViewLogin();
					((TextView)findViewById(R.id.et_mine_telephone)).setText(account);
					((TextView)findViewById(R.id.et_mine_password)).setText(password);
					((Switch)findViewById(R.id.sw_mine_autolog)).setChecked(check_autolog);
					((Switch)findViewById(R.id.sw_mine_remember)).setChecked(check_remenber);
				}
					
			}
			catch(Exception e)
			{
				makeToast("验证出错，连接超时");
				e.printStackTrace();
				setViewLogin();			
			}
		}
	}
	private void setViewLogged()
	{
		setContentView(R.layout.activity_mine_logged);
		
		mHandler.sendEmptyMessage(81);
		
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("我");
		
		if(refresh){                 //如果刷新了信息，再进入设置内容
		
			((TextView)findViewById(R.id.txt_mine_name)).setText(C.userName);
			
			bmAvatar=C.getBitmapFromMemCache(C.userId);
			if(bmAvatar==null)
			{
				makeToast("正在刷新用户头像");
				new Request4Image(C.avatar)
				{
					@Override
					protected void onPostExecute(Bitmap result) {
						if(result!=null)
						{
							((ImageView)findViewById(R.id.img_mine_avatar))	.setImageBitmap(result);
							C.addBitmapToMemoryCache(C.userId, result);
						}
					}
				}.execute();
			}
			else
			{
				((ImageView)findViewById(R.id.img_mine_avatar))	.setImageBitmap(bmAvatar);
				new Request4Image(C.avatar)
				{

					@Override
					protected void onPostExecute(Bitmap result) {
						if(result!=null)
						{
							((ImageView)findViewById(R.id.img_mine_avatar))	.setImageBitmap(result);
							C.addBitmapToMemoryCache(C.userId, result);
						}
						
					}
					
				}.execute();
			}
		}
		
		((LinearLayout) findViewById(R.id.mine_bar)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.mine_order_bar)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.mine_commodity_bar)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.mine_seller_bar)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.mine_favourite_bar)).setOnClickListener(this);
//		((LinearLayout) findViewById(R.id.mine_order_bar)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.setting)).setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) 
	{
		int id=v.getId();
		switch(id)
		{
		case R.id.mine_bar:
			startActivityForResult(new Intent(this,MineDetailActivity.class), 1);
			break;
		case R.id.mine_commodity_bar:
			startActivity(new Intent(this,MineCommodityActivity.class));
			break;
		case R.id.mine_seller_bar:
			startActivity(new Intent(this,MineSellerActivity.class));
			break;
		case R.id.mine_favourite_bar:
			Intent it=new Intent(this,MineCommodityDetailActivity.class);
			it.putExtra("from", 1);
			startActivity(it);
			break;
		case R.id.setting:
			setViewSetting();
			break;
		case R.id.mine_order_bar:
			Intent it1=new Intent(this,MineOrderActivity.class);
			startActivity(it1);
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==0)
		{
			if(resultCode==RESULT_OK)
			{
				makeToast("注册成功,正在跳转");
				refresh = true;
				setViewLogged();
			}
		}
	}
	private void setViewSetting()
	{
		Intent it=new Intent(MineActivity.this,SettingActivity.class);
		startActivity(it);
	}
	public static MyHandler mHandler;
	class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			case 0://从“我”的详细页面返回换头像
				((ImageView)findViewById(R.id.img_mine_avatar)).setImageBitmap(C.getBitmapFromMemCache(C.userId));
				break;
			case 1://设置页面注销
				setViewLogin();
				break;
			case 81://设置Jpush的别名
				JPushInterface.setAlias(getBaseContext(), C.userId,mTagsCallback);
				Log.d("Jpush-UserId",C.userId);
				break;
			case 99:
				String message=(String)msg.obj;
				makeToast(message);
				break;
			}
		}
		public void sendToast(String message)
		{
			Message msg=mHandler.obtainMessage();
			msg.what=99;
			msg.obj=message;
			mHandler.sendMessage(msg);
		}
		
		private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

	        @Override
	        public void gotResult(int code, String alias, Set<String> tags) {
	            String logs ;
	            switch (code) {
	            case 0:
	                logs = "Set tag and alias success :"+C.account;
	                Log.i("Jpush", logs);
	                break;
	                
	            case 6002:
	                logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
	                Log.i("Jpush", logs);
	                
	                break;
	            
	            default:
	                logs = "Failed with errorCode = " + code;
	                Log.e("Jpush","logs");
	            }
	        }
	        
	    };
	}
}
