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
	        compile 'com.github.PrinceOfAndroid:TaosPhotoWall:v1.0'
	}

## 代码使用
``` java
TaosPhotoWallActivity.starForResult(MainActivity.this, true , MAX_PHOTO, CODE_PHOTO_WALL);
```

## 其中引用其他库
[LargeImageView](https://github.com/cshzhang/largeimageview "点击查看")
