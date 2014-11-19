package com.example.tx.activity;

import java.util.HashMap;

import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.MsgValidation;
import com.example.tx.util.PostTask;

import android.app.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class ForgetActivity extends BaseActivity {

	boolean check_passwordCfm=false,v_valicode=false,v_account=false;
	static boolean cansend_msg=true;
	String acc=null,valicode;
	TextView tv_account,tv_password,tv_password_cfm,tv_complete,tv_valicode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forget);
		
		//公共框
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("忘记密码");
		ImageButton ret=(ImageButton)findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		tv_account=(TextView)findViewById(R.id.forget_account);
		tv_complete=(TextView) findViewById(R.id.common_title_bar_login);
		tv_password=(TextView)findViewById(R.id.forget_password);
		tv_password_cfm=(TextView)findViewById(R.id.forget_password_cfm);
		tv_valicode=(TextView)findViewById(R.id.forget_valicode);
		
		tv_account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					HashMap req=new HashMap();
					String account=tv_account.getText().toString();
					if(account.trim().equals(""))
					{
						makeToast("请输入正确的账户");
						v_account=false;
						return;
					}	
					req.put("account", tv_account.getText().toString());
					new PostTask(C.URLcheck_account,req)
					{
						@Override
						protected void onPostExecute(JSONObject result) {
							try
							{
								if(result.getInt("status")==0)
								{
									makeToast("账号不存在");
									v_account=false;
								}
								else
									v_account=true;
							}
							catch(Exception e)
							{
								makeToast("后台错误");
							}
						}
					}.execute();
				}
			}
		});
		
		tv_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if(((TextView)v).getText().toString().length()<7)
						makeToast("请输入6位以上密码");
					
				}
			}
		});
		
		tv_complete.setVisibility(View.VISIBLE);
		tv_complete.setText("完成");
		tv_complete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v_account)
				{
					if(check_passwordCfm)
					{
						if(v_valicode)
						{
							String account=tv_account.getText().toString();
							String password=tv_password.getText().toString();
							HashMap map=new HashMap();
							map.put("account", account);
							map.put("password", password);
							if(password.length() < 7){
								makeToast("密码至少为6位");
								return;
							}
							makeToast("请求已发送...");
							new PostTask(C.URLreset_password,map)
							{
								@Override
								protected void onPostExecute(JSONObject result) {
									try
									{
										if(result.getInt("status")!=0)
										{
											makeToast("修改密码成功");
											finish();
										}
										else
											makeToast("重置失败："+result.getString("description"));
									}
									catch(Exception e)
									{
										e.printStackTrace();
										makeToast("后台错误");
									}
								}
							}.execute();
						}
						else
							makeToast("请输入正确的验证码");
					}
					else
						makeToast("两次输入密码不一致");
				}
				else
					makeToast("请输入正确的用户名");
			}
		});
		
		tv_password_cfm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					String pwd=((TextView)findViewById(R.id.forget_password)).getText().toString();
					String pwd_cfm=((TextView)v).getText().toString();
					if(!pwd.equals(pwd_cfm))
					{
						makeToast("两次输入密码不一致，请重新输入");
						check_passwordCfm=false;
						return;
					}
					check_passwordCfm=true;
				}
			}
		});
		
		((TextView)findViewById(R.id.forget_send_valicode)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!(acc=((TextView)findViewById(R.id.forget_account)).getText().toString()).equals("")&&acc!=null)
				{
					if(cansend_msg)
					{
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
						makeToast("验证码已发送");
						new MsgValidation(acc)
						{
							@Override
							protected void onPostExecute(JSONObject result) {
								try
								{
									if(result.getInt("status")==0)
									{
										valicode=result.getString("valicode");
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
						makeToast("60秒后重新发送");
				}
				else
					makeToast("请先输入用户名");
			}
		});
		
		tv_valicode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
				{
					if((((TextView)v).getText().toString()).equals(valicode))
						v_valicode=true;
					else
						v_valicode=false;
				}
			}
		});

	}
}
