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
import android.widget.Adapter;
import android.widget.AdapterView;
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

//        if (i == R.id.btn_choose_file) {
//            showChoosingFile();
//        } else if (i == R.id.btn_upload) {
//            uploadFile();
//        } else if (i == R.id.btn_download) {
//            downloadFile();
//        }else if (i == R.id.btn_view_video) {
//            Intent intent = new Intent(getApplicationContext(), videoView.class);
//            startActivity(intent);
//        }else

            if (i == R.id.btn_list) {
            getList();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//----------------------------------------------------------------------------------------------------S3

//        imageView = findViewById(R.id.img_file);
//        edtFileName = findViewById(R.id.edt_file_name);
//        tvFileName = findViewById(R.id.tv_file_name);
//        tvFileName.setText("");
//
//        findViewById(R.id.btn_choose_file).setOnClickListener(this);
//        findViewById(R.id.btn_upload).setOnClickListener(this);
//        findViewById(R.id.btn_download).setOnClickListener(this);
//        findViewById(R.id.btn_view_video).setOnClickListener(this);
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
//todo: 리스트 클릭이벤트 구현후 해당 오브젝트 키값으로 geturi 한뒤 해당 uri를 videoview로 전송.
    String[] urikey = {""};

    ListView listView = (ListView)findViewById(R.id.object_list_View);

    List<String> list = new ArrayList<>();

    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,list);
    listView.setAdapter(adapter);
    list.addAll(objectLIST);

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String data = (String) adapterView.getItemAtPosition(position);
            urikey[0] =data;
            Log.i("MyamplifyApp","urikey from listView "+ urikey[0]);
            String[] videouri ={""};
            Amplify.Storage.getUrl(
                    urikey[0],
                    result -> {
                        Log.i("MyAmplifyApp", "Successfully generated: " + result.getUrl());
                       videouri[0]= result.getUrl().toString();
                        Log.i("MyamplifyApp","videoUri from listView 1-"+ videouri[0]);

                        Intent intent = new Intent(getApplicationContext(), videoView.class);
                        intent.putExtra("uri",videouri[0]);
                        startActivity(intent);
                    },
                    error -> Log.e("MyAmplifyApp", "URL generation failure", error)

            );

            Log.i("MyamplifyApp","videoUri from listView 2-"+ videouri[0]); //geturi 보다 먼저 실행된다?

        }

    }
        );


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

    }

    private void downloadFile() {

        Amplify.Storage.downloadFile(
                "testVideo.mp4",
                // 외부저장소-> 최근 버전에선 외부저장소 접근 안됨. MediaStore나 SAF(Storage Access Framework)를 이용 해야함
               new File(getApplicationContext().getFilesDir() + "/download.mp4"),
                result -> Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getName()),
                error -> Log.e("MyAmplifyApp",  "Download Failure", error)
        );


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

