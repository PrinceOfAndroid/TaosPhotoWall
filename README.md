# TaosPhotoWall
本地图片库
* 支持单选多选
* 支持大图预览

## 屏幕截图
<img width="180" height="300" src="https://github.com/PrinceOfAndroid/TaosPhotoWall/blob/master/screenshots/one.png"/> <img width="180" height="300" src="https://github.com/PrinceOfAndroid/TaosPhotoWall/blob/master/screenshots/two.png"/>  <img width="180" height="300" src="https://github.com/PrinceOfAndroid/TaosPhotoWall/blob/master/screenshots/three.png"/>

##  Gradle
	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}


	dependencies {
	        compile 'com.github.PrinceOfAndroid:TaosPhotoWall:v1.1'
	}

## 代码使用
``` java
TaosPhotoWallActivity.starForResult(MainActivity.this, true , MAX_PHOTO, CODE_PHOTO_WALL);

//获取选择的图片地址
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_PHOTO_WALL:
                    //选择图片的本地路径
                    List<String> paths = data.getStringArrayListExtra(TaosPhotoWallActivity.DATA_KEY);
                    break;
            }
        }
    }
```

## 其中引用其他库
[LargeImageView](https://github.com/cshzhang/largeimageview "点击查看")
