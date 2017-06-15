package exercise2.wangzhengli.test.com.treasure.user.login;

/**
 * Created by Administrator on 2017/6/12.
 */

// 登录的视图接口
public interface LoginView {
    void showProgress();// 显示进度
    void hideProgress();// 隐藏进度
    void showMessage(String messgage);// 显示信息
    void navigateToHome();// 跳转页面
}