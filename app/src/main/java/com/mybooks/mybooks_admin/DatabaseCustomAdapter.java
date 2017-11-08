package com.mybooks.mybooks_admin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DatabaseCustomAdapter extends RecyclerView.Adapter<DatabaseCustomAdapter.MyViewHolder> {

    List<String> keyList;
    List<String> valueList;

    public DatabaseCustomAdapter(List<String> keyList, List<String> valueList){
        this.keyList = keyList;
        this.valueList = valueList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.database_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.key.setText(keyList.get(position));
        holder.value.setText(valueList.get(position));
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView key;
        TextView value;

        public MyViewHolder(View itemView) {
            super(itemView);

            key = (TextView) itemView.findViewById(R.id.key);
            key.setText("");
            value = (TextView) itemView.findViewById(R.id.value);
            value.setText("");
        }
    }
}
