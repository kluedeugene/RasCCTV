package com.company.RasCCTV;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity implements  View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final ArrayList<String> objectLIST = new ArrayList<String>();


    @Override
    public void onClick(View view) {
        int i = view.getId();
            if (i == R.id.btn_list) {
            getList();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_list).setOnClickListener(this);

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
    Toast.makeText(this, "Get Video List", Toast.LENGTH_LONG).show();
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

}

