package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail;
    TextInputEditText loginPassword;
    Button loginBtn;
    TextView registerLink;
    ImageButton facebook, google, twitter;
    private FirebaseAuth mAuth;
    LocalStorage localStorage;
    private ProgressDialog progressDialog;
    private CheckBox chkBoxRememberMe;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localStorage = new LocalStorage(LoginActivity.this);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_pass);
        loginBtn = findViewById(R.id.login_btn);
        facebook = findViewById(R.id.facebook);
        google = findViewById(R.id.google);
        twitter = findViewById(R.id.twitter);
        twitter = findViewById(R.id.twitter);
        registerLink = findViewById(R.id.register_link);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoginDetails();
            }
        });

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_checkbox);
        Paper.init(this);

        sharedPreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        int j = sharedPreferences.getInt("Key", 0);

        if (j > 0) {
            Intent activity = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(activity);
        }

        String UserEmail = Paper.book().read(Prevalent.UserEmail);
        String UserPassword = Paper.book().read(Prevalent.UserPassword);
        if (UserEmail != "" && UserPassword != "") {
            if (!TextUtils.isEmpty(UserEmail) && !TextUtils.isEmpty(UserPassword)) {
                loginEmail.setText(UserEmail);
                AllowAccess(UserEmail, UserPassword);

                progressDialog.setTitle("Already logged in");
                progressDialog.setMessage("Please wait....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

            }
        }
    }

    private void AllowAccess(final String UserEmail, final String UserPassword) {
        FirebaseApp.initializeApp(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(UserEmail, UserPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            localStorage.setUid(user.getUid());

                            if (chkBoxRememberMe.isChecked()) {
                                Paper.book().write(Prevalent.UserEmail, UserPassword);
                                Paper.book().write(Prevalent.UserPassword, UserPassword);

                            }

                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            Intent intent = new Intent(LoginActivity.this, Recommendations.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkLoginDetails() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Input your email address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Input your password", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setTitle("Login Account");
            progressDialog.setMessage("Please wait, while we log you in");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            proceedtoLogin(email, password);
        }
    }

    private void proceedtoLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            localStorage.setUid(user.getUid());
                            assert user != null;
                            if (user.isEmailVerified()) {

                                if (chkBoxRememberMe.isChecked()) {
                                    Paper.book().write(Prevalent.UserEmail, email);
                                    Paper.book().write(Prevalent.UserPassword, password);

                                }

                                Intent intent = new Intent(LoginActivity.this, Recommendations.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email before signing in. Kindly chek your inbox or spam messages", Toast.LENGTH_LONG).show();
//                            updateUI(user);
                                progressDialog.dismiss();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
//                            updateUI(null);
                        }
                    }
                });
    }
}