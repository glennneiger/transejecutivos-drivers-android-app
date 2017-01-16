package com.development.transejecutivosdrivers.misc;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Will Montiel on 01/15/2017.
 */

public class VolleyErrorHandler {
    VolleyError volleyError;
    String message = null;

    public void setVolleyError(VolleyError volleyError) {
        this.volleyError = volleyError;
    }



    public void process() {
        //get status code here
        this.message = this.volleyError.getMessage();

        if (TextUtils.isEmpty(this.message)) {
            if (this.volleyError.networkResponse != null) {
                String statusCode = String.valueOf(this.volleyError.networkResponse.statusCode);

                switch (statusCode) {
                    case "404":
                        this.message = Const.error404;
                        break;

                    case "400":
                    case "500":
                        //get response body and parse with appropriate encoding
                        if(this.volleyError.networkResponse.data != null) {
                            try {
                                try {
                                    String response = new String(this.volleyError.networkResponse.data, "UTF-8");
                                    JSONObject resObj = new JSONObject(response);
                                    this.message = (String) resObj.get(JsonKeys.MESSAGE);
                                }
                                catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                }
            }
        }

    }

    public String getMessage() {
        return this.message;
    }
}
