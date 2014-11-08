package com.example.tx;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class UserDetailInfor extends BaseActivity {
	
	private String theUserId;
	
	private ImageView iv_udi;
	private TextView tv_nickname;
	private TextView tv_school;
	private TextView tv_xiaoqu;
	private TextView tv_xueyuan;
	private TextView tv_major;
	private TextView tv_grade;
	private TextView tv_detail_location;
	
	private String s_nickname , s_school , s_xiaoqu , s_xueyuan , s_major , s_grade , s_image , s_location;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_detail_infor);
		
		theUserId = getIntent().getExtras().getString("userId");
		mHandler = new MyHandler();
		
		iv_udi = (ImageView) findViewById(R.id.iv_udi);
		tv_nickname = (TextView) findViewById(R.id.tv_udi_nickname);
		tv_school = (TextView) findViewById(R.id.tv_udi_school);
		tv_xiaoqu = (TextView) findViewById(R.id.tv_udi_xiaoqu);
		tv_xueyuan = (TextView) findViewById(R.id.tv_udi_xueyuan);
		tv_major = (TextView) findViewById(R.id.tv_udi_major);
		tv_grade = (TextView) findViewById(R.id.tv_udi_grade);
		tv_detail_location = (TextView) findViewById(R.id.tv_detail_location);
		
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("个人信息");
		((ImageButton) findViewById(R.id.commen_title_bar_ret)).setVisibility(View.VISIBLE);
		((ImageButton) findViewById(R.id.commen_title_bar_ret)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				HashMap p = new HashMap();
				p.put("sellerId", theUserId);
				Log.d("iiii",".."+theUserId);
				JSONObject r = C.asyncPost(C.URLget_seller_details, p);
				Log.d("iiii","..");
				try {
					if(r.getInt("status") != 0){
						mHandler.sendEmptyMessage(0);
					}
					else {
						
						JSONObject res = r.getJSONObject("sellerDetails");
						JSONObject user = res.getJSONObject("user");
						s_nickname = user.getString("userName");
						s_school = user.getString("college");
						s_xiaoqu = res.getString("campus");
						s_xueyuan = res.getString("school");
						s_major = res.getString("major");
						s_grade = res.getString("grade");
						s_image = user.getString("avatar");
						s_location = user.getString("location");
						Log.d("iiii",s_nickname+","+s_school+","+s_xiaoqu+","+s_xueyuan+","+s_image);
						
						mHandler.sendEmptyMessage(1);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}).start();
		
		
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				makeToast("获取信息失败");
				break;
			case 1:
				tv_nickname.setText(s_nickname);
				tv_school.setText(s_school);
				tv_xiaoqu.setText(s_xiaoqu);
				tv_xueyuan.setText(s_xueyuan);
				tv_major.setText(s_major);
				tv_grade.setText(s_grade);
				tv_detail_location.setText(s_location);
				
				Bitmap b = null;//C.getBitmapFromMemCache(s_image);
				if (b == null) {
					iv_udi.setImageResource(R.drawable.item_pic_default);
		
					//根据url获取图片
					new Request4Image(s_image) 
					{
						@Override
						protected void onPostExecute(Bitmap result) 
						{
							if(result==null) 
								return;
							//C.addBitmapToMemoryCache(id, result);
							iv_udi.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
							//notifyDataSetChanged();
						}
					}.execute();
		
							
				} else {
					iv_udi.setImageBitmap(b);
				}
				
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_detail_infor, menu);
		return true;
	}

}
