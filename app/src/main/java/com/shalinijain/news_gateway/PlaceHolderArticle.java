package com.shalinijain.news_gateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin on 23-04-2018.
 */

public class PlaceHolderArticle extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //display the number of fragments created
    private static final String ARG_SECTION_NUMBER = "section_number";
    private  static final String ARG_ARTICLE_DATA="data article";

    public PlaceHolderArticle() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceHolderArticle newInstance(int sectionNumber,DataofArticle data_article) {
        PlaceHolderArticle fragment = new PlaceHolderArticle();

        //Bundle stores the fragment data
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_ARTICLE_DATA,data_article);

        //to pass the dat from fragment to fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DataofArticle doa=new DataofArticle();
        doa=(DataofArticle)getArguments().getSerializable(ARG_ARTICLE_DATA);
        int nummber=getArguments().getInt(ARG_SECTION_NUMBER);
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        TextView pageNum = rootView.findViewById(R.id.tv_pagenum);
        pageNum.setText(nummber);
        TextView headline = rootView.findViewById(R.id.tv_headline);
        final String url=doa.getWeburl();
        if(headline!=null)
            headline.setText(doa.getTitle());
        headline.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        TextView date_tv = rootView.findViewById(R.id.tv_date);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss");
        Date date_format = null;
        String date=doa.getPublisheddate();
        if(date!=null && !date.contentEquals("null")) {
            try {
                date_format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(date);
                date= new SimpleDateFormat("MMM dd,yyyy hh:mm").format(date_format);

                date_tv.setText(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        TextView author = rootView.findViewById(R.id.tv_author);
        String auth=doa.getAuthor();
        if(doa.getAuthor()!=null && !doa.getAuthor().contentEquals("null")) {
            author.setText(auth);
        }
        TextView text = rootView.findViewById(R.id.tv_text);
        text.setText(doa.getDescription());
        ImageView image =rootView.findViewById(R.id.tv_image);
        final String imageURL = doa.getUrlToimage();
        //Picasso.with(getActivity().getApplicationContext()).load(imageURL).fit().into(image);
        // final String url=news.getExtraURL();
/*
        image.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });*/
        return rootView;
    }
}

