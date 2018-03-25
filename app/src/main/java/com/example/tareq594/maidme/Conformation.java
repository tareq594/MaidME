package com.example.tareq594.maidme;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Conformation extends AppCompatActivity {

    Button ConfirmationButton,CancelButton;
    TextView Datelabel, StartTimeLabel,addressLabel,PackageLabel,CleaningMAterialsLabel,TotalPrice;
    private FirebaseUser user;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conformation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Datelabel = (TextView) findViewById(R.id.DateLabel);
        StartTimeLabel = (TextView) findViewById(R.id.StartTimeLabel);
        addressLabel =(TextView) findViewById(R.id.AddressLabel);
        PackageLabel =(TextView) findViewById(R.id.PackageLabel);
        CleaningMAterialsLabel =(TextView) findViewById(R.id.CleaningMaterialsLabel);
        TotalPrice = (TextView) findViewById(R.id.TotalPriceLabel);


        ConfirmationButton = (Button) findViewById(R.id.ConfirmButton);
        CancelButton = (Button) findViewById(R.id.CancelButton);
        String passDate,passTime,passPack,passCleaning = null;
        double mylat;
        double mylong;
        Bundle bundle = getIntent().getExtras();
         if (bundle!= null){
             passDate = bundle.getString("passDate");
             passTime = bundle.getString("passTime");
             passPack = bundle.getString("passPack");
             mylat = bundle.getDouble("passLat");
             mylong = bundle.getDouble("passLong");
             passCleaning = bundle.getString("passCleaning");
                String MapLink  = "https://www.google.com/maps/search/?api=1&query="+
                        String.valueOf(mylat) +"," +String.valueOf(mylong) ;

             Datelabel.setText(passDate);
             StartTimeLabel.setText(passTime);
            // addressLabel.setText();
             PackageLabel.setText(passPack);
             CleaningMAterialsLabel.setText(passCleaning);
         }





        ConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("root").child("Users").child(user.getUid());



            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


}
}