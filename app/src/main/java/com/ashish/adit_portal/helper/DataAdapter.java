package com.ashish.adit_portal.helper;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.app.AppConfig;
import com.ashish.adit_portal.app.AppController;

import java.util.ArrayList;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private Listener listener;
    private LongClickListener longClickListener;
    public ArrayList<Notice> countries;
    public ArrayList<Notice> selectedItems;
    public interface LongClickListener{
        boolean onLongClick(View v,int position);
    }
    public interface Listener {
         void onClick(int position,View v);
    }

    public DataAdapter(ArrayList<Notice> countries,ArrayList<Notice> selectedItems, Context context) {
        this.countries = countries;
        this.selectedItems=selectedItems;
        this.context = context;
    }
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        if(selectedItems.contains(countries.get(i))){
            viewHolder.cardViewbck.setBackgroundColor(ContextCompat.getColor(context,R.color.card_back));
        }else{
            viewHolder.cardViewbck.setBackgroundColor(ContextCompat.getColor(context,R.color.background));
        }
        final Notice notice = countries.get(i);
        String URL = notice.getUrl();
        final ImageButton download=viewHolder.download;
        final ProgressBar progress=viewHolder.progress;
        if(notice.getvisibilitydownload()==View.INVISIBLE){
            download.setVisibility(View.INVISIBLE);
        }else{
            download.setVisibility(View.VISIBLE);
        }
        if(notice.getVisibilityprogress()==View.INVISIBLE){
            progress.setVisibility(View.INVISIBLE);
        }else{
            progress.setVisibility(View.VISIBLE);
        }
        if(ImageStorage.checkifImageExists(URL)){
            download.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)viewHolder.tv_country.getLayoutParams();
            layoutParams.setMargins(getPixelValue(5),0,getPixelValue(5),0);
            viewHolder.tv_country.setLayoutParams(new RelativeLayout.LayoutParams(layoutParams));
        }else{
            download.setVisibility(View.VISIBLE);
        }
        View cardView = viewHolder.itemView;
        viewHolder.tv_country.setText(notice.getTitle());

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longClickListener!=null){
                    longClickListener.onLongClick(v,viewHolder.getAdapterPosition());
                }
                return true;
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(viewHolder.getAdapterPosition(),v);
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(AppController.isInternetAvailable(context)) {
                    download.setVisibility(View.INVISIBLE);
                    notice.setVisibilitydownload(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    notice.setVisibilityprogress(View.VISIBLE);
                    final String URL = notice.getUrl();
                    ImageRequest request = new ImageRequest(AppConfig.URL_IMAGE + URL,
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    try {
                                        ImageStorage.saveToSdCard(bitmap, URL, context);

                                        progress.setVisibility(View.INVISIBLE);
                                        notice.setVisibilityprogress(View.INVISIBLE);
                                        download.setVisibility(View.INVISIBLE);
                                        notice.setVisibilitydownload(View.INVISIBLE);
                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.tv_country.getLayoutParams();
                                        layoutParams.setMargins(getPixelValue(5), 0, getPixelValue(5), 0);
                                        viewHolder.tv_country.setLayoutParams(new RelativeLayout.LayoutParams(layoutParams));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        progress.setVisibility(View.INVISIBLE);
                                        notice.setVisibilityprogress(View.INVISIBLE);
                                        download.setVisibility(View.VISIBLE);
                                        notice.setVisibilitydownload(View.VISIBLE);
                                    }
                                }
                            }, 0, 0, null,
                            new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, "Something is wrong there might be problem with your internet connection", Toast.LENGTH_SHORT).show();
                                    progress.setVisibility(View.INVISIBLE);
                                    notice.setVisibilityprogress(View.INVISIBLE);
                                    download.setVisibility(View.VISIBLE);
                                    notice.setVisibilitydownload(View.VISIBLE);
                                }
                            });
                    AppController.getInstance().addToRequestQueue(request, "ImageRequest" + viewHolder.getAdapterPosition());
                }else{
                    Toast.makeText(context, "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.INVISIBLE);
                notice.setVisibilityprogress(View.INVISIBLE);
                AppController.getInstance().cancelPendingRequests("ImageRequest"+viewHolder.getAdapterPosition());
                download.setVisibility(View.VISIBLE);
                notice.setVisibilitydownload(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != countries ? countries.size() : 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_country;
        private ImageButton download;
        private ProgressBar progress;
        private RelativeLayout cardViewbck;
        ViewHolder(View view) {
            super(view);
            download = (ImageButton) view.findViewById(R.id.download);
            tv_country = (TextView) view.findViewById(R.id.tv_country);
            progress = (ProgressBar) view.findViewById(R.id.progress);
            cardViewbck=(RelativeLayout) view.findViewById(R.id.cardrelative);
        }
    }
    public void setListener(Listener listener) {
        this.listener = listener;
    }
    public void setOnLongClickListenre(LongClickListener listener){
        this.longClickListener=listener;
    }
    private int getPixelValue(int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }
}