package com.example.maleshen.bakingapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.maleshen.bakingapp.model.Ingredient;
import com.example.maleshen.bakingapp.model.Receipt;
import com.example.maleshen.bakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import moera.ermais.google.com.popular_movies.R;
import moera.ermais.google.com.popular_movies.model.Movie;


public class BakingUtils {

    private BakingUtils() {
        // Hide the implicit public one
    }

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movies.
     *
     * @param forecastJsonStr JSON response from server
     * @return Array of Strings describing movies data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Receipt> getSimpleReceiptStringsFromJson(Context context, String bakingJsonStr)
            throws JSONException {

        List<Receipt> parsedReceiptData;
        JSONArray bakingArray = new JSONArray(bakingJsonStr);
        parsedReceiptData = new ArrayList<>();

        for (int i = 0; i < bakingArray.length(); i++) {
            /* Get the JSON object representing the day */
            JSONObject receiptJson = bakingArray.getJSONObject(i);
            Receipt receipt = new Receipt();

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */

            receipt.setId(receiptJson.getInt("id"));
            receipt.setImage(receiptJson.getString("image"));
            receipt.setIngredients();
            receipt.setName(receiptJson.getString("name"));
            receipt.setServings(receiptJson.getInt("servings"));
            receipt.setSteps();


            parsedReceiptData.add(receipt);
        }

        return parsedReceiptData;
    }

    /**
     * Compress {@link Bitmap} and convert it to byte array
     *
     * @param bitmap Some {@link Bitmap}
     * @return byte array with compressed {@link Bitmap} data
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Decode byte array to {@link Bitmap}
     *
     * @param imageData byte array with {@link Bitmap} data
     * @return decoded {@link Bitmap}
     */
    public static Bitmap getImage(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    public static List<Ingredient> getSimpleIngridientStringsFromJson(Context context, String ingridientsJsonStr)
            throws JSONException {

        List<Ingredient> parsedIngridientData;
        JSONArray ingridientArray = new JSONArray(ingridientsJsonStr);
        parsedIngridientData = new ArrayList<>();

        for (int i = 0; i < ingridientArray.length(); i++) {
            /* Get the JSON object representing the day */
            JSONObject ingridientJson = ingridientArray.getJSONObject(i);
            Ingredient ingredient = new Ingredient();

            ingredient.setIngredient(ingridientJson.getString("ingredient"));
            ingredient.setQuantity(ingridientJson.getDouble("quantity"));
            ingredient.setMeasure(ingridientJson.getString("measure"));

            parsedIngridientData.add(ingredient);

        }
        return parsedIngridientData;
    }

    public static List<Step> getSimpleStepStringsFromJson(Context context, String stepJsonStr)
            throws JSONException {

        List<Step> parsedStepData;
        JSONArray stepArray = new JSONArray(stepJsonStr);
        parsedStepData = new ArrayList<>();

        for (int i = 0; i < stepArray.length(); i++) {
            /* Get the JSON object representing the day */
            JSONObject stepJson = stepArray.getJSONObject(i);
            Step step = new Step();

            step.setDescription(stepJson.getString());
            step.setId();
            step.setShortDescription(stepJson.getString());
            step.setThumbnailURL(stepJson.getString());
            step.setVideoURL(stepJson.getString());
            ingredient.setIngredient(stepJson.getString("ingredient"));
            ingredient.setQuantity(stepJson.getDouble("quantity"));
            ingredient.setMeasure(stepJson.getString("measure"));

            parsedStepData.add(step);

        }
        return parsedStepData;
    }

}
