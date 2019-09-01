package com.example.newsapp.adpter;

public class News {

  private String mTitle;
  private  String mAuthor;
  private String mDate;
  private String mNewsUrl;
  private String mThumbUrl;
  private  String mSection;


  public News(String mTitle, String mAuthor, String mDate, String mNewsUrl,
      String mThumbUrl, String mSection) {
    this.mTitle = mTitle;
    this.mAuthor = mAuthor;
    this.mDate = mDate;
    this.mNewsUrl = mNewsUrl;
    this.mThumbUrl = mThumbUrl;
    this.mSection = mSection;
  }

  public String getmTitle() {
    return mTitle;
  }

  public String getmAuthor() {
    return mAuthor;
  }

  public String getmDate() {
    return mDate;
  }

  public String getmNewsUrl() {
    return mNewsUrl;
  }

  public String getmThumbUrl() {
    return mThumbUrl;
  }

  public String getmSection() {
    return mSection;
  }
}
