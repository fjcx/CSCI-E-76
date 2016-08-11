package com.fjcx.e76.finalproj.shoplist;

import java.util.ArrayList;

import com.fjcx.e76.finalproj.DatabaseHandler;
import com.fjcx.e76.finalproj.MainActivity;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.R.menu;
import com.fjcx.e76.finalproj.pantry.AddPantryItemActivity;
import com.fjcx.e76.finalproj.pantry.PantryActivity;
import com.fjcx.e76.finalproj.pantry.PantryListAdapter;
import com.fjcx.e76.finalproj.pantry.UpdatePantryItemActivity;
import com.fjcx.e76.finalproj.recipe.FindRecipesActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ShoppingListActivity extends Activity {
	
	private DatabaseHandler dbHandler;
	ImageButton addSListItemBtn;
	ImageButton homeMenuBtn;
	Context _context;
	ShoppingListAdapter sladapter;
	ListView shoppinglistview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shopinglist);
		_context = this;
		addListenerOnButton();
		dbHandler =  new DatabaseHandler(this);
		
		ArrayList<String> categories = (ArrayList<String>) dbHandler.getSListGroups(true);
		shoppinglistview = (ListView) findViewById(R.id.slist_list);
		
		sladapter = new ShoppingListAdapter(this, dbHandler, categories);
		shoppinglistview.setAdapter(sladapter);
		
		registerForContextMenu(shoppinglistview);
		shoppinglistview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    ((Activity) _context).openContextMenu(view);
			}
		});
			

	}
	
	public void addListenerOnButton() {
		final Context context = this;
		
		addSListItemBtn = (ImageButton) findViewById(R.id.slist_add_btn);
		addSListItemBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, AddSListItemActivity.class);
			    startActivity(intent);
			}
		});
		homeMenuBtn = (ImageButton) findViewById(R.id.slist_menu_btn);
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
		getMenuInflater().inflate(R.layout.slistcontextmenu, menu);
		menu.setHeaderTitle("Select an Option");
		//menu.setHeaderIcon(R.drawable.logo);
	}
	 
	 @Override
	 public boolean onContextItemSelected(MenuItem item) {
		 AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		 TextView rowViewItem1 = (TextView)info.targetView.findViewById(R.id.slist_item1);
		 String selectedRowName = rowViewItem1.getText().toString();
		 switch(item.getItemId()) {
			case R.id.slistmenu_addtopantry:
				if(selectedRowName!=null){
					Toast.makeText(ShoppingListActivity.this,"Added to Pantry!",Toast.LENGTH_SHORT ).show();
					// Setting shopping list flag to true (i.e. add to shopping list)
					dbHandler.updatePItemOwned(selectedRowName, true);
				}
				return true;
			case R.id.slistmenu_findrecipe:
				Toast.makeText(ShoppingListActivity.this,"Find recipe!",Toast.LENGTH_SHORT ).show();
				Intent intent = new Intent(this, FindRecipesActivity.class);
				Bundle bundle = new Bundle();
				if(selectedRowName!=null){
					bundle.putString("findIngred", selectedRowName.trim());
				}
                
                // pass file resource id to recipe finder activity
                intent.putExtras(bundle);
			    startActivity(intent);
				return true;
			case R.id.slistmenu_edit:
				Toast.makeText(ShoppingListActivity.this,"Update Item Details!",Toast.LENGTH_SHORT ).show();
				Intent intentup = new Intent(this, UpdatePantryItemActivity.class);
				Bundle bundleup = new Bundle();
				if(selectedRowName!=null){
					bundleup.putString("updatePItem", selectedRowName.trim());
				}
				bundleup.putString("prevPage", "slist");
                // pass file resource id to recipe finder activity
                intentup.putExtras(bundleup);
			    startActivity(intentup);
				return true;
			case R.id.slistmenu_delete:
				if(selectedRowName!=null){
					Toast.makeText(ShoppingListActivity.this,"Item Removed from List!",Toast.LENGTH_SHORT ).show();
					// Setting owned flag to false (i.e. removing from pantry)
					dbHandler.updatePItemSList(selectedRowName, false);
					// force refresh -- find a better way!
					Intent refresh =new Intent(_context, ShoppingListActivity.class);
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
