package com.example.tareq594.maidme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.gabrielsamojlo.keyboarddismisser.KeyboardDismisser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rw.loadingdialog.LoadingView;

import java.util.concurrent.TimeUnit;
import android.graphics.Color;
import android.widget.ScrollView;


public class MobilenumberActivity extends AppCompatActivity {

    private EditText mCodeText ;
    private EditText mMobileNumberText ;
    private Button mSendBtn ;

    private EditText mVerificationText;

    private int btntype = 0 ;


    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private FirebaseAuth mAuth;

    private String mVerificationId ;





    @SuppressLint("ResourceType")




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilenumber);

        KeyboardDismisser.useWith(this);


        ConstraintLayout frame = findViewById(R.id.framelayout);

        final LoadingView loadingView = new LoadingView.Builder(this)
                .setBackgroundColor(Color.TRANSPARENT)


                //.setCustomRetryLayoutResource(R.layout.custom_retry)
                .attachTo(frame);



        mCodeText = (EditText) findViewById(R.id.PostalCodeTextField);
        mMobileNumberText = (EditText) findViewById(R.id.mobileNumberTextField3);
        mSendBtn = (Button) findViewById(R.id.sendVerificationButton);
        mAuth = FirebaseAuth.getInstance();
        mVerificationText = (EditText) findViewById(R.id.VerificationCode) ;

        mVerificationText.setVisibility(View.GONE);





        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btntype == 0) {

                    // we want to disable the textfields
                    mCodeText.setEnabled(false);
                    mMobileNumberText.setEnabled(false);
                    mSendBtn.setEnabled(false);
                    loadingView.show();


                    String Phonenumber = mCodeText.getText().toString() + mMobileNumberText.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            Phonenumber,
                            60,
                            TimeUnit.SECONDS,
                            MobilenumberActivity.this,
                            mCallbacks
                    );

                }

                else {
                    String code = mVerificationText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);



            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

// This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("failed", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...

            }


            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("codesent", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later


                btntype = 1 ;
                loadingView.hide();

                mSendBtn.setEnabled(true);
                mVerificationText.setVisibility(View.VISIBLE);
                mCodeText.setVisibility(View.GONE);
                mMobileNumberText.setVisibility(View.GONE);
                mSendBtn.setText("Verify number");




                mVerificationId = verificationId;
               PhoneAuthProvider.ForceResendingToken mResendToken = token;

          //     String code = mVerificationText.getText().toString();
          //      PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            //    signInWithPhoneAuthCredential(credential);


                // we forward to the verification Activity and passed the verification ID
             //  Intent intent = new Intent(MobilenumberActivity.this , VerificationCodeActivity.class);vd
             //  intent.putExtra("VerificationID" , mVerificationId);
             //  intent.putExtra("ResendToken" , mResendToken);
             //  MobilenumberActivity.this.startActivity(intent);



                // ...
            }
        };





        Log.d("tag1", "onCreate: heyy");
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signin", "signInWithCredential:success");

                            if (mAuth.getCurrentUser().getDisplayName() == "oldmobileuser") {

                                Intent homeintent = new Intent(MobilenumberActivity.this , HomeActivity.class);
                                startActivity(homeintent);
                                finish();

                            }



                          else {
                                Intent homeintent = new Intent(MobilenumberActivity.this, SignUpActivity.class);
                                startActivity(homeintent);
                                finish();
                            }

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // hoon bedde a3mal alert aw toast 7atta y6ale3 enno fe error
                            // ma tensa n5ale al ashia2 enabled kman
                            // Sign in failed, display a message and update the UI
                            Log.w("tag2", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }




}
