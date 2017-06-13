package com.a3sj.vts.retrofitpostexample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.a3sj.vts.retrofitpostexample.StartJourneyActivity.dataSave;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    //Declaring views
    private boolean isStopped=false;
    static LocationManager locationManager;
    Location location;
    static MainActivity instance;
    double latitude;
    double longitude;
    ProgressDialog dialog;
    TextView lat;
    TextView lon;
    Button stopJourneyButton;
    //This is our root url
    Boolean isReached=false;
    public static final String ROOT_URL = "http://vts.comeze.com/TrackAppData/InsertLocationUpdates.php";
    private SharedPreferences dataSave1;
    private String route;
    private String departuretime;
    private String bus_number;
    private String date;
    private String driver_email;
    private SharedPreferences journeysharedPreferences1;
    private  String isStarted1;
    private SharedPreferences sharedPreferences;
    private String reachedtime=" ";
    private SharedPreferences journeySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lat = (TextView) findViewById(R.id.latitudeTextView);
        lon = (TextView) findViewById(R.id.longitudeTextView);
        stopJourneyButton= (Button) findViewById(R.id.stopJourneyButton);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait!");
        dialog.setCancelable(false);
        dialog.show();
       journeySharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        departuretime = sharedPreferences.getString(Config.KEY_SELECTEDTIME, "").toString();
        route=getIntent().getExtras().getString("ROUTEKEY1");
        bus_number=getIntent().getExtras().getString("BUSNOKEY1");
        date=sharedPreferences.getString(Config.KEY_SELECTEDDATE," ").toString();
        driver_email=sharedPreferences.getString(Config.EMAIL_SHARED_PREF," ").toString();
        Toast.makeText(MainActivity.this, "Start Journey Now..", Toast.LENGTH_LONG).show();

        //Adding listener to button
        // buttonRegister.setOnClickListener(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        stopJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor2=journeySharedPreferences.edit();
                editor2.putBoolean(Config.JOURNEYSTARTED_SHARED_PREF,false);
                editor2.commit();
                stopGetLocation();
                SharedPreferences.Editor editor = dataSave.edit();
                SharedPreferences.Editor editor1 = dataSave1.edit();
                editor.putString("firstTime", "yes");
                editor1.putString("firstTime", "no");
                editor.commit();
                editor1.commit();
                isStopped=true;
                startActivity(new Intent(MainActivity.this, Home.class));
            }
        });
    }
public static MainActivity getInstance(){
    return instance;
}
    public void stopGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
        locationManager=null;
    }

    private void insertUser(){
        //Here we will handle the http request to insert user to mysql db
        //Here we will handle the http request to insert user to mysql db
        //Creating a RestAdapter
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        //Creating object for our interface
        RegisterAPI api = adapter.create(RegisterAPI.class);

        //Defining the method insertuser of our interface
        api.insertUser(

                //Passing the values by getting it from editTexts
                route,
                bus_number,
                driver_email,
                lat.getText().toString(),
                lon.getText().toString(),
                date,
                departuretime,
                reachedtime,
                //isReached,

                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        BufferedReader reader = null;

                        //An string to store output from the server
                        String output = "";

                        try {
                            //Initializing buffered reader
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            //Reading the output in the string
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Displaying the output as a toast
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        Toast.makeText(MainActivity.this, "Error:"+error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

    }
    @Override
    public void onClick(View view) {
       // insertUser();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        dialog.dismiss();
        lat.setText(String.valueOf(latitude));
        lon.setText(String.valueOf(longitude));
       // Toast.makeText(MainActivity.this, "location changed: " + latitude + " " + longitude, Toast.LENGTH_SHORT).show();
        //insertData();
        dataSave1 = getSharedPreferences("firstTime", 0);
        if(dataSave1.getString("firstTime", "").toString().equals("yes") && locationManager!=null){
            insertUser();
        }
        else{ //  this is the first run of application
            SharedPreferences.Editor editor1 = dataSave1.edit();
            editor1.putString("firstTime", "yes");
            editor1.commit();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isStopped != true) {
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        }
    }
    public void insertData(){
        //Creating a string request
        Toast.makeText(MainActivity.this, route+" "+bus_number+" "+driver_email+" "+latitude+" "+longitude+" "+date+" "+departuretime+" "+reachedtime, Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        if(response.equalsIgnoreCase("Successfully Inserted")){
                            //Creating a shared preference
                            //SharedPreferences sharedPreferences;// = DriverLogin.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            Toast.makeText(MainActivity.this, "Successfully Inserted", Toast.LENGTH_LONG).show();

                            //Starting profile activity
                        }
                        else
                        if(response.equalsIgnoreCase("Not Inserted")){

                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(MainActivity.this, "Not Inserted", Toast.LENGTH_LONG).show();
                        }
                        else
                        if(response.equalsIgnoreCase("Successfully Updated")){

                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(MainActivity.this, "Successfully Updated", Toast.LENGTH_LONG).show();
                        }
                        else
                        if(response.equalsIgnoreCase("Not Updated")){

                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(MainActivity.this, "Not Updated", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Something went wrong:"+response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(MainActivity.this, "Exception: "+error, Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("route",route);
                params.put("bus_number",bus_number);
                params.put("driver_email",driver_email);
                params.put("lattitude",lat.getText().toString());
                params.put("longitude",lon.getText().toString());
                params.put("date",date);
                params.put("departuretime",departuretime);
                params.put("reachedtime",reachedtime);
                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
