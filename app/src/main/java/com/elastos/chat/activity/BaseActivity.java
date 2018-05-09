package com.elastos.chat.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * @author rczhang on 2018/05/08.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected void initVariables() {
    }

    protected void initViews(Bundle savedInstanceState) {
    }

    public boolean put(String strKey, String strValue) {
        SharedPreferences sharedPreferences = this.getApplicationContext().
                getSharedPreferences("ela_hackathon", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(strKey, strValue);
        editor.apply();
        return true;
    }

    public String get(String strKey) {
        SharedPreferences sharedPreferences = this.getApplicationContext().
                getSharedPreferences("ela_hackathon", Context.MODE_PRIVATE);
        return sharedPreferences.getString(strKey, "");
    }
}
