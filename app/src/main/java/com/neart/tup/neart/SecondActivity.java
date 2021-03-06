package com.neart.tup.neart;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class SecondActivity extends AppCompatActivity {

    private Button mSelectBtn;

    private Button mPauseBtn;
    private Button mCancelBtn;

    private final static int FILE_SELECTOR_CODE =1;

    private StorageReference mStorageRef;
    private ProgressBar mProgress;

    private TextView mSizeLabel;
    private TextView mProgressLabel;

    private TextView mfilenamelabel;

    private StorageTask mStorageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mSelectBtn = (Button) findViewById(R.id.select_btn);
        mPauseBtn = (Button) findViewById(R.id.pause_btn);
        mCancelBtn = (Button) findViewById(R.id.cancel_btn);

        mfilenamelabel = (TextView) findViewById(R.id.filename_label);
        mProgress =  (ProgressBar) findViewById(R.id.upload_progress);

        mSizeLabel = (TextView) findViewById(R.id.size_label);
        mProgressLabel = (TextView) findViewById(R.id.progress_label);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileSelector ();
            }
        });


        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String btnText = mPauseBtn.getText().toString();

                if (btnText.equals("Pause Upload")) {

                    mStorageTask.pause();
                    mPauseBtn.setText("Resume Upload");

                } else {

                    mStorageTask.resume();
                    mPauseBtn.setText("Pause Upload");
                }

            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                mStorageTask.cancel();
            }
        });
    }

    private void openFileSelector() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECTOR_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File manager.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){

        if (requestCode == FILE_SELECTOR_CODE && resultCode == RESULT_OK) {

            Uri fileUri = data.getData();

            String uriString = fileUri.toString();

            File myFile =  new File (uriString);
            //String path myFile.getAbsolutePath();

            String displayName = null;

            if (uriString.startsWith("content://")){
                Cursor cursor =  null;
                try {
                    cursor = SecondActivity.this.getContentResolver().query(fileUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName =  cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }

            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            //Progress Bar
            mfilenamelabel.setText(displayName);


            StorageReference riversRef = mStorageRef.child("files/" +displayName);

            mStorageTask = riversRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            Toast.makeText(SecondActivity.this, "File Uploaded!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(SecondActivity.this, "There was an error in uploading file.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                            mProgress.setProgress((int) progress);

                            String progressText = taskSnapshot.getBytesTransferred()/(1024 * 1024) + " / " + taskSnapshot.getTotalByteCount() / (1024 * 1024) + "mb";
                            mSizeLabel.setText(progressText);
                            mProgressLabel.setText( (int) progress + "%");

                        }

                    });


        }

        super.onActivityResult(requestCode,resultCode,data);
    }
}
