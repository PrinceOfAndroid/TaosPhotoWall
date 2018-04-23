package com.taos.up.photowalllib.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/25 0025.
 * 图片bean
 */

public class ImageBean implements Serializable{
    private String imgPath;  //图片地址
    private boolean choose;  //选择状态


    public ImageBean(String imgPath, boolean choose) {
        this.imgPath = imgPath;
        this.choose = choose;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }


}
