package com.company.RasCCTV;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.util.IOUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.*;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//todo: 파일 다운로드 기능
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.util.IOUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;


import com.amazonaws.services.s3.model.Bucket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
// -------------



public class MainActivity extends AppCompatActivity implements  View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final ArrayList<String> objectLIST = new ArrayList<String>();

//    S3관련-----------------------------------------------------------
    private final String KEY = " ";
    private final String SECRET = "";

    private AmazonS3Client s3Client;
    private BasicAWSCredentials credentials;

    //track Choosing Image Intent
    private static final int CHOOSING_IMAGE_REQUEST = 1234;

    private TextView tvFileName;
    private ImageView imageView;
    private EditText edtFileName;

    private Uri fileUri;
    private Bitmap bitmap;
//    S3---------------------------------------------------------------------

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if (i == R.id.btn_choose_file) {
            showChoosingFile();
        } else if (i == R.id.btn_upload) {
            uploadFile();
        } else if (i == R.id.btn_download) {
            downloadFile();
        }else if (i == R.id.btn_list) {
            getList();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//----------------------------------------------------------------------------------------------------S3


       // imageView = findViewById(R.id.img_file);
//        edtFileName = findViewById(R.id.edt_file_name);
        tvFileName = findViewById(R.id.tv_file_name);
        tvFileName.setText("");

        findViewById(R.id.btn_choose_file).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_list).setOnClickListener(this);



        credentials = new BasicAWSCredentials(KEY, SECRET);
        s3Client = new AmazonS3Client(credentials);

        try {
            // Add these lines to add the AWSCognitoAuthPlugin and AWSS3StoragePlugin plugins
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }


        getList();



//------------------------------------------------------------------------------------------------------S3
        //        Fcm 부분
        Intent fcm = new Intent(getApplicationContext(), MyFirebaseMessaging.class);
        startService(fcm);


        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        // Initialize the AWSMobileClient if not initialized
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                Log.i(TAG, "AWSMobileClient initialized. User State is " + userStateDetails.getUserState());
                uploadWithTransferUtility();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Initialization error.", e);
            }
        });

    }



private void getList(){


  //  ArrayList<String> objectLIST = new ArrayList<>();

    Log.i("MyAmplifyApp", "getList call " );
    Toast.makeText(this, "------------getList call-------------", Toast.LENGTH_LONG).show();
//    objectLIST.clear();
    Amplify.Storage.list("",
            result -> {
                objectLIST.clear();
                for (StorageItem item : result.getItems()) {
                    Log.i("MyAmplifyApp", "Item: " + item.getKey());
                    objectLIST.add(item.getKey());
                }
            },
            error -> Log.e("MyAmplifyApp", "List failure", error)
    );

    ListView listView = (ListView)findViewById(R.id.object_list_View);

    List<String> list = new ArrayList<>();

    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
    listView.setAdapter(adapter);

    list.addAll(objectLIST);

}


    //------------------------------------------------------------------------------------------------------------S3
    private void uploadFile() {

        File exampleFile = new File(getApplicationContext().getFilesDir(), "ExampleKey");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exampleFile));
            writer.append("Example file contents");
            writer.close();
        } catch (Exception exception) {
            Log.e("MyAmplifyApp", "Upload failed", exception);
        }

        Amplify.Storage.uploadFile(
                "Example--Key",
                exampleFile,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );

//        if (fileUri != null) {
//            final String fileName = edtFileName.getText().toString();
//
//            if (!validateInputFileName(fileName)) {
//                return;
//            }
//
//            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                    "/" + fileName);
//
//            createFile(getApplicationContext(), fileUri, file);
//
//            TransferUtility transferUtility =
//                    TransferUtility.builder()
//                            .context(getApplicationContext())
//                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                            .s3Client(s3Client)
//                            .build();
//
//            TransferObserver uploadObserver =
//                    transferUtility.upload("public/" + fileName + "." + getFileExtension(fileUri), file);
//
//            uploadObserver.setTransferListener(new TransferListener() {
//
//                @Override
//                public void onStateChanged(int id, TransferState state) {
//                    if (TransferState.COMPLETED == state) {
//                        Toast.makeText(getApplicationContext(), "Upload Completed!", Toast.LENGTH_SHORT).show();
//
//                        file.delete();
//                    } else if (TransferState.FAILED == state) {
//                        file.delete();
//                    }
//                }
//
//                @Override
//                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
//                    int percentDone = (int) percentDonef;
//
//                    tvFileName.setText("ID:" + id + "|bytesCurrent: " + bytesCurrent + "|bytesTotal: " + bytesTotal + "|" + percentDone + "%");
//                }
//
//                @Override
//                public void onError(int id, Exception ex) {
//                    ex.printStackTrace();
//                }
//
//            });
//        }
    }

    private void downloadFile() {
        Amplify.Storage.downloadFile(
                "ciao.txt",
                new File(getApplicationContext().getFilesDir() + "/download.txt"),
                result -> Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName()),
                error -> Log.e("MyAmplifyApp",  "Download Failure", error)
        );

//        if (fileUri != null) {
//
//            final String fileName = edtFileName.getText().toString();
//
//            if (!validateInputFileName(fileName)) {
//                return;
//            }
//
//            try {
//                final File localFile = File.createTempFile("images", getFileExtension(fileUri));
//
//                TransferUtility transferUtility =
//                        TransferUtility.builder()
//                                .context(getApplicationContext())
//                                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                                .s3Client(s3Client)
//                                .build();
//
//                TransferObserver downloadObserver =
//                        transferUtility.download("public/" + fileName + "." + getFileExtension(fileUri), localFile);
//
//                downloadObserver.setTransferListener(new TransferListener() {
//
//                    @Override
//                    public void onStateChanged(int id, TransferState state) {
//                        if (TransferState.COMPLETED == state) {
//                            Toast.makeText(getApplicationContext(), "Download Completed!", Toast.LENGTH_SHORT).show();
//
//                            tvFileName.setText(fileName + "." + getFileExtension(fileUri));
//                            Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                            imageView.setImageBitmap(bmp);
//                        }
//                    }
//
//                    @Override
//                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
//                        int percentDone = (int) percentDonef;
//
//                        tvFileName.setText("ID:" + id + "|bytesCurrent: " + bytesCurrent + "|bytesTotal: " + bytesTotal + "|" + percentDone + "%");
//                    }
//
//                    @Override
//                    public void onError(int id, Exception ex) {
//                        ex.printStackTrace();
//                    }
//
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Toast.makeText(this, "Upload file before downloading", Toast.LENGTH_LONG).show();
//        }
    }



    private void showChoosingFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (bitmap != null) {
            bitmap.recycle();
        }

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean validateInputFileName(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "Enter file name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------------------------------------------------S3 ↥








    public void uploadWithTransferUtility() {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

        File file = new File(getApplicationContext().getFilesDir(), "ciao.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append("Howdy World!");
            writer.close();
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }

        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/ciao.txt",
                        new File(getApplicationContext().getFilesDir(),"ciao.txt"));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d(TAG, "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d(TAG, "Bytes Total: " + uploadObserver.getBytesTotal());


    }
}

