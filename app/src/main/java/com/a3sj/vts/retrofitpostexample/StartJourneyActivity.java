package com.a3sj.vts.retrofitpostexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.a3sj.vts.retrofitpostexample.MainActivity.locationManager;

public class StartJourneyActivity extends AppCompatActivity implements Thread.UncaughtExceptionHandler {
    Button start, stop;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 50;
    private GoogleApiClient client;
    String selectedRoute;
    String selectedTime;
    String selectedBus;
    String selectedDate;
    String currentDate;
    TextView textViewSetRoute;
    TextView textViewSetTime;
    TextView textViewSetBus;
    TextView textView5;
    static SharedPreferences dataSave;
    private String selectedTime1;
    private String selectedDateTime;
    SharedPreferences sharedPreferences;
    String driver_email=" ";
    private SharedPreferences journeySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start_journey);
            start = (Button) findViewById(R.id.startButton);
            stop = (Button) findViewById(R.id.stopButton);
            textViewSetTime= (TextView) findViewById(R.id.textView7);
            textViewSetBus= (TextView) findViewById(R.id.textView8);
            textViewSetRoute= (TextView) findViewById(R.id.textView6);
            textView5= (TextView) findViewById(R.id.textView5);
            textView5.setTextColor(Color.RED);
            journeySharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            selectedRoute=getIntent().getExtras().getString("ROUTEKEY");
            selectedTime=getIntent().getExtras().getString("TIMEKEY");
            selectedBus=getIntent().getExtras().getString("BUSNOKEY");
            selectedDate=getIntent().getExtras().getString("DATEKEY");
            textViewSetRoute.setText(selectedRoute);
            textViewSetRoute.setTextColor(Color.BLUE);
            textViewSetTime.setText(selectedTime);
            textViewSetTime.setTextColor(Color.BLUE);
            textViewSetBus.setText(selectedBus);
            textViewSetBus.setTextColor(Color.BLUE);
            sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            driver_email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "").toString();
            dataSave = getSharedPreferences("firstTime", 0);
            if(dataSave.getString("firstTime", "").toString().equals("no") && locationManager!=null){ // first run is happened
                Intent intent=new Intent(StartJourneyActivity.this, MainActivity.class);
/*                intent.putExtra("ROUTEKEY1",selectedRoute);
                intent.putExtra("BUSKEY1",selectedBus);
                intent.putExtra("DATEKEY",selectedDate);
                intent.putExtra("DEPTIMEKEY",selectedTime1);*/
                startActivity(intent);
            }
            else{ //  this is the first run of application
                SharedPreferences.Editor editor = dataSave.edit();
                editor.putString("firstTime", "no");
                editor.commit();
            }
            start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor2=journeySharedPreferences.edit();
                editor2.putBoolean(Config.JOURNEYSTARTED_SHARED_PREF,false);
                editor2.commit();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                currentDate=simpleDateFormat.format(c.getTime());
                selectedTime1=selectedTime.substring(0,selectedTime.length()-3);
                selectedDateTime=selectedDate+" "+selectedTime1;
               /* if(currentDate.equals(selectedDateTime)) {
                    EnableGPSIfPossible();
                }
                else
                {
                    buildAlertMessageCannotStart();
                }*/
                EnableGPSIfPossible();
            }
        });
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity m=MainActivity.getInstance();
                    m.stopGetLocation();

                    Toast.makeText(StartJourneyActivity.this, "JOURNEY STOPPED", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Exception:"+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private  void buildAlertMessageCannotStart() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This is not the correct time to start this journey..!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }
    private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void EnableGPSIfPossible()
    {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
            Toast.makeText(StartJourneyActivity.this, "Wait for a moment...", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(StartJourneyActivity.this, MainActivity.class);
            intent.putExtra("ROUTEKEY1",selectedRoute);
            intent.putExtra("BUSNOKEY1",selectedBus);
            intent.putExtra("DRIVEREMAILKEY",driver_email);
            intent.putExtra("DATEKEY",selectedDate);
            sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString(Config.KEY_SELECTEDDATE, selectedDate);
            editor.commit();
            sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1=sharedPreferences.edit();
            editor1.putString(Config.KEY_SELECTEDTIME, selectedTime1);
            editor1.commit();
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //checkGPSisON();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        throw new RuntimeException("You Reached at your destination");
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false);
        if(!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_previously_started), Boolean.TRUE);
            edit.commit();
            //showHelp();
        }
    }*/
}
