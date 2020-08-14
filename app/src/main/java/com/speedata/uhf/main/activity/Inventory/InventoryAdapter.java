package com.speedata.uhf.main.activity.Inventory;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.speedata.uhf.R;
import com.speedata.uhf.main.model.MachineryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.RecyclerViewAdapter> {

    private static final int ASSET_NOT_FOUND_YET = 0;
    private static final int ASSET_FOUND = 1;
    private static final int ASSET_ANOTHER_FACTORY = 2;

    private Context context;
    private List<MachineryModel> inventoryList;
    private List<MachineryModel> inventoryListFull;
    private List<MachineryModel> inventoryListFiltered;
    private ItemClickListener itemClickListener;

    public InventoryAdapter(Context context, List<MachineryModel> inventoryLists, ItemClickListener itemClickListeners) {
        this.context = context;
        this.itemClickListener = itemClickListeners;

        //List used currently
        this.inventoryList = inventoryLists;

        //List data Full, no Filter
        this.inventoryListFull = new ArrayList<>();
        this.inventoryListFull.addAll( inventoryLists );

        //List data Filter
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

    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        if (!hex.isEmpty()) {
            char[] hexData = hex.toCharArray();
            for (int count = 0; count < hexData.length - 1; count += 2) {
                int firstDigit = Character.digit( hexData[count], 16 );
                int lastDigit = Character.digit( hexData[count + 1], 16 );
                int decimal = firstDigit * 16 + lastDigit;
                sb.append( (char) decimal );
            }
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        MachineryModel model = inventoryList.get( position );
        holder.tv_department_code.setText( model.getDepartment_code() );
        holder.tv_RFID_code.setText( model.getRFID_code() );
        holder.tv_department_asset_name.setText( model.getDepartment_asset_name() );
        holder.tv_ordinal_numbers.setText( String.valueOf( model.getOrdinal_numbers() ) );

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

    //Update status Assets
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int updateRecyclerView(String epc, String department_code) {
        int count = 0;
        int max_ordinal_number = 0;
        String RFID_Code = "";

        /**
         * Cut 2 character start and end between HEX code
         * HEX: 5354 => TEXT: "ST"
         * HEX: 454E => TEXT: "EN"
         */
        Pattern pattern = Pattern.compile( "5354(.*)454E" );
        Matcher matcher = pattern.matcher( epc );
        if (matcher.find()) {
            RFID_Code = hexToString( matcher.group( 1 ) );

            max_ordinal_number = Collections.max( inventoryList,
                    Comparator.comparingInt( MachineryModel::getOrdinal_numbers ) ).getOrdinal_numbers();

            for (MachineryModel row : inventoryListFull) {
                if (row.getAsset_code().toLowerCase().contains( RFID_Code.toLowerCase() )) {
                    if (row.getStatus() == ASSET_NOT_FOUND_YET && row.getDepartment_code() == department_code) {
                        inventoryListFull.get( count ).setOrdinal_numbers( max_ordinal_number + 1 );
                        inventoryListFull.get( count ).setStatus( ASSET_FOUND );
                        return count;
                    } else if (row.getStatus() == ASSET_NOT_FOUND_YET && row.getDepartment_code() != department_code) {
                        inventoryListFull.get( count ).setOrdinal_numbers( max_ordinal_number + 1 );
                        inventoryListFull.get( count ).setStatus( ASSET_ANOTHER_FACTORY );
                        return count;
                    }
                }
                count++;
            }
        }
        return -1;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    inventoryListFiltered = inventoryListFull;
                } else {
                    List<MachineryModel> filteredList = new ArrayList<>();
                    for (MachineryModel row : inventoryListFull) {
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

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                inventoryList = (List<MachineryModel>) filterResults.values;
                Collections.sort( inventoryList, Comparator.comparingInt( MachineryModel::getOrdinal_numbers ).reversed() );
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIconStatus;
        private TextView tv_department_code, tv_RFID_code, tv_department_asset_name, tv_ordinal_numbers;
        private CardView card_item;
        private ItemClickListener itemClickListener;

        RecyclerViewAdapter(@NonNull View itemView, ItemClickListener itemClickListener) {
            super( itemView );

            mIconStatus = itemView.findViewById( R.id.image_icon_status );
            tv_department_code = itemView.findViewById( R.id.department_code );
            tv_RFID_code = itemView.findViewById( R.id.rfid_code );
            tv_department_asset_name = itemView.findViewById( R.id.department_asset_name );
            tv_ordinal_numbers = itemView.findViewById( R.id.ordinal_numbers );
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