package com.iamkatrechko.clipboardmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;
import com.iamkatrechko.clipboardmanager.data.ClipboardDatabaseHelper.*;

import java.util.ArrayList;

public class ClipsCursorAdapter extends RecyclerView.Adapter<ClipsCursorAdapter.ViewHolder> {
    private Context aContext;
    private ClipboardCursor aClips;
    private ClipClickListener aClickListener;
    private RecyclerView mRecyclerView;
    private Activity mActivity;
    private MultiSelector mMultiSelector;
    private View mEmptyView;

    public interface ClipClickListener {
        /**
         * Вызывается при нажатии на элемент списка
         */
        void onClick(long clipId);

        /**
         * Вызывается при включении/отключении режима множественного выделения
         */
        void onSelectedChange(boolean isSelectedMode, int selectedCount);
    }

    public ClipsCursorAdapter(Context context, ClipClickListener listener) {
        this(context, listener, null);
    }

    public ClipsCursorAdapter(Context context, ClipClickListener listener, Activity activity) {
        mMultiSelector = new MultiSelector();
        aContext = context;
        aClickListener = listener;
        mActivity = activity;

        //TODO Добавить вывод view при пустом адаптере
        /*showEmpty(mQuery.size() == 0);*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addItemDecoration(new ItemDivider(aContext));
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item_clip, parent, false));
    }

    @Override
    public void onBindViewHolder(ClipsCursorAdapter.ViewHolder vHolder, int position) {
        aClips.moveToPosition(position);

        vHolder._id = aClips.getID();
        vHolder.tvId.setText(String.valueOf(aClips.getID()));
        vHolder.tvTitle.setText(aClips.getTitle());
        vHolder.tvContent.setText(aClips.getContent());
        //vHolder.tvDate.setText(aClips.getDate());
        vHolder.tvDate.setText(Util.getTimeInString(aClips.getDate()));
        vHolder.tvCategoryId.setText(String.valueOf(aClips.getCategoryId()));
        vHolder.tvIsDeleted.setText(String.valueOf(aClips.isDeleted()));

        vHolder.ivFavorite.setImageResource(aClips.isFavorite() ?
                R.drawable.ic_star : R.drawable.ic_star_border);

        boolean clipInClipboard = aClips.getContent().equals(Util.getClipboardText(aContext));
        vHolder.tvContent.setTypeface(null, clipInClipboard ? Typeface.BOLD : Typeface.NORMAL);
    }

    class ViewHolder extends SwappingHolder {
        long _id;
        TextView tvId;
        TextView tvTitle;
        TextView tvContent;
        TextView tvDate;
        TextView tvCategoryId;
        TextView tvIsDeleted;
        ImageView ivCopy;
        ImageView ivFavorite;

        ViewHolder(final View itemView) {
            super(itemView, mMultiSelector);
            setSelectionModeBackgroundDrawable(aContext.getResources().getDrawable(R.drawable.selection_drawable));

            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvCategoryId = (TextView) itemView.findViewById(R.id.tvCategoryId);
            tvIsDeleted = (TextView) itemView.findViewById(R.id.tvIsDeleted);
            ivCopy = (ImageView) itemView.findViewById(R.id.ivCopy);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);

            if (UtilPrefences.isShowMetaInAdapter(aContext)) {
                tvId.setVisibility(View.VISIBLE);
                tvCategoryId.setVisibility(View.VISIBLE);
                tvIsDeleted.setVisibility(View.VISIBLE);
            } else {
                tvId.setVisibility(View.GONE);
                tvCategoryId.setVisibility(View.GONE);
                tvIsDeleted.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Если множественное выделение неактивно
                    if (!mMultiSelector.tapSelection(ViewHolder.this)) {
                        if (aClickListener != null) {
                            aClickListener.onClick(_id);
                        }
                    } else {
                        if (mMultiSelector.getSelectedPositions().size() == 0) {
                            resetSelectMode();
                        }else{
                            aClickListener.onSelectedChange(true, mMultiSelector.getSelectedPositions().size());
                        }
                    }
                }
            });

            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!mMultiSelector.isSelectable()) { // (3)
                        mMultiSelector.setSelectable(true); // (4)
                        mMultiSelector.setSelected(ViewHolder.this, true); // (5)
                        if (mActivity != null) {
                            aClickListener.onSelectedChange(true, 1);
                        }
                        return true;
                    }
                    return false;
                }
            });


            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.copyToClipboard(aContext, tvContent.getText().toString());
                    notifyDataSetChanged();
                }
            });

            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aClips.moveToPosition(getAdapterPosition());
                    setIsFavorite(_id, !aClips.isFavorite());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (aClips == null) {
            showEmptyView(true);
            return 0;
        } else {
            int count = aClips.getCount();
            showEmptyView(count == 0);
            return count;
        }
    }

    public void setEmptyView(View emptyView){
        mEmptyView = emptyView;
    }

    private void showEmptyView(boolean isShow){
        if (mEmptyView != null) {
            mEmptyView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }else{
            return;
        }
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(isShow ? View.GONE : View.VISIBLE);
        }
    }

    public ArrayList<Long> getSelectedIds(){
        ArrayList<Long> idsList = new ArrayList<>();
        for (int pos : mMultiSelector.getSelectedPositions()){
            long id = ((ViewHolder) mRecyclerView.findViewHolderForPosition(pos))._id;
            idsList.add(id);
        }
        mMultiSelector.getSelectedPositions();
        return idsList;
    }

    /**
     * Перепривязывает данные в адаптере
     */
    public void setCursor(Cursor cursor) {
        resetSelectMode();
        aClips = new ClipboardCursor(cursor);
        notifyDataSetChanged();
        getItemCount();
    }

    /**
     * Изменяет значение "isFavorite" записи в базе данных
     */
    private void setIsFavorite(long id, boolean isFavorite) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Clip.COLUMN_IS_FAVORITE, isFavorite);

        aContext.getContentResolver().update(Clip.buildClipUri(id), contentValues, null, null);
    }

    /**
     * Выключает режим множественного выделения и сбрасывает выделения элементов
     */
    void resetSelectMode() {
        mMultiSelector.setSelectable(false);
        mMultiSelector.clearSelections();

        aClickListener.onSelectedChange(false, 0);
    }

    /**
     * Удаляет из базы данных выбранные множественным выделением записи
     */
    void deleteSelectedItems() {
        for (long id : getSelectedIds()){
            Uri uri = DatabaseDescription.Clip.buildClipUri(id);
            aContext.getContentResolver().delete(uri, null, null);
        }
        resetSelectMode();
    }

    /**
     * Объединяет содержимое выделенных записей в одну строку
     * @param splitChar Строка-разделитель между записями
     * @param deleteOld Требуется ли удалить объединенные записи
     * @return Объединенная строка
     */
    private String getSplitItemsText(String splitChar, boolean deleteOld) {
        String newClipText = "";

        for (long id : getSelectedIds()){
            Uri uri = DatabaseDescription.Clip.buildClipUri(id);

            ClipboardCursor cursor = new ClipboardCursor(aContext.getContentResolver().query(uri, null, null, null, null));
            if (cursor.moveToFirst()) {
                if (newClipText.equals("")){
                    newClipText += cursor.getContent();
                }else {
                    newClipText += splitChar + cursor.getContent();
                }
            }
            if (deleteOld) {
                aContext.getContentResolver().delete(uri, null, null);
            }
        }

        return newClipText;
    }

    /**
     * Объединяет содержимое выделенных записей в одну строку и создает на ее основе новую запись
     * @param splitChar Строка-разделитель между записями
     * @param deleteOld Требуется ли удалить объединенные записи
     */
    void splitItems(String splitChar, boolean deleteOld) {
        if (mMultiSelector.getSelectedPositions().size() > 1) {
            String newClipText = getSplitItemsText(splitChar, deleteOld);

            Uri uriInsert = Clip.CONTENT_URI;
            ContentValues contentValues = Clip.getDefaultContentValues();
            contentValues.put(Clip.COLUMN_CONTENT, newClipText);
            aContext.getContentResolver().insert(uriInsert, contentValues);

            resetSelectMode();
            Toast.makeText(aContext, R.string.splited, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(aContext, R.string.select_cancel, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Объекдиняет содержимое выделенных записей и отравляет в письме
     */
    void shareItems(){
        String shareText = getSplitItemsText(UtilPrefences.getSplitChar(aContext), false);
        Util.shareText(aContext, shareText);
    }

    /**
     * Изменяет категорию выделенных записей
     */
    void changeCategory(long categoryId){
        for (long id : getSelectedIds()){
            Uri uri = DatabaseDescription.Clip.buildClipUri(id);

            ContentValues contentValues = new ContentValues();
            contentValues.put(Clip.COLUMN_CATEGORY_ID, categoryId);
            aContext.getContentResolver().update(uri, contentValues, null, null);
        }
    }
}

