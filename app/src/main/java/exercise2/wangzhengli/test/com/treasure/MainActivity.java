package exercise2.wangzhengli.test.com.treasure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import Util.ActivityUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import exercise2.wangzhengli.test.com.treasure.treasure.HomeActivity;
import exercise2.wangzhengli.test.com.treasure.user.UserPrefs;
import exercise2.wangzhengli.test.com.treasure.user.login.LoginActivity;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btn_Register)
    Button btnRegister;
    @BindView(R.id.btn_Login)
    Button btnLogin;
    public static final String MAIN_ACTION = "navigate_to_home";
    private Unbinder mUnbinder;
    private ActivityUtils mActivityUtils;
    // 广播接收器
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        // 接收到广播后会调用
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        mActivityUtils= new ActivityUtils(this);

        // 判断用户是不是登录过了
        if (UserPrefs.getInstance().getTokenid()!=-1){
            // 之前已经登录过了
            mActivityUtils.startActivity(HomeActivity.class);
            finish();
        }
        // 注册本地广播
        IntentFilter filter = new IntentFilter(MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
    @OnClick({R.id.btn_Register, R.id.btn_Login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_Register:
                mActivityUtils.startActivity(RegisterActivity.class);
                break;
            case R.id.btn_Login:
                mActivityUtils.startActivity(LoginActivity.class);
                break;
        }
    }
}
