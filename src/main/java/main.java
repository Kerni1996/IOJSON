import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class main {
    public static WebDriver driver = null;
    public static int id = 0;
    public static int lowsodiumCounter = 0;
    public static int lowfatCounter = 0;
    public static int lowCaloriesCounter = 0;
    public static int highproteinCounter = 0;
    public static int highFatModeratProetinCounter = 0;
    public static LinkedList<Recipe> recipes = new LinkedList<Recipe>();
    public static JSONArray recipeJsonList = new JSONArray();
    public static JSONParser jsonParser = new JSONParser();
    public enum Categories
    {
        HIGHFATMODERATEPROTEIN,HIGHPROTEIN, LOWSODIUM, LOWFAT, LOWCALORIE
    }
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "src/main/chromedriver.exe");
        driver = new ChromeDriver();

        //handle cookies
        driver.get("https://www.epicurious.com/recipes-menus");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        cookieButton.click();




        //System.out.println(retrieveImageURL("Korean Marinated Beef "));
        //System.out.println(retrieveImageURL("Trinidad Curry"));
        //System.out.println(retrieveImageURL("gfdbfdjui"));



        try (FileReader reader = new FileReader("src/main/recipes.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray employeeList = (JSONArray) obj;
            //System.out.println(employeeList);

            //Iterate over employee array


            for (int i = 0; i<employeeList.size(); i++) {
                parseEmployeeObject((JSONObject) employeeList.get(i));
            }
            driver.close();
            System.out.println("number of recipes: " + employeeList.size());
            System.out.println("number of recipes labeled: " + recipes.size());
            System.out.println("lowsodium recipes: " + lowsodiumCounter);
            System.out.println("lowfat recipes: " + lowfatCounter);
            System.out.println("lowCalories recipes: " + lowCaloriesCounter);
            System.out.println("highProtein recipes: "+ highproteinCounter);
            System.out.println("highFatModeratProetin recipes: " + highFatModeratProetinCounter);
            //employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String retrieveImageURL(String recipeName) throws Exception{

        try {

            driver.get("https://www.epicurious.com/search/" + URLEncoder.encode(recipeName,"UTF-8"));
            /*
            //driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            //WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
            //cookieButton.click();

            WebElement searchForm = driver.findElement(By.name("search"));
            //WebElement searchForm = driver.findElement(By.cssSelector(".search-utility-hero-form"));
            //searchForm.click();
            //WebElement inputField = driver.findElement(By.id("inputTerms"));
            //inputField.sendKeys(recipeName);
            //inputField.sendKeys(Keys.ENTER);
            searchForm.sendKeys(recipeName);
            searchForm.sendKeys(Keys.ENTER);
            //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            */List<WebElement> headers = driver.findElements(By.tagName("h1"));
            if (headers.size() != 0) {
                if (headers.get(0).getText().equals("DON'T CRY!")) {
                    System.out.println("no recipe/article found");
                    throw new NotFoundException("No recipe or article found");
                }
            /*if (headers.get(0).getText().contains("Cry")){
                System.out.println("error");
                return null;
            }
            */
            }


            List<WebElement> articlesAndRecipes = driver.findElements(By.className("tag"));
            String type = null;
            WebElement recipe = null;
            for (int i = 0; i < articlesAndRecipes.size(); i++) {
                Random rand = new Random();
                recipe = ((ChromeDriver) driver).findElementsByClassName("view-complete-item").get(rand.nextInt(articlesAndRecipes.size()));
                type = "ARTICLE";
                if (articlesAndRecipes.get(i).getText().equals("RECIPE")) {
                    type = "RECIPE";
                    recipe = ((ChromeDriver) driver).findElementsByClassName("view-complete-item").get(i);
                    break;
                }
            }

            String href = recipe.getAttribute("href");

            driver.get(href);

            String src = null;
            if (type.equals("ARTICLE")) {

                WebElement image = driver.findElements(By.className("responsive-image__image")).get(2);
                src = image.getAttribute("src");
            } else if (type.equals("RECIPE")) {
                WebElement image = driver.findElements(By.tagName("source")).get(0);
                src = image.getAttribute("srcset");

            }
            //WebElement recipe = ((ChromeDriver) driver).findElementByClassName("view-complete-item");
            //recipe.click();
            //WebElement image = ((ChromeDriver) driver).findElementByClassName("lead-asset__media responsive-image");
            //String src = image.getAttribute("src");

            //searchForm.sendKeys("TestInput");

            return src;

            //if internet connection is disconnected dont stop program
        }catch (org.openqa.selenium.WebDriverException e){
            TimeUnit.SECONDS.sleep(5);
            return retrieveImageURL(recipeName);
        }
    }

    public static void writeFile(JSONObject jsonObject) throws IOException, ParseException {
        JSONArray recipes = readFile();
        FileWriter file = new FileWriter("src/main/recipesLabeled.json");

        recipes.add(jsonObject);

        file.write(recipes.toJSONString());
        file.flush();
    }

    public static JSONArray readFile() throws ParseException {
        JSONArray employeeList = null;
        try {
            FileReader reader = new FileReader("src/main/recipesLabeled.json");
            //Read JSON file

            Object obj = jsonParser.parse(reader);

             employeeList= (JSONArray) obj;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static Recipe parseEmployeeObject(JSONObject employee) throws IOException, ParseException {

        String title = "";
        JSONArray directions = new JSONArray();
        double fat = 0;
        double calories = 0;
        double protein = 0;
        JSONArray ingredients = new JSONArray();
        double sodium = 0;

        try {
            id++;
            title = (String) employee.get("title");
            //System.out.println(title);
            //Get employee object within list
            directions = (JSONArray) employee.get("directions");
            fat = ((double) employee.get("fat"))*9;
            calories = (double) employee.get("calories");
            protein = ((double) employee.get("protein"))*4;

            ingredients = (JSONArray) employee.get("ingredients");
            sodium = (double) employee.get("sodium");
        }catch (NullPointerException e){
            return null;
        }







        LinkedList<Categories> categories = new LinkedList<Categories>();



        if (sodium<=200){
           categories.add(Categories.LOWSODIUM);
        }


        if (((fat/calories)*100)<=30){
            categories.add(Categories.LOWFAT);
        }

        if (calories<=250){
            categories.add(Categories.LOWCALORIE);
        }

        if (((protein/calories)*100)>=15){
            categories.add(Categories.HIGHPROTEIN);
        }
        if ((((fat/calories)*100)>=55) && (((protein/calories)*100)<=15)){
            categories.add(Categories.HIGHFATMODERATEPROTEIN);
        }
        /*if ((((fat/calories)*100)>=15)&&(((protein/calories)*100)>=10)){
            categories.add(Categories.HIGHFATMODERATEPROTEIN);
        }
        */


        Recipe recipe = null;
        String imageURL = null;

        //check if any of the categories does not have 1000 entities.
        //if yes add to recipes
        //if all all categories of the Recipe have at least 1000 entries, skip the Recipe (to keep the dataset to a manageable size)
        if ((categories.contains(Categories.LOWSODIUM)&&lowsodiumCounter<1000)||(categories.contains(Categories.LOWFAT)&&lowfatCounter<1000)||(categories.contains(Categories.LOWCALORIE)&&lowCaloriesCounter<1000)||(categories.contains(Categories.HIGHPROTEIN)&&highproteinCounter<1000)||(categories.contains(Categories.HIGHFATMODERATEPROTEIN)&&highFatModeratProetinCounter<1000)){
            try {
                imageURL = retrieveImageURL(title);
            }catch (Exception e){
                System.err.println("recipe with id " + id + " could not find image. skipping recipe");
                return recipe;
            }

            recipe = new Recipe(imageURL,directions,fat,categories,calories,protein,title,ingredients,sodium);
            if (categories.contains(Categories.LOWSODIUM)) lowsodiumCounter++;
            if (categories.contains(Categories.HIGHFATMODERATEPROTEIN)) highFatModeratProetinCounter++;
            if (categories.contains(Categories.HIGHPROTEIN)) highproteinCounter++;
            if (categories.contains(Categories.LOWCALORIE)) lowCaloriesCounter++;
            if (categories.contains(Categories.LOWFAT)) lowfatCounter++;

            JSONObject recipeJson = new JSONObject();
            recipeJson.put("imageURL",recipe.getImageURL());
            recipeJson.put("title",recipe.getTitle());
            recipeJson.put("directions",recipe.getDirection());
            recipeJson.put("fat",recipe.getFats());
            recipeJson.put("calories",recipe.getCalories());
            recipeJson.put("protein",recipe.getProtein());
            recipeJson.put("ingredients",recipe.getIngredients());
            recipeJson.put("sodium",recipe.getSodium());
            recipeJson.put("labels",recipe.getCategories());
            recipeJson.put("id",id);
            recipeJsonList.add(recipeJson);
            writeFile(recipeJson);



            recipes.add(recipe);
        }

        return recipe;


        /*System.out.println("directions");
        System.out.println(directions);
        System.out.println(fat);
        System.out.println("calories: " + calories);
        System.out.println("protein: " + protein);
        System.out.println(title);
        System.out.println(ingredients);
        System.out.println(sodium);
        */
        /*
        JSONObject employeeObject = (JSONObject) employee.get("employee");

        //Get employee first name
        String firstName = (String) employeeObject.get("firstName");
        System.out.println(firstName);

        //Get employee last name
        String lastName = (String) employeeObject.get("lastName");
        System.out.println(lastName);

        //Get employee website name
        String website = (String) employeeObject.get("website");
        System.out.println(website);
        */
    }
}


