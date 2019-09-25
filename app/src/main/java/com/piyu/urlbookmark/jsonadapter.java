package com.piyu.urlbookmark;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class jsonadapter extends RecyclerView.Adapter<jsonadapter.ViewHolder> {

  private Context context;
  private List<bookmarkedUrl> listurl;
    boolean urlformat;

    public jsonadapter(Context context, List<bookmarkedUrl> listurl) {
        this.context = context;
        this.listurl = listurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.urlcontainer,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
          final bookmarkedUrl data= listurl.get(i);
          viewHolder.folder_name.setText(listurl.get(i).getFoldername());
          viewHolder.url.setText(listurl.get(i).getUrl());
          viewHolder.gotourl.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  String link=data.getUrl();
                  if(link.matches("https")||link.matches("http"))
                  {
                      urlformat=true;
                  }
                  if(!urlformat)
                  {
                      link="https://"+data.getUrl();
                  }
                  Intent intent = new Intent(Intent.ACTION_VIEW);
                  intent.setData(Uri.parse(link));
                  context.startActivity(intent);



              }
          });
    }

    @Override
    public int getItemCount() {
        return listurl.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView url;
        public TextView folder_name;
        public Button gotourl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folder_name=(TextView) itemView.findViewById(R.id.folder_namecontain);
            url=(TextView)itemView.findViewById(R.id.urlcontain);
            gotourl =(Button) itemView.findViewById(R.id.gotourl);

        }
    }
}
