package com.copasso.cocobill.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.copasso.cocobill.R;
import com.copasso.cocobill.bean.UserBean;
import com.copasso.cocobill.utils.HttpUtils;
import com.copasso.cocobill.utils.SharedPUtils;
import com.copasso.cocobill.utils.StringUtils;
import com.copasso.cocobill.view.OwlView;
import com.google.gson.Gson;

/**
 * Created by zhouas666 on 2017/12/8.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.owl_view)
    OwlView mOwlView;
    @BindView(R.id.login_et_email)
    EditText emailET;
    @BindView(R.id.login_et_username)
    EditText usernameET;
    @BindView(R.id.login_et_password)
    EditText passwordET;
    @BindView(R.id.login_et_rpassword)
    EditText rpasswordET;
    @BindView(R.id.login_tv_sign)
    TextView signTV;
    @BindView(R.id.login_btn_login)
    Button loginBtn;

    //是否是登陆操作
    private boolean isLogin = true;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_login;
    }

    @Override
    protected void initEventAndData() {

        //监听密码输入框的聚焦事件
        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mOwlView.open();
                } else {
                    mOwlView.close();
                }
            }
        });
        rpasswordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mOwlView.open();
                } else {
                    mOwlView.close();
                }
            }
        });

    }

    /**
     * 监听点击事件
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.login_tv_sign, R.id.login_btn_login})
    protected void onClick(final View view) {
        switch (view.getId()) {
            case R.id.login_btn_login:  //button
                if (isLogin){
                    //登陆
                    String username = usernameET.getText().toString();
                    String password = passwordET.getText().toString();
                    if (username.length()==0||password.length()==0){
                        Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT);
                        break;
                    }
                    HttpUtils.userLogin(new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            Gson gson = new Gson();
                            UserBean userBean = gson.fromJson(msg.obj.toString(), UserBean.class);
                            if (userBean.getStatus() == 100) {
                                if (userBean.getState()==1){
                                    SharedPUtils.setCurrentUser(LoginActivity.this,msg.obj.toString());
                                    setResult(RESULT_OK, new Intent());
                                    finish();
                                }else {
                                    Snackbar.make(view, "请先登陆邮箱激活账号", Snackbar.LENGTH_LONG).show();
                                }

                            } else {
                                Snackbar.make(view, userBean.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }, username, password);
                }else {
                    //注册
                    String email = emailET.getText().toString();
                    String username = usernameET.getText().toString();
                    String password = passwordET.getText().toString();
                    String rpassword = rpasswordET.getText().toString();
                    if (email.length()==0||username.length()==0||password.length()==0||rpassword.length()==0){
                        Snackbar.make(view, "请填写必要信息", Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    if (!StringUtils.checkEmail(email)){
                        Snackbar.make(view, "请输入正确的邮箱格式", Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    if (!password.equals(rpassword)){
                        Snackbar.make(view, "两次密码不一致", Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    HttpUtils.userSign(new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            Gson gson = new Gson();
                            UserBean userBean = gson.fromJson(msg.obj.toString(), UserBean.class);
                            if (userBean.getStatus() == 100) {
                                Snackbar.make(view, "注册成功，请先登陆邮箱验证后登陆", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(view, userBean.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }, username, password,email);
                }
                break;
            case R.id.login_tv_sign:  //sign
                if(isLogin){
                    //置换注册界面
                    signTV.setText("Login");
                    loginBtn.setText("Sign Up");
                    rpasswordET.setVisibility(View.VISIBLE);
                    emailET.setVisibility(View.VISIBLE);
                }else {
                    //置换登陆界面
                    signTV.setText("Sign Up");
                    loginBtn.setText("Login");
                    rpasswordET.setVisibility(View.GONE);
                    emailET.setVisibility(View.GONE);
                }
                isLogin=!isLogin;
                break;
            default:
                break;
        }
    }
}
