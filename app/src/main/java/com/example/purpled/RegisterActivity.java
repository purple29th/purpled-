package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Button registerBtn;
    EditText email, password, confirmpassword, username;
    TextView loginLink;
    private FirebaseAuth mAuth;
    LocalStorage localStorage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn =  findViewById(R.id.create_acct_btn);
        email = findViewById(R.id.register_email);
        password =  findViewById(R.id.register_pass);
        username =  findViewById(R.id.register_username);
        confirmpassword = findViewById(R.id.register_confirm_pass);
        loginLink =  findViewById(R.id.login_link);

        localStorage= new LocalStorage(RegisterActivity.this);
        progressDialog = new ProgressDialog( this );

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startregistration();
            }
        });

    }

    private void startregistration() {
        String regusername = username.getText().toString();
        String regemail = email.getText().toString();
        String regpassword = password.getText().toString();
        String regconfirmpassword = confirmpassword.getText().toString();

        if(TextUtils.isEmpty(regusername)){
            Toast.makeText(this, "You must enter a user name", Toast.LENGTH_SHORT).show();
        }else

        if(TextUtils.isEmpty(regemail)){
            Toast.makeText(this, "You must enter you email", Toast.LENGTH_SHORT).show();
        }else

        if(TextUtils.isEmpty(regpassword)){
            Toast.makeText(this, "You must crate a password", Toast.LENGTH_SHORT).show();
        }else

        if(!(regpassword.equals(regconfirmpassword))){
            Toast.makeText(this, "Your password does not match. Please confirm your password", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setTitle( "Creating your account" );
            progressDialog.setMessage( "Please wait...." );
            progressDialog.setCanceledOnTouchOutside( false );
            progressDialog.show();

            createAccount(regusername, regemail, regconfirmpassword);
        }
    }

    private void createAccount(String regusername, String regemail, String regconfirmpassword) {
        FirebaseApp.initializeApp(RegisterActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(regemail, regconfirmpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(TAG, "createUserWithEmail:success");
                    Toast.makeText(RegisterActivity.this, "Authentication successful.",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    localStorage.setUid(user.getUid());


                    // [send_email_verification]
                    assert user != null;
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterActivity.this, "We've sent a verification email to you, Kindly verify your email",
                                    Toast.LENGTH_LONG).show();

                        }
                    });
                    // [END send_email_verification]

                    saveotherUserDatas(regemail, regusername);
//                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
//                    updateUI(null);
                }
            }
        });
    }

    private void saveotherUserDatas(String regemail, String regusername) {
        CollectionReference ref = FirebaseFirestore.getInstance().collection("Users");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd:MM:yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", regemail);
        userMap.put("username", regusername);
        userMap.put("uid", localStorage.getUid());
        userMap.put("CreatedDate", saveCurrentDate);
        userMap.put("CreatedTime", saveCurrentTime);




        ref.document(localStorage.getUid()).set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.child("users").child(localStorage.getUid()).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });



    }
}