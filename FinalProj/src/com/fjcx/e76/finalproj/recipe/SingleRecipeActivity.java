package com.fjcx.e76.finalproj.recipe;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fjcx.e76.finalproj.MainActivity;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.pantry.AddPantryItemActivity;
import com.fjcx.e76.finalproj.recipe.RecipeResultActivity.FindRecipeTask;
import com.fjcx.e76.finalproj.util.ImageThreadLoader;
import com.fjcx.e76.finalproj.util.RestClient;
import com.fjcx.e76.finalproj.util.ImageThreadLoader.ImageLoadedListener;
import com.fjcx.e76.finalproj.util.RestClient.RequestMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class SingleRecipeActivity extends Activity {
	
	private String findRecipeId = null;
	private SingleRecipeItem recipeItem = null;
	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	ImageButton homeMenuBtn;
	Button sourceBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlerecipe);
		addListenerOnButton();
		
		// Grab bundle from previous intent
		Bundle prevbundle = getIntent().getExtras();
		
		recipeItem = new SingleRecipeItem();
		
		// See if bundle contains "resId" key and set resource id for board file we passed
		if(prevbundle.containsKey("recipeid")){
			findRecipeId=prevbundle.getString("recipeid");
		}else{
			// "not found or invalid id"
		}
		
		FindSingleRecipeTask getSingleRecipe =  new FindSingleRecipeTask();
		getSingleRecipe.execute();
		
		TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Recipe");

        TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setIndicator("Nutrition");
        spec2.setContent(R.id.tab2);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);        
	}
	
	public void addListenerOnButton() {
		final Context context = this;
		
		homeMenuBtn = (ImageButton) findViewById(R.id.srecipe_menu_btn);
		homeMenuBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, MainActivity.class);
			    startActivity(intent);
			}
		});
		sourceBtn = (Button) findViewById(R.id.source_btn);
		sourceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				String recipeSourceUrl =recipeItem.getSourceUrl();
				if(recipeSourceUrl!=null){
					Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse(recipeSourceUrl));
				    startActivity(intent);
				}
			}
		});
	}
	
	class FindSingleRecipeTask extends AsyncTask<String, Void, String> {

	    protected String doInBackground(String... urls) {
	    	String _app_id = "ba995c17";
			String _app_key = "c0fd48ab2b685a880e61b5f778b65eb9";
			String yumURL="http://api.yummly.com/v1/api/recipe";
			String recipeUrl="";
			RestClient client = null;
			try {
				recipeUrl=yumURL + "/"+ (URLEncoder.encode(findRecipeId,"UTF-8"));
				client = new RestClient(recipeUrl);
				client.AddParam("_app_id", _app_id);
				client.AddParam("_app_key", _app_key);
				client.Execute(RequestMethod.GET);
			
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e) {
			    e.printStackTrace();
			}
			
			if(client!=null){
				return client.getResponse();
		    }else{
		    	return "";
		    }
	    }

	    protected void onPostExecute(String response) {
	    	System.out.println(response);
	    	recipeItem = parseSingleRecipeJSON(response);
	    	finishInit();
	    }
	}
	
	private SingleRecipeItem parseSingleRecipeJSON(String rawJSON) {
		SingleRecipeItem parsedRecipe = new SingleRecipeItem();
		try {
			JSONObject jsonObj = new JSONObject(rawJSON);
			
		//	JSONArray matches = jsonObj.getJSONArray("matches");
			// lets loop through the JSONArray and get all the matches 
			
			// creating new HashMap
			parsedRecipe.setRecipeName(jsonObj.getString("name"));
			parsedRecipe.setRating(jsonObj.getInt("rating"));
			parsedRecipe.setNumServe(jsonObj.getInt("numberOfServings"));
			
			JSONObject sourceObj = jsonObj.getJSONObject("source");
			parsedRecipe.setSourceUrl(sourceObj.getString("sourceRecipeUrl"));
			
			JSONArray ingredLines = jsonObj.getJSONArray("ingredientLines");
			ArrayList<String> ingred = new ArrayList<String>();
			for (int j = 0; j < ingredLines.length(); j++) { 
				ingred.add(ingredLines.getString(j));
			}
			parsedRecipe.setIngredients(ingred);
			
			JSONObject flavours = jsonObj.getJSONObject("flavors");
			HashMap<String, Integer> flavourmap = new HashMap<String, Integer>();

			try{
				if(!flavours.isNull("Salty")){
					// we get the average and represent each as an int percentage out of 100
					Double totalflavour = (flavours.getDouble("Salty")+flavours.getDouble("Piquant")+
							flavours.getDouble("Sour")+flavours.getDouble("Sweet")+
							flavours.getDouble("Bitter")+flavours.getDouble("Meaty"))/100;
					
					flavourmap.put("Salty", (int) (flavours.getDouble("Salty")/totalflavour));
					flavourmap.put("Piquant", (int)( flavours.getDouble("Piquant")/totalflavour));
					flavourmap.put("Sour", (int) (flavours.getDouble("Sour")/totalflavour));
					flavourmap.put("Sweet", (int) (flavours.getDouble("Sweet")/totalflavour));
					flavourmap.put("Bitter", (int) (flavours.getDouble("Bitter")/totalflavour));
					flavourmap.put("Meaty", (int) (flavours.getDouble("Meaty")/totalflavour));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONArray nutritionArray = jsonObj.getJSONArray("nutritionEstimates");
			HashMap<String,String> nututrionMap = new HashMap<String,String>();
			for (int j = 0; j < nutritionArray.length(); j++) { 
				JSONObject nutel = nutritionArray.getJSONObject(j);
				if(!nutel.isNull("attribute")){
					nututrionMap.put(nutel.getString("attribute"), nutel.getString("value"));
				}
			}
			
			parsedRecipe.setIngredients(ingred);
			parsedRecipe.setFlavors(flavourmap);
			parsedRecipe.setNutrition(nututrionMap);
			
			// get image urls
			JSONArray imgUrls = jsonObj.getJSONArray("images");
			// only need one, so take the first
			if(imgUrls.length()>0){
				parsedRecipe.setImageUrl(imgUrls.getJSONObject(0).getString("hostedSmallUrl"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parsedRecipe;
	}
	
	public void finishInit(){
		final ImageView tab1image = (ImageView) findViewById(R.id.tab1recipeimg);
		Bitmap cachedImage = null;
	    try {
        	cachedImage = imageLoader.loadImage(recipeItem.getImageUrl(), new ImageLoadedListener() {
	    		public void imageLoaded(Bitmap imageBitmap) {
	    			tab1image.setImageBitmap(imageBitmap);
	    		}
        	});
	    } catch (MalformedURLException e) {
	      Log.e("sRecipe","Bad remote image URL: " + recipeItem.getImageUrl(), e);
	    }
		// finish init after response
		TextView tab1Header = (TextView) findViewById(R.id.tab1header);
        tab1Header.setText(recipeItem.getRecipeName());
        // setting ingredients
        TextView tab1Ingred = (TextView) findViewById(R.id.tab1ingred);
        String ingredients = "";
        for(String ingred: recipeItem.getIngredients()){
        	ingredients = ingredients+ingred+"\n";
        }
        tab1Ingred.setText(ingredients);
        
        // setting rating
        RatingBar ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        if(recipeItem.getRating()>0 && recipeItem.getRating()<6){
        	ratingbar.setRating(recipeItem.getRating());
        }
        
        // setting tastes
        HashMap<String, Integer> flavourmap = recipeItem.getFlavors();
        // salty
        ProgressBar saltyBar = (ProgressBar) findViewById(R.id.saltybar);
        if(flavourmap.containsKey("Salty")){
        	saltyBar.setProgress(recipeItem.getFlavors().get("Salty"));
        }else{
        	saltyBar.setProgress(0);
        }
        // piquant
        ProgressBar piqBar = (ProgressBar) findViewById(R.id.piquantbar);
        if(flavourmap.containsKey("Piquant")){
        	piqBar.setProgress(recipeItem.getFlavors().get("Piquant"));
        }else{
        	piqBar.setProgress(0);
        }
        // sour
        ProgressBar sourBar = (ProgressBar) findViewById(R.id.sourbar);
        if(flavourmap.containsKey("Sour")){
        	sourBar.setProgress(recipeItem.getFlavors().get("Sour"));
        }else{
        	sourBar.setProgress(0);
        }
        // sweet
        ProgressBar sweetBar = (ProgressBar) findViewById(R.id.sweetbar);
        if(flavourmap.containsKey("Sweet")){
        	sweetBar.setProgress(recipeItem.getFlavors().get("Sweet"));
        }else{
        	sweetBar.setProgress(0);
        }
        // bitter
        ProgressBar bitterBar = (ProgressBar) findViewById(R.id.bitterbar);
        if(flavourmap.containsKey("Bitter")){
        	bitterBar.setProgress(recipeItem.getFlavors().get("Bitter"));
        }else{
        	bitterBar.setProgress(0);
        }
        // meaty
        ProgressBar meatyBar = (ProgressBar) findViewById(R.id.meatybar);
        if(flavourmap.containsKey("Meaty")){
        	meatyBar.setProgress(recipeItem.getFlavors().get("Meaty"));
        }else{
        	meatyBar.setProgress(0);
        }
        
        // settting nutrition
        HashMap<String, String> nutrition = recipeItem.getNutrition();
        TextView nutriCal = (TextView) findViewById(R.id.cal_value);
        nutriCal.setText(nutrition.get("ENERC_KCAL"));
        
        TextView nutriTFat = (TextView) findViewById(R.id.tfat_value);
        nutriTFat.setText(nutrition.get("FAT"));
        TextView nutriSatFat = (TextView) findViewById(R.id.satfat_value);
        nutriSatFat.setText(nutrition.get("FASAT"));
        TextView nutriTransFat = (TextView) findViewById(R.id.transfat_value);
        nutriTransFat.setText(nutrition.get("FATRN"));
        TextView nutriChol = (TextView) findViewById(R.id.chol_value);
        nutriChol.setText(nutrition.get("CHOLE"));
        TextView nutriSod = (TextView) findViewById(R.id.sod_value);
        nutriSod.setText(nutrition.get("NA"));
        TextView nutriPot = (TextView) findViewById(R.id.pot_value);
        nutriPot.setText(nutrition.get("K"));
        TextView nutriTCarb = (TextView) findViewById(R.id.tcarb_value);
        nutriTCarb.setText(nutrition.get("CHOCDF"));
        TextView nutriFibr = (TextView) findViewById(R.id.dfibr_value);
        nutriFibr.setText(nutrition.get("FIBTG"));
        TextView nutriSugr = (TextView) findViewById(R.id.sugr_value);
        nutriSugr.setText(nutrition.get("SUGAR"));
        TextView nutriProt = (TextView) findViewById(R.id.pro_value);
        nutriProt.setText(nutrition.get("PROCNT"));
        
        TextView nutriVitA = (TextView) findViewById(R.id.vita_value);
        nutriVitA.setText(nutrition.get("VITA_IU"));
        TextView nutriVitC = (TextView) findViewById(R.id.vitc_value);
        nutriVitC.setText(nutrition.get("VITC"));
        TextView nutriCalc = (TextView) findViewById(R.id.calc_value);
        nutriCalc.setText(nutrition.get("CA"));
        TextView nutriFe = (TextView) findViewById(R.id.iron_value);
        nutriFe.setText(nutrition.get("FE"));
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
