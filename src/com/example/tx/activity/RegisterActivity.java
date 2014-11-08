package com.example.tx.activity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.R.id;
import com.example.tx.R.layout;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.MsgValidation;
import com.example.tx.util.PostTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity implements OnFocusChangeListener,OnClickListener{

	boolean v_account=false,v_password=false,v_name=false,v_valicode=false,cansend_msg=true,v_location=false;
	String str_ac;
	String str_pw;
	TextView account,name,passwordCfm,password,school,location,valicode;
	String validationCode=null,text_validationCode=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("注册");
		ImageButton ret=(ImageButton) findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		TextView complete=(TextView) findViewById(R.id.common_title_bar_login);
		complete.setText("完成");
		complete.setVisibility(View.VISIBLE);
		complete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				valicode.clearFocus();
				account.clearFocus();
				name.clearFocus();
				passwordCfm.clearFocus();
				password.clearFocus();
				school.clearFocus();
				location.clearFocus();
				if(v_account)
				{
					if(v_name)
					{
						if(v_password)
						{
							if(v_location){
							if(v_valicode)
							{
								try
								{
									HashMap req=new HashMap();
									str_ac=account.getText().toString();
									str_pw=password.getText().toString();
									req.put("account", str_ac);
									req.put("userName", name.getText().toString());
									req.put("password", str_pw);
									req.put("college", school.getText().toString());
									req.put("location", location.getText().toString());
									new PostTask(C.URLregister,req)
									{
										@Override
										protected void onPostExecute(JSONObject result) {
											try
											{
												if(result.getInt("status")==0)
												{
													makeToast("注册成功");
													JSONObject user=result.getJSONObject("user");
													C.userId=user.getString("userId");
													C.account=user.getString("account");
													C.userName=user.getString("userName");
													C.college=user.getString("location");
													C.avatar=user.getString("avatar");
													C.logged = true;
													setResult(RESULT_OK);
													finish();
												}
												else
													makeToast("注册失败:"+result.getString("description"));
											}catch(Exception e)
											{
												e.printStackTrace();
												makeToast("注册失败，请求超时");
											}
										}
									}.execute();
								}
								catch(Exception e)
								{
									makeToast("后台错误");
									e.printStackTrace();
								}
							}
							else
								makeToast("验证码输入不正确");
							}
							else
								makeToast("请输入地址");
						}
						else
							makeToast("两次输入的密码不同，请重新输入");
					}
					else
						makeToast("用户昵称不符合规范，请重新输入");
				}
				else
					makeToast("请输入正确的用户名");
			}
		});
		
		account=(TextView)findViewById(R.id.register_account);
		name=(TextView)findViewById(R.id.register_nickname);
		password=(TextView)findViewById(R.id.register_password);
		passwordCfm=(TextView)findViewById(R.id.register_password_cfm);
		school=(TextView)findViewById(R.id.register_school);
		location=(TextView)findViewById(R.id.register_location);
		valicode=(TextView)findViewById(R.id.register_valicode);
		
		school.setEnabled(false);
		school.setText("北京理工大学");
		
		account.setOnFocusChangeListener(this);
		name.setOnFocusChangeListener(this);
		passwordCfm.setOnFocusChangeListener(this);
		valicode.setOnFocusChangeListener(this);
		location.setOnFocusChangeListener(this);
		((TextView)findViewById(R.id.register_send_valicode)).setOnClickListener(this);
		
		
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(!hasFocus)
		{
			switch(v.getId())
			{
			case R.id.register_account:
				try
				{
					HashMap req=new HashMap();
					req.put("account", account.getText().toString());
					new PostTask(C.URLcheck_account,req)
					{
						@Override
						protected void onPostExecute(JSONObject result) {
							try 
							{
								if(result.getInt("status")==0)
								{
									v_account=true;
								}
								else
								{
									v_account=false;
									v_valicode=false;
									if(result.getString("description")!=null&&result.getString("description")!="")
										makeToast("账号验证错误"+result.getString("description"));
									else
										makeToast("连接超时");								
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
					}.execute();
					break;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				break;
			case R.id.register_nickname:	
				if(!isValidString(name.getText().toString())){
					v_name = false;
					makeToast("用户昵称只能存在中英文");
					break;
				}
				if(name.getText().toString().length() > 20){
					v_name = false;
					makeToast("用户昵称需要在20个字符以内");
					break;
				}
				HashMap map=new HashMap();
				map.put("userName", name.getText().toString());
				JSONObject res=C.asyncPost(C.URLcheck_username, map);
				try
				{
					if(res.getInt("status")==0)
					{
						v_name=true;
					}
					else
					{
						v_name=false;
						makeToast("用户名验证错误："+res.getString("description"));
					}
				}
				catch(Exception e)
				{
					makeToast("后台错误");
					e.printStackTrace();
				}
				break;
			case R.id.register_password:
				break;
			case R.id.register_password_cfm:
				if((password.getText().toString()).toString().equals(passwordCfm.getText().toString()))
				{
					if(password.getText().toString().length() < 6){
						v_password = false;
						makeToast("密码至少有6位字符");
					}
					else if(password.getText().toString().length() > 20){
						v_password = false;
						makeToast("密码在20字符以内");
					}
					v_password=true;
				}
				else
				{
					v_password=false;
					makeToast("两次输入的密码不同，请重新输入");
				}
				break;
			case R.id.register_valicode:
				text_validationCode=valicode.getText().toString();
				validate();
				break;
			case R.id.register_location:
				if(location.getText().toString().length() == 0){
					v_location = false;
				}
				else{
					v_location = true;
				}
			}
		}
	}
	private void validate()
	{
		if(validationCode!=null&&text_validationCode!=null&&validationCode.equals(text_validationCode))
			v_valicode=true;
		else
			v_valicode=false;
	}
	
	 // 校验  只能是英文字母和中文
    public boolean isValidString(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA5a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }
	
	@Override
	public void onClick(View v) {
		try
		{
			if(cansend_msg)
			{
				if(v_account)
				{
					String acc=account.getText().toString();
					makeToast("验证码已发送");
					cansend_msg=false;
					new Thread()
					{
						@Override
						public synchronized void run()
						{
							try {
								sleep(60000);
								cansend_msg=true;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}.start();
					new MsgValidation(acc)
					{
						@Override
						protected void onPostExecute(JSONObject result) {
							try
							{
								if(result.getInt("status")==0)
								{
									validationCode=result.getString("valicode");
									validate();
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
								makeToast("验证码发送错误");
							}
						}
					}.execute();
				}
				else
				{
					v_valicode=false;
					makeToast("请先输入正确的用户名");
				}
			}
			else
				makeToast("等待60秒后再发送验证码");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
