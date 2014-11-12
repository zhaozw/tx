package com.example.tx.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.adapter.CommentListAdapter;
import com.example.tx.dto.Category;
import com.example.tx.dto.Comment;
import com.example.tx.dto.Item;
import com.example.tx.dto.Image;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;
import com.example.tx.view.ListViewInsideScrollView;
import com.example.tx.view.ResizeLayout;
import com.example.tx.view.ResizeLayout.OnResizeListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;

@SuppressLint("HandlerLeak")
public class ItemDetailActivity extends BaseActivity implements OnResizeListener ,ViewFactory, OnTouchListener{

	public static ItemDetailActivity that = null;
	
	private static final int BIGGER = 30;

	private static final int SMALLER = 31;
	
	//
	private ResizeLayout resizeLayout_itemdetail;

	private Item msg;  //用于接收其他页面item消息
	private Item theItem; //用于从消息页面传来的id获取的item
	private boolean flag = false; //用于判断是否来自于userinfor，是则屏蔽用户名字的按钮
	private com.example.tx.dto.Message msg_m;
	public Bitmap b = null;

	private List<Comment> msgs;
	private ListViewInsideScrollView dataListView;
	private ImageSwitcher is_itemdetail;
	

	private EditText et_comment;
	
	private String refId = "";
	private String refcontent = "";
	private String iid;
	
	private String telephone;
	private String owner;
	private String name;
	
	public static MyHandler mHandler;
	
	//收藏商品按钮
	private TextView tv_like_detail;
	private ImageView iv_like;
	private boolean liked = false;
	//举报商品
	private TextView tv_reportItem;
	//更多按钮
	private ImageButton ib_more_detail;
	
	private Bitmap[] bitmaps;
	private int bitcount;
	private Bitmap default_bitmap;
	private Bitmap cachebitmap;
	private float downX;
	private int currentPosition = 0;

	//装载点点的容器 
    private LinearLayout lll_viewgroup;  
    //点点数组  
    private ImageView[] tips;
    
    //微信分享相关
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_item_detail);

		mHandler = new MyHandler();


		that = this;
		
		
		Drawable d= getResources().getDrawable(R.drawable.default_image); //xxx根据自己的情况获取drawable
		BitmapDrawable bd = (BitmapDrawable) d;
		default_bitmap = bd.getBitmap();
		
		resizeLayout_itemdetail = (ResizeLayout) findViewById(R.id.resizeLayout_itemdetail);
		resizeLayout_itemdetail.setOnResizeListener(this);
		
		//举报商品
		tv_reportItem = (TextView) findViewById(R.id.tv_reportItem);
		//更多按钮
		ib_more_detail = (ImageButton) findViewById(R.id.ib_more_detail);
		
		et_comment = (EditText) findViewById(R.id.et_idetail_comment);
		tv_like_detail = (TextView) findViewById(R.id.tv_like_detail);
		iv_like = (ImageView) findViewById(R.id.iv_like);
		is_itemdetail = (ImageSwitcher) findViewById(R.id.is_idetail_item_pic);
		
		lll_viewgroup = (LinearLayout)findViewById(R.id.lll_viewGroup);
		//绑定imageswitch的事件及工厂
		is_itemdetail.setFactory(this);
		is_itemdetail.setOnTouchListener(this);
		is_itemdetail.setImageDrawable(DrawableFromBitmap(default_bitmap));
		

		final int from = getIntent().getExtras().getInt("from");
		int index = getIntent().getExtras().getInt("index");
		
		//来自于商品列表
		if (from == 0){
			msg = GoodslistActivity.that.msgs.get(index);
			iid = msg.itemId;
			bitcount = msg.image.size();
			bitmaps = new Bitmap[bitcount];
			String[] urls = new String[bitcount];
			for(int i = 0 ; i < bitcount ; i ++){
				bitmaps[i] = default_bitmap;
				urls[i] = msg.image.get(i).picUrl;
			}
			tips = new ImageView[bitcount];
			for(int i=0; i<bitcount; i++){  
	            ImageView mImageView = new ImageView(this);  
	            tips[i] = mImageView;  
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams.rightMargin = 3;  
	            layoutParams.leftMargin = 3;  
	              
	            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
	            lll_viewgroup.addView(mImageView, layoutParams);  
	        }
			cheakNoImage();
			new setBitmapsThread(urls).start();
			
		}
		else if (from == 1){
			msg = UserinforActivity.that.msgs.get(index);
			flag = true;
			iid = msg.itemId;
			bitcount = msg.image.size();
			bitmaps = new Bitmap[bitcount];
			String[] urls = new String[bitcount];
			for(int i = 0 ; i < bitcount ; i ++){
				bitmaps[i] = default_bitmap;
				urls[i] = msg.image.get(i).picUrl;
			}
			tips = new ImageView[bitcount];
			for(int i=0; i<bitcount; i++){  
	            ImageView mImageView = new ImageView(this);  
	            tips[i] = mImageView;  
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams.rightMargin = 3;  
	            layoutParams.leftMargin = 3;  
	              
	            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
	            lll_viewgroup.addView(mImageView, layoutParams);  
	        }
			cheakNoImage();
			new setBitmapsThread(urls).start();
		}
		else if(from == 2){ // from 2, search
			msg = SearchActivity.msgs.get(index);
			iid = msg.itemId;
			bitcount = msg.image.size();
			bitmaps = new Bitmap[bitcount];
			String[] urls = new String[bitcount];
			for(int i = 0 ; i < bitcount ; i ++){
				bitmaps[i] = default_bitmap;
				urls[i] = msg.image.get(i).picUrl;
			}
			tips = new ImageView[bitcount];
			for(int i=0; i<bitcount; i++){  
	            ImageView mImageView = new ImageView(this);  
	            tips[i] = mImageView;  
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams.rightMargin = 3;  
	            layoutParams.leftMargin = 3;  
	              
	            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
	            lll_viewgroup.addView(mImageView, layoutParams);  
	        }
			cheakNoImage();
			new setBitmapsThread(urls).start();
		}
		else if(from == 3){  //根据msg_m的id获取商品信息
			
			msg_m = MessageActivity.that.msgs.get(index);
			//String id = msg_m.refId;
			iid = msg_m.refId;
			msg = new Item(iid, "", 0, "", "", "", "", null, null);
			new getItemThread().start();
		}
		else if(from==4)
		{
			msg=MineCommodityDetailActivity.that.msgs.get(index);
			iid=msg.itemId;
			bitcount = msg.image.size();
			bitmaps = new Bitmap[bitcount];
			String[] urls = new String[bitcount];
			for(int i = 0 ; i < bitcount ; i ++){
				bitmaps[i] = default_bitmap;
				urls[i] = msg.image.get(i).picUrl;
			}
			tips = new ImageView[bitcount];
			for(int i=0; i<bitcount; i++){  
	            ImageView mImageView = new ImageView(this);  
	            tips[i] = mImageView;  
	            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            layoutParams.rightMargin = 3;  
	            layoutParams.leftMargin = 3;  
	              
	            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
	            lll_viewgroup.addView(mImageView, layoutParams);  
	        }
			cheakNoImage();
			new setBitmapsThread(urls).start();
		}
			
		if(from < 3||from==4){

//			final ImageView pic = (ImageView) findViewById(R.id.is_idetail_item_pic);
//			
//			if(msg.image.size() == 0){
//				Drawable d= getResources().getDrawable(R.drawable.default_image); //xxx根据自己的情况获取drawable
//				BitmapDrawable bd = (BitmapDrawable) d;
//				Bitmap bm = bd.getBitmap();
//				b = bm;
//			}
//			else
//				b = C.getBitmapFromMemCache(msg.image.get(0).picId);
//			if (b == null) {
//				pic.setImageResource(R.drawable.item_pic_default);
//	
//				//根据url获取图片
//				new Request4Image(msg.image.get(0).picUrl) 
//				{
//					@Override
//					protected void onPostExecute(Bitmap result) 
//					{
//						if(result==null) 
//							return;
//						//C.addBitmapToMemoryCache(id, result);
//						pic.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
//						//notifyDataSetChanged();
//					}
//				}.execute();
//	
//						
//			} else {
//				pic.setImageBitmap(b);
//			}
//			
//			
//			int len = getWindowManager().getDefaultDisplay().getWidth() - 10;
//			pic.setMinimumHeight(len);
//			pic.setMinimumWidth(len);
	
			((TextView) findViewById(R.id.tv_idetail_price)).setText("¥"
					+ msg.price);
			((TextView) findViewById(R.id.tv_idetail_name)).setText(msg.itemName);
			((TextView) findViewById(R.id.tv_idetail_owner)).setText(msg.seller.userName+" ");
			String l = msg.seller.location;
			if(l.equals(""))
				l = "未知";
			((TextView) findViewById(R.id.tv_idetail_position))
					.setText(l);
			
			if(msg.details.equals("")) {
				((TextView) findViewById(R.id.tv_idetail_intro)).setText("这懒货啥也没说~~");			
			} else {
				((TextView) findViewById(R.id.tv_idetail_intro)).setText(msg.details);	
			}
	
			
	
			telephone = msg.seller.account;
			owner = msg.seller.userName;
			name = msg.itemName;
		}
		
		//返回
		((ImageButton) findViewById(R.id.ib_back_detail)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		
		//获取是否已经收藏该商品的按钮
		new Thread(new Runnable(){

			@Override
			public void run() {
				HashMap p = new HashMap();
				if(!C.logged || C.userId.equals("")){
					return;
				}
				p.put("itemId", msg.itemId);
				p.put("userId", C.userId);
				JSONObject res = C.asyncPost(C.URLis_favorite_item, p);
				try {
					if (res.getInt("status")== 0) {
						//TODO 没有收藏
						//Log.d("like","like");
						liked = false;
					}
					else if(res.getInt("status")== 1){
						//TODO 已经收藏
						//Log.d("like","liked");
						liked = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(11);
			}
			
		}).start();
		
		//点击喜欢商品按钮
		tv_like_detail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new Thread(new Runnable(){

					@Override
					public void run() {
						if(!C.logged||C.userId.equals("")){
							mHandler.sendToast("您还未登陆！");
							return;
						}
						if(!liked){
							try {
								HashMap p = new HashMap();
								p.put("itemId", msg.itemId);
								p.put("userId", C.userId);
								//Log.d("itemdetail",msg.itemId+"=="+C.userId);
								JSONObject res = C.asyncPost(C.URLadd_item_to_favorite, p);
								if (!(res.getInt("status")== 0)) {
									//mHandler.sendToast(res.getString("description"));
									return;
								}else {
									mHandler.sendToast("收藏成功");
									liked = true;
									mHandler.sendEmptyMessage(11);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else{
							try {
								HashMap p = new HashMap();
								p.put("itemId", msg.itemId);
								p.put("userId", C.userId);
								//Log.d("itemdetail",msg.itemId+"=="+C.userId);
								JSONObject res = C.asyncPost(C.URLremove_item_from_favorite, p);
								if (!(res.getInt("status")== 0)) {
									//mHandler.sendToast(res.getString("description"));
									return;
								}else {
									mHandler.sendToast(res.getString("description"));
									liked = false;
									mHandler.sendEmptyMessage(11);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
											
					}
					
				}).start();
			}
			
		});
		
		if(flag)
			((ImageView) findViewById(R.id.iv_left)).setVisibility(View.GONE);
		
		//点击卖家名字
		((TextView) findViewById(R.id.tv_idetail_owner)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO 跳转到查看别人信息界面
				if(flag)
					return;
				Intent intent = new Intent(ItemDetailActivity.this,UserinforActivity.class);
				Bundle bundle = new Bundle();
				String theUserId;
				if(from != 3)
					theUserId = msg.seller.userId;
				else
					theUserId = theItem.seller.userId;
				bundle.putString("userId", theUserId);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
		
		//点击联系卖家
		((Button) findViewById(R.id.btn_connect))
				.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						LinearLayout ll_cover = (LinearLayout) findViewById(R.id.ll_cover);
						ll_cover.setVisibility(View.VISIBLE);
						
						LinearLayout ll_connect = (LinearLayout) findViewById(R.id.ll_connect);
						ll_connect.setVisibility(View.VISIBLE);
					}
					
				});
		((Button) findViewById(R.id.b_cancle))
				.setOnClickListener(new OnClickListener(){
		
					@Override
					public void onClick(View v) {
						LinearLayout ll_cover = (LinearLayout) findViewById(R.id.ll_cover);
						ll_cover.setVisibility(View.GONE);
						
						LinearLayout ll_connect = (LinearLayout) findViewById(R.id.ll_connect);
						ll_connect.setVisibility(View.GONE);
					}
					
				});
		((LinearLayout) findViewById(R.id.ll_cover)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LinearLayout ll_cover = (LinearLayout) findViewById(R.id.ll_cover);
				ll_cover.setVisibility(View.GONE);
				
				LinearLayout ll_connect = (LinearLayout) findViewById(R.id.ll_connect);
				ll_connect.setVisibility(View.GONE);

				LinearLayout ll_report = (LinearLayout)findViewById(R.id.ll_report);
				ll_report.setVisibility(View.GONE);
			}
			
		});

		((Button) findViewById(R.id.btn_idetail_phone))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						new AlertDialog.Builder(ItemDetailActivity.this)
								.setTitle("提示")
								.setMessage(
										"确认打电话给 " + owner + "(" + telephone
												+ ") 吗？")
								.setPositiveButton("确认",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												Intent intent = new Intent(
														Intent.ACTION_CALL,
														Uri.parse("tel:"
																+ telephone));
												startActivity(intent);
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).show();

					}
				});

		((Button) findViewById(R.id.btn_idetail_sms))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final EditText et = new EditText(
								ItemDetailActivity.this);
						et.setText("【来自淘学】" + owner + " 你好！我看到你出售的商品【" + name
								+ "】很感兴趣，能否讨论一下具体的交易地点和时间？谢谢！");

						new AlertDialog.Builder(ItemDetailActivity.this)
								.setTitle(
										"发送消息给" + owner + "(" + telephone
												+ ")：")
								.setIcon(android.R.drawable.ic_dialog_email)
								.setView(et)
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												SmsManager manager = SmsManager
														.getDefault();
												ArrayList<String> list = manager
														.divideMessage(et
																.getText()
																.toString()); // 因为一条短信有字数限制，因此要将长短信拆分
												for (String text : list) {
													manager.sendTextMessage(
															telephone, null,
															text, null, null);
												}
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).show();
					}
				});

		et_comment.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {

					String comment = ((EditText) v).getText().toString();
					if (comment.equals(""))
						return true;
					
					if(C.logged == false) {
						makeToast("请登录后再评论！");
						return true;
					}
					
					((EditText) v).setText("");

					HashMap p = new HashMap();
					try {
						p.put("itemId", iid);
						p.put("content", refcontent+comment);
						p.put("refId", refId);
						if (C.logged) {
							p.put("userId", C.userId);
						}
						
						Log.d("itemdetail",iid+","+comment+","+refId+"=="+C.userId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					makeToast("正在提交评论");
					new PostTask(C.URLwrite_item_comments, p)
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
//					JSONObject res = C.asyncPost(C.URLwrite_item_comments, p);
					
					return true;
				}
				return false;
			}
		});
		
		//更多按钮 点击事件
		ib_more_detail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				LinearLayout ll = (LinearLayout) findViewById(R.id.ll_more);
				if(ll.getVisibility() == View.GONE){
					ll.setVisibility(View.VISIBLE);
				}
				else if(ll.getVisibility() == View.VISIBLE){
					ll.setVisibility(View.GONE);
				}
			}
			
		});
		
		//举报商品  一下四个按钮分别是举报 ，评论不和谐 ，内容不和谐， 取消
		tv_reportItem.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(!C.logged){
					makeToast("登录后才能举报");
					return;
				}
				
				LinearLayout ll_cover = (LinearLayout)findViewById(R.id.ll_cover);
				LinearLayout ll_report = (LinearLayout)findViewById(R.id.ll_report);
				
				ll_cover.setVisibility(View.VISIBLE);
				ll_report.setVisibility(View.VISIBLE);
				
			}
			
		});
		
		((Button) findViewById(R.id.btn_report_itemcomment)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HashMap p = new HashMap();
				p.put("userId", C.userId);
				p.put("itemId", iid);
				p.put("content", "评论不和谐");
				
				reportThread thread = new reportThread(p);
				thread.start();
				
				LinearLayout ll_cover = (LinearLayout)findViewById(R.id.ll_cover);
				LinearLayout ll_report = (LinearLayout)findViewById(R.id.ll_report);
				
				ll_cover.setVisibility(View.GONE);
				ll_report.setVisibility(View.GONE);
			}
			
		});
		
		((Button) findViewById(R.id.btn_report_itemcontent)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HashMap p = new HashMap();
				p.put("userId", C.userId);
				p.put("itemId", iid);
				p.put("content", "内容不和谐");
				
				reportThread thread = new reportThread(p);
				thread.start();
				
				LinearLayout ll_cover = (LinearLayout)findViewById(R.id.ll_cover);
				LinearLayout ll_report = (LinearLayout)findViewById(R.id.ll_report);
				
				ll_cover.setVisibility(View.GONE);
				ll_report.setVisibility(View.GONE);
			}
			
		});
		
		((Button) findViewById(R.id.b_report_cancle)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LinearLayout ll_cover = (LinearLayout)findViewById(R.id.ll_cover);
				LinearLayout ll_report = (LinearLayout)findViewById(R.id.ll_report);
				
				ll_cover.setVisibility(View.GONE);
				ll_report.setVisibility(View.GONE);
			}
			
		});
		

		dataListView = (ListViewInsideScrollView) findViewById(R.id.lv_idetail);
		dataListView.setEmptyView(findViewById(R.id.ll_comment_empty));
		dataListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String talker = msgs.get(position).maker.userName;
				refId = msgs.get(position).commentId;
				refcontent = "回复" + talker + ":  ";
				et_comment.setHint("回复" + talker + ":  ");
				et_comment.clearFocus();
			}
		});

		new SetDataListThread().start();

	}

	

	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				dataListView.setAdapter(new CommentListAdapter(
						ItemDetailActivity.this, msgs));
				dataListView.setListViewHeightBasedOnChildren();
				break;

			case 1:
				new SetDataListThread().start();
				break;
				
			case 2:
				
//				final ImageView pics = (ImageView) findViewById(R.id.iv_idetail_item_pic);				
//				
//				b = C.getBitmapFromMemCache(theItem.image.get(0).picId);
//				if (b == null) {
//					pics.setImageResource(R.drawable.item_pic_default);
//		
//					//根据url获取图片
//					new Request4Image(theItem.image.get(0).picUrl) 
//					{
//						@Override
//						protected void onPostExecute(Bitmap result) 
//						{
//							if(result==null) 
//								return;
//							//C.addBitmapToMemoryCache(id, result);
//							pics.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
//							//notifyDataSetChanged();
//						}
//					}.execute();
//		
//							
//				} else {
//					pics.setImageBitmap(b);
//				}
//				
//				
//				int len = getWindowManager().getDefaultDisplay().getWidth() - 10;
//				pics.setMinimumHeight(len);
//				pics.setMinimumWidth(len);
				
				bitcount = theItem.image.size();
				bitmaps = new Bitmap[bitcount];
				String[] urls = new String[bitcount];
				for(int i = 0 ; i < bitcount ; i ++){
					bitmaps[i] = default_bitmap;
					urls[i] = theItem.image.get(i).picUrl;
				}
				tips = new ImageView[bitcount];
				for(int i=0; i<bitcount; i++){  
		            ImageView mImageView = new ImageView(ItemDetailActivity.this);  
		            tips[i] = mImageView;  
		            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		            layoutParams.rightMargin = 3;  
		            layoutParams.leftMargin = 3;  
		              
		            mImageView.setBackgroundResource(R.drawable.point_unfocused);  
		            lll_viewgroup.addView(mImageView, layoutParams);  
		        }
				cheakNoImage();
				new setBitmapsThread(urls).start();
		
				((TextView) findViewById(R.id.tv_idetail_price)).setText("¥"
						+ theItem.price);
				((TextView) findViewById(R.id.tv_idetail_name)).setText(theItem.itemName);
				((TextView) findViewById(R.id.tv_idetail_owner)).setText(theItem.seller.userName+" ");
				String l = theItem.seller.location;
				if(l.equals(""))
					l = "未知";
				((TextView) findViewById(R.id.tv_idetail_position))
						.setText(l);
				
				if(theItem.details.equals("")) {
					((TextView) findViewById(R.id.tv_idetail_intro)).setText("这懒货啥也没说~~");			
				} else {
					((TextView) findViewById(R.id.tv_idetail_intro)).setText(theItem.details);	
				}
		
				telephone = theItem.seller.account;
				owner = theItem.seller.userName;
				name = theItem.itemName;
				
				break;
			//改变收藏按钮
			case 11:
				if(liked)
					//变为蓝色心
					iv_like.setImageResource(R.drawable.like_dislike_26);
					
				else
					//变为原来的心
					iv_like.setImageResource(R.drawable.like_26);
				break;

			case 99:
				String toast = (String) msg.obj;
				makeToast(toast);
				break;
			
			case BIGGER:
				//makeToast("变大");
				refId = "";
				et_comment.setHint("发表评论（回车发送）");
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
				p.put("itemId", iid);
				//Log.d("comments","start"+iid);
				JSONObject res = C.asyncPost(C.URLget_item_comments, p);
				if (!(res.getInt("status")== 0)) {
					mHandler.sendToast("网络有问题！");
					return;
				}
				
				//Log.d("comments","getted");

				JSONArray comments = res.getJSONArray("comments");
				
				for (int i = 0; i < comments.length(); i++) {
					JSONObject comment = comments.getJSONObject(i);
					JSONObject maker = comment.getJSONObject("maker");
					
					
					//Log.d("comments",comment.getString("refId"));
					
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
				//Log.d("comments","成功");
			} catch (Exception e) {
				e.printStackTrace();
			}

			mHandler.sendEmptyMessage(0);
		}

	}
	
	private class getItemThread extends Thread{

		@Override
		public void run() {
			try {
				HashMap p = new HashMap();
				p.put("itemId", iid);
				//Log.d("item","=============="+iid);
				JSONObject res = C.asyncPost(C.URLget_item_details, p);
				if( !(res.getInt("status") == 0) ) {
					mHandler.sendToast("网络有问题！");
					return ;
				}
				
				//Log.d("item","geteeeeeeeeeeeeed"+res.getString("description"));
				
				JSONObject item = res.getJSONObject("item");
				//Log.d("item","item,,,,"+res.getString("description"));
				JSONArray a=item.getJSONArray("images");
				//Log.d("item","images,,,,"+res.getString("description"));
				List<Image> images=new ArrayList<Image>();
				for(int j=0;j<a.length();j++)
				{
					JSONObject oo=a.getJSONObject(j);
					images.add(new Image(oo.getString("picId"),oo.getString("picUrl")));
				}
				
				
				
				JSONObject so = item.getJSONObject("seller");
				User su=new User(so.getString("userId"),so.getString("account"),so.getString("userName"),so.getString("college"),so.getString("location"),so.getString("avatar"));
				theItem = new Item(item.getString("itemId")
						,item.getString("itemName")
						,(float)item.getDouble("price")
						,item.getString("details")
						,item.getString("releaseTime")
						,item.getString("unshelveTime")
						,item.getString("categoryId")
						,su
						,images
						);
				//Log.d("item","lalala");
				//
				mHandler.sendEmptyMessage(2);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	//举报的线程
	private class reportThread extends Thread{
		private HashMap p;
		
		public reportThread(HashMap p){
			this.p = p;
		}

		@Override
		public void run() {
			JSONObject res = C.asyncPost(C.URLreport_Item, p);
			//Log.d("report","yse");
			try {
				if(res.getInt("status") != 0){
					mHandler.sendToast(res.getString("description"));
				}
				mHandler.sendToast(res.getString("description"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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


	@Override
	public void OnResize(int w, int h, int oldw, int oldh) {
		int change = BIGGER;
	    if (h < oldh) {
	    change = SMALLER;
		}
		mHandler.sendEmptyMessage(change);//做你要做的事情
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
                    is_itemdetail.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));  
                    is_itemdetail.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));  
                    currentPosition --;  
                    setdrawable();
                    //is_itemdetail.setImageResource(imgIds[currentPosition % bitmaps.length]);  
                    //setViewgroupBackground(currentPosition);  
                }else{  
                	is_itemdetail.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));  
                	is_itemdetail.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));  
                    currentPosition = bitmaps.length - 1;  
                    setdrawable();
                    //is_itemdetail.setImageResource(imgIds[currentPosition % bitmaps.length]);  
                    //setViewgroupBackground(currentPosition);  
                }  
            }   
              
            if(lastX < downX){  
                if(currentPosition < bitmaps.length - 1){  
                	is_itemdetail.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
                	is_itemdetail.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
                    currentPosition ++ ;  
                    setdrawable();
                    //is_itemdetail.setImageResource(imgIds[currentPosition % bitmaps.length]);  
                   // setViewgroupBackground(currentPosition);  
                }else{  
                	is_itemdetail.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));  
                	is_itemdetail.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_out));  
                    currentPosition = 0;  
                    setdrawable();
//                    is_itemdetail.setImageResource(imgIds[currentPosition % bitmaps.length]);  
//                    setViewgroupBackground(currentPosition);  
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

	public void setdrawable(){
		Log.d("setdrawable","ok---"+currentPosition+"---"+bitmaps.length);
		if(bitmaps[currentPosition % bitmaps.length] == null)
			is_itemdetail.setImageDrawable(DrawableFromBitmap(default_bitmap));
		 is_itemdetail.setImageDrawable(DrawableFromBitmap(bitmaps[currentPosition % bitmaps.length]));
		 setViewgroupBackground(currentPosition);
	}
	
	private void setViewgroupBackground(int selectItems) {
		for(int i=0; i<tips.length; i++){    
            if(i == selectItems){    
                tips[i].setBackgroundResource(R.drawable.point_focused);    
            }else{    
                tips[i].setBackgroundResource(R.drawable.point_ununfocused);    
            }
        }    
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
	            lll_viewgroup.addView(mImageView, layoutParams);  
	        }
			setdrawable();
		}
	}

	@Override
	public View makeView() {
		final ImageView i = new ImageView(this);  
        i.setBackgroundColor(0xff000000);  
        i.setScaleType(ImageView.ScaleType.CENTER_CROP);  
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
        return i; 
	}
	
}