/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.datafrominternet.utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;
    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicatorProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);
        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);

        final int ACTION_SEARCH = R.id.action_search;

        switch(item.getItemId())
        {
            case ACTION_SEARCH :
                makeGithubSearchQuery();
                return true;
        }
        return false;
    }

    class GithubQueryTask extends AsyncTask<URL, Void, String> implements
            com.example.android.datafrominternet.GithubQueryTask {

        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(final URL ... searchUrls) {
            try {
                // query results response
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(searchUrls[0]);
                return jsonResponse;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String jsonStr) {
            mLoadingIndicatorProgressBar.setVisibility(View.INVISIBLE);

            if(jsonStr != null && !jsonStr.isEmpty()) {
                mSearchResultsTextView.setText(jsonStr);
                showJsonDataView();
            }
            else
                showErrorMessage();
        }
    }

    private void makeGithubSearchQuery()
    {
        String searchQuery = mSearchBoxEditText.getText().toString();

        // build the query url
        final URL searchUrl = NetworkUtils.buildUrl(searchQuery);
        mUrlDisplayTextView.setText(searchUrl.toString());

        // query in background thread.
        new GithubQueryTask().execute(searchUrl);
    }

    private void showJsonDataView()
    {
        mSearchResultsTextView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage()
    {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
    }
}
