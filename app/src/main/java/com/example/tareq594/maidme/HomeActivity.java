package com.example.tareq594.maidme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener , NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "" ;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;
    private ImageButton mCallendar;
    private TextView mStartTime;
    private Spinner mPackageSpinner;
    private Switch mMaterialSwitch;
    private Button mMaidmeBtn;
    Calendar myCalendar = Calendar.getInstance();
    private String DateString;
    private String StartTimeString ;
    ArrayList<String> packages = new ArrayList<String>();
    private Button mSign_out ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                updateDate();
            }

        };






        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);

        final DatePickerDialog dialog = new DatePickerDialog(this, date, c.get(c.YEAR), c.get(c.MONTH), c.get(c.DAY_OF_MONTH));
        long now = System.currentTimeMillis() - 1000L;
        dialog.getDatePicker().setMinDate(now + (1000*60*60*24*1L));
        dialog.getDatePicker().setMaxDate(now + (1000*60*60*24*30L));


        mCallendar = (ImageButton) findViewById(R.id.Calendarbutton);
        mStartTime = (TextView) findViewById(R.id.StarttimeLabel);
        mPackageSpinner = (Spinner) findViewById(R.id.PackageSpinner);
        mMaterialSwitch = (Switch) findViewById(R.id.MaterialSwitch);
        mMaidmeBtn = (Button) findViewById(R.id.MaidMeBtn);
        mSign_out = (Button) findViewById(R.id.Signout);





        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference ref = database.getReference("root").child("PackagesEnglish");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

                List<String> values = dataSnapshot.getValue(t);
                Log.d("values", String.valueOf(values));

                for (String value : values) {
                    // hoon bedde a3mal array ll packages spinner 7atta adeefo ba3deen
                    packages.add(value);
                }


                setpackagesSpiner();





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // here i want to retrive data for packages spinner









        final Calendar mCalendar = Calendar.getInstance();


        final TimePickerDialog.OnTimeSetListener starttimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a");

                // I want to add if statment to see if the time within range if not / then show an alert and open the dialog again
                // from 8:00 to 20:00

                Log.d("hour", String.valueOf(hourOfDay));
                Log.d("hour", String.valueOf(minute));


                mStartTime.setText(mSDF.format(mCalendar.getTime()));
                StartTimeString = mSDF.format(mCalendar.getTime());
            }
        };






        final TimePickerDialog StartTimedialog = new TimePickerDialog(this,starttimeListener,8,0,false);

        TimePicker newtimepicker = (TimePicker) dialog.findViewById(R.id.timePickertest);






        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTimedialog.show();
            }
        });

        mSign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });

        mCallendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 dialog.show();
            }
        });


        mMaidmeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                          Intent authintent = new Intent(HomeActivity.this, LocationActivity.class);
                String passDate,passTime,passPack,passCleaning = null;
                passDate = DateString;
                passTime = StartTimeString;
                passPack = mPackageSpinner.getSelectedItem().toString();
                passCleaning = String.valueOf(mMaterialSwitch.isChecked());
                authintent.putExtra("passDate",passDate);
                authintent.putExtra("passTime",passTime);
                authintent.putExtra("passPack",passPack);
                authintent.putExtra("passCleaning",passCleaning);
                          startActivity(authintent);

            }
        });




    }

    private void setpackagesSpiner() {
        ArrayAdapter<String> Packagesadapter = new ArrayAdapter<String>(this , android.R.layout.simple_spinner_item , packages);


        // Specify the layout to use when the list of choices appears
        Packagesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner

        mPackageSpinner.setAdapter(Packagesadapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();



             if (currentUser == null ) {



                 Intent authintent = new Intent(HomeActivity.this, MobilenumberActivity.class);
                  startActivity(authintent);
                  finish();
              }

              else {
                 Log.d("why", "why not this: ");
                 Log.d(TAG, String.valueOf(currentUser));
                 Log.d(TAG, currentUser.getDisplayName());

                 if (!"oldmobileuser".equals(currentUser.getDisplayName())) {
                   // Log.d("test", currentUser.getDisplayName());
                    Intent authintent = new Intent(HomeActivity.this, MobilenumberActivity.class);
                    startActivity(authintent);
                    finish();

                }

             }


    }

    private void updateDate() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        DateString = sdf.format(myCalendar.getTime());

    }


    @Override
    public void onTimeChanged(TimePicker timePicker, int HourOfDay, int MinOfHour) {
        if (HourOfDay > 20 && HourOfDay < 8  ){
            Log.d(TAG, "Time is invalid ");
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected: ");

        if (id == R.id.nav_SignOut) {
            mAuth.signOut();

        }

       // DrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}