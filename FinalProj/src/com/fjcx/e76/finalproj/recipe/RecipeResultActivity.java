package com.fjcx.e76.finalproj.recipe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.R.menu;
import com.fjcx.e76.finalproj.util.RestClient;
import com.fjcx.e76.finalproj.util.RestClient.RequestMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class RecipeResultActivity extends ListActivity {
	
	private String findRecipe = null;
	private String findIngred = null;
	private String findIngred2 = null;
	private String findIngred3 = null;
	private boolean glutenFree = false;
	private boolean dairyFree = false;
	private ArrayList<RecipeItem>recipelist;
	private Context _context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_context = this;

		// Grab bundle from previous intent
		Bundle prevbundle = getIntent().getExtras();
		
		// See if bundle contains passed values
		if (prevbundle!=null) {
			if(prevbundle.containsKey("recipename")){
				findRecipe=prevbundle.getString("recipename");
			}
			if(prevbundle.containsKey("ingredient")){
				findIngred=prevbundle.getString("ingredient");
			}
			if(prevbundle.containsKey("ingredient2")){
				findIngred2=prevbundle.getString("ingredient2");
			}
			if(prevbundle.containsKey("ingredient3")){
				findIngred3=prevbundle.getString("ingredient3");
			}
			if(prevbundle.containsKey("chkDairy")){
				glutenFree = true;
			}
			if(prevbundle.containsKey("chkGluten")){
				dairyFree = true;
			}
		}
		
		FindRecipeTask getRecipe =  new FindRecipeTask();
		getRecipe.execute();
	}
	
	public void finishInit(){
        // Updating parsed JSON data into ListView
		ListAdapter adapter = new RecipeListAdapter(this, R.layout.recipelistrow, recipelist);
 
        setListAdapter(adapter);
 
        // selecting single ListView item
        ListView recipelistview = getListView();
 
        // Launching new screen on Selecting Single ListItem
        recipelistview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // transition to single Result view
            	Intent intent = new Intent(_context, SingleRecipeActivity.class);
                Bundle bundle = new Bundle();
                RecipeItem recipeitem = recipelist.get(position);                
                bundle.putString("recipeid", recipeitem.getRecipeId());
                
                // pass file resource id to main activity
                intent.putExtras(bundle);
			    startActivity(intent);
            }
        });
	}
	
	private ArrayList<RecipeItem> parseRecipeJSON(String rawJSON) {
		ArrayList<RecipeItem> recipeList = new ArrayList<RecipeItem>();
		try {
			//JSONObject jsonObj = new JSONObject(sampResponse);
			JSONObject jsonObj = new JSONObject(rawJSON);
			
			JSONArray matches = jsonObj.getJSONArray("matches");
			// lets loop through the JSONArray and get all the matches 
			for (int i = 0; i < matches.length(); i++) { 
				// creating new HashMap
				String recipeId = matches.getJSONObject(i).getString("id").toString();
				String recipeName = matches.getJSONObject(i).getString("recipeName").toString();
				String recipeImgUrl = null;
				ArrayList<String> ingredientlist = new ArrayList<String>();
				ArrayList<String> flavorlist = new ArrayList<String>();
				// get image urls
				JSONArray imgUrls = matches.getJSONObject(i).getJSONArray("smallImageUrls");
				// only need one, so take the first
				if(imgUrls.length()>0){
					recipeImgUrl = imgUrls.getString(0);
				}
				// get ingredient list
				JSONArray ingredients = matches.getJSONObject(i).getJSONArray("ingredients");
				for (int j = 0; j < ingredients.length(); j++) { 
					ingredientlist.add(ingredients.getString(j));
				}
 
				recipeList.add(new RecipeItem(recipeId,recipeName,recipeImgUrl,ingredientlist,flavorlist));
			} 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return recipeList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class FindRecipeTask extends AsyncTask<String, Void, String> {

	    protected String doInBackground(String... urls) {
	    	String _app_id = "ba995c17";
			String _app_key = "c0fd48ab2b685a880e61b5f778b65eb9";
			String yumURL="http://api.yummly.com/v1/api/recipes";
			
			RestClient client = new RestClient(yumURL);
			
			client.AddParam("_app_id", _app_id);
			client.AddParam("_app_key", _app_key);
			try {
				if(findRecipe!=null && !findRecipe.equals("")){
					client.AddParam("q", URLEncoder.encode(findRecipe,"UTF-8"));
				}
			
				if(findIngred!=null){
					client.AddParam("allowedIngredient[]", URLEncoder.encode(findIngred,"UTF-8"));
				}
				if(findIngred2!=null){
					client.AddParam("allowedIngredient[]", URLEncoder.encode(findIngred2,"UTF-8"));
				}
				if(findIngred3!=null){
					client.AddParam("allowedIngredient[]", URLEncoder.encode(findIngred3,"UTF-8"));
				}
 
				if(glutenFree){
					client.AddParam("allowedAllergy[]", "393^Gluten-Free");
				}
				if(dairyFree){
					client.AddParam("allowedAllergy[]", "396^Dairy-Free");
				}
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// artificially limit results to 20
			client.AddParam("maxResult", "20");

			try {
			    client.Execute(RequestMethod.GET);
			} catch (Exception e) {
			    e.printStackTrace();
			}

			return client.getResponse();
	    }

	    protected void onPostExecute(String response) {
	    	//System.out.println(response);
	    	recipelist = parseRecipeJSON(response);
	    	// no results
			if(recipelist.size()<1){
				AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				builder.setTitle("Sorry, no Results Found!");
				builder.setMessage("Please try some different ingredients").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                //do things
				        	   	Intent intent = new Intent(_context, FindRecipesActivity.class);
				        	   	// could pass values back here, but the search was no good, so let them clear instead
							    startActivity(intent);
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}else{
				// found results, finish initialising list
				finishInit();
			}
	    }
	}
	
	private String sampResponse = "{\"attribution\":{\"html\":\"<a href='http://www.yummly.com/recipes/onion-soup'>onion soup recipes</a> search powered by <img alt='Yummly' src='http://static.yummly.com/api-logo.png'/>\",\"url\":\"http://www.yummly.com/recipes/onion-soup\",\"text\":\"onion soup recipes: search powered by Yummly\",\"logo\":\"http://static.yummly.com/api-logo.png\"},\"totalMatchCount\":41845,\"facetCounts\":{},\"matches\":[{\"attributes\":{\"course\":[\"Soups\",\"Lunch and Snacks\"],\"cuisine\":[\"American\"]},\"flavors\":{\"salty\":0.16666666666666666,\"sour\":0.6666666666666666,\"sweet\":0.8333333333333334,\"bitter\":0.16666666666666666,\"meaty\":0.16666666666666666,\"piquant\":0.0},\"rating\":5,\"id\":\"Onion-soup-345134\",\"smallImageUrls\":[\"http://i.yummly.com/Onion-soup-345134-305211.s.jpg\",\"http://i.yummly.com/Onion-soup-345134-305173.s.jpg\"],\"sourceDisplayName\":\"Lottie + Doof\",\"totalTimeInSeconds\":null,\"ingredients\":[\"low salt chicken broth\",\"onions\",\"country style bread\",\"sticks butter\",\"sherry wine vinegar\",\"sage leaves\",\"grated lemon peel\"],\"recipeName\":\"Onion Soup\"},{\"attributes\":{\"course\":[\"Soups\"],\"cuisine\":[\"French\"]},\"flavors\":{\"salty\":0.6666666666666666,\"sour\":0.6666666666666666,\"sweet\":0.6666666666666666,\"bitter\":0.3333333333333333,\"meaty\":0.3333333333333333,\"piquant\":0.0},\"rating\":5,\"id\":\"French-_red_-onion-soup-345182\",\"smallImageUrls\":[\"http://i.yummly.com/French-_red_-onion-soup-345182-305278.s.jpg\"],\"sourceDisplayName\":\"Lottie + Doof\",\"totalTimeInSeconds\":null,\"ingredients\":[\"low sodium chicken broth\",\"manchego cheese\",\"water\",\"star anise\",\"baguette\",\"black peppercorns\",\"olive oil\",\"dry red wine\",\"red onions\"],\"recipeName\":\"French (Red) Onion Soup\"},{\"attributes\":{\"course\":[\"Soups\",\"Appetizers\",\"Lunch and Snacks\"],\"cuisine\":[\"French\"]},\"flavors\":{\"salty\":0.8333333333333334,\"sour\":0.5,\"sweet\":0.6666666666666666,\"bitter\":0.5,\"meaty\":0.3333333333333333,\"piquant\":0.0},\"rating\":4,\"id\":\"French-Onion-Soup-Recipezaar_27\",\"smallImageUrls\":[\"http://i2.yummly.com/French-Onion-Soup-Recipezaar_27.s.png\",\"http://i.yummly.com/French-Onion-Soup-Recipezaar_27-165716.s.jpg\"],\"sourceDisplayName\":\"Food.com\",\"totalTimeInSeconds\":2580.0,\"ingredients\":[\"pepper\",\"sugar\",\"vegetable oil\",\"onions\",\"french bread\",\"beef broth\",\"swiss cheese\"],\"recipeName\":\"French Onion Soup\"}],\"criteria\":{\"requirePictures\":false,\"excludedIngredients\":[],\"allowedIngredients\":[],\"terms\":[\"onion\",\"soup\"],\"facetFields\":[],\"resultsToSkip\":0,\"maxResults\":0}}";

}
