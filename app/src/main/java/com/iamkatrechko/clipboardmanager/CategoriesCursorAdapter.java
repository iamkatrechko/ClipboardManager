package com.iamkatrechko.clipboardmanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamkatrechko.clipboardmanager.data.ClipboardDatabaseHelper;

public class CategoriesCursorAdapter extends RecyclerView.Adapter<CategoriesCursorAdapter.ViewHolder>{
    private Context aContext;
    private ClipboardDatabaseHelper.CategoryCursor aCategoryCursor;

    public MyClickListener mMyClickListener;

    public interface MyClickListener {
        void onEditClick(long categoryId);
        void onDeleteClick(long categotyId);
    }

    public CategoriesCursorAdapter(Context context, MyClickListener myClickListener){
        aContext = context;
        mMyClickListener = myClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new ItemDivider(aContext));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        aCategoryCursor.moveToPosition(position);

        vHolder._id = aCategoryCursor.getID();
        vHolder.tvTitle.setText(aCategoryCursor.getTitle());

        if (position == 0){
            vHolder.ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (aCategoryCursor == null){
            return 0;
        }else{
            return aCategoryCursor.getCount();
        }
    }

    public void setCursor(Cursor categoryCursor){
        aCategoryCursor = new ClipboardDatabaseHelper.CategoryCursor(categoryCursor);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public long _id;
        public TextView tvTitle;
        public ImageView ivEdit;
        public ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMyClickListener.onEditClick(_id);
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMyClickListener.onDeleteClick(_id);
                }
            });
        }
    }
}
