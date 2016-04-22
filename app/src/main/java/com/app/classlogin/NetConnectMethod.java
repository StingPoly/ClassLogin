package com.app.classlogin;


import static com.app.classlogin.NetParameter.Server_URL;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.util.Log;

//import com.google.android.gcm.GCMRegistrar;

public final class NetConnectMethod {
	
	private final static int MAX_ATTEMPTS = 8;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	
	static boolean Register(final Context context, final String regId, final String classnumber){
//		Log.i("TAG", "registering device (regId = " + regId + ")");
//		String serverUrl = Server_URL + "/register.php";
//		Map<String, String> params = new HashMap<String, String>();
//
//		params.put("regId", regId);
//        params.put("classnumber", classnumber);
//        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
//
//        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
//            Log.d("TAG", "Attempt #" + i + " to register");
//            try {
//                //displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));
//                //**
//                post(serverUrl, params);
//                GCMRegistrar.setRegisteredOnServer(context, true);
//                String message = context.getString(R.string.server_registered);
//                CommonUtilities.displayMessage(context, message);
//                return true;
//            } catch (IOException e) {
//                Log.e("TAG", "Failed to register on attempt " + i, e);
//                if (i == MAX_ATTEMPTS) {
//                    break;
//                }
//                try {
//                    Log.d("TAG", "Sleeping for " + backoff + " ms before retry");
//                    Thread.sleep(backoff);
//                } catch (InterruptedException e1) {
//                    // Activity finished before we complete - exit.
//                    Log.d("TAG", "Thread interrupted: abort remaining retries!");
//                    Thread.currentThread().interrupt();
//                    return false;
//                }
//                // increase backoff exponentially
//                backoff *= 2;
//            }
//        }
		return false;
	}
	
	
	private static void post(String endpoint, Map<String, String> params)
            throws IOException {
//        URL url;
//        try {
//            url = new URL(endpoint);
//        } catch (MalformedURLException e) {
//            throw new IllegalArgumentException("invalid url: " + endpoint);
//        }
//        StringBuilder bodyBuilder = new StringBuilder();
//        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
//        // constructs the POST body using the parameters
//        while (iterator.hasNext()) {
//            Entry<String, String> param = iterator.next();
//            bodyBuilder.append(param.getKey()).append('=')
//                    .append(param.getValue());
//            if (iterator.hasNext()) {
//                bodyBuilder.append('&');
//            }
//        }
//        String body = bodyBuilder.toString();
//        Log.v("TAG", "Posting '" + body + "' to " + url);
//        byte[] bytes = body.getBytes();
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//            conn.setFixedLengthStreamingMode(bytes.length);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type",
//                    "application/x-www-form-urlencoded;charset=UTF-8");
//            // post the request
//            OutputStream out = conn.getOutputStream();
//            out.write(bytes);
//            out.close();
//            // handle the response
//            int status = conn.getResponseCode();
//            if (status != 200) {
//              throw new IOException("Post failed with error code " + status);
//            }
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
     }

}
