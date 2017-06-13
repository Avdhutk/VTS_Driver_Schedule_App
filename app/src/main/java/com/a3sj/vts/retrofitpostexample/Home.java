package com.a3sj.vts.retrofitpostexample;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.a3sj.vts.retrofitpostexample.StartJourneyActivity.dataSave;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView textViewDriverName;
    TextView textViewDriverEmail;
    TextView noDataTextView;
    static String url = Config.DATA_URL.trim().toString();
    static String url1="http://vts.comeze.com/TrackAppData/getOtherSchedule.php";
    static String route = "";
    static String time = "";
    static String driver_name=" ";
    static String bus_no=" ";
    static String date=" ";
    private ProgressDialog loading;
    SharedPreferences sharedPreferences;
    String driver_email=" ";
    String nameOfDriver=" ";
    public ArrayList<String> arrayList1;
    public ArrayList<String> arrayList2;
    public ArrayList<String> arrayList3;
    public ArrayList<String> arrayList4;
    SharedPreferences journeySharedPreferences;
    public boolean isStarted=false;
    public ScheduleListViewAdapter scheduleListViewAdapter;
    private int year;
    private int month;
    private int day;
    //static SharedPreferences dataSave;
    public static boolean currentDate;
    static final int DATE_PICKER_ID = 1111;
    ListView listView;
    String new_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dataSave = getSharedPreferences("firstTime", 0);

        if (!isConnected(this))
            buildDialog(this).show();
     /*   else{ //  this is the first run of application
            SharedPreferences.Editor editor = dataSave.edit();
            editor.putString("firstTime", "no");
            editor.commit();
        }*/
            loading = ProgressDialog.show(this, "Please wait...", "Fetching Your Data...", false, false);
            sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            driver_email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "").toString();
            nameOfDriver = sharedPreferences.getString(Config.NAME_SHARED_PREF, "").toString();
            noDataTextView = (TextView) findViewById(R.id.NoDataTextView);
            listView = (ListView) findViewById(R.id.scheduleList);
            arrayList1 = new ArrayList<>();
            arrayList2 = new ArrayList<>();
            arrayList3 = new ArrayList<>();
            arrayList4 = new ArrayList<>();
            currentDate = true;
            getScheduleData();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            journeySharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            isStarted = journeySharedPreferences.getBoolean(Config.JOURNEYSTARTED_SHARED_PREF, false);
            if (isStarted) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(Home.this, StartJourneyActivity.class);
                    intent.putExtra("ROUTEKEY", arrayList1.get(position));
                    intent.putExtra("TIMEKEY", arrayList2.get(position));
                    intent.putExtra("BUSNOKEY", arrayList3.get(position));
                    intent.putExtra("DATEKEY", arrayList4.get(position));
                    startActivity(intent);
                }
            });
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            arrayList1.clear();
            arrayList2.clear();
            arrayList3.clear();
            listView.invalidateViews();
            loading = ProgressDialog.show(this, "Refreshing...", "Please Wait...", false, false);
            getScheduleData();
            //scheduleListViewAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.other_schedule) {
            // Handle the camera action
        } else if (id == R.id.verify) {

        } else if (id == R.id.logout) {
            //logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(Home.this, DriverLogin.class);
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void getScheduleData() {


            if (currentDate) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            ListView listView = (ListView) findViewById(R.id.scheduleList);

                            @Override
                            public void onResponse(String response) {
                                loading.dismiss();
                                //If we are getting success from server
                                try {
                                    if (response.equalsIgnoreCase("No Schedule Found")) {
                                        noDataTextView.setText("No Routine Found For Today..!");
                                        noDataTextView.setTextColor(Color.RED);
                                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                                        navigationView.setNavigationItemSelectedListener(Home.this);
                                        View headerView = navigationView.getHeaderView(0);
                                        //View headerView= LayoutInflater.from(Home.this).inflate(R.layout.nav_header_home,null);
                                        textViewDriverName = (TextView) headerView.findViewById(R.id.textViewDriverName);
                                        textViewDriverEmail = (TextView) headerView.findViewById(R.id.textViewDriverEmail);
                                        textViewDriverName.setText(nameOfDriver);
                                        textViewDriverEmail.setText(driver_email);
                                    } else {
                                        noDataTextView.setText("Your Today's Routine:");
                                        noDataTextView.setTextColor(Color.BLUE);
                                        JSONObject jsonRootObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonRootObject.getJSONArray("schedule");
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                        driver_name = jsonObject1.getString(Config.KEY_NAME);

                                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                                        navigationView.setNavigationItemSelectedListener(Home.this);
                                        View headerView = navigationView.getHeaderView(0);
                                        //View headerView= LayoutInflater.from(Home.this).inflate(R.layout.nav_header_home,null);
                                        textViewDriverName = (TextView) headerView.findViewById(R.id.textViewDriverName);
                                        textViewDriverEmail = (TextView) headerView.findViewById(R.id.textViewDriverEmail);
                                        textViewDriverName.setText(driver_name);
                                        textViewDriverEmail.setText(driver_email);
                                        int length1 = jsonArray.length();
                                        for (int i = 0; i < length1; i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            HashMap<String, String> temp = new HashMap<String, String>();
                                            bus_no = jsonObject.getString(Config.KEY_BUS);
                                            time = jsonObject.getString(Config.KEY_TIME);
                                            route = jsonObject.getString(Config.KEY_ROUTE);
                                            date = jsonObject.getString(Config.KEY_DATE);
                                            arrayList1.add(route);
                                            arrayList2.add(time);
                                            arrayList3.add(bus_no);
                                            arrayList4.add(date);
                                            //Toast.makeText(Home.this, arrayList1.get(i)+" "+arrayList2.get(i)+" "+arrayList3.get(i), Toast.LENGTH_LONG).show();
                                        }
                                        scheduleListViewAdapter = new ScheduleListViewAdapter(Home.this, arrayList1, arrayList2, arrayList3);
/*                                ArrayAdapter adapter = new ArrayAdapter<String>(Home.this,
                                        R.layout.schedule_list_values_activity,R.id.listTitle, mobileArray);*/

                                        listView.setAdapter(scheduleListViewAdapter);
                                    }
                                } catch (Exception e) {
                                    loading.dismiss();
                                    // Toast.makeText(Home.this, "Exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.dismiss();
                                //You can handle error here if you want
                                //Toast.makeText(Home.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {


                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        //Adding parameters to request
                        //SharedPreferences sharedPreferences = DriverLogin.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");
                        params.put(Config.KEY_EMAIL, driver_email);
                        //returning parameter
                        return params;
                    }
                };


                //Adding the string request to the queue
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            } else {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                        new Response.Listener<String>() {

                            ListView listView = (ListView) findViewById(R.id.scheduleList);

                            @Override
                            public void onResponse(String response) {
                                loading.dismiss();
                                //If we are getting success from server
                                try {
                                    if (response.equalsIgnoreCase("No")) {
                                        noDataTextView.setText("No Schedule Found For " + new_date + "..!");
                                        noDataTextView.setTextColor(Color.RED);
                                    } else {
                                        noDataTextView.setText("Routine For " + new_date + ":");
                                        noDataTextView.setTextColor(Color.BLUE);
                                        JSONObject jsonRootObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonRootObject.getJSONArray("schedule");
                                        int length1 = jsonArray.length();
                                        for (int i = 0; i < length1; i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            HashMap<String, String> temp = new HashMap<String, String>();
                                            bus_no = jsonObject.getString(Config.KEY_BUS);
                                            time = jsonObject.getString(Config.KEY_TIME);
                                            route = jsonObject.getString(Config.KEY_ROUTE);
                                            date = jsonObject.getString(Config.KEY_DATE);
                                            arrayList1.add(route);
                                            arrayList2.add(time);
                                            arrayList3.add(bus_no);
                                            arrayList4.add(date);
                                            //Toast.makeText(Home.this, arrayList1.get(i)+" "+arrayList2.get(i)+" "+arrayList3.get(i), Toast.LENGTH_LONG).show();
                                        }
                                        scheduleListViewAdapter = new ScheduleListViewAdapter(Home.this, arrayList1, arrayList2, arrayList3);
/*                                ArrayAdapter adapter = new ArrayAdapter<String>(Home.this,
                                        R.layout.schedule_list_values_activity,R.id.listTitle, mobileArray);*/

                                        listView.setAdapter(scheduleListViewAdapter);
                                    }
                                } catch (Exception e) {
                                    loading.dismiss();
                                    noDataTextView.setText("No Routine Found For " + new_date + "..!");
                                    noDataTextView.setTextColor(Color.RED);
                                    //noDataTextView.setGravity(100);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.dismiss();
                                //You can handle error here if you want
                                //Toast.makeText(Home.this,error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }) {


                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        //Adding parameters to request
                        //SharedPreferences sharedPreferences = DriverLogin.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "Not Available");

                        params.put(Config.KEY_EMAIL, driver_email);
                        params.put("date", new_date);
                        //returning parameter
                        return params;
                    }
                };


                //Adding the string request to the queue
                RequestQueue requestQueue = Volley.newRequestQueue(Home.this.getApplicationContext());
                requestQueue.add(stringRequest);

//            scheduleListViewAdapter.notifyDataSetChanged();
            }
        }


    public void logout(MenuItem item) {
        logout();
    }
    public void getOtherSchedule(MenuItem item){
        //noinspection deprecation
        showDialog(DATE_PICKER_ID);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day+1);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            arrayList1.clear();
            arrayList2.clear();
            arrayList3.clear();
            listView.invalidateViews();
            currentDate=false;

           new_date=new StringBuilder().append(year)
                    .append("-").append(month + 1).append("-").append(day)
                    .toString();
            // Show selected date
            getScheduleData();
        }
    };

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You Have No Internet Connection");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return builder;
    }
}
