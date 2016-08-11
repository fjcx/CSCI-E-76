package com.fjcx.e76.finalproj.shoplist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fjcx.e76.finalproj.DatabaseHandler;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.model.PantryItem;
import com.fjcx.e76.finalproj.util.SectionedAdapter;

public class ShoppingListAdapter extends SectionedAdapter {
	
	private Context _context;
	private List<String> _categories; 
	
	public ShoppingListAdapter(Context context, DatabaseHandler dbHandler, ArrayList<String> categories){
		super();
		this._context = context;
		this._categories = categories;
		createSections(dbHandler);
	}
	
	// get shopping list items from database
	private void createSections(DatabaseHandler dbHandler){
		// create the grid item mapping
		String[] from = new String[] {"col_name","col_quant"};
		int[] to = new int[] {R.id.slist_item1, R.id.slist_item2};
		for(String category: _categories){
			List<PantryItem> slItems = dbHandler.getSListItemsInShopCat(category,true);
			ArrayList<HashMap<String, String>> shoppingItemList = new ArrayList<HashMap<String, String>>();
			// check db for items on shopping list
			for (PantryItem slitem : slItems) {
				if(slitem!=null){
					HashMap<String, String> map = new HashMap<String, String>();
		            map.put("col_name", slitem.getPantryItemName());
		            map.put("col_quant", ""+slitem.getPurchQuantity()+" "+slitem.getScale());
		            shoppingItemList.add(map);
				}else{
					//pantryItemList.add("Item1");
				}
			}
			//addSection(category, new ArrayAdapter(_context,android.R.layout.simple_list_item_1, pantryItemList));
			addSection(category, new SimpleAdapter(_context, shoppingItemList, R.layout.gridslistitem, from, to));
		}
	}
	
	protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
		TextView result = (TextView) convertView;
		
		if (convertView == null) {
			result = (TextView) inflater.inflate(R.layout.groupheader, null);
		}
		
		result.setText(caption);
		return (result);
	}
   
	
}