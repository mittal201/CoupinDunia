package com.example.anshul.jsonparsing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Anshul on 04/12/15.
 */
public class CustomAdapter extends BaseAdapter {
    private static LayoutInflater inflator = null;
    ArrayList<RestaurantGetterSetter> listRestaurants;
    Activity callingActivity;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    NetworkImageView mNetworkImageView;

    @Override
    public int getCount() {
        return listRestaurants.size();
    }

    public CustomAdapter(ArrayList<RestaurantGetterSetter> listRestaurants, Activity callingActivity) {
        this.listRestaurants = listRestaurants;
        this.callingActivity = callingActivity;
        inflator = (LayoutInflater) callingActivity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getItem(int position) {
        return listRestaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflator.inflate(R.layout.list_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.brandName = (TextView) convertView.findViewById(R.id.brandName);
            //  viewHolder.imageView=(ImageView)convertView.findViewById(R.id.imageView);
            viewHolder.networkImageView = (NetworkImageView) convertView.findViewById(R.id.imageView);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.neighbourhood = (TextView) convertView.findViewById(R.id.neighbourhood);
            viewHolder.offers = (TextView) convertView.findViewById(R.id.offers);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (listRestaurants.size() > 0) {
            mRequestQueue = Volley.newRequestQueue(callingActivity);
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });


            RestaurantGetterSetter objectRestaurant;
            objectRestaurant = listRestaurants.get(position);
            StringBuilder builder = new StringBuilder();
            for (String s : objectRestaurant.getName()) {
                if (s != null) {
                    builder.append("*");
                    builder.append(s);
                    builder.append("  ");
                }
            }
            viewHolder.name.setText(builder);
            // viewHolder.name.setText(Arrays.toString(objectRestaurant.getName()).replaceAll("\\[|\\]|null", ""));
            viewHolder.brandName.setText(objectRestaurant.getBrandName());
            Log.e("sizenotzero", objectRestaurant.getBrandName());

            viewHolder.networkImageView.setImageUrl(objectRestaurant.getUrl(), mImageLoader);
            viewHolder.distance.setText(" " + objectRestaurant.getDistance() + " m ");
            viewHolder.neighbourhood.setText(objectRestaurant.getNeighbouhood());
            viewHolder.offers.setText(objectRestaurant.getOffers() + " offers");


        }
        return convertView;
    }

    public class ViewHolder {
        TextView name;
        TextView brandName;
        NetworkImageView networkImageView;
        TextView distance;
        TextView neighbourhood;
        TextView offers;
    }
}
