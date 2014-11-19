package com.example.tx.activity;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.adapter.CategoryMarketAdapter;
import com.example.tx.adapter.MarketListAdapter;
import com.example.tx.dto.Category;
import com.example.tx.dto.Categorys;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;
import com.example.tx.view.MyGridView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class MarketActivity extends BaseActivity implements ViewFactory, OnTouchListener {
	
	//市场顶部的图片切换器
	private ImageSwitcher is_markettop;
	
	private Thread thread;
	
	private float downX;
	//切换的图片ID
    //private int[] imgIds;  
    //选中的当前图片序号
    private int currentPosition;  
    //装载点点的容器 
    private LinearLayout ll_viewgroup;  
    //点点数组  
    static private ImageView[] tips;
    private Bitmap[] bitmaps;
    
    //gridview
    private MyGridView gv_category;
    private List<Category> msgs;

	private boolean needRefresh;
	
	private Bitmap[] topBitmaps;
	private Bitmap default_bitmap;
	private String urls[];
	private int bitcount = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market);
		
		//Init
		is_markettop = (ImageSwitcher)findViewById(R.id.is_markettop);
		ll_viewgroup = (LinearLayout)findViewById(R.id.ll_viewGroup);
		
//		RelativeLayout rl=(RelativeLayout)findViewById(R.id.rl_markettop);
//		ll_viewgroup.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,ll_viewgroup.getLayoutParams().width*2/5));

		Drawable d= getResources().getDrawable(R.drawable.loading); //xxx根据自己的情况获取drawable
		BitmapDrawable bd = (BitmapDrawable) d;
		default_bitmap = bd.getBitmap();
		
		msgs = new ArrayList<Category>();
		
		//绑定imageswitch的事件及工厂
		is_markettop.setFactory(this);
		is_markettop.setOnTouchListener(this);
		
		//imgIds = new int[]{R.drawable.i2 , R.drawable.i1 , R.drawable.i3};
		
		
		
		//初始设置imageswitch和ll_viewgroup
		currentPosition = 0;
		is_markettop.setImageResource(R.drawable.loading);
//		is_markettop.setImageResource(R.drawable.loading);
//		is_markettop.setImageResource(R.drawable.loading);
		

		((ImageButton) findViewById(R.id.ib_search)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MarketActivity.this , SearchActivity.class);
				startActivity(intent);
			}
			
		});
		
		gv_category = (MyGridView) findViewById(R.id.GV_category);
		//点击市场中部商品列表里面的选项？
		gv_category.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index,
					long id) {
				Intent intent = new Intent(MarketActivity.this,GoodslistActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", (String)msgs.get(index).name);
				bundle.putString("categoryid", (String)msgs.get(index).id);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
		
		
		mHandler = new MyHandler();
		new SwitchImageThread().start();
		new SetDataListThread().start();

	}

	private void setViewgroupBackground(int selectItems) {
		for(int i=0; i<tips.length; i++){    
            if(i == selectItems){    
                tips[i].setBackgroundResource(R.drawable.point_focused);    
            }else{    
                tips[i].setBackgroundResource(R.drawable.point_unfocused);    
            }
        }    
	}
	
	private void switchRight(){
		if(currentPosition < bitcount - 1){  
        	is_markettop.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
        	is_markettop.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
            currentPosition ++ ;  
            setdrawable(); 
        }else{  
        	is_markettop.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
            is_markettop.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
            currentPosition = 0;  
            setdrawable();
        }
	}

	@SuppressWarnings("deprecation")
	@Override
	//设置图片显示比例
	public View makeView() {
		final ImageView i = new ImageView(this);  
        i.setBackgroundColor(0xff000000);  
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);  
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
        return i; 
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:{  
            //手指按下的X坐标  
            downX = event.getX();  
            break;  
        }  
        case MotionEvent.ACTION_UP:{  
            float lastX = event.getX();  
            //抬起的时候的X坐标大于按下的时候就显示上一张图片  
            if(lastX > downX){  
                if(currentPosition > 0){   
                    is_markettop.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));  
                    is_markettop.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));  
                    currentPosition --;  
                    setdrawable();
                }else{  
                	is_markettop.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));  
                    is_markettop.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));  
                    currentPosition = bitcount - 1;    //循环显示图片，这个currentPosition记录的是图片的编号
                    setdrawable();
                }  
            }   
              
            if(lastX < downX){    //下一张图片
                if(currentPosition < bitcount - 1){  
                	is_markettop.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
                	is_markettop.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
                    currentPosition ++ ;  
                    setdrawable();
                }else{  
                	is_markettop.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
                    is_markettop.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
                    currentPosition = 0;  //刚才显示的是最后一张
                    setdrawable();
                }  
            }  
            }  
              
            break;  
        }  
  
        return true;  
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				//Log.d("getcategory", "hander");
				gv_category.setAdapter(new CategoryMarketAdapter(MarketActivity.this, msgs ,gv_category));
				break;
				
			case 1:
				new SetDataListThread().start();
				break;
				
			case 3:
				switchRight();
				
				break;
			case 44:
				//填充imageswitcher的图片
				tips = new ImageView[bitcount];
				for(int i=0; i<bitcount; i++){  
		            ImageView mImageView = new ImageView(MarketActivity.this);  
		            tips[i] = mImageView;  
		            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		           //左右边距
		            layoutParams.rightMargin = 3;  
		            layoutParams.leftMargin = 3;  
		              
		            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
		            ll_viewgroup.addView(mImageView, layoutParams);  
		        } 
				setViewgroupBackground(currentPosition);
				
				new setBitmapsThread(urls).start();
				
				break;
				
				
			case 99:
				String toast = (String) msg.obj;
				makeToast(toast);
				break;

			default:
				break;
			}
		}
		
		public void sendToast(String toast) {
			Message toastMessage = mHandler.obtainMessage();
			toastMessage.obj = toast;
			toastMessage.what = 99;
			mHandler.sendMessage(toastMessage);
			return ;
		}
		
	};
	
	private class SwitchImageThread extends Thread{

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				//Log.d("switch","right");
				
				try {
					Thread.sleep(4000);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(3);
			}
		}
		
	}
	

	private class SetDataListThread extends Thread {
		
		@Override
		public synchronized void run() {
			
			try {
				//获取主页的三个图片
				//Log.d("gethomepageimages", "startgetimages");
				HashMap hm = new HashMap();
				JSONObject ret = C.asyncPost(C.URLget_homepage_images, hm);
				if(!(ret.getInt("status") == 0) ){
					mHandler.sendToast("网络有问题！");
					return ;
				}
				JSONArray images = ret.getJSONArray("homePageImages");
				bitcount = images.length();
				urls = new String[bitcount];
				bitmaps = new Bitmap[bitcount];
				
				for(int i = 0 ; i < bitcount ; i ++){
					bitmaps[i] = default_bitmap;
				}
				
				topBitmaps = new Bitmap[bitcount];
				for(int i = 0 ; i < bitcount ; i ++){
					urls[i] = images.getString(i);
				}
				
				
				
				mHandler.sendEmptyMessage(44);
				
				
				//获取分类信息
				//Log.d("getcategory","startget");
				HashMap p = new HashMap();
				JSONObject res = C.asyncPost(C.URLget_categories, p);
				if( !(res.getInt("status") == 0) ) {
					mHandler.sendToast("网络有问题！");
					return ;
				}
				
				JSONArray categories = res.getJSONArray("categories");
				
				if(Categorys.categorys != null)
					Categorys.categorys.clear();
				for(int i=0;i<categories.length();i++) {
					JSONObject category = categories.getJSONObject(i);
					
					msgs.add(new Category(
							null,
							category.getString("id"),
							category.getString("name"),
							category.getString("image")
							));
					
					//Log.d("getcategory",category.getString("name")+category.getString("image"));
					
					//把种类信息保存起来
					
					Categorys.categorys.add(new Category(
							null,
							category.getString("id"),
							category.getString("name"),
							category.getString("image")
							));
					
					
				}	
				mHandler.sendEmptyMessage(0);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Log.d("getcategory","send");
			//mHandler.sendEmptyMessage(0);
		}
		
	}
	
	//设置bitmaps内容的线程
		private class setBitmapsThread extends Thread{
			String[] picUrls;
			public setBitmapsThread(String[] picUrls){
				this.picUrls = picUrls;
			}
			@Override
			public void run() {
				for(int i = 0 ; i < picUrls.length ; i ++){
					
					new Request4Image(picUrls[i]) 
					{
						@Override
						protected void onPostExecute(Bitmap result) 
						{
							if(result==null) 
								return;
							//cachebitmap = result;
							for(int j = 0 ;j < picUrls.length ; j ++){
								if(bitmaps[j].equals(default_bitmap)){
									bitmaps[j] = result;
									break;
								}
							}
							
						}
					}.execute();
					
					//bitmaps[i] = cachebitmap;
				}
				
			}
			
			
		}
	
	public void setdrawable(){
		if(bitmaps == null){
			makeToast("请检查网络连接！");
			return;
		}
		//Log.d("setdrawable","ok---"+currentPosition+"---"+bitmaps.length);
		if(bitmaps[currentPosition % bitmaps.length] == null)
			is_markettop.setImageDrawable(DrawableFromBitmap(default_bitmap));
		is_markettop.setImageDrawable(DrawableFromBitmap(bitmaps[currentPosition % bitmaps.length]));
		 setViewgroupBackground(currentPosition);
	}
	
	public Drawable DrawableFromBitmap(Bitmap bitmap) {
		if(bitmap == null)
			Log.d("setdrawable","null");
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		// 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
		return bd;
		}


	@Override
	protected void onResume() {
		super.onResume();
		
		if(needRefresh) {
			needRefresh = false;
			new SetDataListThread().start();
			//Log.v("Market", "Refresh");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	

}
