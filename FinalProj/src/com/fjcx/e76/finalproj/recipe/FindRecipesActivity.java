package com.fjcx.e76.finalproj.recipe;

import com.fjcx.e76.finalproj.MainActivity;
import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.id;
import com.fjcx.e76.finalproj.R.layout;
import com.fjcx.e76.finalproj.R.menu;
import com.fjcx.e76.finalproj.pantry.AddPantryItemActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class FindRecipesActivity extends Activity {
	
	Context _context;
	ImageButton homeMenuBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipes);
		
		_context = this;
		addListenerOnButton();
		
		// Grab bundle from previous intent
		Bundle prevbundle = getIntent().getExtras();
		
		// See if bundle contains "resId" key and set resource id for board file we passed
		if (prevbundle!=null && prevbundle.containsKey("findIngred")) {
			TextView ingredToFind = (TextView) findViewById(R.id.ingred_text);
			ingredToFind.setText(prevbundle.getString("findIngred"));
		}
		
		//Creating Button variable
        Button findRecipeBtn = (Button) findViewById(R.id.recipe_btn);      
       
        //Adding Listener to button
        findRecipeBtn.setOnClickListener(new View.OnClickListener() {         
            @Override
            public void onClick(View v) {
            	EditText recipeToFind = (EditText) findViewById(R.id.recipename_text);
            	EditText ingredToFind = (EditText) findViewById(R.id.ingred_text);
            	EditText ingred2ToFind = (EditText) findViewById(R.id.ingred2_text);
            	EditText ingred3ToFind = (EditText) findViewById(R.id.ingred3_text);
                CheckBox chkDairy = (CheckBox) findViewById(R.id.chkDairyfree);
                CheckBox chkGluten = (CheckBox) findViewById(R.id.chkGluten);
                
                Intent intent = new Intent(_context, RecipeResultActivity.class);
                Bundle bundle = new Bundle();
                
                bundle.putString("recipename", recipeToFind.getText().toString());
                
                if(!ingredToFind.getText().toString().equals("")){
                	bundle.putString("ingredient", ingredToFind.getText().toString());
                }
                
                if(!ingred2ToFind.getText().toString().equals("")){
                	bundle.putString("ingredient2", ingred2ToFind.getText().toString());
                }
                
                if(!ingred3ToFind.getText().toString().equals("")){
                	bundle.putString("ingredient3", ingred3ToFind.getText().toString());
                }
                
                if(chkDairy.isChecked()){
                	bundle.putString("chkDairy", "true");
                }
                
                if(chkGluten.isChecked()){
                	bundle.putString("chkGluten", "true");
                }
                
                // pass file resource id to main activity
                intent.putExtras(bundle);
			    startActivity(intent);
            }
        });
	}
	
	public void addListenerOnButton() {
		final Context context = this;
		
		homeMenuBtn = (ImageButton) findViewById(R.id.recipe_menu_btn);
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
