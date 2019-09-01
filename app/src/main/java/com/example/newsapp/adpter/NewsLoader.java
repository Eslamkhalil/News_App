package com.example.newsapp.adpter;


import android.content.AsyncTaskLoader;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newsapp.QueryUtils;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
 private String mUrl;

  public NewsLoader(@NonNull Context context , String url) {
    super(context);
    this.mUrl=url;
  }

  @Override
  protected void onStartLoading() {
    forceLoad();
  }

  @Nullable
  @Override
  public List<News> loadInBackground() {
    if(mUrl == null){

      return null;
    }
    return QueryUtils.fetchNewsData(mUrl);
  }
}
