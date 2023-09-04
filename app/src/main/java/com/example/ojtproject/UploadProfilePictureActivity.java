package com.example.ojtproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePictureActivity extends AppCompatActivity {

    private ProgressBar uploadProfilePictureActivityProgressBar;
    private ImageView uploadProfilePictureActivityImageViewDisplayPicture;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ReadWriteUserDetails user;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button uploadProfilePictureActivityButtonChoosePicture = findViewById(R.id.uploadProfilePictureActivityButtonChoosePicture);
        Button uploadProfilePictureActivityButtonUploadDisplayPicture = findViewById(R.id.uploadProfilePictureActivityButtonUploadDisplayPicture);
        uploadProfilePictureActivityProgressBar = findViewById(R.id.uploadProfilePictureActivityProgressBar);
        uploadProfilePictureActivityImageViewDisplayPicture = findViewById(R.id.uploadProfilePictureActivityImageViewDisplayPicture);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();

        //Set User's current DP in ImageView (if uploaded already). Use Picasso for imageViewer setImage
        //Regular URIs.
        Picasso.get().load(uri).into(uploadProfilePictureActivityImageViewDisplayPicture);

        //Choosing picture to upload
        uploadProfilePictureActivityButtonChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        //Upload Image
        uploadProfilePictureActivityButtonUploadDisplayPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePictureActivityProgressBar.setVisibility(View.VISIBLE);
                uploadPic();
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            uploadProfilePictureActivityImageViewDisplayPicture.setImageURI(uriImage);
        }
    }

    private void uploadPic() {
        if (uriImage != null) {
            //Save the image with uid of the currently logged in user
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "." + getFileExtension(uriImage));

            // Storing the image URL into real time database
            String fileReferenceString = String.valueOf(fileReference);
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Registered Users").child(firebaseUser.getUid()).child("imageUrl").get().addOnCompleteListener(fileReferenceObject -> {
                if (fileReferenceObject.isSuccessful()) {
                    if (fileReferenceObject.getResult().getValue() == null) {
                        databaseReference.child("Registered Users").child(firebaseUser.getUid()).child("imageUrl").setValue(fileReferenceString);
                    }
                }
            });

            //Upload image to Storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();

                            //Finally set the display image of the user after upload
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });

                    uploadProfilePictureActivityProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePictureActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePictureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            uploadProfilePictureActivityProgressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePictureActivity.this, "No File Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    //Obtain File Extension of the image
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.commonMenuItemContactAdmin:
                openEmailAppChooser();
                break;
            case R.id.commonMenuItemLogout:
                authProfile = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);//So when phone is opened, user will be redirected to the MainFragment instead of HomePageActivity
                editor.apply();
                Intent intent = new Intent(UploadProfilePictureActivity.this, MainActivity.class);

                //Don't let user go back to HomePageActivity or any activities related opened after logout button was tapped if back button of phone is tapped
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                authProfile.signOut();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openEmailAppChooser() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"borjy46@gmail.com"}); // Replace with admin's email address
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Admin");

        // Check if there's an app that can handle the email intent
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
        } else {
            // If no email app is available, open browser to Gmail website
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/"));
            startActivity(browserIntent);
        }
    }

    public void onBackPressed() {
        String openedFrom = getIntent().getStringExtra("openedFrom");
        if ("UserUpdateProfileActivity".equals(openedFrom)) {
            Intent intent = new Intent(UploadProfilePictureActivity.this, UserUpdateProfileActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(UploadProfilePictureActivity.this, HomeActivity.class);
            intent.putExtra("openedFrom", "UserProfileFragment");
            startActivity(intent);
        }
    }
}