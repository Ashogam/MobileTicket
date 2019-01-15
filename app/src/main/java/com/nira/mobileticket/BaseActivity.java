package com.nira.mobileticket;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {


    protected void showProgessBar() {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.parent_view).setVisibility(View.GONE);
    }

    protected void hideProgressBar() {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        findViewById(R.id.parent_view).setVisibility(View.VISIBLE);
    }


    protected void showPopUpAlert(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }


    protected void showPopUpAlert(String title, String message, final Class destination) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(BaseActivity.this, destination));
                    }
                }).show();
    }


    protected void showPopUpAlert(String title, String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
                .setPositiveButton("Ok", listener).show();
    }

    protected void showToastAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    protected void setUserProfile(String name, String profilePic) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.nira.mobileticket", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("profilePic", profilePic);
        editor.apply();
    }


    protected String getUserProfile(String key) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.nira.mobileticket", Context.MODE_PRIVATE);
        String value = prefs.getString(key, "NA");
        return value;
    }

    public void hideKeyboardFrom() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().findViewById(android.R.id.content).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
