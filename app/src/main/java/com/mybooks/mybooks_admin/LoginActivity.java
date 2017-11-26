package com.mybooks.mybooks_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextView mUsername;
    private TextView mPassword;
    private TextView mSign_inBtn;
    private TextView serial_no;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (TextView) findViewById(R.id.username_sign_in);
        mPassword = (TextView) findViewById(R.id.password_sign_in);
        mSign_inBtn = (TextView) findViewById(R.id.signBtn);

        serial_no = (TextView) findViewById(R.id.serial_no);
        serial_no.setText("Serial No: " + Build.SERIAL);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        //progressDialog.show();

        mSign_inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername.setError(null);
                mPassword.setError(null);

                if (TextUtils.isEmpty(mUsername.getText().toString())) {
                    mUsername.setError("This field is required");
                    return;
                } else if (TextUtils.isEmpty(mPassword.getText().toString())) {
                    mPassword.setError("This field is required");
                    return;
                }
                signIn(mUsername.getText().toString(), mPassword.getText().toString());
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    verifyDevice(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                } else {
                    progressDialog.dismiss();
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signIn(String username, final String password) {

        progressDialog.setMessage("Singing in...");
        progressDialog.show();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    verifyDevice(mUsername.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void verifyDevice(String username) {
        progressDialog.setMessage("Verifying device...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Admin")
                .child(username.replace(".", "*"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dataBasekey = String.valueOf(dataSnapshot.child("key").getValue());

                //Toast.makeText(getApplicationContext(), "dataBasekey: "+ dataBasekey + "\nBuild.SERIAL: " + Build.SERIAL, Toast.LENGTH_SHORT).show();

                if (Build.SERIAL.equals(dataBasekey)) {
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Unauthorized device", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
