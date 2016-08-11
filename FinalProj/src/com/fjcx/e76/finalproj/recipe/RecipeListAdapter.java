package com.fjcx.e76.finalproj.recipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fjcx.e76.finalproj.R;
import com.fjcx.e76.finalproj.R.id;
import com.fjcx.e76.finalproj.model.PantryItem;
import com.fjcx.e76.finalproj.util.ImageThreadLoader;
import com.fjcx.e76.finalproj.util.ImageThreadLoader.ImageLoadedListener;

public class RecipeListAdapter extends ArrayAdapter<RecipeItem> {
	  private final static String TAG = "MediaItemAdapter";
	  private int resourceId = 0;
	  private LayoutInflater inflater;
	  private Context context;
	 
	  private ImageThreadLoader imageLoader = new ImageThreadLoader();
	 
	  public RecipeListAdapter(Context context, int resourceId, List<RecipeItem> mediaItems) {
	    super(context, 0, mediaItems);
	    this.resourceId = resourceId;
	    inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    this.context = context;
	  }
	 
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	 
	    View view;
	    TextView textTitle;
	    TextView textTimer;
	    final ImageView image;
	 
	    view = inflater.inflate(resourceId, parent, false);
	 
	    try {
	      textTitle = (TextView)view.findViewById(R.id.recipelistName);
	      image = (ImageView)view.findViewById(R.id.recipeImg);
	    } catch( ClassCastException e ) {
	      Log.e(TAG, "Your layout must provide an image and a text view with ID's icon and text.", e);
	      throw e;
	    }
	 
	    RecipeItem item = getItem(position);
	    Bitmap cachedImage = null;
	    try {
	      cachedImage = imageLoader.loadImage(item.getImageUrl(), new ImageLoadedListener() {
	      public void imageLoaded(Bitmap imageBitmap) {
	      image.setImageBitmap(imageBitmap);
	      notifyDataSetChanged();                }
	      });
	    } catch (MalformedURLException e) {
	      Log.e(TAG, "Bad remote image URL: " + item.getImageUrl(), e);
	    }
	 
	    textTitle.setText(item.getRecipeName());
	 
	    if( cachedImage != null ) {
	      image.setImageBitmap(cachedImage);
	    }
	 
	    return view;
	  }
	}