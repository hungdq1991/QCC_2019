package com.speedata.uhf.main.activity.Inventory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.speedata.uhf.R;
import com.speedata.uhf.main.model.MachineryModel;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.RecyclerViewAdapter> {

    private static final int ASSET_NOT_FOUND_YET = 0;
    private static final int ASSET_FOUND = 1;
    private static final int ASSET_ANOTHER_FACTORY = 2;
    private Context context;
    private List<MachineryModel> inventoryList, inventoryListFull, inventoryListFiltered;
    private ItemClickListener itemClickListener;

    public InventoryAdapter(Context context, List<MachineryModel> inventoryLists, ItemClickListener itemClickListeners) {
        this.context = context;
        this.inventoryList = inventoryLists;
        this.itemClickListener = itemClickListeners;

        this.inventoryListFull = new ArrayList<>();
        this.inventoryListFull.addAll( inventoryLists );

        this.inventoryListFiltered = new ArrayList<>();
        this.inventoryListFiltered.addAll( inventoryLists );
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.activity_inventory_row,
                parent, false );
        return new RecyclerViewAdapter( view, itemClickListener );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        MachineryModel model = inventoryList.get( position );
        holder.tv_department_code.setText( model.getDepartment_code() );
        holder.tv_RFID_code.setText( model.getRFID_code() );
        holder.tv_department_asset_name.setText( model.getDepartment_asset_name() );

        if (model.getStatus() == ASSET_NOT_FOUND_YET) {
            holder.mIconStatus.setImageResource( R.drawable.icon_x );
            holder.card_item.setBackgroundColor( Color.WHITE );
        } else {
            holder.mIconStatus.setImageResource( R.drawable.icon_ok );
            if (model.getStatus().equals( ASSET_ANOTHER_FACTORY )) {
                holder.card_item.setBackgroundColor( Color.YELLOW );
            } else {
                holder.card_item.setBackgroundColor( Color.WHITE );
            }
        }
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public int updateRecyclerView(String epc, String department_code) {
        int count = 0;
        for (MachineryModel row : inventoryList) {
            if (row.getRFID_code().toLowerCase().contains( epc.toLowerCase() )) {
                if (row.getStatus() == ASSET_NOT_FOUND_YET && row.getDepartment_code() == department_code) {
                    inventoryList.get( count ).setStatus( ASSET_FOUND );
                    notifyItemChanged( count );
                    return count;
                } else if (row.getStatus() == ASSET_NOT_FOUND_YET
                        && row.getDepartment_code() != department_code) {
                    inventoryList.get( count ).setStatus( ASSET_ANOTHER_FACTORY );
                    notifyItemChanged( count );
                    return count;
                }
            }
            count++;
        }
        return -1;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    inventoryListFiltered = inventoryList;
                } else {
                    List<MachineryModel> filteredList = new ArrayList<>();
                    for (MachineryModel row : inventoryList) {
                        // Name match condition. This might differ depending on your
                        // requirement here we are looking for name or phone number match
                        if (row.getDepartment_code().toLowerCase().contains( charString.toLowerCase() )
                                || (row.getStatus() == ASSET_ANOTHER_FACTORY)) {
                            filteredList.add( row );
                        }
                    }

                    inventoryListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = inventoryListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                inventoryList.clear();
                inventoryList = (List<MachineryModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

//    public String[] Split_String_To_Array(int length, String string_Input)
//    {
//        return Iterables.toArray(
//            Splitter.fixedLength(length)
//                    .split(string_Input),
//            String.class
//        );
//    }

    public class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIconStatus;
        private TextView tv_department_code, tv_RFID_code, tv_department_asset_name;
        private CardView card_item;
        private ItemClickListener itemClickListener;

        RecyclerViewAdapter(@NonNull View itemView, ItemClickListener itemClickListener) {
            super( itemView );

            mIconStatus = itemView.findViewById( R.id.image_icon_status );
            tv_department_code = itemView.findViewById( R.id.department_code );
            tv_RFID_code = itemView.findViewById( R.id.rfid_code );
            tv_department_asset_name = itemView.findViewById( R.id.department_asset_name );
            card_item = itemView.findViewById( R.id.card_item );

            this.itemClickListener = itemClickListener;
            card_item.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick( v, getAdapterPosition() );
        }
    }
}