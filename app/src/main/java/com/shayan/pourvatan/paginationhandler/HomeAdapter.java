package com.shayan.pourvatan.paginationhandler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shayanpourvatan on 4/17/17.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    Context context;
    LayoutInflater layoutInflater;
    List<Data> data;


    public HomeAdapter(Context context, List<Data> datas) {
        this.context = context;
        this.data = datas;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Data> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeViewHolder(layoutInflater.inflate(R.layout.row_home_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.onBind();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;

        public HomeViewHolder(View itemView) {
            super(itemView);

            titleTV = (TextView) itemView.findViewById(R.id.row_title);
        }

        public void onBind() {
            titleTV.setText(data.get(getAdapterPosition()).toString());

        }
    }
}
