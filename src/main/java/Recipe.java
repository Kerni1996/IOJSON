import org.json.simple.JSONArray;

import java.util.LinkedList;

public class Recipe {
    private JSONArray direction;
    private double fats;
    private double calories;
    private double protein;
    private String title;
    private JSONArray ingredients;
    private double sodium;
    private JSONArray categories;
    private String imageURL;

    public String getImageURL() {
        return imageURL;
    }

    public Recipe(String imageURL, JSONArray directions, double fats, LinkedList<main.Categories> categories, double calories, double protein, String title, JSONArray ingredients, double sodium) {
        this.direction = directions;
        this.fats = fats;
        this.categories = new JSONArray();
        this.imageURL = imageURL;

        //convert LinkedList to JSONArray
        for (int i = 0; i<categories.size(); i++){
            this.categories.add(categories.get(i).toString());
        }
        //System.out.println(ingredients);
        //System.out.println(this.categories);
        this.calories = calories;
        this.protein = protein;
        this.title = title;
        this.ingredients = ingredients;
        this.sodium = sodium;
    }




    public JSONArray getDirection() {
        return direction;
    }

    public double getFats() {
        return fats;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public String getTitle() {
        return title;
    }

    public JSONArray getIngredients() {
        return ingredients;
    }

    public double getSodium() {
        return sodium;
    }

    public JSONArray getCategories() {
        return categories;
    }
}
