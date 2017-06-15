package exercise2.wangzhengli.test.com.treasure.user.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import Util.ActivityUtils;
import Util.RegexUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import exercise2.wangzhengli.test.com.treasure.MainActivity;
import exercise2.wangzhengli.test.com.treasure.R;
import exercise2.wangzhengli.test.com.treasure.custom.AlertDialogFragment;
import exercise2.wangzhengli.test.com.treasure.treasure.HomeActivity;
import exercise2.wangzhengli.test.com.treasure.user.User;

public class LoginActivity extends AppCompatActivity implements LoginView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_Username)
    EditText mEtUsername;
    @BindView(R.id.et_Password)
    EditText mEtPassword;
    @BindView(R.id.tv_forgetPassword)
    TextView tvForgetPassword;
    @BindView(R.id.btn_Login)
    Button mBtnLogin;
    private String mUserName;
    private String mPassword;
    private ProgressDialog mProgressDialog;
    private ActivityUtils mActivityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        ButterKnife.bind(this);

        mActivityUtils = new ActivityUtils(this);

        // toolbar作为ActionBar展示
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            // 设置返回箭头，Android已经处理好了，内部是选项菜单处理的，已经提供好了id：android.R.id.home
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 显示返回箭头

            // 设置标题
            getSupportActionBar().setTitle(R.string.login);
        }

        // 设置文本变化的监听

        mEtUsername.addTextChangedListener(mTextWatcher);
        mEtPassword.addTextChangedListener(mTextWatcher);

    }
//文本变化监听
    private TextWatcher mTextWatcher =new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mUserName = mEtUsername.getText().toString();
        mPassword = mEtPassword.getText().toString();
        // 判断用户名和密码都不为空，按钮才可以点击
        boolean canLogin = !(TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassword));
        mBtnLogin.setEnabled(canLogin);
    }
};
    // 用于处理选项菜单的选择事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            // 处理ActionBar的返回箭头的事件
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, "登录", "正在登录中，请稍后~");
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String messgage) {
        mActivityUtils.showToast(messgage);
    }

    @Override
    public void navigateToHome() {
        mActivityUtils.startActivity(HomeActivity.class);

        // 发送一个本地广播到MainActivity，通知他关闭掉
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

    @OnClick(R.id.btn_Login)
    public void onViewClicked() {
      //账号不符合规则弹出一个对话框
        if (RegexUtils.verifyUsername(mUserName)!=RegexUtils.VERIFY_SUCCESS){
          //弹出一个对话框提示：采用采用DialogFragment的方式
            AlertDialogFragment.getInstances(getString(R.string.username_error),getString(R.string.username_rules))
                    .show(getSupportFragmentManager(),"\"usernameerror\"");//展示
        }
        // 密码不符合规范
        if (RegexUtils.verifyPassword(mPassword)!=RegexUtils.VERIFY_SUCCESS){
            // 弹出一个对话框提示
            AlertDialogFragment.getInstances(getString(R.string.password_error),getString(R.string.password_rules))
                    .show(getSupportFragmentManager(),"passworderror");
            return;
        }
        // 验证都没问题，需要进行网络请求完成登录
        new LoginPresenter(this).login(new User(mUserName,mPassword));
    }
}
