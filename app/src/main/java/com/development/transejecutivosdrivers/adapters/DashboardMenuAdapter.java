package com.development.transejecutivosdrivers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.DashboardMenu;

/**
 * Created by developer on 3/21/16.
 */
public class DashboardMenuAdapter extends BaseAdapter {
    private Context context;

    public DashboardMenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return DashboardMenu.ITEMS.length;
    }

    @Override
    public DashboardMenu getItem(int position) {
        return DashboardMenu.ITEMS[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item_menu, viewGroup, false);
        }

        ImageView iconImage = (ImageView) view.findViewById(R.id.icon_image);
        TextView iconName = (TextView) view.findViewById(R.id.icon_name);

        final DashboardMenu item = getItem(position);
        Glide.with(iconImage.getContext())
                .load(item.getIdDrawable())
                .into(iconImage);

        iconName.setText(item.getName());

        return view;
    }
}
