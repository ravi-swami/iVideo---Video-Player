package com.example.ivideo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> myVideos = fetchVideos(Environment.getExternalStorageDirectory());
                        String[] items = new String[myVideos.size()];
                        for(int i=0; i<myVideos.size(); i++){
                            items[i] = myVideos.get(i).getName().replace(".mp4", "");
                        }

                        ArrayAdapter ad = new ArrayAdapter(MainActivity.this, android.R.layout.simple_selectable_list_item, items);
                        listView.setAdapter(ad);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, PlayVideo.class);
                                String currentVideo = myVideos.get(position).getName();
                                intent.putExtra("songList", myVideos);
                                intent.putExtra("currentVideo", currentVideo);
                                intent.putExtra("position", position);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> fetchVideos(File file){
        ArrayList arrayList = new ArrayList();
        File[] videos = file.listFiles();
        if(videos != null){
            for(File myFile: videos){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchVideos(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp4") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}