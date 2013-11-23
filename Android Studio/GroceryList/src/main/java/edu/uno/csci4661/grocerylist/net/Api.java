package edu.uno.csci4661.grocerylist.net;

import android.accounts.NetworkErrorException;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class Api {

    // if result == "{}", there was an error
    public static String GET(String url, Map<String, String> params, Map<String, String> headers) {

        /*
            Need to clean the params to make sure the are safe
            to transport bu encoding them for URI strings
         */
        Iterator it = params.keySet().iterator();
        while (it.hasNext()) {

            try {
                String key = it.next().toString();
                String value = params.get(key);

                if (value != null) {
                    value = URLEncoder.encode(params.get(key), "UTF-8");
                }

                params.put(key, value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String json = "{}";

        String paramString = Joiner.on("&").withKeyValueSeparator("=").useForNull("").join(params);


        HttpGet request = new HttpGet(url + "?" + paramString);
        request.addHeader("mobile-app", "true");

        // add the additional header
        it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            request.addHeader((String) pairs.getKey(), (String) pairs.getValue());
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;

        try {
            response = httpClient.execute(request);
            Response result = new Response(response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase(),
                    EntityUtils.toString(response.getEntity()));

            json = result.getData();

        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return json;
    }

    public static String POST(String url, Map<String, Object> params, Map<String, String> headers) throws NetworkErrorException, IOException {
        String json = "{}";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonEncodedString = gson.toJson(params);

        HttpPost request = new HttpPost(url);
        request.setEntity(new ByteArrayEntity(jsonEncodedString.getBytes()));
        request.addHeader("Content-Type", "application/json");

        // add the additional header
        Iterator it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            request.addHeader((String) pairs.getKey(), (String) pairs.getValue());
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;

        Response httpResult;
        try {
            response = httpClient.execute(request);
            httpResult = new Response(response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase(),
                    EntityUtils.toString(response.getEntity()));

            json = httpResult.getData();
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return json;
    }
}
