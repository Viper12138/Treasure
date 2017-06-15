package exercise2.wangzhengli.test.com.treasure;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import exercise2.wangzhengli.test.com.treasure.user.UserPrefs;

/**
 * Created by Administrator on 2017/6/12.
 */

public class TreasureApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 用户仓库的初始化
        UserPrefs.init(this);
        // 百度地图的SDK的初始化
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
    }
}
