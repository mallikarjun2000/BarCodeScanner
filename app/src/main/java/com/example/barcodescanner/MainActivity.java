package com.example.barcodescanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    public static String GOOGLE_BOOKS_URL="https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private TextView textView;
    private TextView NameOfBook;
    private FetchBooks fetchBooks;
    private ProgressDialog mProgressDialog;
    private TextView AuthorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#FF8E24AA"));

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF8E24AA")));

        Button btn = findViewById(R.id.button);
        NameOfBook = findViewById(R.id.NameOFBook);
        mProgressDialog = new ProgressDialog(this);
        textView = findViewById(R.id.textview);
        fetchBooks = new FetchBooks();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarCode(v);
            }
        });

    }
    public void scanBarCode(View v){
        Intent i = new Intent(MainActivity.this,Cam_Scanner.class);
        startActivityForResult(i,0);
        startFetching(GOOGLE_BOOKS_URL);
    }

    public void startFetching(String url){
        fetchBooks.execute(url);
        mProgressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0)
        {
            if(resultCode == CommonStatusCodes.SUCCESS)
            {
                if(data!=null)
                {
                    Barcode barcode =data.getParcelableExtra("barcode");
                    boolean validUrl = URLUtil.isValidUrl( "your.uri" );
                    if(validUrl)
                    {

                    }
                    textView.setText("ISBN Code : "+barcode.rawValue);
                    String temp = barcode.rawValue;
                    GOOGLE_BOOKS_URL = GOOGLE_BOOKS_URL+temp;
                    mProgressDialog.show();
                }
                else
                {
                    textView.setText("No Data Accuired");
                }
            }
        }
    }

    public class FetchBooks extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... Url) {
            String result = Utils.createjson(GOOGLE_BOOKS_URL);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                UpdateUI(result);
                mProgressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void UpdateUI(String result)throws JSONException {
        JSONObject baseObject = new JSONObject(result);
        JSONArray items  = baseObject.getJSONArray("items");
        JSONObject item = items.getJSONObject(0);
        JSONObject volumeInfo = item.getJSONObject("volumeInfo");
        String title = volumeInfo.getString("title");
        NameOfBook.setText(title);
        JSONArray authors = volumeInfo.getJSONArray("authors");
        AuthorView = findViewById(R.id.author);
        String nameOfAuthor="";
        for(int i=0;i<authors.length();i++)
        {
            nameOfAuthor = nameOfAuthor + authors.getString(i) + " ";
        }
        AuthorView.setText(nameOfAuthor);
        String publishedYear = volumeInfo.getString("publishedDate");
        TextView yearView = findViewById(R.id.YearPublished);
        yearView.setText(publishedYear);
        String Description = volumeInfo.getString("description");
        TextView descriptionView = findViewById(R.id.descriptionofBook);
        descriptionView.setText(Description);
        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
        String imageurl = imageLinks.getString("thumbnail");
        ImageView imageView = findViewById(R.id.imageOfBook);
        /*URI uri=null;
        try {
            uri = new URI(imageurl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Log.i("Image",imageurl);*/
        Picasso.with(MainActivity.this).load(imageurl).placeholder(R.mipmap.ic_launcher).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        //Picasso.with(MainActivity.this).load(imageurl+".jpg").fit().into(imageView);*/

        if(title == "" || nameOfAuthor == "" || publishedYear == "" || Description == "")
        {
            Log.i("CASEINFO :","CASE PASSED");
            mProgressDialog.dismiss();
            fetchBooks.cancel(false);
            return;
        }
        return;
    }
}
