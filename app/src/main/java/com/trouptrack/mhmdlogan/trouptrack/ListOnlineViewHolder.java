package com.trouptrack.mhmdlogan.trouptrack;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by MhmdLoGaN on 06/03/2018.
 */

public class ListOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtEmail;
    ItemClickListenener itemClickListenener;
    public ListOnlineViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        txtEmail = (TextView)itemView.findViewById(R.id.txtMail);

    }

    public void setItemClickListenener(ItemClickListenener itemClickListener) {
        this.itemClickListenener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListenener.onClick(v,getAdapterPosition());
    }
}
