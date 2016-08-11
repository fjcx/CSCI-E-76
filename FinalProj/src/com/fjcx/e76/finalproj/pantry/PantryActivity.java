package com.fjcx.e76.finalproj.pantry;

import java.util.ArrayList;

import com.fjcx.e76.finalproj.DatabaseHandler;
import com.fjcx.e76.finalproj.MainActivity;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.id;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.R.menu;
import com.fjcx.e76.finalproj.model.PantryItem;
import com.fjcx.e76.finalproj.recipe.FindRecipesActivity;
import com.fjcx.e76.finalproj.shoplist.ShoppingListActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PantryActivity extends Activity {
	
	private ArrayList<String> pantryList = new ArrayList<String>();
	private DatabaseHandler dbHandler;
	ImageButton addPantryItemBtn;
	ImageButton homeMenuBtn;
	Context _context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pantry);
		_context = this;
		addListenerOnButton();
		
		dbHandler =  new DatabaseHandler(this);
		
		ArrayList<String> categories = (ArrayList<String>) dbHandler.getPantryGroups(true);
		ListView pantrylistview = (ListView) findViewById(R.id.pantry_list);
		
		PantryListAdapter padapter = new PantryListAdapter(this, dbHandler, categories);
		pantrylistview.setAdapter(padapter);
		
		registerForContextMenu(pantrylistview);
		pantrylistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    ((Activity) _context).openContextMenu(view);
			}
		});
	}
	
	public void addListenerOnButton() {
		final Context context = this;
		
		addPantryItemBtn = (ImageButton) findViewById(R.id.pantry_add_btn);
		addPantryItemBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, AddPantryItemActivity.class);
			    startActivity(intent);
			}
		});
		homeMenuBtn = (ImageButton) findViewById(R.id.pantry_menu_btn);
		homeMenuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, MainActivity.class);
			    startActivity(intent);
			}
		});
	}
	
	 @Override
	 public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.layout.pantrycontextmenu, menu);
		menu.setHeaderTitle("Select an Option");
	}
	 
	 @Override
	 public boolean onContextItemSelected(MenuItem item) {
		 AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		 TextView rowViewItem1 = (TextView)info.targetView.findViewById(R.id.item1);
		 String selectedRowName = rowViewItem1.getText().toString();
		 
		 switch(item.getItemId()) {
			
			case R.id.pmenu_addtoslist:
				if(selectedRowName!=null){
					Toast.makeText(PantryActivity.this,"Added to Grocery List!",Toast.LENGTH_SHORT ).show();
					// Setting shopping list flag to true (i.e. add to shopping list)
					dbHandler.updatePItemSList(selectedRowName, true);
				}
				return true;
			case R.id.pmenu_findrecipe:
				Toast.makeText(PantryActivity.this,"Find recipe!",Toast.LENGTH_SHORT ).show();
				Intent intent = new Intent(this, FindRecipesActivity.class);
				Bundle bundle = new Bundle();
				if(selectedRowName!=null){
					bundle.putString("findIngred", selectedRowName.trim());
				}
                
                // pass file resource id to recipe finder activity
                intent.putExtras(bundle);
			    startActivity(intent);
				return true;
			case R.id.pmenu_edit:
				Toast.makeText(PantryActivity.this,"Update Item Details!",Toast.LENGTH_SHORT ).show();
				Intent intentup = new Intent(this, UpdatePantryItemActivity.class);
				Bundle bundleup = new Bundle();
				if(selectedRowName!=null){
					bundleup.putString("updatePItem", selectedRowName.trim());
				}
				bundleup.putString("prevPage", "pantry");
                // pass file prevpage to know where to return to
                intentup.putExtras(bundleup);
			    startActivity(intentup);
				return true;
			case R.id.pmenu_delete:
				if(selectedRowName!=null){
					Toast.makeText(PantryActivity.this,"Item Removed!",Toast.LENGTH_SHORT ).show();
					// Setting owned flag to false (i.e. removing from pantry)
					dbHandler.updatePItemOwned(selectedRowName, false);
					// force refresh -- find a better way!
					Intent refresh =new Intent(_context, PantryActivity.class);
				    startActivity(refresh);
				}
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
