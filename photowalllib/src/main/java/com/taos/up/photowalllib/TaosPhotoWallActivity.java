package com.taos.up.photowalllib;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taos.up.photowalllib.adapters.PhotoGroupAdapter;
import com.taos.up.photowalllib.adapters.PhotoWallAdapter;
import com.taos.up.photowalllib.beans.ImageBean;
import com.taos.up.photowalllib.beans.ImageGroupBean;
import com.taos.up.photowalllib.utils.ScreenUtils;
import com.taos.up.photowalllib.utils.SizeUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by PrinceOfAndroid on 2018/4/20 0020.
 */

public class TaosPhotoWallActivity extends AppCompatActivity {
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvCount;
    private RecyclerView rePhotoWall;
    private TextView tvChooseGroup;
    private TextView tvSure;

    private List<ImageBean> datas;
    private HashMap<String, List<ImageBean>> mGroupMap;
    private boolean canMulti;
    private int max;
    private PhotoWallAdapter photoWallAdapter;

    private Dialog groupDialog;
    private List<ImageGroupBean> imageGroups;
    private HashMap<String, Boolean> selectImgs;
    private int groupPosition;
    private int selectNum;

    public static final int LARGE_PHOTO = 1;
    public static final String DATA_KEY = "imgPaths";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_photo_wall);
        initData();
        initView();
    }

    public static void starForResult(Activity activity, boolean canMulti,
                                     int max, int requestCode) {
        Intent intent = new Intent(activity, TaosPhotoWallActivity.class);
        intent.putExtra("canMulti", canMulti);
        intent.putExtra("max", max);
        activity.startActivityForResult(intent, requestCode);
    }


    private void initData() {
        Bundle bundle = getIntent().getExtras();
        canMulti = bundle.getBoolean("canMulti");
        max = bundle.getInt("max");

        datas = new ArrayList<>();
        selectImgs = new HashMap<>();
        mGroupMap = new HashMap<>();
        if (checkPermission()) {
            getImg();
        } else {
            ActivityCompat.requestPermissions(TaosPhotoWallActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * 检测读取权限
     *
     * @return
     */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(TaosPhotoWallActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCount = (TextView) findViewById(R.id.tv_count);
        rePhotoWall = (RecyclerView) findViewById(R.id.re_photowall);
        tvChooseGroup = (TextView) findViewById(R.id.tv_choose_group);
        tvSure = (TextView) findViewById(R.id.tv_sure);

        tvCount.setText("确定(" + selectNum + "/" + max + ")");
        photoWallAdapter = new PhotoWallAdapter(TaosPhotoWallActivity.this, datas, canMulti);
        rePhotoWall.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        rePhotoWall.setAdapter(photoWallAdapter);

        tvChooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGroupDialog();
            }
        });

        photoWallAdapter.setOnPhotoChooseClickListener(new PhotoWallAdapter.OnPhotoChooseClickListener() {
            @Override
            public void photoChooseClick(View view, int position) {
                boolean isSelect = !datas.get(position).isChoose();
                if (isSelect) {
                    if (selectNum == max) {
                        Toast.makeText(TaosPhotoWallActivity.this, "最多可选" + max + "张图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectNum++;
                } else {
                    selectNum--;
                }
                String imgPath = datas.get(position).getImgPath();
                //记录操作选择（key为图片地址  唯一）
                selectImgs.put(imgPath, isSelect);
                //更新adapter的数据
                datas.get(position).setChoose(isSelect);
                photoWallAdapter.notifyItemChanged(position);
                tvCount.setText("确定(" + selectNum + "/" + max + ")");
            }
        });

        photoWallAdapter.setOnPhotoClickListener(new PhotoWallAdapter.OnPhotoClickListener() {
            @Override
            public void photoClick(View view, int position) {
                Intent intent = new Intent(TaosPhotoWallActivity.this, PhotoPagerActivity.class);
                intent.putExtra("index", position);
                intent.putExtra("imgs", (Serializable) datas);
                intent.putExtra("selectNum", selectNum);
                intent.putExtra("max", max);
                startActivityForResult(intent, LARGE_PHOTO);
            }
        });

        tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> imgPaths = new ArrayList<>();
                Iterator<Map.Entry<String, Boolean>> it = selectImgs.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Boolean> entry = it.next();
                    if (entry.getValue()) {
                        imgPaths.add(entry.getKey());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra(DATA_KEY, (Serializable) imgPaths);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 设置状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }


    /**
     * 扫描本地所有图片
     */
    private void getImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ImageBean> allImgs = new ArrayList<>();
                mGroupMap.clear();
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = TaosPhotoWallActivity.this.getContentResolver();
                //只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                if (mCursor == null) {
                    return;
                }
                while (mCursor.moveToNext()) {
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    ImageBean bean = new ImageBean(path, false);
                    allImgs.add(bean);
                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();
                    //根据父路径名将图片放入到mGruopMap中
                    if (!mGroupMap.containsKey(parentName)) {
                        List<ImageBean> childList = new ArrayList<ImageBean>();
                        ImageBean imageBean = new ImageBean(path, false);
                        childList.add(imageBean);
                        mGroupMap.put(parentName, childList);
                    } else {
                        mGroupMap.get(parentName).add(new ImageBean(path, false));
                    }
                }
                //添加全部图片的集合
                Collections.reverse(allImgs);
                datas.clear();
                mGroupMap.put("全部图片", allImgs);
                datas.addAll(allImgs);

                mCursor.close();

                //组装弹窗数据
                imageGroups = subGroupOfImage(mGroupMap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upDateUi();
                    }
                });
            }
        }).start();
    }

    /**
     * 组装分组界面的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     * 用于展示图库分组列表
     *
     * @param mGroupMap
     * @return
     */
    private List<ImageGroupBean> subGroupOfImage(HashMap<String, List<ImageBean>> mGroupMap) {
        if (mGroupMap.size() == 0) {
            return null;
        }
        //遍历
        List<ImageGroupBean> list = new ArrayList<ImageGroupBean>();
        Iterator<Map.Entry<String, List<ImageBean>>> it = mGroupMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<ImageBean>> entry = it.next();
            ImageGroupBean mGroupBean = new ImageGroupBean();
            //根据key获取其中图片list
            String key = entry.getKey();
            List<ImageBean> value = entry.getValue();
            mGroupBean.setFolderName(key);//获取该组文件夹名称
            mGroupBean.setImageCounts(value.size());//获取该组图片数量
            mGroupBean.setTopImagePath(value.get(0).getImgPath());//获取该组的第一张图片
            //将全部图片放在第一位置
            if (mGroupBean.getFolderName().equals("全部图片")) {
                list.add(0, mGroupBean);
            } else {
                list.add(mGroupBean);
            }
        }
        return list;
    }


    private void upDateUi() {
        tvTitle.setText(imageGroups.get(groupPosition).getFolderName());
        photoWallAdapter.notifyDataSetChanged();
    }


    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImg();
                } else {
                    Toast.makeText(TaosPhotoWallActivity.this, "读取权限获取失败", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


    /**
     * 图库文件夹dialog
     */
    public void showGroupDialog() {
        if (groupDialog == null) {
            groupDialog = new Dialog(TaosPhotoWallActivity.this, R.style.dialog);
            final View view = getLayoutInflater().inflate(R.layout.view_photo_list, null);
            final PhotoGroupAdapter photoGroupAdapter = new PhotoGroupAdapter(imageGroups, TaosPhotoWallActivity.this);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.re_photo_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(TaosPhotoWallActivity.this));
            recyclerView.setAdapter(photoGroupAdapter);
            int totalHeight = 0;
            totalHeight = SizeUtils.dp2px(108, TaosPhotoWallActivity.this) * imageGroups.size();
            int screenHeight = ScreenUtils.getScreenHeight(TaosPhotoWallActivity.this);

            if (totalHeight > screenHeight / 2) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight / 2);
                recyclerView.setLayoutParams(params);
            }
            groupDialog.setContentView(view);
            Window dialogWindow = groupDialog.getWindow();
            dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
            dialogWindow.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialogWindow.setWindowAnimations(R.style.bottom_dialog_anim);
            photoGroupAdapter.setGroupClickListener(new PhotoGroupAdapter.GroupClickListener() {
                @Override
                public void groupClick(int position, View v) {
                    photoGroupAdapter.setSelectIndex(position);
                    groupDialog.dismiss();
                    if (position != groupPosition) {
                        datas.clear();
                        List<ImageBean> imageBeen = mGroupMap.get(imageGroups.get(position).getFolderName());
                        for (ImageBean ib : imageBeen) {
                            //是否对该图片进行过选择操作？
                            if (selectImgs.containsKey(ib.getImgPath())) {
                                ib.setChoose(selectImgs.get(ib.getImgPath()));
                            }
                            datas.add(ib);
                        }
                        photoWallAdapter.notifyDataSetChanged();
                    }
                    groupPosition = position;
                }
            });
        }
        groupDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LARGE_PHOTO:
                    List<Integer> seletIndexs = data.getIntegerArrayListExtra("select");
                    selectNum = data.getIntExtra("selectNum", 0);
                    for (Integer it : seletIndexs) {
                        datas.get(it).setChoose(true);
                        //记录操作选择（key为图片地址  唯一）
                        selectImgs.put(datas.get(it).getImgPath(), true);
                    }
                    photoWallAdapter.notifyDataSetChanged();
                    tvCount.setText("确定(" + selectNum + "/" + max + ")");
                    break;
            }
        }
    }
}
