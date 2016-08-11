/*
 * Author: Frank O'Connor
 * Final Project
 * Class CSCI E-76
 * 04/07/13
 */
package com.fjcx.e76.finalproj;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fjcx.e76.finalproj.model.PantryItem;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "PantryItemManager.db";
 
    // PantryItem table name
    private static final String TABLE_PANTRYITEMS = "pantryitems";
 
    // PantryItem Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PITEM_NAME = "fitem_id";
    private static final String KEY_CATEG = "category";
    private static final String KEY_EXPIR = "expiration";
    private static final String KEY_QUANT = "quantity";
    private static final String KEY_OWNED = "isowned";
    private static final String KEY_ONSLIST = "onslist";
    private static final String KEY_PURCHQUANT = "purchasequant";
    private static final String KEY_SHOP = "shop";
    private static final String KEY_SCALE = "scale";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	// remove drop table part
        String CREATE_PANTRYITEM_TABLE = "CREATE TABLE " + TABLE_PANTRYITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
        		+ KEY_PITEM_NAME + " TEXT,"
        		+ KEY_CATEG + " TEXT," 
                + KEY_EXPIR + " TEXT," 
                + KEY_QUANT  + " TEXT,"
                + KEY_OWNED  + " INTEGER, "
                + KEY_ONSLIST  + " INTEGER, "
                + KEY_PURCHQUANT  + " TEXT, "
                + KEY_SHOP  + " TEXT, "
                + KEY_SCALE  + " TEXT  )";
        db.execSQL(CREATE_PANTRYITEM_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRYITEMS);
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    
    // Adding new PantryItem
    public void addPantryItem(PantryItem pantryitem) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_PITEM_NAME, pantryitem.getPantryItemName().toLowerCase()); // Food Name
        values.put(KEY_CATEG, pantryitem.getCategory().toLowerCase()); // Food Category
        values.put(KEY_EXPIR, pantryitem.getExpiration()); // Expiration Date as long
        values.put(KEY_QUANT, pantryitem.getQuantity()); // Quantity as int
        int isowned = (pantryitem.getIsOwned()==true)? 1:0;
        values.put(KEY_OWNED, isowned); // is owned
        int onshoplist = (pantryitem.getOnSList()==true)? 1:0;
        values.put(KEY_ONSLIST, onshoplist); // on shopping list
        values.put(KEY_PURCHQUANT, pantryitem.getPurchQuantity()); // Quantity to purchase
        values.put(KEY_SHOP, pantryitem.getShop()); // shop to purchase in
        values.put(KEY_SCALE, pantryitem.getScale()); // quantity scale
 
        // Inserting Row
        db.insert(TABLE_PANTRYITEMS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single pantryitem by id
    public PantryItem getPantryItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        PantryItem pantryitem = null;
        Cursor cursor = db.query(TABLE_PANTRYITEMS, new String[] { KEY_ID,
        		KEY_PITEM_NAME, KEY_CATEG, KEY_EXPIR, KEY_QUANT, KEY_OWNED, KEY_ONSLIST, KEY_PURCHQUANT, KEY_SHOP, KEY_SCALE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            try{
            	if(cursor.getString(0)!=null){
            		boolean isOwned = (Integer.parseInt(cursor.getString(5))==1)?true:false;
            		boolean onSList = (Integer.parseInt(cursor.getString(6))==1)?true:false;
            		pantryitem = new PantryItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
            				cursor.getString(2), Long.parseLong(cursor.getString(3)), Integer.parseInt(cursor.getString(4)),isOwned,
            				onSList,Integer.parseInt(cursor.getString(7)),cursor.getString(8),cursor.getString(9));
            	}
            }catch(Exception e){
            	// our table could be null, review and deal with this
            }
        }
        return pantryitem;
    }
    
    // Getting single pantryitem by name
    public PantryItem getPantryItem(String pitemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        PantryItem pantryitem = null;
        Cursor cursor = db.query(TABLE_PANTRYITEMS, new String[] { KEY_ID,
        		KEY_PITEM_NAME, KEY_CATEG, KEY_EXPIR, KEY_QUANT, KEY_OWNED, KEY_ONSLIST, KEY_PURCHQUANT, KEY_SHOP, KEY_SCALE}, KEY_PITEM_NAME + "=?",
                new String[] { String.valueOf(pitemName) }, null, null, null, null);
        
        if (cursor != null){
            cursor.moveToFirst();
            try{
            	if(cursor.getString(0)!=null){
            		boolean isOwned = (Integer.parseInt(cursor.getString(5))==1)?true:false;
            		boolean onSList = (Integer.parseInt(cursor.getString(6))==1)?true:false;
            		pantryitem = new PantryItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
            				cursor.getString(2), Long.parseLong(cursor.getString(3)), Integer.parseInt(cursor.getString(4)),isOwned,
            				onSList,Integer.parseInt(cursor.getString(7)),cursor.getString(8),cursor.getString(9));
            	}
            }catch(Exception e){
            	// our table could be null, review and deal with this
            }
        }
        return pantryitem;
    }
    
    // Getting All PantryItems
    public List<PantryItem> getAllPantryItems(boolean ownedflag) {
        List<PantryItem> pantryItemList = new ArrayList<PantryItem>();
        // Select All Query
        String selectQuery = null;
        if(ownedflag){
        	selectQuery = "SELECT  * FROM " + TABLE_PANTRYITEMS +" WHERE "+ KEY_OWNED + " = '1'";;
        }else{
        	selectQuery = "SELECT  * FROM " + TABLE_PANTRYITEMS;
        }
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	PantryItem pantryitem = new PantryItem();
            	pantryitem.setID(Integer.parseInt(cursor.getString(0)));
            	pantryitem.setPantryItemName(cursor.getString(1));
            	pantryitem.setCategory(cursor.getString(2));
            	pantryitem.setExpiration(Long.parseLong(cursor.getString(3)));
            	pantryitem.setQuantity(Integer.parseInt(cursor.getString(4)));
            	boolean isOwned = (Integer.parseInt(cursor.getString(5))==1)?true:false;
            	pantryitem.setIsOwned(isOwned);
            	boolean onSList = (Integer.parseInt(cursor.getString(6))==1)?true:false;
            	pantryitem.setOnSList(onSList);
            	pantryitem.setPurchQuantity(Integer.parseInt(cursor.getString(7)));
            	pantryitem.setShop(cursor.getString(8));
            	pantryitem.setScale(cursor.getString(9));
                // Adding pantryitem to list
            	pantryItemList.add(pantryitem);
            } while (cursor.moveToNext());
        }
        return pantryItemList;
    }
    
    // Get distinct groups
    public List<String> getPantryGroups(boolean ownedflag) {
        List<String> pantryGroups = new ArrayList<String>();
        // Select All Query
        String selectQuery = null;
        if(ownedflag){
        	selectQuery = "SELECT DISTINCT "+ KEY_CATEG + " FROM " + TABLE_PANTRYITEMS +" WHERE "+ KEY_OWNED + " = '1'";;
        }else{
        	selectQuery = "SELECT DISTINCT "+ KEY_CATEG + " FROM " + TABLE_PANTRYITEMS;
        }
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	String category = cursor.getString(0);
                // Adding pantryitem to list
            	pantryGroups.add(category);
            } while (cursor.moveToNext());
        }
        return pantryGroups;
    }
    
    
    // Get distinct shop groups for shopping list
    public List<String> getSListGroups(boolean onshoplist) {
        List<String> shopGroups = new ArrayList<String>();
        // Select All Query
        String selectQuery = null;
        if(onshoplist){
        	selectQuery = "SELECT DISTINCT "+ KEY_SHOP + " FROM " + TABLE_PANTRYITEMS +" WHERE "+ KEY_ONSLIST + " = '1'";;
        }else{
        	selectQuery = "SELECT DISTINCT "+ KEY_SHOP + " FROM " + TABLE_PANTRYITEMS;
        }
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	String category = cursor.getString(0);
                // Adding pantryitem to list
            	shopGroups.add(category);
            } while (cursor.moveToNext());
        }
        if(!shopGroups.contains("Other")){
            shopGroups.add("Other");       	
        }
        return shopGroups;
    }
    
 // Getting All PantryItems
    public List<PantryItem> getPantryItemsInCategory(String category, boolean ownedflag) {
        List<PantryItem> pantryItemList = new ArrayList<PantryItem>();
        // Select All Query
        String selectQuery = null;
        if(ownedflag){
        	selectQuery = "SELECT  * FROM " + TABLE_PANTRYITEMS +" WHERE "+ KEY_CATEG + " = '"+ category+"'" +" AND "+ KEY_OWNED + " = '1'";;
        }else{
        	selectQuery = "SELECT  * FROM " + TABLE_PANTRYITEMS +" WHERE "+ KEY_CATEG + " = '"+ category+"'";
        }
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	PantryItem pantryitem = new PantryItem();
            	pantryitem.setID(Integer.parseInt(cursor.getString(0)));
            	pantryitem.setPantryItemName(cursor.getString(1));
            	pantryitem.setCategory(cursor.getString(2));
            	pantryitem.setExpiration(Long.parseLong(cursor.getString(3)));
            	pantryitem.setQuantity(Integer.parseInt(cursor.getString(4)));
            	boolean isOwned = (Integer.parseInt(cursor.getString(5))==1)?true:false;
            	pantryitem.setIsOwned(isOwned);
            	boolean onSList = (Integer.parseInt(cursor.getString(6))==1)?true:false;
            	pantryitem.setOnSList(onSList);
            	pantryitem.setPurchQuantity(Integer.parseInt(cursor.getString(7)));
            	pantryitem.setShop(cursor.getString(8));
            	pantryitem.setScale(cursor.getString(9));
                // Adding pantryitem to list
            	pantryItemList.add(pantryitem);
            } while (cursor.moveToNext());
        }
        return pantryItemList;
    }
    
    // Getting All Shopping List Items in shop category
    public List<PantryItem> getSListItemsInShopCat(String category, boolean onSlist) {
        List<PantryItem> pantryItemList = new ArrayList<PantryItem>();
        // Select All Query
        String selectQuery = null;
        if(onSlist){
        	selectQuery = "SELECT  * FROM " + TABLE_PANTRYITEMS +" WHERE "+ KEY_SHOP + " = '"+ category+"'" +" AND "+ KEY_ONSLIST + " = '1'";;
        }else{
        	selectQuery = "SELECT  * FROM " + TABLE_PANTRYITEMS +" WHERE "+ KEY_SHOP + " = '"+ category+"'";
        }
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	PantryItem pantryitem = new PantryItem();
            	pantryitem.setID(Integer.parseInt(cursor.getString(0)));
            	pantryitem.setPantryItemName(cursor.getString(1));
            	pantryitem.setCategory(cursor.getString(2));
            	pantryitem.setExpiration(Long.parseLong(cursor.getString(3)));
            	pantryitem.setQuantity(Integer.parseInt(cursor.getString(4)));
            	boolean isOwned = (Integer.parseInt(cursor.getString(5))==1)?true:false;
            	pantryitem.setIsOwned(isOwned);
            	boolean onSList = (Integer.parseInt(cursor.getString(6))==1)?true:false;
            	pantryitem.setOnSList(onSList);
            	pantryitem.setPurchQuantity(Integer.parseInt(cursor.getString(7)));
            	pantryitem.setShop(cursor.getString(8));
            	pantryitem.setScale(cursor.getString(9));
                // Adding pantryitem to list
            	pantryItemList.add(pantryitem);
            } while (cursor.moveToNext());
        }
        return pantryItemList;
    }
    
    
 
    // Updating single pantryitem
    public int updatePantryItem(PantryItem pantryitem) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_PITEM_NAME, pantryitem.getPantryItemName().toLowerCase());
        values.put(KEY_CATEG, pantryitem.getCategory().toLowerCase());
        values.put(KEY_EXPIR, pantryitem.getExpiration());
        values.put(KEY_QUANT, pantryitem.getQuantity());
        int isowned = (pantryitem.getIsOwned()==true)? 1:0;
        values.put(KEY_OWNED, isowned); // is owned
        int onslist = (pantryitem.getOnSList()==true)? 1:0;
        values.put(KEY_ONSLIST, onslist); // on shopping list
        values.put(KEY_PURCHQUANT, pantryitem.getPurchQuantity());
        values.put(KEY_SHOP, pantryitem.getShop());
        values.put(KEY_SCALE, pantryitem.getScale());
 
        // updating row
        return db.update(TABLE_PANTRYITEMS, values, KEY_ID + " = ?", new String[] { String.valueOf(pantryitem.getID()) });
    }
    
    // Updating single pantryitem
    public int updatePItemOwned(String pantryitemName, boolean isOwned) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        int isowned = (isOwned==true)? 1:0;
        values.put(KEY_OWNED, isowned); // is owned
 
        // updating row
        return db.update(TABLE_PANTRYITEMS, values, KEY_PITEM_NAME + " = ?", new String[] { String.valueOf(pantryitemName.toLowerCase()) });
    }
    
    // Updating whether item is on shopping list or not
    public int updatePItemSList(String pantryitemName, boolean onSList) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        int onslist = (onSList==true)? 1:0;
        values.put(KEY_ONSLIST, onslist); // is owned
 
        // updating row
        return db.update(TABLE_PANTRYITEMS, values, KEY_PITEM_NAME + " = ?", new String[] { String.valueOf(pantryitemName.toLowerCase()) });
    }
    
    // Updating which shop item is to be bought in
    public int updatePItemShop(String pantryitemName, String shopname) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_SHOP, shopname); // is owned
 
        // updating row
        return db.update(TABLE_PANTRYITEMS, values, KEY_PITEM_NAME + " = ?", new String[] { String.valueOf(pantryitemName.toLowerCase()) });
    }
 
    // Deleting single pantryitem
    public void deletePantryItem(PantryItem pantryitem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRYITEMS, KEY_ID + " = ?", new String[] { String.valueOf(pantryitem.getID()) });
        db.close();
    }
    
    // Deleting all pantryitems
    public void deleteAllPantryItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Review - probably a cleaner way of deleting everything 
        db.delete(TABLE_PANTRYITEMS, KEY_ID + " <> ?", new String[] { String.valueOf("-1") });
        db.close();
    }
 
    // Getting pantryitems Count
    public int getPantryItemCount() {
        String countQuery = "SELECT  COUNT(*) FROM " + TABLE_PANTRYITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowcount = 0;
        if (cursor != null) {
        	cursor.moveToFirst();                       // Always one row returned.
            rowcount = cursor.getInt(0);
        }
        cursor.close();
 
        return rowcount;
    }
    
	// populate sample list of options for pantry if none exist
    public void prePopulateFoodItems(){
    	// Condiments
    	addPantryItem(new PantryItem("chicken broth","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("beef broth","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("chutney","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("hoisin sauce","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("honey","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("horseradish","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("gelatin","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("ketchup","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("maple syrup","condiments", 0,0,true,false,0,"CVS",""));
		addPantryItem(new PantryItem("mayonnaise","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("molasses","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("mustard","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("peanut butter","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("pickles","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("salsa","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("sliced pimientos","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("soy sauce","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("Terikiyi sauce","condiments", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("Worcestershire sauce","condiments", 0,0,false,false,0,"CVS",""));
		// Baking
		addPantryItem(new PantryItem("all-purpose flour","baking", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("unbleached flour or bread flour","baking", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("granulated sugar","baking", 0,0,false,false,0,"Other","")); 
		addPantryItem(new PantryItem("powdered sugar","baking", 0,0,true,false,0,"Other","")); 
		addPantryItem(new PantryItem("brown sugar","baking", 0,0,false,false,0,"Other","")); 
		addPantryItem(new PantryItem("cornstarch","baking", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("baking powder","baking", 0,0,false,true,2,"CVS","g")); 
		addPantryItem(new PantryItem("baking soda","baking", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("yeast","baking", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("vegetable shortening","baking", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("chocolate","baking", 0,0,false,false,0,"CVS",""));
		// Pasta
		addPantryItem(new PantryItem("angel hair","pasta", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("egg noodles","pasta", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("fettuccine","pasta", 0,0,false,false,0,"Trader Joes","")); 
		addPantryItem(new PantryItem("fusille","pasta", 0,0,false,false,0,"Trader Joes","")); 
		addPantryItem(new PantryItem("lasagna","pasta", 0,0,false,true,3,"Trader Joes","")); 
		addPantryItem(new PantryItem("linguine","pasta", 0,0,false,false,0,"Trader Joes",""));
		addPantryItem(new PantryItem("penna","pasta", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("spaghetti","pasta", 0,0,false,false,0,"CVS",""));
		// Dried Goods 
		addPantryItem(new PantryItem("dried beans","dried goods", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("dried fruits","dried goods", 0,0,true,false,0,"CVS",""));  
		addPantryItem(new PantryItem("currants","dried goods", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("raisins","dried goods", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("dates","dried goods", 0,0,false,false,0,"CVS","")); 
		// Rice 
		addPantryItem(new PantryItem("Arborio rice","rice", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("brown rice","rice", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("white rice","rice", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("wild rice","rice", 0,0,false,false,0,"CVS",""));
		// Tomatoes
		addPantryItem(new PantryItem("chopped tomatoes","tomatoes", 0,0,false,false,0,"CVS",""));
		addPantryItem(new PantryItem("sundried tomatoes","tomatoes", 0,0,false,true,5,"Wholefoods","")); 
		addPantryItem(new PantryItem("tomato sauce","tomatoes", 0,0,false,false,0,"Wholefoods","")); 
		addPantryItem(new PantryItem("tomato paste","tomatoes", 0,0,false,false,0,"Wholefoods","")); 
		addPantryItem(new PantryItem("whole tomatoes","tomatoes", 0,0,false,false,0,"CVS",""));
		// Oils 
		addPantryItem(new PantryItem("extra-virgin olive oil","oils", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("vegetable oil","oils", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("walnut oil","oils", 0,0,false,false,0,"CVS","")); 
		// Milks 
		addPantryItem(new PantryItem("evaporated milk","milks", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("powdered buttermilk","milks", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("powdered milk","milks", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("sweetened condensed milk","milks", 0,0,false,false,0,"CVS",""));
		// Fruit
		addPantryItem(new PantryItem("Lemons","friut", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("Limes","friut", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("Oranges","friut", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("Apples","friut", 0,0,false,false,0,"CVS","")); 
		// Vegetables
		addPantryItem(new PantryItem("Potatoes","vegetables", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("Onions","vegetables", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("Carrots","vegetables", 0,0,false,false,0,"CVS","")); 
		// Spice Rack
		addPantryItem(new PantryItem("anise seeds","spices", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("basil leaves","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("bay leaves","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("caraway seeds","spices", 0,0,false,false,0,"Trader Joes",""));   
		addPantryItem(new PantryItem("cayenne pepper","spices", 0,0,false,false,0,"Trader Joes",""));   
		addPantryItem(new PantryItem("chili powder","spices", 0,0,false,false,0,"Trader Joes",""));   
		addPantryItem(new PantryItem("cilantro","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("coriander","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("cumin","spices", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("curry powder","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("dillweed","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("fennel seeds","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("Herbes de Provence","spices", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("marjoram","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("dried mustard","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("oregano","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("paprika","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("red pepprs","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("rosemary","spices", 0,0,false,false,0,"Trader Joes",""));   
		addPantryItem(new PantryItem("peppercorns","spices", 0,0,false,false,0,"Trader Joes",""));  
		addPantryItem(new PantryItem("poppy seeds","spices", 0,0,false,false,0,"Trader Joes",""));   
		addPantryItem(new PantryItem("sage","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("salt","spices", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("saffron","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("sesame seeds","spices", 0,0,false,false,0,"CVS",""));   
		addPantryItem(new PantryItem("Tabasco","spices", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("tarragon","spices", 0,0,false,false,0,"Wholefoods",""));   
		addPantryItem(new PantryItem("thyme","spices", 0,0,false,false,0,"Wholefoods",""));   
		addPantryItem(new PantryItem("turmeric","spices", 0,0,false,false,0,"CVS","")); 
		addPantryItem(new PantryItem("cinnamon","spices", 0,0,false,false,0,"CVS",""));  
		addPantryItem(new PantryItem("cloves","spices", 0,0,false,false,0,"CVS",""));   
	}
 
}