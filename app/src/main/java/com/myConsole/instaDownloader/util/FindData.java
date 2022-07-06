package com.myConsole.instaDownloader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.myConsole.instaDownloader.interfaces.GetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;


public class FindData {

    private Context context;
    private GetData getData;

    public FindData(Context context, GetData getData) {
        this.context = context;
        this.getData = getData;
    }

    public void data(String stringData) {

        ArrayList<String> arrayList = new ArrayList<>();

        if (stringData.matches("https://www.instagram.com/(.*)")) {
            String[] data = stringData.split(Pattern.quote("?"));
            String string = data[0];
            if (isNetworkAvailable()) {
                if (Method.isDownload) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(string + "?__a=1", null, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            String res = new String(responseBody);

                            try {
                                JSONObject jsonObject = new JSONObject(res);

                                String link = null;

                                JSONObject objectGraphql = jsonObject.getJSONObject("graphql");
                                JSONObject objectMedia = objectGraphql.getJSONObject("shortcode_media");
                                boolean isVideo = objectMedia.getBoolean("is_video");
                                if (isVideo) {
                                    link = objectMedia.getString("video_url");
                                } else {
                                    link = objectMedia.getString("display_url");
                                }

                                arrayList.add(link);

                                try {
                                    JSONObject objectSidecar = objectMedia.getJSONObject("edge_sidecar_to_children");
                                    JSONArray jsonArray = objectSidecar.getJSONArray("edges");

                                    arrayList.clear();

                                    String edgeSidecar = null;

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject object = jsonArray.getJSONObject(i);
                                        JSONObject node = object.getJSONObject("node");
                                        boolean is_video_group = node.getBoolean("is_video");
                                        if (is_video_group) {
                                            edgeSidecar = node.getString("video_url");
                                        } else {
                                            edgeSidecar = node.getString("display_url");
                                        }
                                        arrayList.add(edgeSidecar);

                                    }

                                } catch (Exception e) {
                                    Log.e("error_show", e.toString());
                                }

                                getData.getData(arrayList, "", true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                getData.getData(arrayList, "not_support", false);
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            getData.getData(arrayList, "wrong", false);
                        }
                    });
                } else {
                    getData.getData(arrayList,"download_msg", false);
                }
            } else {
                getData.getData(arrayList, "internet_connection", false);
            }
        } else {
            getData.getData(arrayList,"not_support", false);
        }
    }

    //network check
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
