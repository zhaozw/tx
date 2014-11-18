package com.example.tx.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.dto.UserDetails;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;

public class C 
{
	static private final int Method=3;
	//传输所使用的协议模式
	static public final int TRANSPORT_METHOD_JSON=1;
	static public final int TRANSPORT_METHOD_XML=2;
	static public final int TRANSPORT_METHOD_MULITY=3;
	
	//短信验证地址
	private static String MsgValidationUrl = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
	
	//服务器IP
//	static public final String IP = "http://bittaoxue.sinaapp.com";

//	static public final String IP="http://112.126.67.182:8080";
//	static public final String IP="http://125.116.16.122:8888";
//	static public final String IP="http://10.12.38.114:8080";
	static public final String IP="http://112.126.67.182:8080";
//	static public final String IP="http://114.215.150.9:8080";
//	static public final String IP="http://114.215.150.9:8080/";//
//
	
	//WebView地址
		static public final String WebView_event=IP+"/Discovery/Activity";
		//关于
		static public final String WebView_about=IP+"/About";
		//帮助
		static public final String WebView_help=IP+"/Help";
		//建议
		static public final String WebView_advice=IP+"/Advice";
	
	//添加
		//发布商品
		static public final String URLitem_upload=IP+"/AddItem";
	
	//登陆与注册
		//登陆
		static public final String URLlogin=IP+"/Login";
		//校验手机号
		static public final String URLcheck_account=IP+"/CheckAccount";
		//校验用户名
		static public final String URLcheck_username=IP+"/CheckUserName";
		//请求验证码
		static public final String URLsend_checkcode=IP+"/SendCheckCode";
		//验证验证码
		static public final String URLverify_checkcode=IP+"/VerifyCheckCode";
		//注册
		static public final String URLregister=IP+"/Register";
		//重置密码
		static public final String URLreset_password=IP+"/ResetPassword";
		
	//我
		//查看个人资料
		static public final String URLget_my_profile=IP+"/Personal/GetMyProfile";
		//修改密码
		static public final String URLmodify_password=IP+"/Personal/ModifyPassword";
		//修改我的个人资料
		static public final String URLmodify_my_profile=IP+"/Personal/ModifyMyProfile";
		//获取我发布的所有商品的种类
		static public final String URLget_my_categories=IP+"/Personal/GetMyCategories";
		//获取我发布的商品
		static public final String URLget_my_items=IP+"/Personal/GetMyItems";
		//编辑我的商品
		static public final String URLedit_my_item=IP+"/Personal/EditMyItem";
		//删除我的商品
		static public final String URLdelete_my_item=IP+"/Personal/DeleteMyItem";
		//获取我关注的卖家
		static public final String URLget_my_favorite_sellers=IP+"/Personal/GetMyFavoriteSellers";
		//获取我收藏的宝贝
		static public final String URLget_my_favorite_items=IP+"/Personal/GetMyFavoriteItems";
		//商品下架
		static public final String URLunshelve_my_item=IP+"/Personal/UnshelveMyItem";
		//商品续期
		static public final String URLrenew_my_item=IP+"/Personal/RenewMyItem";
		//获取院系
		static public final String URLget_school=IP+"/Personal/GetSchools";
		//获取专业
		static public final String URLget_major=IP+"/Personal/GetMajors";
		//获取年级
		static public final String URLget_grade=IP+"/Personal/GetGrades";
		//获取我正在审核中的商品个数
		static public final String URLget_my_reviewing_item_count=IP+"/Personal/GetMyReviewingItemCount";
		//获取我正在审核中的商品
		static public final String URLget_my_reviewing_items=IP+"/Personal/GetMyReviewingItems";
	
	//市场
		//获取homepageimage
		static public final String URLget_homepage_images = IP + "/Market/GetHomePageImages";
		//获取所有善品种类
		static public final String URLget_categories=IP+"/Market/GetCategories";
		//获取商品列表
		static public final String URLget_items=IP+"/Market/GetItems";
		//获取商品详细信息
		static public final String URLget_item_details=IP+"/Market/GetItemDetails";
		//获取商品评论
		static public final String URLget_item_comments=IP+"/Market/GetItemComments";
		//撰写商品评论
		static public final String URLwrite_item_comments=IP+"/Market/WriteItemComment";
		//收藏商品
		static public final String URLadd_item_to_favorite=IP+"/Market/AddItemToFavorite";
		//获取卖家联系方式
		static public final String URLget_seller_contact=IP+"/Market/GetSellerContact";
		//获取卖家的详细信息
		static public final String URLget_seller_details=IP+"/Market/GetSellerDetails";
		//添加关注
		static public final String URLadd_seller_to_favorite=IP+"/Market/AddSellerToFavorite";
		//取消关组
		static public final String URLremove_seller_from_favorite = IP + "/Market/RemoveSellerFromFavorite";
		//取消收藏
		static public final String URLremove_item_from_favorite=IP+"/Market/RemoveItemFromFavorite";
		//取消关注
		static public final String URLunfocus_seller=IP+"/Market/RemoveSellerFromFavorite";
		//是否收藏商品
		static public final String URLis_favorite_item=IP+"/Market/IsFavoriteItem";
		//是否关注卖家
		static public final String URLis_favorite_seller=IP+"/Market/IsFavoriteSeller";
		//举报商品
		static public final String URLreport_Item = IP + "/Market/ReportItem";
	
	//消息
		//获取用户未读消息
		static public final String URLget_messages=IP+"/Message/GetMessages";
		//获取全部消息
		static public final String URLget_all_messages=IP+"/Message/GetAllMessages";
		//将一条消息设置为已读
		static public final String URLread_message=IP+"/Message/ReadMessage";
	
	//树洞
		//获取树洞列表
		static public final String URLget_talks=IP+"/Talk/GetTalks";
		//获取某一条树洞的评论
		static public final String URLget_talk_comments=IP+"/Talk/GetTalkComments";
		//发布树洞
		static public final String URLadd_talk=IP+"/Talk/AddTalk";
		//撰写树洞评论
		static public final String URLwrite_talk_comment=IP+"/Talk/WriteTalkComment";
		//获取树洞消息
		static public final String URLget_talk_by_id=IP+"/Talk/GetTalkById";
		//举报树洞信息
		static public final String URLreport_talk = IP + "/Talk/ReportTalk";
		
	//校内商店
		//获取所有商铺
		static public final String URLget_shops=IP+"/Shop/GetShops";
		//获取商铺中的商品
		static public final String URLget_shop_items=IP+"/Shop/GetShopItems";
		//获取商铺商品的评论信息
		static public final String URLget_shop_item_comments=IP+"/Shop/GetItemComments";
		//获取商铺商品的详细信息
		static public final String URLget_shop_item_Detail=IP+"/Shop/GetShopItemDetails";
		//评论商铺商品
		static public final String URLwrite_item_comment=IP+"/Shop/WriteItemComment";
		//获取我的订单
		static public final String URLget_my_orders=IP+"/Shop/GetMyOrders";
		//生成订单
		static public final String URLcreate_order=IP+"/Shop/CreateOrder";
		//获取商铺联系方式
		static public final String URLget_shop_contact=IP+"/Shop/GetShopContact";
		//删除我的订单
		static public final String URLdelete_shop_order=IP+"/Shop/DeleteOrder";
		
	/*
	 * *********************************以下为旧版URL，以上为新版URL********************
	 */
	
	
//	static public final String URLget_item_pic_b64 = IP + "/get_item_pic_b64/";
//	
//	
//	//添加商品
////	static public final String URLitem_upload = IP + "/item_upload/";
//	//下架
//	static public final String URLitem_offline = IP + "/item_offline/";
//	// static public final String URLitem_online = IP + "/item_online/";
//	//商品删除
//	static public final String URLitem_delete = IP + "/item_delete/";
//	//商品续期
//	static public final String URLitem_renew = IP + "/item_renew/";
//
//	static public final String URLget_comments = IP + "/get_comments/";
//	static public final String URLadd_comment = IP + "/add_comment/";
	/*
	 * ***********************以上为各个行为的IP，以下为个人信息****************************
	 */
	
	static public boolean logged = false;
	
	static public String account="";
	static public String userName="";
	static public String userId="";
	static public String college="";
	static public String location="";
	static public String avatar="";

	static public UserDetails myDetail=null;
	
	static public Bitmap avatar_bit=null;
	
	static public final int COMMODITY_TYPE_ALL=0;
	static public final int COMMODITY_TYPE_COMPUTER=1;
	static public final int COMMODITY_TYPE_PHONE=2;
	static public final int COMMODITY_TYPE_BOOK=3;
	
	private static LruCache<String, Bitmap> mMemoryCache;
	static {
		// 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
		// LruCache通过构造函数传入缓存值，以KB为单位。
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// 使用最大可用内存值的1/4作为缓存的大小。
		int cacheSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// 重写此方法来衡量每张图片的大小，默认返回图片数量。
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
//		bigMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//			@Override
//			protected int sizeOf(String key, Bitmap bitmap) {
//				// 重写此方法来衡量每张图片的大小，默认返回图片数量。
//				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
//			}
//		};
	}
	
	static public JSONObject asyncPost(String toUrl, HashMap content) {
		PostThread t = new PostThread(toUrl, content);
		t.start();
		try 
		{
			t.join();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		return t.res;
	}
	
	static class PostThread extends Thread {

		private String toUrl;
		private HashMap content;

		public JSONObject res;

		PostThread(String toUrl, HashMap content) 
		{
			this.toUrl = toUrl;
			this.content = content;
		}

		@Override
		public void run() 
		{
			res = C.post(toUrl, content);
		}
	}
	
	public static JSONObject post(String toUrl, HashMap content) {

		switch(Method)
		{
		case 1:
			try 
			{
				JSONObject req=new JSONObject();
				for(Object okey:content.keySet())
				{
					String key=okey.toString();
					req.put(key, content.get(key));
				}
				
				URL url = new URL(toUrl);
				HttpURLConnection connection=(HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				
				connection.connect();
				DataOutputStream dos=new DataOutputStream(connection.getOutputStream());
				
				dos.writeBytes(URLEncoder.encode(req.toString(), "UTF-8"));
				dos.flush();
				dos.close();
				
				BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				StringBuffer sb=new StringBuffer();
				String line;
				while((line=br.readLine())!=null)
					sb.append(line);
				br.close();
				connection.disconnect();
				return new JSONObject(sb.toString());
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			break;
		case 2:
			try
			{
				String req=Map2XML(content);
				
				URL url = new URL(toUrl);
				HttpURLConnection connection=(HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Charset", "UTF-8");

				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				
				connection.connect();
				DataOutputStream dos=new DataOutputStream(connection.getOutputStream());
				
				dos.writeBytes(req);
				dos.flush();
				dos.close();
				
				BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				StringBuffer sb=new StringBuffer();
				String line;
				while((line=br.readLine())!=null)
					sb.append(line);
				br.close();
				connection.disconnect();
				return new JSONObject(sb.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			break;
		case 3:
			try 
			{
				String BOUNDARY ="ZnGpDtePMx0KrHh_G0X99Yef9r8JZsRJSXC";

				URL url=new URL(toUrl);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
				
				OutputStream out = connection.getOutputStream();
				
				Map2Multi(content, BOUNDARY, out);
				
				out.write(("--" + BOUNDARY + "--\r\n").getBytes("UTF-8"));
				out.flush();
				out.close();
				
				
				// read response
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				String line;
				while((line= reader.readLine())!=null)
					response.append(line);
				reader.close();
				connection.disconnect();
				return new JSONObject(response.toString());

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		JSONObject ret = new JSONObject();
		try 
		{
			ret.put("status", -1);
			ret.put("description","网络有问题");
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return ret;
	}
	public static JSONObject MsgValidation(String mobile)
	{
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(MsgValidationUrl); 
		
		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

		
		int mobile_code = (int)((Math.random()*9+1)*100000);

	    String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。"); 

		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", "cf_taoxueapp"), 
			    new NameValuePair("password", "taoxue@123"), //密码可以使用明文密码或使用32位MD5加密
			    new NameValuePair("mobile", mobile), 
			    new NameValuePair("content", content),
		};
		
		method.setRequestBody(data);		
		
		JSONObject res=null;
		
		try {
			client.executeMethod(method);	
			
			String SubmitResult =method.getResponseBodyAsString();
					
			Document doc = DocumentHelper.parseText(SubmitResult); 
			Element root = doc.getRootElement();


			String code = root.elementText("code");	
			String msg = root.elementText("msg");	
			String smsid = root.elementText("smsid");	
			
			res=new JSONObject();
						
			if(code.equals("2")){
				res.put("status", 0);
				res.put("valicode", mobile_code);
			}
			else
				res.put("status", -1);
		}
		catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}
	public static void Map2Multi(HashMap map,String boundary,OutputStream out)
	{
		try
		{
			for(Object key:map.keySet())
			{
				if(map.get(key) instanceof File)
				{
					File file=(File)map.get(key);
					out.write(("--"+boundary+"\r\n").getBytes("UTF-8"));
					out.write(("Content-Disposition: form-data; name=\""+key+"\"; filename=\""+file.getName()+"\"\r\n").getBytes("UTF-8"));
					String type=file.getName().substring(file.getName().lastIndexOf("."),file.getName().length());
					if(type.equals(".jpg"))
						out.write(("Content-Type: image/jpeg\r\n\r\n").getBytes("UTF-8"));
					else if(type.equals(".png"))
						out.write(("Content-Type: image/png\r\n\r\n").getBytes("UTF-8"));
					
					InputStream fileIn = new FileInputStream(file);
					byte[] imageData = new byte[1024];
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					int length = 0;
					while ((length = fileIn.read(imageData)) != -1) {
						stream.write(imageData, 0, length);
					}
					fileIn.close();
					out.write(stream.toByteArray());
					out.write(("\r\n").getBytes("UTF-8"));
				}
				else if(map.get(key) instanceof File[])
				{
					File[] f=(File[])map.get(key);
					for(int j=0;j<f.length;j++)
					{
						File file=(File)f[j];
						out.write(("--"+boundary+"\r\n").getBytes("UTF-8"));
						out.write(("Content-Disposition: form-data; name=\""+key+"\"; filename=\""+file.getName()+"\"\r\n").getBytes("UTF-8"));
						String type=file.getName().substring(file.getName().lastIndexOf("."),file.getName().length());
						if(type.equals(".jpg"))
							out.write(("Content-Type: image/jpeg\r\n\r\n").getBytes("UTF-8"));
						else if(type.equals(".png"))
							out.write(("Content-Type: image/png\r\n\r\n").getBytes("UTF-8"));
						
						InputStream fileIn = new FileInputStream(file);
						byte[] imageData = new byte[1024];
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						int length = 0;
						while ((length = fileIn.read(imageData)) != -1) {
							stream.write(imageData, 0, length);
						}
						fileIn.close();
						out.write(stream.toByteArray());
						out.write(("\r\n").getBytes("UTF-8"));
					}
				}
				else if(map.get(key) instanceof String[])
				{
					String[] s=(String[])map.get(key);
					for(int i=0;i<s.length;i++)
					{
						String str=s[i];
						out.write(("--"+boundary+"\r\n").getBytes("UTF-8"));
						out.write(("Content-Disposition: form-data; name=\""+key.toString()+"\"\r\n\r\n").getBytes("UTF-8"));
						out.write((str+"\r\n").getBytes("UTF-8"));
					}
				}
				else
				{
					out.write(("--"+boundary+"\r\n").getBytes("UTF-8"));
					out.write(("Content-Disposition: form-data; name=\""+key.toString()+"\"\r\n\r\n").getBytes("UTF-8"));
					out.write((map.get(key).toString()+"\r\n").getBytes("UTF-8"));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static String Map2XML(HashMap map)
	{
		StringBuffer sb=new StringBuffer();
		for(Object key:map.keySet())
		{
			try 
			{
				sb.append(key);
				sb.append('=');
				sb.append(URLEncoder.encode((String) map.get(key), "utf-8"));
				sb.append('&');
			} 
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	public static void addBitmapToMemoryCache(String id, Bitmap bitmap) {
		if(id == null || bitmap == null)
			return;
		String key = String.valueOf(id);
		if (getBitmapFromMemCache(id) == null) {
			mMemoryCache.put(key, bitmap);
			InCache.put(id, bitmap);
		}
		else{
			mMemoryCache.remove(key);
			mMemoryCache.put(key, bitmap);
			InCache.put(id, bitmap);
		}
		
	}

	public static Bitmap getBitmapFromMemCache(String id) {
		
		if(mMemoryCache.get(id) != null){
			return  mMemoryCache.get(id);
		}
		else 
			return InCache.get(id);
	}
	
	/*
	 * ****************** image encoding operations *********************
	 */

	public static String PicBytesToBase64(byte[] data) {
		return Base64.encodeToString(data, Base64.URL_SAFE | Base64.NO_WRAP);
	}

	public static byte[] Base64ToPicBytes(String data) {
		return Base64.decode(data, Base64.URL_SAFE | Base64.NO_WRAP);
	}

	/*
	 * Bitmap to String
	 */
	public static String Bitmap2String(Bitmap b) throws IOException
	{
		String avatar="";
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG,1,baos);
		baos.flush();
		baos.close();
		byte[] bytes=baos.toByteArray();
		avatar=C.PicBytesToBase64(bytes);
		return avatar;
	}
	
	public static File Bitmap2File(Bitmap b)
	{
		File file=null;
		try 
		{
			String path=Environment.getExternalStorageDirectory().getPath() + "/"+getRandomFileName()+".png";
			file=new File(path);
			file.createNewFile();
			FileOutputStream out=new FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	@SuppressLint("SimpleDateFormat")
	public static String getRandomFileName()
	{
		return (new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))+System.currentTimeMillis();
	}
	
	//缓存到本地图片
	public static class InCache{
		public static void put(String id , Bitmap m){
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Taoxue/Image/");
			if(!file.exists())
				file.mkdirs();
			file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Taoxue/Image/" + id + ".png");
			try {
				if(!file.exists())
					file.createNewFile();
				FileOutputStream FOS = new FileOutputStream(file);
				if(m == null){
					Log.d("baocun", "这里");
				}
				m.compress(Bitmap.CompressFormat.PNG, 100, FOS);
				FOS.flush();
				FOS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static Bitmap get(String id){ 
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Taoxue/Image/" + id + ".png";
			 File mfile=new File(path);
			 if (mfile.exists()) {//若该文件存在
			     Bitmap bm = BitmapFactory.decodeFile(path);
			     return bm;
			 }
			 else
				 return null;
		}
		
	}
	
}