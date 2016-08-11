package com.fjcx.e76.finalproj.pantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.fjcx.e76.finalproj.DatabaseHandler;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.id;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.model.PantryItem;
import com.fjcx.e76.finalproj.util.SectionedAdapter;

public class PantryListAdapter extends SectionedAdapter {
	
	private Context _context;
	private List<String> _categories; 
	
	public PantryListAdapter(Context context, DatabaseHandler dbHandler, ArrayList<String> categories){
		super();
		this._context = context;
		this._categories = categories;
		createSections(dbHandler);
	}
	
	// get pantry items from database
	private void createSections(DatabaseHandler dbHandler){
		// create the grid item mapping
		String[] from = new String[] {"col_name","col_expir","col_quant"};
		int[] to = new int[] {R.id.item1, R.id.item2, R.id.item3};
		for(String category: _categories){
			List<PantryItem> pItems = dbHandler.getPantryItemsInCategory(category,true);
			ArrayList<HashMap<String, String>> pantryItemList = new ArrayList<HashMap<String, String>>();
			// check db for pantry items
			for (PantryItem pitem : pItems) {
				if(pitem!=null){
					/*pantryItemList.add(pitem.getPantryItemName()+ ","+ pitem.getCategory()+ "," + pitem.getExpiration() 
							+ ","+pitem.getQuantity() + ","+pitem.getIsOwned());*/
					HashMap<String, String> map = new HashMap<String, String>();
					
					String expiryDateStr = "";
					long expirms = pitem.getExpiration();
					// if date exists parse it, otherwise leave blank 
					if(expirms>0){
						expiryDateStr = new SimpleDateFormat("MM/dd/yy").format(new Date(expirms));
					}
		            map.put("col_name", pitem.getPantryItemName().toLowerCase());
		            map.put("col_expir", expiryDateStr);
		            map.put("col_quant", ""+pitem.getQuantity()+" "+pitem.getScale());
		            pantryItemList.add(map);
				}else{
					//pantryItemList.add("Item1");
				}
			}
			//addSection(category, new ArrayAdapter(_context,android.R.layout.simple_list_item_1, pantryItemList));
			addSection(category, new SimpleAdapter(_context, pantryItemList, R.layout.griditem, from, to));
		}
	}
	
	protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
		TextView result = (TextView) convertView;
		
		if (convertView == null) {
			result = (TextView) inflater.inflate(R.layout.groupheader, null);
		}
		result.setText(capFirst(caption));
		return (result);
	}
	
	private String capFirst(String lower){
		String upperString = lower;
		if(lower!=null){
			upperString = lower.substring(0,1).toUpperCase() + lower.substring(1);
		}
		return upperString;
	}
   
	
}