package com.speedata.uhf.main.activity.History;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.speedata.uhf.R;
import com.speedata.uhf.main.model.ResultInventoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.RecyclerViewAdapter> {
    Context context;
    List<ResultInventoryModel> resultModelList;
    ItemClickListener itemClickListener;

    public HistoryAdapter(Context context,
                          List<ResultInventoryModel> resultModels,
                          ItemClickListener itemClickListener) {
        this.context = context;
        this.resultModelList = resultModels;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_history_row, parent, false);
        return new RecyclerViewAdapter(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        ResultInventoryModel model = resultModelList.get( position );
        holder.tv_inventory_date.setText( model.getInventory_date() );
        holder.tv_inventory_department.setText( model.getInventory_department() );
        holder.tv_total.setText( model.getFind() + " / " + model.getTotal() );
    }

    @Override
    public int getItemCount() {
        return resultModelList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_inventory_date, tv_inventory_department, tv_total;
        CardView card_item;
        ItemClickListener itemClickListeners;

        public RecyclerViewAdapter(@NonNull View itemView, ItemClickListener itemClickListener) {
            super( itemView );
            tv_inventory_date = itemView.findViewById( R.id.date_inventory );
            tv_inventory_department = itemView.findViewById( R.id.inventory_department );
            tv_total = itemView.findViewById( R.id.total );
            card_item = itemView.findViewById( R.id.card_item );

            this.itemClickListeners = itemClickListener;
            card_item.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick( v, getAdapterPosition() );
        }
    }
}
