package com.example.tx.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.adapter.TalkCommentListAdapter;
import com.example.tx.dto.User;
import com.example.tx.dto.TalkComment;
import com.example.tx.dto.Talk;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;
import com.example.tx.view.ListViewInsideScrollView;
import com.example.tx.view.ResizeLayout;
import com.example.tx.view.ResizeLayout.OnResizeListener;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TreeholeCommentActivity extends BaseActivity implements OnResizeListener{
	
	private ResizeLayout resizeLayout_talkcomment;
	private static final int BIGGER = 30;

	private static final int SMALLER = 31;
	
	private ListViewInsideScrollView dataListView;
	private EditText et_comment;
	
	private ImageView iv_treeholeitem_com;
	
	private Talk msg_t;//用于获取当前页面的内容
	private com.example.tx.dto.Message msg_m;
	private String id;

	private List<TalkComment> msgs;//用于存储当前页面的评论
	private Talk theTalk;
	
	private String refId = "";
	private String refcontent = "";
	
	private boolean needrefresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_treehole_comment);
		
		resizeLayout_talkcomment = (ResizeLayout) findViewById(R.id.resizeLayout_talkcomment);
		resizeLayout_talkcomment.setOnResizeListener(this);
		dataListView = (ListViewInsideScrollView) findViewById(R.id.lv_treehole_comment);
		et_comment = (EditText) findViewById(R.id.et_treehole_comment);
		iv_treeholeitem_com = (ImageView) findViewById(R.id.iv_treeholeitem_com);
		
		msgs = new ArrayList<TalkComment>();
		
		refId = "";
		
		int index = getIntent().getIntExtra("index", 0);
		int from = getIntent().getIntExtra("from", 0);
		
		if(from == 0){ //如果来自树洞
		    msg_t = TreeholeActivity.that.msgs.get(index);
		    id = msg_t.id;
		    ((TextView) findViewById(R.id.tv_treeholename_com)).setText(msg_t.sender.userName);
			((TextView) findViewById(R.id.tv_treeholecontent_com)).setText(msg_t.content);
			((TextView) findViewById(R.id.tv_treeholetime_com)).setText(msg_t.time);
			new Request4Image(msg_t.sender.avatar) 
			{
				@Override
				protected void onPostExecute(Bitmap result) 
				{
					if(result==null) 
						return;
					iv_treeholeitem_com.setImageBitmap(result);
				}
			}.execute();
			
		}
		else if(from == 3){  //如果来自消息，则需要刷新
			msg_m = MessageActivity.that.msgs.get(index);
			id = msg_m.refId;
			
			
			//TODO 内容需要重新获取
			
			new getTalkThread().start();
			needrefresh = true;
		}
			
		
		
		((ImageButton) findViewById(R.id.ib_back_thcomment)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		//dataListView.setEmptyView(findViewById(R.id));
		dataListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String talker = msgs.get(position).sender.userName;
				refcontent = "回复" + talker + ":  ";
				et_comment.setHint("回复" + talker + ":  ");
				et_comment.clearFocus();
				refId = msgs.get(position).id;
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
					
					//未登录，直接返回
					if(C.logged == false) {
						makeToast("请登录后再评论！");
						return true;
					}
					
					((EditText) v).setText("");

					HashMap p = new HashMap();
					try {
						p.put("talkId", id);
						p.put("userId", C.userId);
						p.put("content", refcontent + comment);
						p.put("refId", refId);
						Log.d("talkDetail",id+","+comment+","+refId+"=="+C.userId);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					JSONObject ret = C.asyncPost(C.URLwrite_talk_comment, p);
					try {
						if(ret.getInt("status") == 0)
						{
							//makeToast(ret.getString("description"));
							needrefresh = true;
							new SetDataListThread().start();
							MainActivity.mHandler.sendEmptyMessage(1);
							refId = "";
							refcontent = "";
						}
						else
							makeToast("评论失败");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					return true;
				}
				return false;
			}
		});
		
		((ImageButton) findViewById(R.id.ib_reporttalk)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(C.logged == false){
					makeToast("登陆后才能举报");
					return;
				}
				LinearLayout ll_talkcover = (LinearLayout)findViewById(R.id.ll_talkcover);
				ll_talkcover.setVisibility(View.VISIBLE);
				
				LinearLayout ll_reporttalk = (LinearLayout)findViewById(R.id.ll_reporttalk);
				ll_reporttalk.setVisibility(View.VISIBLE);
				
			}
			
		});
		
		((Button) findViewById(R.id.btn_reporttalkcontent)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HashMap p = new HashMap();
				p.put("talkId", id);
				p.put("userId", C.userId);
				p.put("content", "内容不和谐");
				
				new reportTalkThread(p).start();
				
				LinearLayout ll_talkcover = (LinearLayout)findViewById(R.id.ll_talkcover);
				ll_talkcover.setVisibility(View.GONE);
				
				LinearLayout ll_reporttalk = (LinearLayout)findViewById(R.id.ll_reporttalk);
				ll_reporttalk.setVisibility(View.GONE);
			}
			
		});
		
		((Button) findViewById(R.id.btn_reporttalkcomment)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HashMap p = new HashMap();
				p.put("talkId", id);
				p.put("userId", C.userId);
				p.put("content", "评论不和谐");
				
				new reportTalkThread(p).start();
				
				LinearLayout ll_talkcover = (LinearLayout)findViewById(R.id.ll_talkcover);
				ll_talkcover.setVisibility(View.GONE);
				
				LinearLayout ll_reporttalk = (LinearLayout)findViewById(R.id.ll_reporttalk);
				ll_reporttalk.setVisibility(View.GONE);
			}
			
		});
		
		((Button) findViewById(R.id.b_reporttalkcancle)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LinearLayout ll_talkcover = (LinearLayout)findViewById(R.id.ll_talkcover);
				ll_talkcover.setVisibility(View.GONE);
				
				LinearLayout ll_reporttalk = (LinearLayout)findViewById(R.id.ll_reporttalk);
				ll_reporttalk.setVisibility(View.GONE);
			}
			
		});
		
		
		
		((LinearLayout)findViewById(R.id.ll_talkcover)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LinearLayout ll_talkcover = (LinearLayout)findViewById(R.id.ll_talkcover);
				ll_talkcover.setVisibility(View.GONE);
				
				LinearLayout ll_reporttalk = (LinearLayout)findViewById(R.id.ll_reporttalk);
				ll_reporttalk.setVisibility(View.GONE);
			}
			
		});
		
		mHandler = new MyHandler();
		new SetDataListThread().start();
		
	}
	
	public static MyHandler mHandler;
	
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				dataListView.setAdapter(new TalkCommentListAdapter(
						TreeholeCommentActivity.this, msgs));
				dataListView.setListViewHeightBasedOnChildren();
				break;

			case 1:
				new SetDataListThread().start();
				break;
				
			case 2:
				((TextView) findViewById(R.id.tv_treeholename_com)).setText(theTalk.sender.userName);
				((TextView) findViewById(R.id.tv_treeholecontent_com)).setText(theTalk.content);
				((TextView) findViewById(R.id.tv_treeholetime_com)).setText(theTalk.time);
				
				new Request4Image(theTalk.sender.avatar) 
				{
					@Override
					protected void onPostExecute(Bitmap result) 
					{
						if(result==null) 
							return;
						iv_treeholeitem_com.setImageBitmap(result);
					}
				}.execute();
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
				HashMap p = new HashMap();
				try {
					p.put("talkId", id);
					JSONObject ret = C.asyncPost(C.URLget_talk_comments, p);
					if(!(ret.getInt("status") == 0)){
						mHandler.sendToast("网络有问题！");
						return ;
					}
					
					msgs.clear();
					
					JSONArray talkcomments = ret.getJSONArray("talkComments");
					Log.d("talkc", talkcomments.length()+"");
					
					for(int i = 0 ;i < talkcomments.length();i++){
						JSONObject talkcomment = talkcomments.getJSONObject(i);
						JSONObject sender = talkcomment.getJSONObject("sender");
						Log.d("talkc", talkcomments.length()+talkcomment.getString("talkId"));
						msgs.add(new TalkComment(
								talkcomment.getString("id"),
								talkcomment.getString("time"),
								talkcomment.getString("content"),
								//talkcomment.getString("refId"),
								talkcomment.getString("senderId"),
								talkcomment.getString("talkId"),
								new User(
										sender.getString("userId"),
										sender.getString("account"),
										sender.getString("userName"),
										sender.getString("college"),
										sender.getString("location"),
										sender.getString("avatar")
										)
								));
						
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
					

			mHandler.sendEmptyMessage(0);
		}

	}
	
	private class getTalkThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap p = new HashMap();
			p.put("talkId", id);
			
			JSONObject res = C.asyncPost(C.URLget_talk_by_id, p);
			try {
				if(res.getInt("status") != 0){
					//makeToast(res.getString("description"));
					return;
				}
				
				//Log.d("talks","获取成功了");
				
				JSONObject item = res.getJSONObject("talk");
				JSONObject talksender = item.getJSONObject("sender");
//				JSONArray talkcomments = item.getJSONArray("comments");
//				
//				for(int j = 0 ; j < talkcomments.length();j++){
//					JSONObject talkcomment = talkcomments.getJSONObject(j);
//					JSONObject commentsender = talkcomment.getJSONObject("sender");
//					
//					msgs.add(new TalkComment(
//							talkcomment.getString("id"),
//							talkcomment.getString("time"),
//							talkcomment.getString("content"),
//							//talkcomment.getString("refId"),
//							talkcomment.getString("senderId"),
//							talkcomment.getString("talkId"),
//							new User(
//									commentsender.getString("userId"),
//									commentsender.getString("account"),
//									commentsender.getString("userName"),
//									commentsender.getString("college"),
//									commentsender.getString("location"),
//									commentsender.getString("acatar")
//									)
//							));
//				}
				
				theTalk = new Talk(
						item.getString("id"),
						item.getString("time"),
						item.getString("content"),
						item.getString("senderId"),
						new User(
								talksender.getString("userId"),
								talksender.getString("account"),
								talksender.getString("userName"),
								talksender.getString("college"),
								talksender.getString("location"),
								talksender.getString("avatar")
								)
						);
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mHandler.sendEmptyMessage(2);
		}
		
	}
	
	//举报的线程
	private class reportTalkThread extends Thread{
		private HashMap p;
		
		public reportTalkThread(HashMap p){
			this.p = p;
		}

		@Override
		public void run() {
			JSONObject res = C.asyncPost(C.URLreport_talk, p);
			Log.d("report","yse");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.treehole_comment, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(needrefresh) {
			needrefresh = false;
			new SetDataListThread().start();
			Log.v("treeholecomment", "Refresh");
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

}
