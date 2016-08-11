package com.fjcx.e76.finalproj.recipe;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleRecipeItem {
	
	private String recipeName;
	private String imageUrl;
	private String sourceUrl;
	private int rating;
	private int numServe;
	private int cookingTime;
	private ArrayList<String> ingredients;
    private HashMap<String, Integer> flavors;
    private HashMap<String, String> nutrition;
    
    public SingleRecipeItem() {
    	
    }
    
    public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getNumServe() {
		return numServe;
	}

	public void setNumServe(int numServe) {
		this.numServe = numServe;
	}

	public int getCookingTime() {
		return cookingTime;
	}

	public void setCookingTime(int cookingTime) {
		this.cookingTime = cookingTime;
	}

	public ArrayList<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}

	public HashMap<String, Integer> getFlavors() {
		return flavors;
	}

	public void setFlavors(HashMap<String, Integer> flavourmap) {
		this.flavors = flavourmap;
	}

	public HashMap<String, String> getNutrition() {
		return nutrition;
	}

	public void setNutrition(HashMap<String, String> nutrition) {
		this.nutrition = nutrition;
	}

}
