package com.speedata.uhf.main.activity.Department;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.speedata.uhf.R;
import com.speedata.uhf.main.model.DepartmentModel;

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.RecyclerViewAdapter> {

    Context context;
    List<DepartmentModel> departmentList;
    ItemClickListener itemClickListener;

    public DepartmentAdapter(Context context, List<DepartmentModel> departmentList, ItemClickListener itemClickListener) {
        this.context = context;
        this.departmentList = departmentList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.activity_department_row,
                parent, false );
        return new RecyclerViewAdapter( view, itemClickListener );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        DepartmentModel model = departmentList.get( position );
        holder.tv_department_name1.setText( model.getDepartment_name1() );
        holder.tv_department_name2.setText( model.getDepartment_name2() );
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_department_name1, tv_department_name2;
        CardView card_item;
        ItemClickListener itemClickListener;

        RecyclerViewAdapter(@NonNull View itemView, ItemClickListener itemClickListener) {
            super( itemView );
            tv_department_name1 = itemView.findViewById( R.id.department_name1 );
            tv_department_name2 = itemView.findViewById( R.id.department_name2 );
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
