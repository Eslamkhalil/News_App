package com.example.newsapp.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapp.R;
import com.squareup.picasso.Picasso;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.View_holder> {

 private List<News> list_news;


  private Context context;

  public NewsAdapter(List<News> list_news, Context context) {
    this.list_news = list_news;
    this.context = context;
  }

  @NonNull
  @Override
  public View_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new View_holder(
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull View_holder holder, int position) {
    News news_id = list_news.get(position);
    holder.titleNewsTextView.setText(news_id.getmTitle());
    holder.authorNewsTextView.setText(news_id.getmAuthor());
    holder.sectionNewsTextView.setText(news_id.getmSection());
    holder.publicationDateTextView.setText(news_id.getmDate());

    if (news_id.getmThumbUrl().isEmpty() || news_id.getmThumbUrl().length()<1){

      holder.thumbnailImageView.setImageResource(R.drawable.ic_launcher_background);
    }else{
      Picasso.with(context).load(news_id.getmThumbUrl()).into(holder.thumbnailImageView);
    }




  }

  @Override
  public int getItemCount() {
    return list_news.size();
  }





  public class View_holder extends RecyclerView.ViewHolder{

    TextView titleNewsTextView,authorNewsTextView,sectionNewsTextView,publicationDateTextView;
    ImageView thumbnailImageView;




    private View_holder(@NonNull View itemView) {
      super(itemView);
       titleNewsTextView = itemView.findViewById(R.id.news_title);
       authorNewsTextView =  itemView.findViewById(R.id.author_news);
       thumbnailImageView =  itemView.findViewById(R.id.thumbnail_image);
       sectionNewsTextView = itemView.findViewById(R.id.section_type);
       publicationDateTextView =  itemView.findViewById(R.id.publicationDate);
    }
  }
}
