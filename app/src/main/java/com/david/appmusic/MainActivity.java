package com.david.appmusic;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // below line calling method to
        // load our recycler views and search view.
        initializeAlbumsRV();
        initializePopularAlbumsRV();
        initializeTrendingAlbumsRV();
        initializeSearchView();
    }


    private void initializeSearchView() {
        // edit text for search views.
        EditText searchEdt = findViewById(R.id.idEdtSearch);
        searchEdt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // on below line calling method to search tracks.
                    searchTracks(searchEdt.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    //open search
    // tracks activity to search tracks.
    private void searchTracks(String searchQuery) {
        // display search results in search activity.
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("searchQuery", searchQuery);
        startActivity(i);
    }

    //  method is use to get token.
    private String getToken() {

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sh.getString("token", "Not Found");
    }

    @Override
    protected void onStart() {
        super.onStart();
        generateToken();
    }

    private void generateToken() {
        // on below line creating a variable for
        // url to generate access token
        String url = "https://accounts.spotify.com/api/token?grant_type=client_credentials";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        // on below line making string request to generate access token.
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                // on below line getting access token and
                // saving it to shared preferences.
                String tk = jsonObject.getString("access_token");
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("token", "Bearer " + tk);
                myEdit.apply();
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // method to handle errors.
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // on below line passing headers.
                // Make sure to add your authorization.
                headers.put("Authorization", "Basic OWFmMmFmY2FjMzU2NDA3Nzk1N2EyODViZmMyNTgzODc6NGJjZmRjMDdhYzExNDAyYWEyOTdlODI1OTU1ODBiNzg=");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        // adding request to queue.
        queue.add(request);
    }


    private void initializeAlbumsRV() {

        RecyclerView albumsRV = findViewById(R.id.idRVAlbums);
        ArrayList<AlbumRVModal> albumRVModalArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModalArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);

        String url = "https://api.spotify.com/v1/albums?ids=0lgs2Sa82lyX89nBUWyUy6,6Ad1E9vl75ZB3Ir87zwXIJ,7aYMP5hWW3v4U6VYLIzlz6,45gsxfnJ5Nt2RZp82SQenc";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //  line making json object request to parse json data.
        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");
                        // on below line adding data to array list.
                        albumRVModalArrayList.add(new AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // on below line passing headers.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(albumObjReq);
    }

    private void initializePopularAlbumsRV() {

        RecyclerView albumsRV = findViewById(R.id.idRVPopularAlbums);
        ArrayList<AlbumRVModal> albumRVModalArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModalArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);
        // creating a variable for url
        String url = "https://api.spotify.com/v1/albums?ids=4FftCsAcXXD1nFO9RFUNFO,7dqftJ3kas6D0VAdmt3k3V,1F46k12341LHQGIFlMjop4,23krijKfse53ocaki0SfA0,6gp7h46R7nNNYBlN04dEQY";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        // on below line making json object request to parse json data.
        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");

                        albumRVModalArrayList.add(new AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // on below line passing headers.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(albumObjReq);
    }

    private void initializeTrendingAlbumsRV() {

        RecyclerView albumsRV = findViewById(R.id.idRVTrendingAlbums);
        ArrayList<AlbumRVModal> albumRVModalArrayList = new ArrayList<>();
        AlbumRVAdapter albumRVAdapter = new AlbumRVAdapter(albumRVModalArrayList, this);
        albumsRV.setAdapter(albumRVAdapter);

        String url = "https://api.spotify.com/v1/albums?ids=5WTsC6KDdxp9xVQrAMIU6c,2d9BCZeAAhiZWPpbX9aPCW,06mXfvDsRZNfnsGZvX2zpb,5fY8mYgYGkyaJ9XvVOzVQA";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest albumObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray albumArray = response.getJSONArray("albums");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject albumObj = albumArray.getJSONObject(i);
                        String album_type = albumObj.getString("album_type");
                        String artistName = albumObj.getJSONArray("artists").getJSONObject(0).getString("name");
                        String external_ids = albumObj.getJSONObject("external_ids").getString("upc");
                        String external_urls = albumObj.getJSONObject("external_urls").getString("spotify");
                        String href = albumObj.getString("href");
                        String id = albumObj.getString("id");
                        String imgUrl = albumObj.getJSONArray("images").getJSONObject(1).getString("url");
                        String label = albumObj.getString("label");
                        String name = albumObj.getString("name");
                        int popularity = albumObj.getInt("popularity");
                        String release_date = albumObj.getString("release_date");
                        int total_tracks = albumObj.getInt("total_tracks");
                        String type = albumObj.getString("type");
                        //  line adding data to array list.
                        albumRVModalArrayList.add(new AlbumRVModal(album_type, artistName, external_ids, external_urls, href, id, imgUrl, label, name, popularity, release_date, total_tracks, type));
                    }
                    albumRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get data : " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // on below line passing headers.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", getToken());
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // line adding request to queue.
        queue.add(albumObjReq);
    }

}
