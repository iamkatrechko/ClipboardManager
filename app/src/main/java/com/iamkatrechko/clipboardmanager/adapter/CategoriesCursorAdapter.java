package com.iamkatrechko.clipboardmanager.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iamkatrechko.clipboardmanager.ItemDivider;
import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.CategoryCursor;

/**
 * Адаптер списка категорий заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class CategoriesCursorAdapter extends RecyclerView.Adapter<CategoriesCursorAdapter.ViewHolder> {

    /** Список категорий */
    private CategoryCursor aCategoryCursor;
    /** Слушатель нажатий */
    private MyClickListener mMyClickListener;

    /**
     * Конструктор
     * @param clickListener слушатель нажатий
     */
    public CategoriesCursorAdapter(MyClickListener clickListener) {
        mMyClickListener = clickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new ItemDivider(recyclerView.getContext()));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        aCategoryCursor.moveToPosition(position);
        vHolder.bindView(aCategoryCursor);
    }

    @Override
    public int getItemCount() {
        if (aCategoryCursor == null) {
            return 0;
        } else {
            return aCategoryCursor.getCount();
        }
    }

    /**
     * Устанавливает данные в адаптер
     * @param categoryCursor список категорий
     */
    public void setCursor(Cursor categoryCursor) {
        aCategoryCursor = new CategoryCursor(categoryCursor);
    }

    /** Слушатель нажатий */
    public interface MyClickListener {

        /**
         * Редактирование категории
         * @param categoryId идентификатор категории
         */
        void onEditClick(long categoryId);

        /**
         * Удаление категории
         * @param categoryId идентификатор категории
         */
        void onDeleteClick(long categoryId);
    }

    /** Холдер основного элемента списка */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /** Идентификатор категории */
        private long _id;
        /** Заголовок */
        private TextView tvTitle;
        /** Кнопка "редактировать" */
        private ImageView ivEdit;
        /** Кнопка "удалить" */
        private ImageView ivDelete;

        /**
         * Конструктор
         * @param itemView виджет элемента списка
         */
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

        /**
         * Привязывает данные к виджету списка
         * @param cursor данные
         */
        public void bindView(CategoryCursor cursor) {
            _id = cursor.getID();
            tvTitle.setText(cursor.getTitle());
            if (getAdapterPosition() == 0) {
                ivDelete.setVisibility(View.GONE);
            }
        }
    }
}
