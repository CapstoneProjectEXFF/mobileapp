package com.project.capstone.exchangesystem.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FirebaseImg {
    ProgressDialog progressDialog;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseUser fbUser = fbAuth.getCurrentUser();

    PostAction postAction = new PostAction();


    public FirebaseImg() {
    }

    public void uploadImagesToFireBase(final Context context, final List<Uri> selectedImages, final Item item, final DonationPost donationPost, final User user, final String authorization, final int action, final SharedPreferences.Editor editor) {
        final List<String> urlList = new ArrayList<>();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Đang tải hình ảnh...");
        progressDialog.show();

        for (int i = 0; i < selectedImages.size(); i++) {

            if (selectedImages.get(i) != null) {
                StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());
                reference.putFile(selectedImages.get(i)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlList.add(uri.toString());
                                    if (urlList.size() == selectedImages.size()) {
                                        progressDialog.dismiss();
                                        if (item != null){
                                            postAction.manageItem(item, urlList, authorization, context, action);
                                        } else if (donationPost != null) {
                                            postAction.manageDonation(donationPost, urlList, authorization, context, action);
                                        } else if (user != null) {
                                            postAction.manageUser(user, urlList.get(0), authorization, context, action, editor);
                                        }
                                    }

                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setMessage("Vui lòng chờ!!!");
                    }
                });
            }
        }
    }

    public boolean checkLoginFirebase() {
        final boolean[] result = {true};
        if (fbUser == null) {
            fbAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
//                    Toast.makeText(CreateDonationPostActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    result[0] = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(CreateDonationPostActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    result[0] = false;
                }
            });
        }
        return result[0];
    }

    public void deleteImageOnFirebase(String url){
        StorageReference reference = firebaseStorage.getReferenceFromUrl(url);
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("deleteURLFirebase", "delete url on Firebase successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("deleteURLFirebase", "delete url on Firebase failed");
            }
        });
    }
}
