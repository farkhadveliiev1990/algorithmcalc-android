package com.shindev.rulecalculator.activity;

import android.app.AlertDialog;
import android.appwidget.AppWidgetProviderInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shindev.rulecalculator.R;
import com.shindev.rulecalculator.util.APIManager;
import com.shindev.rulecalculator.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassActivity extends AppCompatActivity {

    private TextInputEditText txt_id;
    private TextInputLayout tll_id;
    private CheckBox chb_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_pass);
        AppUtils.initUIActivity(this);
        setToolbar();

        initUIView();
    }

    private void initUIView() {
        tll_id = findViewById(R.id.tll_forgot_userid);
        txt_id = findViewById(R.id.txt_forgot_id);
        txt_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                tll_id.setHelperText("");
            }
        });
        chb_id = findViewById(R.id.chk_forgot_id);
        chb_id.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                tll_id.setEnabled(false);
                txt_id.setEnabled(false);
            } else {
                tll_id.setEnabled(true);
                txt_id.setEnabled(true);
            }
        });
    }

    private void setToolbar() {
        setTitle("????????????");
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_left_white);
            toolbar.setNavigationOnClickListener(v -> {
                onBackPressed();
            });
        }
    }

    public void onClickBtnForgot(View view) {
        String idStr = txt_id.getText().toString();
        if (idStr.length() == 0) {
            tll_id.setHelperText("????????????????????? ????????????????????????");
            return;
        }
        final String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Map<String, String> params = new HashMap<>();
        params.put("email", idStr);
        params.put("deviceid", deviceId);

        APIManager.onAPIConnectionResponse(APIManager.LAO_FORGOT_PASSWORD, params, APIManager.APIMethod.POST, new APIManager.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj, int ret) {
                if (ret == 10000) {
                    try {
                        JSONObject result = obj.getJSONObject("result");
                        String pass = result.getString("password");
                        String alertStr = "";
                        if (chb_id.isChecked()) {
                            String userid = result.getString("userid");
                            alertStr = "??????????????????????????????????????????\n userid = " + userid + "\n password = " + pass + "\n ????????????????????????????????????";
                        } else {
                            alertStr = "??????????????????????????????????????????\n password = " + pass + "\n ????????????????????????????????????";
                        }

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotPassActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        alertDialog.setTitle("??????");
                        alertDialog.setMessage(alertStr);
                        alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> {
                            onBackPressed();
                        });
                        alertDialog.show();

                    } catch (JSONException e) {
                        Toast.makeText(ForgotPassActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else if (ret == 100001) {
                    Toast.makeText(ForgotPassActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {

            }

            @Override
            public void onEventServerError(Exception e) {

            }
        });
    }
}
