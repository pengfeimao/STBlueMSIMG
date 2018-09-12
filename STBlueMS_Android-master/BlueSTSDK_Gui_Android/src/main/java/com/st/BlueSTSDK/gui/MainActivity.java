/*
 * Copyright (c) 2017  STMicroelectronics – All rights reserved
 * The STMicroelectronics corporate logo is a trademark of STMicroelectronics
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 *   and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name nor trademarks of STMicroelectronics International N.V. nor any other
 *   STMicroelectronics company nor the names of its contributors may be used to endorse or
 *   promote products derived from this software without specific prior written permission.
 *
 * - All of the icons, pictures, logos and other images that are provided with the source code
 *   in a directory whose title begins with st_images may only be used for internal purposes and
 *   shall not be redistributed to any third party or modified in any way.
 *
 * - Any redistributions in binary form shall not include the capability to display any of the
 *   icons, pictures, logos and other images that are provided with the source code in a directory
 *   whose title begins with st_images.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package com.st.BlueSTSDK.gui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Main activity, it will show the ST logo and a button for the about page and one for start the ble
 * scanning
 */
public abstract class MainActivity extends AppCompatActivity {

    /**
     * The number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 1000;
    private static final String SPLASH_SCREEN_WAS_SHOWN = MainActivity.class.getCanonicalName()+"" +
            ".SplashWasShown";
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private final Handler mHideHandler = new Handler();

    private View mControlsView;
    private final Runnable mShowContentRunnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mControlsView = findViewById(R.id.main_content_controls);

        TextView versionText = (TextView) findViewById(R.id.versionText);
        TextView appText = (TextView) findViewById(R.id.appNameText);
        //show the version using the data in the manifest
        String version=null;
        CharSequence appName=null;

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            appName = getPackageManager().getApplicationLabel(pInfo.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }//try-catch

        if(versionText!=null){
            if(version!=null){
                versionText.append(version);
            }else
                versionText.setText("");
        }

        if(appText!=null && appName!=null){
            appText.setText(appName);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        boolean splashWasShown=false;
        if(savedInstanceState!=null){
            splashWasShown = savedInstanceState.getBoolean(SPLASH_SCREEN_WAS_SHOWN,false);
        }//if

        // Trigger the initial showSplashScreen() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        if(!splashWasShown) {
            showSplashScreen();
            delayedShowContent(AUTO_HIDE_DELAY_MILLIS);
        }else{
            mControlsView.setVisibility(View.VISIBLE);
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putBoolean(SPLASH_SCREEN_WAS_SHOWN,true);
    }

    private void showSplashScreen() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowContentRunnable);
    }

    /**
     * Schedules a call to showContent() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedShowContent(int delayMillis) {
        mHideHandler.removeCallbacks(mShowContentRunnable);
        mHideHandler.postDelayed(mShowContentRunnable, delayMillis);
    }

    /**
     * function called when the start ble scan button is pressed
     * @param view view pressed
     */
    public abstract void startScanBleActivity(View view);

    /**
     * function called when the about button is pressed
     * @param view view pressed
     */
    public abstract void startAboutActivity(View view);
}
