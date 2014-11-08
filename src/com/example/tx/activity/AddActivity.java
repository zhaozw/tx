package com.example.tx.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


















import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.dto.Category;
import com.example.tx.dto.Categorys;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AddActivity extends BaseActivity {
	
	private final String imgLoaction=
			"file://"+ Environment.getExternalStorageDirectory().getPath() + "/temp1.png";
			
	private final Uri imageUri= 
			Uri.parse(imgLoaction);
	private File[] pics=new File[3];
	//private File pic;
	private int count=0;
	private boolean isExchange;
	private int exchangeNum;
	private ImageView changeView;
	
	private Bitmap picBitmap = null;
	
	private AlertDialog ad;
			
	
	private EditText et_name;
	private EditText et_price;
	private EditText et_description;
	private ImageView iv_upload_pic;
	private Spinner spinner_add;
	
	private List<String> list = new ArrayList<String>();
	private String categoryId;
	
	
	private boolean flag = false;
	
	//private static final String IMAGE_FILE_LOCATION = "file://"
			//+ Environment.getExternalStorageDirectory().getPath() + "/temp.png";
	//private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
	
	private static final int aspectX = 1;
	private static final int aspectY = aspectX;
	private static final int outputX = 320;
	private static final int outputY = outputX;
	
	//private String[] pics=new String[10];
	
	//用于spinner的分类
	private List<String> cateids = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		et_name = (EditText) findViewById(R.id.et_goodname);
		et_price = (EditText) findViewById(R.id.et_goodprice);
		et_description = (EditText) findViewById(R.id.et_gooddescription);
		iv_upload_pic = (ImageView)findViewById(R.id.iv_upload_pic);
		spinner_add = (Spinner) findViewById(R.id.spinner_add); 
		
		mHandler = new MyHandler();
		
		//spinner设置
		if(Categorys.categorys == null || Categorys.categorys.size() < 1)
		{
			Log.d("setcategory","start");
			new setCategoryThread().start();
		}
		for(int i = 1 ;i < Categorys.categorys.size() ; i ++){
			if(!Categorys.categorys.get(i).name.equals("免费专区")){
				list.add(Categorys.categorys.get(i).name);
				cateids.add(Categorys.categorys.get(i).id);
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner_add.setAdapter(adapter);
		spinner_add.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {
				categoryId = cateids.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				categoryId = cateids.get(0);
			}
			
		});
		
		((Button) findViewById(R.id.b_add_no)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				et_name.setText("");
				et_price.setText("");
				et_description.setText("");
				spinner_add.setSelection(0);
				iv_upload_pic.setImageResource(R.drawable.commodity_add);
				flag = false;
				
				MainActivity.that.radio2.setChecked(false);
				switch (MainActivity.that.currenttag){
				case 0:
					MainActivity.that.radio0.setChecked(true);
					break;
				case 1:
					MainActivity.that.radio1.setChecked(true);
					break;
				case 3:
					MainActivity.that.radio3.setChecked(true);
					break;
				case 4:
					MainActivity.that.radio4.setChecked(true);
					break;
				}
				finish();
			}
			
		});
		
		((Button) findViewById(R.id.b_add)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String name = et_name.getText().toString().trim();
				
				makeToast("正在操作...");
				
				
				if(name.equals("")) {
					makeToast("请输入物品名称！");
					return;
				}
				
				if(picBitmap == null) {
					makeToast("请上传物品图片！");
					return;
				}
				
				float price = 0;
				if(et_price.getText().toString().equals("")){
					makeToast("请输入价格");
					return;
				}
				try {
					price = Float.valueOf(et_price.getText().toString());
				} catch(Exception e) {
					makeToast("请输入价格！");
					return;
				}
								
				String type_str = "";
				String intro = et_description.getText().toString().trim();
				
				String pic_b64 = "";
				if(picBitmap != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					picBitmap.compress(Bitmap.CompressFormat.PNG, 1, baos);
					try {
						baos.flush();
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					pic_b64 = C.PicBytesToBase64(baos.toByteArray());
				}
				
				if(intro.equals("")){
					makeToast("请描述一下您的商品");
					return;
				}
				if(price < 0 || price > 50000){
					makeToast("价格应在0-50000元之内");
					return;
				}
				
				File[] pi=new File[count];
				for(int i=0;i<count;i++)
					pi[i]=pics[i];
				
				try {

					HashMap p = new HashMap();

					p.put("itemName", name);
					p.put("price", price);
					p.put("categoryId", categoryId);
					p.put("details", intro);
					p.put("images", pi);
					p.put("userId", C.userId);
					
					Log.d("upload",name+price+categoryId+intro+C.userId+pics);
					
					new uploadThread(p).start();
					
					MainActivity.that.tabhost.setCurrentTabByTag("tab4");
					MainActivity.that.radio4.setChecked(true);
					MainActivity.that.currenttag = 4;
					finish();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			
		});
		
		iv_upload_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(count >= 3)
					return;
				ad=new AlertDialog.Builder(AddActivity.this).setTitle("选择上传方法").setSingleChoiceItems(new String[]{"拍照","从照片中选择"}, 0, new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which)
						{
						case 0:
							Intent intent0 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							intent0.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(intent0, 1);
							ad.cancel();
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
							ad.cancel();
							break;
						}
					}
				}).setNegativeButton("取消", null).show();
			}
		});
		
		
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
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	class setCategoryThread extends Thread{

		@Override
		public void run() {
			HashMap p = new HashMap();
			JSONObject res = C.asyncPost(C.URLget_categories, p);
			try {
				
				if( !(res.getInt("status") == 0) ) {
					//mHandler.sendToast("网络有问题！");
					return ;
				}
				
				JSONArray categories = res.getJSONArray("categories");
				for(int i=0;i<categories.length();i++) {
					JSONObject category = categories.getJSONObject(i);
					Categorys.categorys.add(new Category(
							null,
							category.getString("id"),
							category.getString("name"),
							category.getString("image")
							));
					Log.d("category",category.getString("name"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				
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
	
	private class uploadThread extends Thread{
		HashMap p;
		public uploadThread(HashMap p){
			this.p = p;
		}
		@Override
		public void run() {
			JSONObject ret = C.asyncPost(C.URLitem_upload, p);
			
			try {
				if(!(ret.getInt("status") == 0) ){
					mHandler.sendToast("网络有问题！"+ret.getString("description"));
					return ;
				}else{
					mHandler.sendToast("上传成功！");
					//MainActivity.that.tabhost.setCurrentTabByTag("tab4");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode)
		{
		case 1://照相
			if (resultCode != RESULT_OK) {
				return;
			}
			cropImageUri(imageUri, 2);
			flag = true;
			break;
		case 2://本地照片
			if(resultCode==RESULT_CANCELED)
				return;
//			if (picBitmap != null && !picBitmap.isRecycled()) {
//				picBitmap.recycle();
//			}
			picBitmap = decodeUriAsBitmap(imageUri);
			//pic = C.Bitmap2File(picBitmap);
			if(isExchange){
				pics[exchangeNum] = C.Bitmap2File(picBitmap);
				if(changeView != null)
					changeView.setImageBitmap(picBitmap);
				isExchange = false;
				break;
			}
			pics[count++]=C.Bitmap2File(picBitmap);
			
			//iv_upload_pic.setImageBitmap(picBitmap);

			LayoutParams lp=new LinearLayout.LayoutParams(150,150);// LinearLayout.LayoutParams.WRAP_CONTENT);
			((MarginLayoutParams) lp).setMargins(10, 0, 0, 10);
			ImageView iv = new ImageButton(this);
			iv.setImageBitmap(picBitmap);
			iv.setLayoutParams(lp);
			iv.setTag(count - 1);
			iv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(final View v) {
					ad=new AlertDialog.Builder(AddActivity.this).setTitle("选择上传方法").setSingleChoiceItems(new String[]{"拍照","从照片中选择"}, 0, new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch(which)
							{
							case 0:
								Intent intent0 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								intent0.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
								startActivityForResult(intent0, 1);
								ad.cancel();
								isExchange = true;
								exchangeNum = (Integer) v.getTag();
								changeView = (ImageView) v;
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
								ad.cancel();
								isExchange = true;
								exchangeNum = (Integer) v.getTag();
								changeView = (ImageView) v;
								break;
							}
							
						}
					}).setNegativeButton("取消", null).show();
				}
				
			});
			((GridLayout) findViewById(R.id.gl_add)).addView(iv,0);
			flag = true;
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			MainActivity.that.radio2.setChecked(false);
			switch (MainActivity.that.currenttag){
			case 0:
				MainActivity.that.radio0.setChecked(true);
				break;
			case 1:
				MainActivity.that.radio1.setChecked(true);
				break;
			case 3:
				MainActivity.that.radio3.setChecked(true);
				break;
			case 4:
				MainActivity.that.radio4.setChecked(true);
				break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}
