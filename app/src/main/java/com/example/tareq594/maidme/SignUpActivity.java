package com.example.tareq594.maidme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity implements  com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {


    private Spinner Genderspinner ;
    private Spinner Relationsspinner;
    private Button Signupbutton;
    private FirebaseUser user;
    private EditText FirstName;
    private EditText LastName;


    EditText mBirthdate ;
    SimpleDateFormat simpleDateFormat;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBirthdate = (EditText) findViewById(R.id.Birthdate);
        Signupbutton = (Button) findViewById(R.id.SignUp);
        FirstName = (EditText) findViewById(R.id.FirstName);
        LastName = (EditText) findViewById(R.id.LastName);



        Signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("root").child("Users").child(user.getUid());

                ref.child("First Name").setValue(FirstName.getText().toString());
                ref.child("Last Name").setValue(FirstName.getText().toString());
                ref.child("Gender").setValue(Genderspinner.getSelectedItem().toString());
                ref.child("Relation status").setValue(Relationsspinner.getSelectedItem().toString());
                ref.child("Age").setValue(mBirthdate.getText().toString());


                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName("oldmobileuser")
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("profileUpdated", "User profile updated.");
                                    Intent authintent = new Intent(SignUpActivity.this, HomeActivity.class);
                                    startActivity(authintent);
                                    finish();

                                }
                            }
                        });


            }
        });


        simpleDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);








        mBirthdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDate(1980, 0, 1, R.style.DatePickerSpinner);


            }
        });



        Genderspinner = (Spinner) findViewById(R.id.spinner2);
        Relationsspinner = (Spinner) findViewById(R.id.spinner3);

        String[] Gender = {"Male","Female"};
        String[] Relations = {"Married" , "Singe" , "Other"};



        ArrayAdapter<String> Genderadapter = new ArrayAdapter<String>(this , android.R.layout.simple_spinner_item , Gender);


        // Specify the layout to use when the list of choices appears
        Genderadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner

        Genderspinner.setAdapter(Genderadapter);


        ArrayAdapter<String> Relationadapter = new ArrayAdapter<String>(this , android.R.layout.simple_spinner_item , Relations);


        // Specify the layout to use when the list of choices appears
        Relationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner

        Relationsspinner.setAdapter(Relationadapter);


        //  String text = spinner.getSelectedItem().toString();






    }





    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(SignUpActivity.this)
                .callback(SignUpActivity.this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(1994, 4, 10)
                .maxDate(2017, 0, 1)
                .minDate(1930, 0, 1)
                .build()
                .show();
    }

    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        mBirthdate.setText(simpleDateFormat.format(calendar.getTime()));

        Log.d("tets", simpleDateFormat.format(calendar.getTime()));


    }
}

