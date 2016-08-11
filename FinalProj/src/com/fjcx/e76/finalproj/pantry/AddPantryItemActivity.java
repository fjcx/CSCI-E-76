package com.fjcx.e76.finalproj.pantry;

import java.util.ArrayList;
import java.util.HashMap;

import com.fjcx.e76.finalproj.DatabaseHandler;
import com.fjcx.e76.finalproj.MainActivity;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.id;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.R.menu;
import com.fjcx.e76.finalproj.model.PantryItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AddPantryItemActivity extends Activity {
	
	private DatabaseHandler dbHandler;
	ImageButton homeMenuBtn;
	private String selectedcat = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addpantryitem);
		// setup pantryitem database
		dbHandler =  new DatabaseHandler(this);
		addListenerOnButton();
		
		// Grab bundle from previous intent
		Bundle prevbundle = getIntent().getExtras();
		
		// See if bundle contains "resId" key and set resource id for board file we passed
		if (prevbundle!=null && prevbundle.containsKey("category")) {
			selectedcat = prevbundle.getString("category");
		}
		ListView additemlistview = (ListView) findViewById(R.id.additem_list);
		TextView additemheader = (TextView) findViewById(R.id.additemheader);
		
		if(selectedcat==null){
			ArrayList<String> categories = (ArrayList<String>) dbHandler.getPantryGroups(false);
			categories.add("Type in...");
			additemheader.setText("Choose Category");
			additemlistview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,categories));
		}else{
			ArrayList<PantryItem> categoryItems = (ArrayList<PantryItem>) dbHandler.getPantryItemsInCategory(selectedcat, false);
			additemheader.setText("Choose Item");
			ArrayList<String> pantryItemList = new ArrayList<String>();
			// check db for pantry items
			pantryItemList.add("Type in...");
			for (PantryItem pitem : categoryItems) {
				if(pitem!=null){
		            pantryItemList.add(pitem.getPantryItemName());
				}
			}
			additemlistview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,pantryItemList));
		}
		
		additemlistview.setOnItemClickListener(new OnItemClickListener() {
			 @Override
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 // start the home screen if the back button wasn't pressed already 
                String itemStr = ((TextView)view).getText().toString();
                
                if(itemStr.equals("Type in...")){
                	// type in custom item
                	Intent intent = new Intent(AddPantryItemActivity.this, UpdatePantryItemActivity.class);
	                startActivity(intent);
                }else{
	                if(selectedcat==null){
	                	Intent intent = new Intent(AddPantryItemActivity.this, AddPantryItemActivity.class);
		                Bundle bundle = new Bundle();
		                bundle.putString("category", itemStr);
		                // pass file resource id to main activity
		                intent.putExtras(bundle);
		                AddPantryItemActivity.this.startActivity(intent);
	                }else{
	                	Intent intent = new Intent(AddPantryItemActivity.this, PantryActivity.class);
	                	dbHandler.updatePItemOwned(itemStr,true);
		                AddPantryItemActivity.this.startActivity(intent);
	                }
                }
			 }
		});
	}
	
	public void addListenerOnButton() {
		final Context context = this;
		homeMenuBtn = (ImageButton) findViewById(R.id.additem_menu_btn);
		homeMenuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, MainActivity.class);
			    startActivity(intent);
			}
		});
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
