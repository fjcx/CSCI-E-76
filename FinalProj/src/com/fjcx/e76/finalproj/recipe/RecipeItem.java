package com.fjcx.e76.finalproj.recipe;

import java.util.ArrayList;

public class RecipeItem {
	private String recipeId;
	private String imageUrl;
	private String recipeName;
    private ArrayList<String> flavors;
    private ArrayList<String> ingredients;
    
    public RecipeItem() {
    	
    }

    public RecipeItem(String recipeId, String recipeName, String imageUrl, ArrayList<String> ingredients, ArrayList<String> flavors) {
    	this.recipeId = recipeId;
        this.imageUrl = imageUrl;
        this.recipeName = recipeName;
        this.flavors = flavors;
        this.ingredients = ingredients;
    }
    
    public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public void setFlavors(ArrayList<String> flavors) {
		this.flavors = flavors;
	}

	public void setIngredients(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}

    public ArrayList<String> getFlavors() {
        return flavors;
    }
    public ArrayList<String> getIngredients() {
        return ingredients;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    public String getRecipeName() {
        return recipeName;
    }
    public String getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
	}

}
