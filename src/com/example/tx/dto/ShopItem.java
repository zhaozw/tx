package com.example.tx.dto;

public class ShopItem {
	public String itemId;          // 商品ID
	public String itemName;        // 商品名称
	public float price;            // 价格
    public String details;         // 详情
    public String clazz;           // 分类
    public String shopId;          // 所属商铺ID
    public int status;	           // 状态，0表示正常，1表示已删除
    //added by fjb stock
    public long stock;				//库存量

    /* 附加属性 */
    public Image[] images;
    public Comment[] comments;
    
    public ShopItem()
    {
    	
    }
    
    public ShopItem(String itemId,String itemName,float price,String details,String clazz,String shopId,int status,Image[] images,Comment[] comments)
    {
    	this.itemId=itemId;
    	this.itemName=itemName;
    	this.price=price;
    	this.details=details;
    	this.clazz=clazz;
    	this.shopId=shopId;
    	this.status=status;
    	this.images=images;
    	this.comments=comments;
    }
}
