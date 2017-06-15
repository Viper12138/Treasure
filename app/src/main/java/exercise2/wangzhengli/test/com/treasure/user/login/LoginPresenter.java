package exercise2.wangzhengli.test.com.treasure.user.login;

/**
 * Created by Administrator on 2017/6/12.
 */

import exercise2.wangzhengli.test.com.treasure.network.NetClient;
import exercise2.wangzhengli.test.com.treasure.user.User;
import exercise2.wangzhengli.test.com.treasure.user.UserPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gqq on 2017/6/12.
 */

// 登录的业务
public class LoginPresenter {

    private LoginView mLoginView;

    public LoginPresenter(LoginView loginView) {
        mLoginView = loginView;
    }

    public void login(User user){

        // 显示进度条
        mLoginView.showProgress();

        NetClient.getInstance().getTreasureApi().login(user).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {

                // 进度条隐藏
                mLoginView.hideProgress();

                if (response.isSuccessful()){
                    LoginResult loginResult = response.body();
                    if (loginResult==null){
                        // 显示一个信息
                        mLoginView.showMessage("未知的错误");
                        return;
                    }

                    if (loginResult.getCode()==1){
                        // 真正的成功
                        // 保存头像和tokenId
                        UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+loginResult.getHeadpic());
                        UserPrefs.getInstance().setTokenid(loginResult.getTokenid());
                        // 导航到主页面
                        mLoginView.navigateToHome();
                    }
                    // 显示信息
                    mLoginView.showMessage(loginResult.getMsg());
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                // 隐藏进度条
                mLoginView.hideProgress();
                // 显示信息
                mLoginView.showMessage("请求失败："+t.getMessage());
            }
        });
    }
}
