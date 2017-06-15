package exercise2.wangzhengli.test.com.treasure.user.register;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface RegisterView {
    void showProgress();// 显示进度

    void hideProgress();// 隐藏进度

    void showMessage(String msg);// 显示信息

    void navigateToHome();// 跳转页面
}
