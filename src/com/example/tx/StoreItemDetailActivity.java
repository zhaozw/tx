package com.example.tx;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.activity.MainActivity;
import com.example.tx.activity.StoreDetailActivity;
import com.example.tx.adapter.CommentListAdapter;
import com.example.tx.dto.Comment;
import com.example.tx.dto.Image;
import com.example.tx.dto.ShopItem;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;
import com.example.tx.view.ListViewInsideScrollView;
import com.example.tx.view.ResizeLayout;
import com.example.tx.view.ResizeLayout.OnResizeListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;

public class StoreItemDetailActivity extends BaseActivity implements OnResizeListener ,ViewFactory, OnTouchListener {
	
	private static final int BIGGER = 100;
	private static final int SMALLER = 101;
	
	//控件
	private ResizeLayout resizeLayout_storeitemdetail;      //根据最外部的长度判断输入框的状态
	private ImageSwitcher is_storeitem_pic;                 //顶部图片栏
	private LinearLayout ll_storeitem_viewGroup;            //右下角小点点的容器
	private TextView tv_storeitem_name;                     //商品名
	private TextView tv_storeitem_price;                    //商品价格
	private TextView tv_storeitem_intro;                    //商品简介
	private ListViewInsideScrollView lv_storeitem_comment;  //评论列表
	private LinearLayout ll_itemcomment_empty;              //无评论的替换view
	private EditText et_storeitem_comment;                  //输入评论的框
	
	//点点数组  
    private ImageView[] tips;
    private Bitmap[] bitmaps;
	private int bitcount;
	private Bitmap default_bitmap;
	private Bitmap cachebitmap;
	private float downX;
	private int currentPosition = 0;

	//
	private ShopItem theShopitem;
	private ArrayList<Comment> msgs;
	
	private String refcontent = "";
	private String refId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_item_detail);
		
		mHandler = new MyHandler();
		
		//绑定控件
		resizeLayout_storeitemdetail = (ResizeLayout) findViewById(R.id.resizeLayout_storeitemdetail);
		is_storeitem_pic = (ImageSwitcher) findViewById(R.id.is_storeitem_pic);
		ll_storeitem_viewGroup = (LinearLayout)findViewById(R.id.ll_storeitem_viewGroup);
		tv_storeitem_name = (TextView)findViewById(R.id.tv_storeitem_name);
		tv_storeitem_price = (TextView)findViewById(R.id.tv_storeitem_price);
		tv_storeitem_intro = (TextView)findViewById(R.id.tv_storeitem_intro);
		lv_storeitem_comment = (ListViewInsideScrollView)findViewById(R.id.lv_storeitem_comment);
		ll_itemcomment_empty = (LinearLayout)findViewById(R.id.ll_itemcomment_empty);
		et_storeitem_comment = (EditText)findViewById(R.id.et_storeitem_comment);
		
		Drawable d= getResources().getDrawable(R.drawable.default_image); //xxx根据自己的情况获取drawable
		BitmapDrawable bd = (BitmapDrawable) d;
		default_bitmap = bd.getBitmap();
		
		//设置监听
		resizeLayout_storeitemdetail.setOnResizeListener(this);
		//设置工厂
		is_storeitem_pic.setFactory(this);
		is_storeitem_pic.setOnTouchListener(this);
		is_storeitem_pic.setImageDrawable(DrawableFromBitmap(default_bitmap));
		
		//判断从那个页面跳转的，如果是从消息页面，则从网上获取theshopitem
		//从列表页面进来的直接设置，从消息页面进来的需要先获取再通过handler设置
		Intent intent = getIntent();
		int from = intent.getIntExtra("from", -1);
		if(from == -1){
			theShopitem = StoreDetailActivity.that.theShopitem;
			//设置一些值
			tv_storeitem_name.setText(theShopitem.itemName);
			tv_storeitem_price.setText("￥"+String.valueOf(theShopitem.price));
			tv_storeitem_intro.setText(theShopitem.details);
			bitcount = theShopitem.images.length;
			bitmaps = new Bitmap[bitcount];
			String[] urls = new String[bitcount];
			for(int i1 = 0 ; i1 < bitcount ; i1 ++){
				bitmaps[i1] = default_bitmap;
				urls[i1] = theShopitem.images[i1].picUrl;
			}
			tips = new ImageView[bitcount];
			for(int i=0; i<bitcount; i++){  
	            ImageView mImageView = new ImageView(this);  
	            tips[i] = mImageView;  
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams.rightMargin = 3;  
	            layoutParams.leftMargin = 3;  
	              
	            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
	            ll_storeitem_viewGroup.addView(mImageView, layoutParams);  
	        }
			cheakNoImage();
			new setBitmapsThread(urls).start();
			new SetDataListThread().start();
		}
		else
			new GetItemDataThread(intent.getStringExtra("redId")).start();
		
		((ImageButton) findViewById(R.id.ib_back_storeitemdetail)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		//提交评论
		et_storeitem_comment.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER &&
						event.getAction() == KeyEvent.ACTION_DOWN){
					String comment = et_storeitem_comment.getText().toString();
					if(comment.equals("")){
						makeToast("请输入内容后评论！");
						return false;
					}
					if(C.logged == false){
						makeToast("请登录后再评论！");
						return false;
					}
					
					((EditText) v).setText("");
					
					HashMap p = new HashMap();
					try {
						p.put("itemId", theShopitem.itemId);
						p.put("content", refcontent+comment);
						p.put("refId", refId);
						if (C.logged) {
							p.put("userId", C.userId);
						}
						
						Log.d("itemdetail",theShopitem.itemId+","+comment+","+refId+"=="+C.userId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					makeToast("正在提交评论");
					
					new PostTask(C.URLwrite_item_comment, p)
					{
						@Override
						protected void onPostExecute(JSONObject res) {
							// TODO 自动生成的方法存根
							try {
								Log.d("itemdetail",res.getInt("status")+",,"+res.getString("description"));
								if(res.getInt("status") != 0){
									makeToast("评论失败");
									new SetDataListThread().start();
									return;
								}
								refId = "";//成功后把引用的Id置为空
								refcontent = "";
								//makeToast(res.getString("description"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							new SetDataListThread().start();
							MainActivity.mHandler.sendEmptyMessage(1);
						}
						
					}.execute();
					return true;
				}
				return false;
			}
			
		});
		
		lv_storeitem_comment.setEmptyView(ll_itemcomment_empty);
		lv_storeitem_comment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String talker = msgs.get(position).maker.userName;
				refId = msgs.get(position).commentId;
				refcontent = "回复" + talker + ":  ";
				et_storeitem_comment.setHint("回复" + talker + ":  ");
				et_storeitem_comment.clearFocus();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.store_item_detail, menu);
		return true;
	}
	
	public void cheakNoImage(){
		if(bitcount == 0){
			bitmaps = new Bitmap[1];
			bitcount = 1;
			bitmaps[0] = default_bitmap;
			
			tips = new ImageView[bitcount];
			for(int i=0; i<bitcount; i++){  
	            ImageView mImageView = new ImageView(this);  
	            tips[i] = mImageView;  
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams.rightMargin = 3;  
	            layoutParams.leftMargin = 3;  
	              
	            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
	            ll_storeitem_viewGroup.addView(mImageView, layoutParams);  
	        }
			setdrawable();
		}
	}
	
	public void setdrawable(){
		Log.d("setdrawable","ok---"+currentPosition+"---"+bitmaps.length);
		if(bitmaps[currentPosition % bitmaps.length] == null)
			is_storeitem_pic.setImageDrawable(DrawableFromBitmap(default_bitmap));
		is_storeitem_pic.setImageDrawable(DrawableFromBitmap(bitmaps[currentPosition % bitmaps.length]));
		setViewgroupBackground(currentPosition);
	}
	
	//设置点点的背景
	private void setViewgroupBackground(int selectItems) {
		for(int i=0; i<tips.length; i++){    
            if(i == selectItems){    
                tips[i].setBackgroundResource(R.drawable.point_focused);    
            }else{    
                tips[i].setBackgroundResource(R.drawable.point_ununfocused);    
            }
        }    
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//Log.d("setdrawable","touch");
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
		                	is_storeitem_pic.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));  
		                	is_storeitem_pic.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));  
		                    currentPosition --;  
		                    setdrawable();
		                }else{  
		                	is_storeitem_pic.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));  
		                	is_storeitem_pic.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));  
		                    currentPosition = bitmaps.length - 1;  
		                    setdrawable();
		                }  
		            }   
		              
		            if(lastX < downX){  
		                if(currentPosition < bitmaps.length - 1){  
		                	is_storeitem_pic.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
		                	is_storeitem_pic.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
		                    currentPosition ++ ;  
		                    setdrawable();
		                }else{  
		                	is_storeitem_pic.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
		                	is_storeitem_pic.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
		                    currentPosition = 0;  
		                    setdrawable();
		                }  
		            }  
		            }  
		              
		            break;  
		        }  
		  
		        return true;  
	}
	
	public Drawable DrawableFromBitmap(Bitmap bitmap) {
		BitmapDrawable bd = null;
		if(bitmap == null){
			Log.d("setdrawable","null");
			bd = new BitmapDrawable(default_bitmap);
		}
		else
			bd = new BitmapDrawable(bitmap);
		// 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
		return bd;
	}

	@Override
	public View makeView() {
		final ImageView i = new ImageView(this);  
        i.setBackgroundColor(0xff000000);  
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);  
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
        return i; 
	}

	@Override
	public void OnResize(int w, int h, int oldw, int oldh) {
		int change = BIGGER;
	    if (h < oldh) {
	    change = SMALLER;
		}
		mHandler.sendEmptyMessage(change);//做你要做的事情
	}
	
	public static MyHandler mHandler;
	
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				lv_storeitem_comment.setAdapter(new CommentListAdapter(
						StoreItemDetailActivity.this, msgs));
				lv_storeitem_comment.setListViewHeightBasedOnChildren();
				break;

			case 1:
				new SetDataListThread().start();
				break;
				//获取到theShopItem
			case 33:
				//设置一些值
				tv_storeitem_name.setText(theShopitem.itemName);
				tv_storeitem_price.setText("￥"+String.valueOf(theShopitem.price));
				tv_storeitem_intro.setText(theShopitem.details);
				bitcount = theShopitem.images.length;
				bitmaps = new Bitmap[bitcount];
				String[] urls = new String[bitcount];
				for(int i1 = 0 ; i1 < bitcount ; i1 ++){
					bitmaps[i1] = default_bitmap;
					urls[i1] = theShopitem.images[i1].picUrl;
				}
				tips = new ImageView[bitcount];
				for(int i=0; i<bitcount; i++){  
		            ImageView mImageView = new ImageView(StoreItemDetailActivity.this);  
		            tips[i] = mImageView;  
		            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		            layoutParams.rightMargin = 3;  
		            layoutParams.leftMargin = 3;  
		              
		            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
		            ll_storeitem_viewGroup.addView(mImageView, layoutParams);  
		        }
				cheakNoImage();
				new setBitmapsThread(urls).start();
				new SetDataListThread().start();
				break;
				
			case BIGGER:
				//makeToast("变大");
				refId = "";
				et_storeitem_comment.setHint("发表评论（回车发送）");
				refcontent = "";
				break;
			case SMALLER:
				//makeToast("变小");
				break;
			case 22:
				setdrawable();
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
			return;
		}

	};
	
	private class SetDataListThread extends Thread {

		@Override
		public synchronized void run() {
			msgs = new ArrayList<Comment>();
			try {
				HashMap p = new HashMap();
				p.put("itemId", theShopitem.itemId);
				JSONObject res = C.asyncPost(C.URLget_shop_item_comments, p);
				if (!(res.getInt("status")== 0)) {
					mHandler.sendToast("网络有！");
					return;
				}
				
				Log.d("comments","getted");

				JSONArray comments = res.getJSONArray("comments");
				
				for (int i = 0; i < comments.length(); i++) {
					JSONObject comment = comments.getJSONObject(i);
					JSONObject maker = comment.getJSONObject("maker");
					
					msgs.add(
							new Comment(
							comment.getString("commentId"),
							comment.getString("time"),
							comment.getString("content"),
							//comment.getString("refId"),
							comment.getString("makerId"),
							
							new User(maker.getString("userId"),
									maker.getString("account"),
									maker.getString("userName"),
									maker.getString("college"),
									maker.getString("location"),
									maker.getString("avatar")
									)
							)
							);
				}
				Log.d("comments","成功");
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(0);
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
							cachebitmap = result;
							for(int j = 0 ;j < picUrls.length ; j ++){
								if(bitmaps[j].equals(default_bitmap)){
									bitmaps[j] = result;
									if(bitmaps[0] != null)
										mHandler.sendEmptyMessage(22);
									break;
								}
							}
							
						}
					}.execute();
					//bitmaps[i] = cachebitmap;
				}
			}
		}
	
	//获取item的详细信息
	class GetItemDataThread extends Thread{
		private String id;
		public GetItemDataThread(String id){
			this.id = id;
		}
		
		@Override
		public void run() {
			HashMap map = new HashMap();
			map.put("itemId", this.id);
			JSONObject res = C.asyncPost(C.URLget_shop_item_Detail, map);
			try {
				if(res.getInt("status") != 0){
					mHandler.sendToast("网络有！");
					return;
				}
				
				//生成ShopItem theShopitem
				JSONObject item = res.getJSONObject("item");
				JSONArray images = item.getJSONArray("images");
				int imagenum = images.length();
				Image[] imgs = new Image[imagenum];
				for(int i = 0 ; i < imagenum ; i ++){
					JSONObject oo = images.getJSONObject(i);
					imgs[i] = new Image(oo.getString("picId"),oo.getString("picUrl"));
				}
				theShopitem = new ShopItem(item.getString("itemId"),
						item.getString("itemName"),
						(float)item.getDouble("price"),
						item.getString("details"),
						item.getString("clazz"),
						item.getString("shopId"),
						item.getInt("status"),
						imgs,
						null);
				
				mHandler.sendEmptyMessage(33);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
