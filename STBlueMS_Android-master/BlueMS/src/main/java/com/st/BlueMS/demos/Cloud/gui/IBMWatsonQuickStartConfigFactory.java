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

package com.st.BlueMS.demos.Cloud.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.st.BlueMS.R;
import com.st.BlueMS.demos.Cloud.IBMWatsonQuickStartFactory;
import com.st.BlueMS.demos.Cloud.MqttClientConnectionFactory;
import com.st.BlueMS.demos.Cloud.MqttClientUtil;
import com.st.BlueSTSDK.Node;

/**
 *  Object that help to configure the Ibm Watson Iot/BlueMX service, using the quickstart configuration
 */
public class IBMWatsonQuickStartConfigFactory implements MqttClientConfigurationFactory,TextWatcher {

    private static final String CONF_PREFERENCE = IBMWatsonConfigFactory.class.getCanonicalName();
    private static final String DEVICE_KEY = CONF_PREFERENCE+".DEVICE_KEY";

    private static final String FACTORY_NAME="IBM Watson IoT - Quickstart";

    private TextInputLayout mDeviceIdLayout;
    private EditText mDeviceIdText;
    private Node.Type mNodeType;

    private void loadFromPreferences(SharedPreferences pref){
        mDeviceIdText.setText(pref.getString(DEVICE_KEY,""));
    }

    private void storeToPreference(SharedPreferences pref){
        pref.edit()
                .putString(DEVICE_KEY,mDeviceIdText.getText().toString())
                .apply();
    }

    @Override
    public void attachParameterConfiguration(Context c, ViewGroup root) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.cloud_config_bluemx_quickstart,root);
        mDeviceIdText = (EditText) v.findViewById(R.id.blueMXQuick_deviceId);
        mDeviceIdLayout = (TextInputLayout) v.findViewById(R.id.blueMXQuick_deviceIdWrapper);
        mDeviceIdText.addTextChangedListener(this);
        loadFromPreferences(c.getSharedPreferences(CONF_PREFERENCE,Context.MODE_PRIVATE));
    }

    @Override
    public void loadDefaultParameters(@Nullable Node n) {
        if(n==null){
            mNodeType = Node.Type.GENERIC;
            return;
        }//else
        if(mDeviceIdText.getText().length()==0)
            mDeviceIdText.setText(MqttClientUtil.getDefaultCloudDeviceName(n));
        mNodeType=n.getType();
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public MqttClientConnectionFactory getConnectionFactory() throws IllegalArgumentException {
        Context c = mDeviceIdText.getContext();
        storeToPreference(c.getSharedPreferences(CONF_PREFERENCE,Context.MODE_PRIVATE));
        return new IBMWatsonQuickStartFactory(mNodeType.name(),mDeviceIdText.getText().toString());
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String input = mDeviceIdText.getText().toString();
        input = input.replace(" ","");
        if(input.isEmpty()) {
            mDeviceIdLayout.setErrorEnabled(true);
            mDeviceIdLayout.setError(mDeviceIdText.getContext().getString(R.string.cloudLog_bluemx_deviceIdError));
        }else{
            mDeviceIdLayout.setError(null);
            mDeviceIdLayout.setErrorEnabled(false);
        }

    }
}
