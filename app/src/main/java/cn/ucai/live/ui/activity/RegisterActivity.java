package cn.ucai.live.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.OnCompleteListener;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.L;
import cn.ucai.live.utils.MD5;
import cn.ucai.live.utils.MFGT;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.Result;
import cn.ucai.live.utils.ResultUtils;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    @BindView(R.id.email)
    EditText mEtUsername;
    @BindView(R.id.password)
    EditText mEtPassword;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nickname)
    EditText mEtNickname;
    @BindView(R.id.com_password)
    EditText mEtComPassword;
    String username, nickname, password;
    ProgressDialog pd;
    IUserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        model = new UserModel();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.register)
    public void registerAppServer() {
        if (checkInput()) {
            showDialog();
            L.e(TAG, "password=" + MD5.getMessageDigest(password));
            model.register(RegisterActivity.this, username, nickname, MD5.getMessageDigest(password),
                    new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            L.e(TAG, "s=" + s);
                            boolean success = false;
                            if (s != null) {
                                Result result = ResultUtils.getResultFromJson(s, String.class);
                                if (result != null) {
                                    if (result.isRetMsg()) {
                                        success = true;
                                        registerEMServer();
                                    } else if (result.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                                        CommonUtils.showShortToast(R.string.User_already_exists);
                                    } else {
                                        CommonUtils.showShortToast(R.string.Registration_failed);
                                    }
                                }
                            }
                            if (!success) {
                                pd.dismiss();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            L.e(TAG, "error" + error);
                            pd.dismiss();
                            CommonUtils.showShortToast(R.string.Registration_failed);
                        }
                    });
        }
    }

    private void registerEMServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(username, MD5.getMessageDigest(password));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showToast("注册成功");
                            PreferenceManager.getInstance().setCurrentUserName(username);
//                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//                            finish();
                            MFGT.gotoLogin(RegisterActivity.this);
                        }
                    });
                } catch (final HyphenateException e) {
                    unregister();
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showLongToast("注册失败：" + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void unregister() {
        model.unregister(RegisterActivity.this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.e(TAG, "result=" + result);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void showDialog() {
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("正在注册...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    public boolean checkInput() {
        username = mEtUsername.getText().toString().trim();
        nickname = mEtNickname.getText().toString().trim();
        password = mEtPassword.getText().toString().trim();
        String confirm_pwd = mEtComPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mEtUsername.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
            mEtNickname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mEtPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mEtComPassword.requestFocus();
            return false;
        } else if (!password.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
