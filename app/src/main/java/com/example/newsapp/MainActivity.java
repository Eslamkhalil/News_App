package com.example.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import com.example.newsapp.adpter.News;
import com.example.newsapp.adpter.NewsAdapter;
import com.example.newsapp.adpter.NewsLoader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<List<News>> {

  private TextView mEmptyStateTextView;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private String mUrlRequestGuardianApi = "https://content.guardianapis.com/search?q=trump&order-date=published&show-section=true&show-fields=headline,thumbnail&show-references=author&show-tags=contributor&page=10&page-size=20&api-key=test";
  private SearchView msearchView;
  private NewsAdapter adapter;
  ProgressBar simpleProgressBar;
  public static final String LOG_TAG = MainActivity.class.getName();
  private static final int News_LOADER_ID = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mEmptyStateTextView = findViewById(R.id.Empty_text);
    simpleProgressBar = findViewById(R.id.progressBar);
    mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

    if (isNetworkAvailable(this)) {

      initRyec(new ArrayList<News>());

      LoaderManager loaderManager = getLoaderManager();
      Log.i(LOG_TAG, "Test: initLoader Called ");
      loaderManager.initLoader(News_LOADER_ID, null, this);


    } else {

      simpleProgressBar.setVisibility(View.GONE);
      mEmptyStateTextView.setVisibility(View.VISIBLE);
      Toast.makeText(this, "There's No Internet Connections", Toast.LENGTH_SHORT).show();
    }
    on_Refresh();

  }

  @Override
  protected void onResume() {
    super.onResume();
    restartLoader();
  }

  private void restartLoader() {
    if (isNetworkAvailable(MainActivity.this)) {
      simpleProgressBar.setVisibility(View.VISIBLE);
      mEmptyStateTextView.setVisibility(View.GONE);
      getLoaderManager().restartLoader(News_LOADER_ID, null, this);
    }
  }

  private void on_Refresh() {
    mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        restartLoader();
        mSwipeRefreshLayout.setRefreshing(false);

      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);

    MenuItem searchItem = menu.findItem(R.id.search_bar);
    msearchView = (SearchView) searchItem.getActionView();

    msearchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String s) {
        if (isNetworkAvailable(MainActivity.this) && s.length() > 2) {
          mEmptyStateTextView.setVisibility(View.GONE);
          updateQueryUlr(s);
          restartLoader();

        } else {
          mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
        return false;
      }

      @Override
      public boolean onQueryTextChange(String s) {

        return false;
      }
    });
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    return super.onOptionsItemSelected(item);
  }

  private void updateQueryUlr(String searchValue) {
    mUrlRequestGuardianApi = "";
    if (searchValue.contains(" ")) {
      searchValue = searchValue.replace(" ", "+");
    }
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("https://content.guardianapis.com/search?q=").
        append(searchValue).
        append(
            "&order-date=published&show-section=true&show-fields=headline,thumbnail&show-references=author&show-tags=contributor&page=10&page-size=20&api-key=test");
    mUrlRequestGuardianApi = stringBuilder.toString();
    Log.i("Url from updateQuery", mUrlRequestGuardianApi);
  }

  private void initRyec(List<News> encaps) {

    RecyclerView recyclerView = findViewById(R.id.list);
    mEmptyStateTextView.setVisibility(View.GONE);
    simpleProgressBar.setVisibility(View.VISIBLE);

    if (encaps == null || encaps.size() < 1) {
      recyclerView.setVisibility(View.GONE);
      mEmptyStateTextView.setVisibility(View.VISIBLE);

    } else {
      mEmptyStateTextView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
      simpleProgressBar.setVisibility(View.GONE);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      adapter = new NewsAdapter(encaps, this);
      recyclerView.setAdapter(adapter);
    }
  }

  public static boolean isNetworkAvailable(Context context) {

    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    return activeNetwork != null &&
        activeNetwork.isConnectedOrConnecting();

  }

  @Override
  public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
//    Log.i("There is no instance", ": Created new one loader at the beginning!");
    mEmptyStateTextView.setVisibility(View.GONE);
    Log.e("onCreateLoader Url", mUrlRequestGuardianApi);
    return new NewsLoader(this, mUrlRequestGuardianApi);
  }

  @Override
  public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

    if (null != news) {
      if (news.isEmpty()) {
        Log.e(LOG_TAG, "Searched data " + String.valueOf(news.size()));
        simpleProgressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setVisibility(View.VISIBLE);
        initRyec(new ArrayList<News>());

      } else {
        initRyec(news);

      }
    }

  }

  @Override
  public void onLoaderReset(Loader<List<News>> loader) {
    loader.reset();


  }
}
