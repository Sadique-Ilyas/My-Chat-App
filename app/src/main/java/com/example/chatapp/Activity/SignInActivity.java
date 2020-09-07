package com.example.chatapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class SignInActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button loginBtn;
    private TextView forgotPasswordTxt, signUpTxt;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initializing Views
        editTextEmail = findViewById(R.id.edit_text_signIn_email);
        editTextPassword = findViewById(R.id.edit_text_signIn_password);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPasswordTxt = findViewById(R.id.forgotPasswordTxt);
        signUpTxt = findViewById(R.id.signUpTxt);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Login Button On Click Event
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() && password.isEmpty()) {
                    editTextEmail.setError("Email Id Required !");
                    editTextPassword.setError("Password Required !");
                    editTextEmail.requestFocus();
                } else if (email.isEmpty()) {
                    editTextEmail.setError("Email Id Required !");
                    editTextEmail.requestFocus();
                } else if (password.isEmpty()) {
                    editTextPassword.setError("Password Required !");
                    editTextPassword.requestFocus();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                            Toast.makeText(SignInActivity.this, "Logged In !", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Snackbar.make(findViewById(R.id.signInActivity), "Please verify your Email Address", Snackbar.LENGTH_LONG)
                                                    .setAction("Resend Email", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (firebaseUser != null) {
                                                                firebaseUser.reload();
                                                                if (!firebaseUser.isEmailVerified()) {
                                                                    firebaseUser.sendEmailVerification();
                                                                    Toast.makeText(SignInActivity.this, "Email Sent ! Check your Mail Inbox", Toast.LENGTH_LONG).show();
                                                                } else {
                                                                    Toast.makeText(SignInActivity.this, "Your email has been verified ! You can login now.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        }
                                                    }).show();
                                        }
                                    } else {
                                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(SignInActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Convert a String to Spannable String (Elevated Text like Button)
        String signUpText = "Don't have an Account ? Sign Up here";
        SpannableString ss1 = new SpannableString(signUpText);
        StyleSpan bold_italic = new StyleSpan(Typeface.BOLD_ITALIC);
        UnderlineSpan underline = new UnderlineSpan();
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        };

        ss1.setSpan(clickableSpan1, 24, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(bold_italic, 24, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(underline, 24, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(new ForegroundColorSpan(Color.CYAN), 24, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signUpTxt.setText(ss1);
        signUpTxt.setMovementMethod(LinkMovementMethod.getInstance());

        String forgotText = "Forgot Password ???";
        SpannableString ss = new SpannableString(forgotText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
                finish();
            }
        };

        ss.setSpan(clickableSpan, 0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotPasswordTxt.setText(ss);
        forgotPasswordTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void myFingerprintAuth() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
//                Toast.makeText(this, "App can authenticate using biometrics.", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                Toast.makeText(this, "No Fingerprint Scanner available on this device.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                Toast.makeText(this, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                Toast.makeText(this, "The user hasn't associated any Fingerprint with their account.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
//                Toast.makeText(SignInActivity.this, "Authentication Error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
//                Toast.makeText(SignInActivity.this, "Authentication Failed !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(SignInActivity.this, "Authentication Succeeded !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Login using Fingerprint")
                .setDescription("Place your hand on the Fingerprint Scanner")
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            myFingerprintAuth();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            myFingerprintAuth();
        }
    }
}