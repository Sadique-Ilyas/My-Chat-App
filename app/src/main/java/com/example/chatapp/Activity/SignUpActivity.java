package com.example.chatapp.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView signUpImageView, arrow;
    private TextView chooseImageTxt;
    private EditText editTextName, editTextEmail, editTextPassword, editTextPhoneNumber;
    private Button createAccountBtn;
    private TextView signInText, signUpNameTxt, signUpEmailTxt, signUpPhoneNumberTxt;
    private Uri signUpImageUri;
    private String onlineStatus = "Offline";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userReference = database.getReference("Users");
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        // Initializing Views
        signUpImageView = findViewById(R.id.signUp_image_view);
        arrow = findViewById(R.id.arrow);
        YoYo.with(Techniques.SlideInRight).duration(2000).repeat(100).playOn(arrow);
        chooseImageTxt = findViewById(R.id.txt_choose_image);
        signUpNameTxt = findViewById(R.id.signUp_name_txt);
        signUpEmailTxt = findViewById(R.id.signUp_email_txt);
        signUpPhoneNumberTxt = findViewById(R.id.signUp_phoneNumber_txt);
        editTextName = findViewById(R.id.edit_text_signUp_name);
        editTextEmail = findViewById(R.id.edit_text_signUp_email);
        editTextPhoneNumber = findViewById(R.id.edit_text_signUp_phoneNumber);
        editTextPassword = findViewById(R.id.edit_text_signUp_password);
        createAccountBtn = findViewById(R.id.loginBtn);
        signInText = findViewById(R.id.signInTxt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Creating Account...\nThis may take few seconds");

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Users");

        // Floating Action Button On Click Event
        signUpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Create Account Button On Click Event
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                final String name = editTextName.getText().toString().trim();
                final String email = editTextEmail.getText().toString().trim();
                final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
                    editTextName.setError("Name Required !");
                    editTextEmail.setError("Email Required !");
                    editTextPassword.setError("Password Required !");
                    editTextName.requestFocus();
                } else if (name.isEmpty()) {
                    editTextName.setError("Name Required !");
                    editTextName.requestFocus();
                } else if (email.isEmpty()) {
                    editTextEmail.setError("Email Required !");
                    editTextEmail.requestFocus();
                } else if (phoneNumber.isEmpty()) {
                    editTextPhoneNumber.setError("Phone Number Required !");
                    editTextPhoneNumber.requestFocus();
                } else if (password.isEmpty()) {
                    editTextPassword.setError("Password Required !");
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Account Created !", Toast.LENGTH_SHORT).show();
                                        emailVerification();
                                        uploadProfile(name, email, phoneNumber);
                                    } else {
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Set the value entered in edit text to text view by updating the text every 100 millisecond
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                signUpNameTxt.setText(editTextName.getText());
                                signUpEmailTxt.setText(editTextEmail.getText());
                                signUpPhoneNumberTxt.setText(editTextPhoneNumber.getText());
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        thread.start();

        // Convert a String to Spannable String (Elevated Text like Button)
        String signInTxt = "Already have an Account ? Login here";
        SpannableString ss1 = new SpannableString(signInTxt);
        StyleSpan bold_italic = new StyleSpan(Typeface.BOLD_ITALIC);
        UnderlineSpan underline = new UnderlineSpan();
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        };

        ss1.setSpan(clickableSpan1, 26, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(bold_italic, 26, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss1.setSpan(underline, 26, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signInText.setText(ss1);
        signInText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void emailVerification() {
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Verification Email Sent !!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Upload User Profile (name, email, phone number, image)
    private void uploadProfile(final String name, final String email, final String phoneNumber) {
        if (signUpImageUri != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd _ HH:mm:ss");
            String currentDateTime = simpleDateFormat.format(new Date());
            StorageReference fileReference = storageReference.child(firebaseAuth.getCurrentUser().getUid())
                    .child("IMG_" + currentDateTime + "." + getFileExtension(signUpImageUri));
            fileReference.putFile(signUpImageUri)
                    .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SignUpActivity.this, "Profile Pic Saved", Toast.LENGTH_SHORT).show();

                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete()) ;
                            Uri url = uri.getResult();
                            User user = new User(name, email, phoneNumber, url.toString(), onlineStatus, name.toLowerCase(), "Seen");
                            // database
                            userReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(user)
                                    .addOnSuccessListener(SignUpActivity.this, new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Profile Info Saved !", Toast.LENGTH_SHORT).show();
                                            emailVerificationDialog();
                                        }
                                    })
                                    .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void emailVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verify Email")
                .setMessage("An Email is sent to your mail account. Please verify it's you, to login")
                .setCancelable(false)
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Opens file chooser to select profile photo
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Checks if request Code and result code is valid then puts the image on image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            signUpImageUri = data.getData();
            Picasso.get().load(signUpImageUri).into(signUpImageView);
            chooseImageTxt.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        }
    }

    // Returns the file extension of a file
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}