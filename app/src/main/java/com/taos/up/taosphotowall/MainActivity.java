package com.taos.up.taosphotowall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;

import com.taos.up.photowalllib.TaosPhotoWallActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnPhotoWall;
    private RecyclerView rePath;
    private SelectImgAdapter adapter;
    private static final int CODE_PHOTO_WALL = 1;
    private static final int MAX_PHOTO = 9;
    private List<String> imgPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgPaths = new ArrayList<>();
        btnPhotoWall = (Button) findViewById(R.id.btn_photo_wall);
        rePath = (RecyclerView) findViewById(R.id.re_path);
        adapter = new SelectImgAdapter(MainActivity.this, imgPaths);
        rePath.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        rePath.setAdapter(adapter);

        btnPhotoWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaosPhotoWallActivity.starForResult(MainActivity.this, true
                        , MAX_PHOTO, CODE_PHOTO_WALL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_PHOTO_WALL:
                    imgPaths.clear();
                    List<String> paths = data.getStringArrayListExtra(TaosPhotoWallActivity.DATA_KEY);
                    imgPaths.addAll(paths);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
