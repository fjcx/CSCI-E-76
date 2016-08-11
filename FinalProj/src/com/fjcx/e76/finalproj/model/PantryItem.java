/*
 * Author: Frank O'Connor
 * Final Project
 * Class CSCI E-76
 * 04/07/13
 */
package com.fjcx.e76.finalproj.model;

public class PantryItem {
	
	//private variables
    int _id;
    String _pitem_name;
    String _category;
    long _expiration;
    int _quantity;
    boolean _is_owned;
    int _purch_quantity;
    boolean _on_slist;
    String _shop;
    String _scale;

	// Empty constructor
    public PantryItem(){
 
    }
    
    // constructor
    public PantryItem(int id, String piname, String cat, long exp, int quant, boolean isOwned, boolean onshoplist, int purchQuant, String shop, String scale){
        this._id = id;
        this._pitem_name = piname;
        this._category = cat;
        this._expiration = exp;
        this._quantity = quant;
        this._is_owned = isOwned;
        this._purch_quantity = purchQuant;
        this._on_slist = onshoplist;
        this._shop = shop;
        this._scale= scale;
    }
 
    // constructor
    public PantryItem(String piname, String cat, long exp, int quant, boolean isOwned, boolean onshoplist, int purchQuant, String shop, String scale){
        this._pitem_name = piname;
    	this._category = cat;
        this._expiration = exp;
        this._quantity = quant;
        this._is_owned = isOwned;
        this._purch_quantity = purchQuant;
        this._on_slist = onshoplist;
        this._shop = shop;
        this._scale= scale;
    }
    
    // getting ID
    public int getID(){
        return this._id;
    }
 
    // setting id
    public void setID(int id){
        this._id = id;
    }
 
    // getting category
    public String getCategory(){
        return this._category;
    }
    
    // setting category
    public void setCategory(String cat){
        this._category = cat;
    }
    
    // getting PantryItemName
    public String getPantryItemName(){
        return this._pitem_name;
    }
 
    // setting PantryItemName
    public void setPantryItemName(String piname){
        this._pitem_name = piname;
    }
    
    // getting expiration
    public long getExpiration(){
        return this._expiration;
    }
 
    // setting expiration
    public void setExpiration(long exp){
        this._expiration = exp;
    }
 
    // getting quantity
    public int getQuantity(){
        return this._quantity;
    }
 
    // setting quantity
    public void setQuantity(int quant){
        this._quantity = quant;
    }
    
    // getting isowned
    public boolean getIsOwned(){
        return this._is_owned;
    }
 
    // setting isowned
    public void setIsOwned(boolean isOwned){
        this._is_owned = isOwned;
    }
    
    // getting onslist
    public boolean getOnSList(){
        return this._on_slist;
    }
 
    // setting onslist
    public void setOnSList(boolean onSList){
        this._on_slist = onSList;
    }
    
    // getting quantity
    public int getPurchQuantity(){
        return this._purch_quantity;
    }
 
    // setting quantity
    public void setPurchQuantity(int purchQuant){
        this._purch_quantity = purchQuant;
    }
    
    // get shop to purchase item in
    public String getShop() {
		return _shop;
	}

    // set shop to purchase item in
	public void setShop(String shop) {
		this._shop = shop;
	}
	
	// set quantity scale
	public String getScale() {
		return _scale;
	}
	
	// get quantity scale
	public void setScale(String scale) {
		this._scale = scale;
	}

}
