package com.example.newsapp;


import android.text.TextUtils;
import android.util.Log;

import com.example.newsapp.adpter.News;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class QueryUtils {


  private static final String LOG_TAG = QueryUtils.class.getSimpleName();

  private QueryUtils() {
  }

  /**
   * Return a list of {@link com.example.newsapp.adpter.News} objects that has been built up from
   * parsing a JSON response.
   */
  private static List<News> extractFeatureFromJson(String earthquakeJSON) {

    if (TextUtils.isEmpty(earthquakeJSON)) {
      return null;
    }

    // Create an empty ArrayList that we can start adding News to
    List<News> Update_News = new ArrayList<>();

    try {

      // Create a JSONObject from the JSON response string
      JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
      Log.e(LOG_TAG, "response: " + earthquakeJSON);

      JSONObject news_object = baseJsonResponse.getJSONObject("response");

      JSONArray newsesArray = news_object.getJSONArray("results");

      Log.e(LOG_TAG, "RESULTS ARRAY: " + newsesArray);
      // For each earthquake in the newsArray, create an {@link news} object
      for (int i = 0; i < newsesArray.length(); i++) {

        // Get a single news at position i within the list of earthquakes
        JSONObject currentNews = newsesArray.getJSONObject(i);
        // For a given news , extract the JSONObject associated with the
        Log.println(Log.INFO, LOG_TAG, String.valueOf(currentNews));

        String authorArticle;

        if (currentNews.has("tags")) {

          JSONArray tagsArray = currentNews.getJSONArray("tags");

          if (!currentNews.isNull("tags") && tagsArray.length() > 0) {

            JSONObject tagObject = (JSONObject) tagsArray.get(0);
            Log.e(LOG_TAG, "tags " + tagsArray);
            authorArticle = tagObject.getString("webTitle");

          } else {
            authorArticle = "* unknown author *";
          }
        } else {
          authorArticle = "* missing author *";
        }

        String titleArticle = currentNews.getString("webTitle");
        Log.e(LOG_TAG, "article " + titleArticle);

        String sectionArticle = "#" + currentNews.getString("sectionName");
        Log.e(LOG_TAG, "sectionArticle " + sectionArticle);
        String imageArticle;

        //   if(currentNews.has("fields")){ }
        JSONObject fields = currentNews.getJSONObject("fields");
        Log.e(LOG_TAG, "Fields " + fields);

        if (fields.has("thumbnail")) {
          imageArticle = fields.getString("thumbnail");
          Log.e(LOG_TAG, "IMAGE " + imageArticle);
        } else {
          continue;

        }

        String dateArticle = currentNews.getString("webPublicationDate");
        Log.e(LOG_TAG, "dateArticle : " + dateArticle);

        String urlArticle = currentNews.getString("webUrl");
        Log.e(LOG_TAG, "URL Article " + urlArticle);

        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateArticle);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        dateArticle = formattedDate;

        // Create a new {@link Earthquake} object with the magnitude, location, time,
        // and url from the JSON response.

        News news = new News(titleArticle, authorArticle, dateArticle, urlArticle, imageArticle,
            sectionArticle);

        // Add the new {@link Earthquake} to the list of earthquakes.
        Update_News.add(news);
      }

    } catch (JSONException | ParseException e) {
      // If an error is thrown when executing any of the above statements in the "try" block,
      // catch the exception here, so the app doesn't crash. Print a log message
      // with the message from the exception.
      Log.e("QueryUtils", "Problem parsing the News JSON results", e);
    }

    // Return the list of news
    return Update_News;


  }


  private static URL createUrl(String stringUrl) {
    URL url = null;
    Log.e("MyUrl", stringUrl + "  ");

    try {
      url = new URL(stringUrl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "Error with creating URL ", e);
    }

    return url;
  }

  private static String makeHttpRequest(URL url) throws IOException {
    String jsonResponse = "";

    // If the URL is null, then return early.
    if (url == null) {
      return jsonResponse;
    }

    HttpURLConnection urlConnection = null;
    InputStream inputStream = null;
    try {
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setReadTimeout(10000 /* milliseconds */);
      urlConnection.setConnectTimeout(15000 /* milliseconds */);
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      // If the request was successful (response code 200),
      // then read the input stream and parse the response.
      if (urlConnection.getResponseCode() == 200) {
        inputStream = urlConnection.getInputStream();
        jsonResponse = readFromStream(inputStream);
      } else {
        Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
      }
    } catch (IOException e) {
      Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return jsonResponse;
  }

  private static String readFromStream(InputStream inputStream) throws IOException {
    StringBuilder output = new StringBuilder();
    if (inputStream != null) {
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
          Charset.forName("UTF-8"));
      BufferedReader reader = new BufferedReader(inputStreamReader);
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        line = reader.readLine();
      }
    }
    return output.toString();
  }

  public static List<News> fetchNewsData(String requestUrl) {

    Log.i(LOG_TAG, "Test: fetchNewsData Called ");
    Log.i(LOG_TAG, "Test:" + requestUrl);

    // Create URL object
    URL url = createUrl(requestUrl);

    // Perform HTTP request to the URL and receive a JSON response back
    String jsonResponse = null;
    try {
      jsonResponse = makeHttpRequest(url);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Problem making the HTTP request.", e);
    }

    // Extract relevant fields from the JSON response and create a list of {@link news}s

    // Return the list of {@link News}s
    return extractFeatureFromJson(jsonResponse);
  }


}