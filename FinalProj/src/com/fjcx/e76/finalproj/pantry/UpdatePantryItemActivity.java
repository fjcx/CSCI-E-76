package com.fjcx.e76.finalproj.pantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.fjcx.e76.finalproj.DatabaseHandler;
import com.fjcx.e76.finalproj.MainActivity;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.id;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.R.menu;
import com.fjcx.e76.finalproj.model.PantryItem;
import com.fjcx.e76.finalproj.recipe.RecipeItem;
import com.fjcx.e76.finalproj.shoplist.ShoppingListActivity;
import com.fjcx.e76.finalproj.shopsnear.ShopsNearActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class UpdatePantryItemActivity extends FragmentActivity  {
	
	Context _context;
	private DatabaseHandler dbHandler;
	ImageButton homeMenuBtn;
	ImageButton upexpirycal;
	ImageButton upexpirycalclear;
	Button updateBtn;
	String previousPage = "";
	String storeStr = "";
	static final int DATE_DIALOG_ID = 999;
	private DatePicker dpResult;
	private String selecteditem = null;
	private TextView upexpiry;
	private int year;
	private int month;
	private int day;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatepitem);
		_context = this;
		// setup pantryitem database
		dbHandler =  new DatabaseHandler(this);
		addListenerOnButton();
		
		// Grab bundle from previous intent
		Bundle prevbundle = getIntent().getExtras();
		
		// See if bundle contains values
		if (prevbundle!=null && prevbundle.containsKey("updatePItem")) {
			selecteditem = prevbundle.getString("updatePItem");
		}
		// See if bundle contains values
		if (prevbundle!=null && prevbundle.containsKey("prevPage")) {
			previousPage = prevbundle.getString("prevPage");
		}
		// See if bundle contains values
		if (prevbundle!=null && prevbundle.containsKey("updateStore")) {
			storeStr = prevbundle.getString("updateStore");
		}
		
		if(selecteditem!=null){
			PantryItem pitem = dbHandler.getPantryItem(selecteditem);
			if(pitem!=null){
				EditText upitemname = (EditText) findViewById(R.id.uitemname_text);
				upitemname.setText(selecteditem);
				EditText upcategory = (EditText) findViewById(R.id.ucategory_text);
				upcategory.setText(pitem.getCategory());
				EditText upstore = (EditText) findViewById(R.id.ushop_text);
				upstore.setText(pitem.getShop());
				EditText upquantity = (EditText) findViewById(R.id.uquantity_text);
				upquantity.setText(""+pitem.getQuantity());
				upexpiry = (EditText) findViewById(R.id.uexpiry_text);
				long expirydatems = pitem.getExpiration();
				if(expirydatems>0){
					String expiryStr = new SimpleDateFormat("MM/dd/yy").format(new Date(expirydatems));
					upexpiry.setText(expiryStr);
				}else{
					upexpiry.setText("none");
				}
				EditText upquantitypurch = (EditText) findViewById(R.id.uquantitypurch_text);
				upquantitypurch.setText(""+pitem.getPurchQuantity());
				EditText upscale = (EditText) findViewById(R.id.uscale_text);
				upscale.setText(""+pitem.getScale());
			}
		}else{
			// if store name is passed to update item
			if(storeStr!=null){
				EditText upstore = (EditText) findViewById(R.id.ushop_text);
				upstore.setText(storeStr);
			}
			// blank selected item, all fields are blank then
		}
		
	}
	
	public void addListenerOnButton() {
		homeMenuBtn = (ImageButton) findViewById(R.id.uitem_menu_btn);
		homeMenuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(_context, MainActivity.class);
			    startActivity(intent);
			}
		});
		upexpirycal = (ImageButton) findViewById(R.id.uexpiry_calbtn);
		upexpirycal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectDate(arg0);
			}
		});
		upexpirycalclear = (ImageButton) findViewById(R.id.uexpiry_calclearbtn);
		upexpirycalclear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				upexpiry = (EditText)findViewById(R.id.uexpiry_text);
				upexpiry.setText("none");
			}
		});
		updateBtn = (Button) findViewById(R.id.updateitem_btn);
		updateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// do actual update with values
				updatePItem();
			}
		});
	}
	
	private void updatePItem(){
		EditText upitemname = (EditText) findViewById(R.id.uitemname_text);	
		EditText upcategory = (EditText) findViewById(R.id.ucategory_text);		
		EditText upstore = (EditText) findViewById(R.id.ushop_text);
		EditText upquantity = (EditText) findViewById(R.id.uquantity_text);
		EditText upexpiry = (EditText) findViewById(R.id.uexpiry_text);
		EditText upquantitypurch = (EditText) findViewById(R.id.uquantitypurch_text);
		EditText upscale = (EditText) findViewById(R.id.uscale_text);
		
		PantryItem updatedPItem = dbHandler.getPantryItem(upitemname.getText().toString());
		boolean isNewItem = false;
		if(updatedPItem==null){
			// name of item has changed, so treat as a new Item
			isNewItem = true;
			updatedPItem = new PantryItem();		
		}
		
		// update item
		if(upitemname.getText().toString()!=null && !upitemname.getText().toString().equals("")){
			updatedPItem.setPantryItemName(upitemname.getText().toString());
		}else{
			updatedPItem.setPantryItemName("Chicken");
		}
		
		// update item
		if(upcategory.getText().toString()!=null && !upcategory.getText().toString().equals("")){
			updatedPItem.setCategory(upcategory.getText().toString());
		}else{
			updatedPItem.setCategory("Other");
		}
		if(upstore.getText().toString()!=null && !upstore.getText().toString().equals("")){
			updatedPItem.setShop(upstore.getText().toString());
		}else{
			updatedPItem.setShop("Other");
		}
		if(upscale.getText().toString()!=null){
			updatedPItem.setScale(upscale.getText().toString());
		}else{
			updatedPItem.setScale("");
		}
		
		if(upquantity.getText().toString()!=null && !upquantity.getText().toString().equals("")){
			int ownedqunt = Integer.parseInt(upquantity.getText().toString());
			updatedPItem.setQuantity(ownedqunt);
			if(ownedqunt>0){
				updatedPItem.setIsOwned(true);	// if is owned add to pantry
			}
		}else{
			updatedPItem.setQuantity(0);
			updatedPItem.setOnSList(true);
		}
		if(upquantitypurch.getText().toString()!=null && !upquantitypurch.getText().toString().equals("")){
			int purchqunt = Integer.parseInt(upquantitypurch.getText().toString());
			updatedPItem.setPurchQuantity(purchqunt);
			if(purchqunt>0){
				updatedPItem.setOnSList(true);	// if quantity to be purchased is > 0 then add to shopping list
			}else{
				updatedPItem.setOnSList(false); // if quantity to be purchased is == 0 then remove from shopping list
			}
		}else{
			updatedPItem.setPurchQuantity(0);
			updatedPItem.setIsOwned(true);
		}
		String expStr = upexpiry.getText().toString();
		if(expStr.equals("none")){
			updatedPItem.setExpiration(0);
		}else{
			try {
				SimpleDateFormat dateForm = new SimpleDateFormat("MM/dd/yy");
				Date tmpdate = dateForm.parse(expStr);
				updatedPItem.setExpiration(tmpdate.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if(!isNewItem){
			dbHandler.updatePantryItem(updatedPItem);
		}else{
			// setting some defaults
			updatedPItem.setIsOwned(true);
			dbHandler.addPantryItem(updatedPItem);		
		}
		
		Intent intent;
		if(previousPage.equals("slist")){
			intent = new Intent(_context, ShoppingListActivity.class);
		}else if(previousPage.equals("shopsnear")){
			intent = new Intent(_context, ShopsNearActivity.class);
		}else{
			intent = new Intent(_context, PantryActivity.class);
		}
	    startActivity(intent);
	}
	
	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
			newFragment.show(getFragmentManager(), "DatePicker");
		}
		public void populateSetDate(int year, int month, int day) {
			upexpiry = (EditText)findViewById(R.id.uexpiry_text);
			upexpiry.setText(month+"/"+day+"/"+year);
		}

		@SuppressLint("ValidFragment")
		public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}
		 
		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			populateSetDate(yy, mm+1, dd);
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
