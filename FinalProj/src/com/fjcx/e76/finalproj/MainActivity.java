package com.fjcx.e76.finalproj;

import com.fjcx.e76.finalproj.pantry.PantryActivity;
import com.fjcx.e76.finalproj.recipe.FindRecipesActivity;
import com.fjcx.e76.finalproj.shoplist.ShoppingListActivity;
import com.fjcx.e76.finalproj.shopsnear.ShopsNearActivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ImageButton pantry_btn;
	ImageButton recipe_btn;
	ImageButton shoplist_btn;
	ImageButton calendar_btn;
	ImageButton shops_btn;
	ImageButton achieve_btn;
	private DatabaseHandler dbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		addListenerOnButton();
		
		// setup pantryitem database
	//		this.deleteDatabase("PantryItemManager.db");  // -- removing database when updating it's structure
		dbHandler =  new DatabaseHandler(this);
		
		// need to check if database is empty, if so populate with values (will happen first time)
		if(dbHandler.getPantryItemCount()<3){
			// should move this to async task ???
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Pre-polpulating Database for first use, \nPlease be patient!").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                //do things
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			// !!! *deleting all for testing* !!!
			//	dbHandler.deleteAllPantryItems();
			dbHandler.prePopulateFoodItems();	// should check if any items exist first !!
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void addListenerOnButton() {
		final Context context = this;
		// Pantry
		pantry_btn = (ImageButton) findViewById(R.id.pantry_btn);
		pantry_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  Intent intent = new Intent(context, PantryActivity.class);
			    startActivity(intent);
			}
		});
		// Recipes
		recipe_btn = (ImageButton) findViewById(R.id.recipe_btn);
		recipe_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  Intent intent = new Intent(context, FindRecipesActivity.class);
			    startActivity(intent);
			}
		});
		// Shopping List
		shoplist_btn = (ImageButton) findViewById(R.id.shoplist_btn);
		shoplist_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  Intent intent = new Intent(context, ShoppingListActivity.class);
			    startActivity(intent);
			}
		});
		// Meal Calendar
		calendar_btn = (ImageButton) findViewById(R.id.calendar_btn);
		calendar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// do nothing at the mo, to be implenmented later
			 /* Intent intent = new Intent(context, MealCalActivity.class);
			    startActivity(intent);*/
			}
		});
		// Shops Nearby
		shops_btn = (ImageButton) findViewById(R.id.shops_btn);
		shops_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			  Intent intent = new Intent(context, ShopsNearActivity.class);
			    startActivity(intent);
			}
		});
		// Chievos !
		achieve_btn = (ImageButton) findViewById(R.id.achieve_btn);
		achieve_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// do nothing at the mo, to be implenmented later
			  /*Intent intent = new Intent(context, AchievementActivity.class);
			    startActivity(intent);*/
			}
		});
				
	}

}
