package com.example.tx.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.dto.Categorys;
import com.example.tx.dto.Image;
import com.example.tx.dto.Item;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;;

public class CommodityItemDetailActivity extends BaseActivity {
	
	private final String imgLoaction="file://"+ Environment.getExternalStorageDirectory().getPath() + "/temp.png";
	private final Uri imageUri=Uri.parse(imgLoaction);
	
	private static final int aspectX = 1;
	private static final int aspectY = aspectX;
	private static final int outputX = 320;
	private static final int outputY = outputX;
	
	private Bitmap picBitmap = null;
	private AlertDialog ad;
	
	private File[] pics=new File[10];
	private int count=0;
	private boolean isExchange;
	private int exchangeNum;
	private ImageView changeView;
	
	public static CommodityItemDetailActivity that;
	
	private TextView commodity_name,commodity_price,commodity_description;
	private ImageView image_add;
	private Spinner spinner_class;
	
	private List<String> list;      //存储种类的文字，spinner显示文字，根据选取item的position获得Category中对应的Id
	private String categoryId;      //存储选取的种类的Id；默认为原来的Id；
	
	private Item msg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_commodity_item_detail);
		that=this;

		int index=getIntent().getIntExtra("index", -1);
		if(index==-1)
		{
			makeToast("找不到index");
			return ;
		}
		msg=MineCommodityDetailActivity.that.msgs.get(index);
		
		commodity_name=(TextView) findViewById(R.id.commodity_name);
		commodity_name.setText(msg.itemName);
		spinner_class=(Spinner) findViewById(R.id.spiner_class);
		commodity_price=(TextView) findViewById(R.id.commidty_price);
		commodity_price.setText(String.valueOf(msg.price));
		commodity_description=(TextView) findViewById(R.id.commodity_description);
		commodity_description.setText(msg.details);
		
		image_add=(ImageView)findViewById(R.id.commodity_pic_add);
		
		//公共框
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("编辑商品");
		ImageButton ret=(ImageButton)findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		TextView edit=(TextView)findViewById(R.id.common_title_bar_login);
		edit.setText("完成");
		edit.setVisibility(View.VISIBLE);
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					save();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			private void save() throws JSONException {
				HashMap req=new HashMap();
				req.put("itemId", msg.itemId);
				req.put("itemName",commodity_name.getText().toString());

				String categoryName=spinner_class.getSelectedItem().toString();
				int ii=-1;
				for(int i=0;i<Categorys.getCategorys().size();i++)
				{
					if(Categorys.getCategorys().get(i).name.trim().equals(categoryName))
					{
						ii=i;
						break;
					}
				}
				String caId=null;
				if(ii!=-1)
					caId=Categorys.getCategorys().get(ii).id;
				else
				{
					makeToast("后台错误");
					return;
				}
				
				float pri =  Float.parseFloat(commodity_price.getText().toString());
				if(pri > 50000 || pri < 0){
					makeToast("价格只能在0-5000元之间");
					return;
				}

				req.put("price", Float.parseFloat(commodity_price.getText().toString()));
				req.put("categoryId", caId);
				req.put("details",commodity_description.getText().toString());
				File[] p=new File[count];
				for(int i=0;i<count;i++)
					p[i]=pics[i];
				req.put("images", p);
				req.put("userId", C.userId);
				makeToast("正在提交，请等待");
				new PostTask(C.URLedit_my_item, req)
				{
					@Override
					protected void onPostExecute(JSONObject result) {
						try 
						{
							if(result.getInt("status")!=0)
							{
								makeToast("保存失败，连接服务器超时");
							}
							else
							{
								makeToast("修改成功");
								MineCommodityDetailActivity.mhandler.sendEmptyMessage(1);
								finish();
							}
						} 
						catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
				}.execute();
			}
		});
		
		//spinner设置
		int selected=-1;
		list=new ArrayList<String>();
		for(int i = 1 ;i < Categorys.categorys.size() ; i ++){
			list.add(Categorys.getCategorys().get(i).name);
			if(Categorys.getCategorys().get(i).id.equals(msg.categoryId))
				selected=i;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_class.setAdapter(adapter);
		spinner_class.setSelection(selected - 1);
		spinner_class.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {
				categoryId = Categorys.categorys.get(position+1).id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				// TODO 这里改为原来item的种类Id
				categoryId = Categorys.categorys.get(1).id;
			}
			
		});

		for(int i=0;i<msg.image.size();i++)
		{
			final Image im=msg.image.get(i);
			Bitmap b=C.getBitmapFromMemCache(im.picId);
			if(b==null)
			{
				new Request4Image(im.picUrl)
				{
					@Override
					protected void onPostExecute(Bitmap result) {
						C.addBitmapToMemoryCache(im.picId, result);
						addPicAtFront(result);
						pics[count++]=C.Bitmap2File(result);
						if(count==3)
							image_add.setVisibility(View.GONE);
					}
				}.execute();
			}
			else
			{
				addPicAtFront(b);
				pics[count++]=C.Bitmap2File(b);
				if(count==3)
					image_add.setVisibility(View.GONE);
			}
		}
		
		image_add.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				ad=new AlertDialog.Builder(CommodityItemDetailActivity.this).setTitle("选择上传方法").setSingleChoiceItems(new String[]{"拍照","从照片中选择"}, 0, new DialogInterface.OnClickListener() 
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
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		switch(requestCode)
		{
		case 1://照相
			if (resultCode != RESULT_OK) {
				return;
			}
			cropImageUri(imageUri, 2);
			break;
		case 2://本地照片
			if(resultCode==RESULT_CANCELED)
				return;
//			if (picBitmap != null && !picBitmap.isRecycled()) {
//				picBitmap.recycle();
//			}
			picBitmap = decodeUriAsBitmap(imageUri);
			if(isExchange){
				pics[exchangeNum] = C.Bitmap2File(picBitmap);
				if(changeView != null)
					changeView.setImageBitmap(picBitmap);
				isExchange = false;
				break;
			}
			pics[count++]=C.Bitmap2File(picBitmap);
//			ImageView img=new ImageView(this);
//			img.setImageBitmap(picBitmap);
//			LayoutParams lp=new LinearLayout.LayoutParams(80, 80);
//			lp.setMargins(10, 0, 0, 0);
//			img.setLayoutParams(lp);
//			((LinearLayout)findViewById(R.id.commodity_item_detail_linearlayout_pic)).addView(img, 0);
			addPicAtFront(picBitmap);
			break;
		default:
			break;
		}
	}
	private void addPicAtFront(Bitmap m)
	{
		ImageView img=new ImageView(this);
		img.setImageBitmap(m);
		LayoutParams lp=new LinearLayout.LayoutParams(120, 120);
		lp.setMargins(10, 0, 0, 0);
		img.setLayoutParams(lp);
		img.setTag(count - 1);
		img.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				ad=new AlertDialog.Builder(CommodityItemDetailActivity.this).setTitle("选择上传方法").setSingleChoiceItems(new String[]{"拍照","从照片中选择"}, 0, new DialogInterface.OnClickListener() 
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
		((LinearLayout)findViewById(R.id.commodity_item_detail_linearlayout_pic)).addView(img, 0);
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
