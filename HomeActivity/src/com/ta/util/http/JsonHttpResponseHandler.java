/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.ta.util.http;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.apache.http.Header;
import android.os.Message;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link com.ta.util.http.AsyncHttpClient}, with automatic parsing into a {@link org.json.JSONObject}
 * or {@link org.json.JSONArray}.
 * <p>
 * This class is designed to be passed to get, post, put and delete requests
 * with the {@link #onSuccess(org.json.JSONObject)} or {@link #onSuccess(org.json.JSONArray)}
 * methods anonymously overridden.
 * <p>
 * Additionally, you can override the other event methods from the
 * parent class.
 */
public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {
    protected static final int SUCCESS_JSON_MESSAGE = 100;

    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json object found in the server response (if any)
     */
    public void onSuccess(JSONObject response) {}


    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     * @param response the parsed json array found in the server response (if any)
     */
    public void onSuccess(JSONArray response) {}

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     * @param statusCode the status code of the response
     * @param headers the headers of the HTTP response
     * @param response the parsed json object found in the server response (if any)
     */
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        onSuccess(statusCode, response);
    }

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     * @param statusCode the status code of the response
     * @param response the parsed json object found in the server response (if any)
     */
    public void onSuccess(int statusCode, JSONObject response) {
        onSuccess(response);
    }

    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     * @param statusCode the status code of the response
     * @param headers the headers of the HTTP response
     * @param response the parsed json array found in the server response (if any)
     */
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        onSuccess(statusCode, response);
    }

    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     * @param statusCode the status code of the response
     * @param response the parsed json array found in the server response (if any)
     */
    public void onSuccess(int statusCode,  JSONArray response) {
        onSuccess(response);
    }

    public void onFailure(Throwable e, JSONObject errorResponse) {}
    public void onFailure(Throwable e, JSONArray errorResponse) {}


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    @Override
    protected void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        if (statusCode != HttpStatus.SC_NO_CONTENT){        
            try {
                Object jsonResponse = parseResponse(responseBody);
	        sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, new Object[]{statusCode, headers, jsonResponse}));
    	    } catch(JSONException e) {
    	        sendFailureMessage(e, responseBody);
    	    }
        } else {
            sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, new Object[]{statusCode, new JSONObject()}));
    	}
    }


    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    @Override
    protected void handleMessage(Message msg) {
        switch(msg.what){
            case SUCCESS_JSON_MESSAGE:
                Object[] response = (Object[]) msg.obj;
                handleSuccessJsonMessage(((Integer) response[0]).intValue(),(Header[]) response[1] ,response[2]);
                break;
            default:
                super.handleMessage(msg);
        }
    }

    protected void handleSuccessJsonMessage(int statusCode,Header[] headers, Object jsonResponse) {
        if(jsonResponse instanceof JSONObject) {
            onSuccess(statusCode, headers, (JSONObject)jsonResponse);
        } else if(jsonResponse instanceof JSONArray) {
            onSuccess(statusCode, headers, (JSONArray)jsonResponse);
        } else {
            onFailure(new JSONException("Unexpected type " + jsonResponse.getClass().getName()), (JSONObject)null);
        }
    }

    protected Object parseResponse(String responseBody) throws JSONException {
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
		responseBody = responseBody.trim();
		if(responseBody.startsWith("{") || responseBody.startsWith("[")) {
			result = new JSONTokener(responseBody).nextValue();
		}
		if (result == null) {
			result = responseBody;
		}
		return result;
    }

    @Override
    protected void handleFailureMessage(Throwable e, String responseBody) {
        try {
            if (responseBody != null) {
                Object jsonResponse = parseResponse(responseBody);
                if(jsonResponse instanceof JSONObject) {
                    onFailure(e, (JSONObject)jsonResponse);
                } else if(jsonResponse instanceof JSONArray) {
                    onFailure(e, (JSONArray)jsonResponse);
                } else {
                    onFailure(e, responseBody);
                }
            }else {
                onFailure(e, "");
            }
        }catch(JSONException ex) {
            onFailure(e, responseBody);
        }
    }
}
