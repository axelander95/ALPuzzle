package com.kimerasoft_ec.alpuzzle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;

public class PuzzleItemAdapter extends BaseAdapter {
    private List<PuzzleItem> items;
    private Context context;
    public PuzzleItemAdapter(Context context, List<PuzzleItem> items)
    {
        this.context = context;
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    private class ImageHolder
    {
        private ImageView ivPart;

        public ImageHolder(ImageView ivPart) {
            this.ivPart = ivPart;
        }

        public ImageView getIvPart() {
            return ivPart;
        }

        public void setIvPart(ImageView ivPart) {
            this.ivPart = ivPart;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.image_item, null);
        ImageView ivPart = (ImageView) convertView.findViewById(R.id.ivPart);
        ImageHolder holder = new ImageHolder(ivPart);
        holder.getIvPart().setImageBitmap(items.get(position).getImage());
        return convertView;
    }
}
