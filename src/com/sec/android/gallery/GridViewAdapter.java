package com.sec.android.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Ganna Pliskovska(g.pliskovska@samsung.com)
 */
class GridViewAdapter extends ArrayAdapter<ImageItem> {
    final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();

    public GridViewAdapter(Context context) {
        super(context, 0);
    }

    public void addPhoto(ImageItem imageItem) {
        imageItems.add(imageItem);
    }

    public int getCount() {
        return imageItems.size();
    }

    public ImageItem getItem(int position) {
        return imageItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.gridview_item, parent, false);
            holder = new ViewHolder();
            holder.imageName = (TextView) row.findViewById(R.id.image_name);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
            row.forceLayout();
        }

        ImageItem item = getItem(position);
        holder.imageName.setText(item.getName());
        holder.image.setImageBitmap(item.getImage());
        return row;
    }

    static class ViewHolder {
        TextView imageName;
        ImageView image;
    }
}