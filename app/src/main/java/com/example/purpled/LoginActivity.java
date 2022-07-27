package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;

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
    private static final int RC_SIGN_IN = 123;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            progressDialog.setTitle("Already logged in with google");
            progressDialog.setMessage("Please wait....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            localStorage.setUid(currentUser.getUid());

            logmein();
        }
    }

    private void logmein() {
        Intent intent = new Intent(LoginActivity.this, Recommendations.class);
        startActivity(intent);
        progressDialog.dismiss();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localStorage = new LocalStorage(LoginActivity.this);

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(getApplication());
//
//
//        callbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        // App code
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//                });
        
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_pass);
        loginBtn = findViewById(R.id.login_btn);
//        facebook = findViewById(R.id.facebook);
//        google = findViewById(R.id.google);
//        twitter = findViewById(R.id.twitter);
//        twitter = findViewById(R.id.twitter);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

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
        createRequest();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignin();
            }
        });

//        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setReadPermissions(Arrays.asList(EMAIL));
//        // If you are using in a fragment, call loginButton.setFragment(this);
//
//        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//            }
//        });

    }

    private void createRequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    //...Google API login starts here

    private void googleSignin() {
        Intent googleintent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleintent, RC_SIGN_IN);
         }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == RC_SIGN_IN){
           Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           try{
               GoogleSignInAccount account = task.getResult(ApiException.class);
               firebaseAuthWithGoogle(account);

           } catch (ApiException e) {
               Toast.makeText(this, " not good"+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
           }
       }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            localStorage.setUid(user.getUid());
                            progressDialog.setTitle("Already logged in with google");
                            progressDialog.setMessage("Please wait....");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            if (user != null) {
                                Intent intent = new Intent(LoginActivity.this, Recommendations.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Sorry, Auth failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //...Google Api login ends here...

//    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }

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
                                Toast.makeText(LoginActivity.this, "Please verify your email before signing in. Kindly check your inbox or spam messages", Toast.LENGTH_LONG).show();
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