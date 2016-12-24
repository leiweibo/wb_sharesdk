# wb_sharesdk

**如何使用**

### 导入到工程: 

- 方法一. 直接本地拷贝整个目录到你的项目
    0. 在全局的build.gradle里面添加：
        ```
        classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'
        ```
        这样做的目的是因为，打算把项目提升发布到jitpack上面去，而这个`plugin`需要在这一步用到

    1. 将library里面的内容拷贝到根目录下面命名为 wb_sharesdk(名字任意)，然后将这个moudle添加到你的`settings.xml` 以及`build.gradle`
        
        例子:
        settings.xml 
        ```
        include ':niubang-sharesdk'
        ```

        build.gradle
        ```
        compile project(':wb-sharesdk')
        ```



- 方法二. 使用gradle配置



### 基本信息的配置:

基本信息的配置需要注意的几个点如下：（细节请自行查阅demo项目下面的`MainActivity`）


```
微信分享：

需要在包名目录下添加, 添加一个wxapi的包，并且添加一个类添加一个WXEntryActivity，
这个类继承WXCallbackActivity即可    
```

然后在AndroidManifest里面添加如下配置：
        ```
        <!-- Weibo在用webview登录的时候需要用到 -->
        <activity
            android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser"
            android:configChanges="keyboardHidden|orientation"
            android:exported="false"
            android:windowSoftInputMode="adjustResize" > 
        </activity>

        <!-- QQ和QQ空间需要用到 -->
        <activity
            android:name="com.tencent.tauth.AuthActivity"
            android:launchMode="singleTask"
            android:noHistory="true" >
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <!------ 这里请输入自己的appkey ---->
                <data android:scheme="tencentxxxxx" />
            </intent-filter>
        </activity>
        <activity
            android:name="com.tencent.connect.common.AssistActivity"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
        <!-- 微信回调activity -->
        <activity
            android:name="com.niubang.uguma.wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
        ```
### 在Application里面注册appkey或者appid
```
SharePlatformConfig.setQQ(QQ_APP_ID);
SharePlatformConfig.setSina(WEIBO_APP_KEY);
SharePlatformConfig.setWeixin(WEIXIN_APP_ID, WEIXIN_APP_SCRECT);
```

具体的实现请参考MainActivity.java
