package exercise2.wangzhengli.test.com.treasure;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import Util.ActivityUtils;
import Util.RegexUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import exercise2.wangzhengli.test.com.treasure.custom.AlertDialogFragment;
import exercise2.wangzhengli.test.com.treasure.treasure.HomeActivity;
import exercise2.wangzhengli.test.com.treasure.user.User;
import exercise2.wangzhengli.test.com.treasure.user.register.RegisterPresenter;
import exercise2.wangzhengli.test.com.treasure.user.register.RegisterView;

public class RegisterActivity extends AppCompatActivity implements RegisterView {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_Username)
    EditText mEtUsername;
    @BindView(R.id.et_Password)
    EditText mEtPassword;
    @BindView(R.id.et_Confirm)
    EditText mEtConfirm;
    @BindView(R.id.btn_Register)
    Button mBtnRegister;
    private String mUserName;
    private String mPassword;
    private String confirm;
    private ProgressDialog mProgressDialog;
    private ActivityUtils mActivityUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            //返回箭头和标题
            // 返回箭头和标题
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.register);
        }
        mEtUsername.addTextChangedListener(mTextWatcher);
        mEtPassword.addTextChangedListener(mTextWatcher);
        mEtConfirm.addTextChangedListener(mTextWatcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //文本监听
    private TextWatcher mTextWatcher = new TextWatcher() {
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
             confirm = mEtConfirm.getText().toString();

            boolean canRegister = !(TextUtils.isEmpty(mUserName)) || !(TextUtils.isEmpty(mPassword)) || !(TextUtils.isEmpty(confirm));
            mBtnRegister.setEnabled(canRegister);
        }
    };

    @OnClick(R.id.btn_Register)
    public void OnClick() {
        //账号不符合规范
        if (RegexUtils.verifyUsername(mUserName) != RegexUtils.VERIFY_SUCCESS) {
            //弹出对话框
            AlertDialogFragment.getInstances(getString(R.string.username_error), getString(R.string.username_rules))
                    .show(getSupportFragmentManager(), "usernameerror");
            return;
        }
        //密码不符合规范
        if (RegexUtils.verifyPassword(mPassword) != RegexUtils.VERIFY_SUCCESS) {
            AlertDialogFragment.getInstances(getString(R.string.password_error), getString(R.string.password_rules))
                    .show(getSupportFragmentManager(), "passworderror");
            return;
        }
        if (!(mPassword.equals(confirm))) {
            AlertDialogFragment.getInstances(getString(R.string.password_error), getString(R.string.please_confirm_password))
                    .show(getSupportFragmentManager(), "confirmpassword");
            return;
        }
        //做注册的网络请求
        new RegisterPresenter(this).register(new User(mUserName,mPassword));
    }

    @Override
    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, "注册", "正在注册中，请稍后~");
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void navigateToHome() {
        mActivityUtils.startActivity(HomeActivity.class);
        finish();

        // 发送广播：Main页面关闭
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
