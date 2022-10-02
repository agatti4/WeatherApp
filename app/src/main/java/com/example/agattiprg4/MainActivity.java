package com.example.agattiprg4;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

//Period class to store each period of weather
class period {
    public String timePeriodArray;
    public int temperatureArray;
    public String descArray;

    //Constructor
    public period(String timePeriodArray, int temperatureArray, String descArray) {
        this.timePeriodArray = timePeriodArray;
        this.temperatureArray = temperatureArray;
        this.descArray = descArray;
    }

    //Getters
    public String getTimePeriodArray() {
        return timePeriodArray;
    }

    public int getTemperatureArray() {
        return temperatureArray;
    }

    public String getDescArray() {
        return descArray;
    }
}

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private String zipCode = "";
    private int longtPulled;
    private int lattPulled;
    private int counter = 1;
    public period[] arr = new period[14];
    ArrayList<myList> theList;
    myListAdapter itemAdapter;
    View adaptView;

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        /**
         * When a list is clicked on gets the data
         * @param adapterView
         * @param view
         * @param i
         * @param l
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            adaptView = view;
            itemAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Method called when app is started
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Create", "Oncreate");

        generateList(); //Creates an empty list

        itemAdapter = new myListAdapter(this, theList);
        ListView listViews = findViewById(R.id.listViews);
        listViews.setAdapter(itemAdapter);

        listViews.setOnItemClickListener(listener);

    }

    /**
     * Updates the cityName when zip code entered
     * @param weather
     */
    private void updateUiCoord(EventCoord weather) {
        // Display the city name in the UI
        TextView nameOfCityTextView = (TextView) findViewById(R.id.enterCityName);
        nameOfCityTextView.setText(weather.nameOfCity);
        nameOfCityTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Method that creates an empty list of myList objects
     */
    private void generateList() {
        theList = new ArrayList<myList>();
    }

    /**
     * Method that adds the forecast
     *
     * @param view
     */
    public void addForecast(View view) {

        //Pulls the zip code
        EditText zipTV = findViewById(R.id.enterZip);
        String enterZipString = zipTV.getText().toString();

        zipCode = enterZipString;


        //Checks if the zipcode is length 5
        if (enterZipString.length() == 5) {
            //Removes old forecast
            int counter2 = counter;
            for (int i = counter - 1; i > 0; i--) {
                theList.remove(itemAdapter.getItem(i - 1));
                counter2--;
            }
            counter = counter2;

            //Task to get the name of the city and latt and longt
            WeatherAsyncTaskCoord taskCoord = new WeatherAsyncTaskCoord();
            taskCoord.execute();

            //Task to get the forecasts
            WeatherAsyncTask task = new WeatherAsyncTask();
            task.execute();

            //Hides The Keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            itemAdapter = new myListAdapter(this, theList);
            ListView listViews = findViewById(R.id.listViews);
            listViews.setAdapter(itemAdapter);

            listViews.setOnItemClickListener(listener);

            //Hides The Keyboard
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        else { //Toast if not a valid zip code
            Toast toast= Toast.makeText(getApplicationContext(),
                    "Invalid Zip Code", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the the forecasts
     */
    private class WeatherAsyncTask extends AsyncTask<URL, Void, Event> {

        @Override
        protected Event doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl("https://api.weather.gov/gridpoints/BOX/" + lattPulled + "," + longtPulled + "/forecast");
            String help = "https://api.weather.gov/gridpoints/BOX/" + lattPulled + "," + longtPulled + "/forecast";
            Log.i("URL: ", help);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            Event weather = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link WeatherAsyncTask}
            return weather;
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                //add line to fix 403 error
                urlConnection.setRequestProperty("User-Agent", "MyApplication/v1.0 (http://foo.bar.baz; foo@bar.baz)");
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "HTTP error, response code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem reading stream from HTTP response", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


        /**
         * Return an {@link Event} object by parsing out information
         * about the first forecast
         */
        private Event extractFeatureFromJson(String weatherJSON) {
            if (TextUtils.isEmpty(weatherJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(weatherJSON);
                JSONObject propertyObject = baseJsonResponse.getJSONObject("properties");
                JSONArray periodArray = propertyObject.getJSONArray("periods");
                // If there are results in the features array
                if (periodArray.length() > 0) {
                    for (int i = 0; i < 14; i++) {
                        JSONObject indexObject = periodArray.getJSONObject(i);
                        String timePeriod = indexObject.getString("name");
                        int temperature = indexObject.getInt("temperature");
                        String descriptionSpeed = indexObject.getString("windSpeed");
                        String descriptionDir = indexObject.getString("windDirection");
                        String description = descriptionSpeed + " " + descriptionDir;
                        arr[i] = new period(timePeriod, temperature, description);
                        theList.add(new myList(i, arr[i].getTimePeriodArray(), arr[i].getTemperatureArray(), arr[i].getDescArray()));
                        counter++;

                        Log.i("Test", String.valueOf(i));
                        Log.i("timePeriod", arr[i].getTimePeriodArray());
                        Log.i("temperature", String.valueOf(temperature));
                        Log.i("timePeriod", description);
                        // Create a new {@link Event} object
                        //return new Event(timePeriod, temperature, description);
                    }
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the weather JSON results", e);
            }
            //Hides The Keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            return null;
        }
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the name of the city and pulls long and lat
     */
    private class WeatherAsyncTaskCoord extends AsyncTask<URL, Void, EventCoord> {

        @Override
        protected EventCoord doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrlCoord(zipCode);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            EventCoord weather = extractCoordFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link WeatherAsyncTask}
            return weather;
        }

        /**
         * Update the screen with the given forecast (which was the result of the
         * {@link WeatherAsyncTaskCoord}).
         */
        @Override
        protected void onPostExecute(EventCoord weather) {
            if (weather == null) {
                return;
            }

            updateUiCoord(weather);
        }

        //Creates url based off the zipcode to get long and lat
        private URL createUrlCoord(String zipcode) {
            URL url = null;
            try {
                url = new URL("https://geocode.xyz/" + zipcode + "?json=1");
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                //add line to fix 403 error
                urlConnection.setRequestProperty("User-Agent", "MyApplication/v1.0 (http://foo.bar.baz; foo@bar.baz)");
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "HTTP error, response code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem reading stream from HTTP response", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private EventCoord extractCoordFromJson(String weatherJSON) {
            if (TextUtils.isEmpty(weatherJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(weatherJSON);
                JSONObject cityPull = baseJsonResponse.getJSONObject("standard");
                String nameOfCity = cityPull.getString("city");
                int longt = abs(baseJsonResponse.getInt("longt"));
                longtPulled = longt;
                int latt = abs(baseJsonResponse.getInt("latt"));
                lattPulled = latt;
                // Create a new {@link Event} object
                return new EventCoord(nameOfCity, longt, latt);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the forecast JSON results", e);
            }
            return null;
        }
    }
}
