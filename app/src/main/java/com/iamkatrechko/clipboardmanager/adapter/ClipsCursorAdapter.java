package com.iamkatrechko.clipboardmanager.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.iamkatrechko.clipboardmanager.ItemDivider;
import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.ClipCursor;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.Clip;
import com.iamkatrechko.clipboardmanager.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.util.Util;
import com.iamkatrechko.clipboardmanager.util.UtilPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер списка заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ClipsCursorAdapter extends RecyclerView.Adapter<ClipsCursorAdapter.ViewHolder> {

    /** Контекст */
    private Context context;
    /** Список заметок */
    private ClipCursor clipCursor;
    /** Слушатель нажатий */
    private ClipClickListener clickListener;
    /** Виджет списка */
    private RecyclerView recyclerView;
    /** Помощник множественного выделения */
    private MultiSelector multiSelector;
    /** Виджет пустого списка */
    private View emptyView;

    /**
     * Конструктор
     * @param context  контекст
     * @param listener слушатель нажатий
     */
    public ClipsCursorAdapter(Context context, ClipClickListener listener) {
        multiSelector = new MultiSelector();
        clickListener = listener;
        this.context = context;
        //TODO Добавить вывод view при пустом адаптере
        /*showEmpty(mQuery.size() == 0);*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        this.recyclerView.addItemDecoration(new ItemDivider(context));
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item_clip, parent, false));
    }

    @Override
    public void onBindViewHolder(ClipsCursorAdapter.ViewHolder vHolder, int position) {
        clipCursor.moveToPosition(position);
        vHolder.bindView(clipCursor);
    }

    @Override
    public int getItemCount() {
        if (clipCursor == null) {
            showEmptyView(true);
            return 0;
        } else {
            int count = 0;
            try {
                count = clipCursor.getCount();
            } catch (Exception ignored) {

            }
            showEmptyView(count == 0);
            return count;
        }
    }

    /**
     * Устанавливает виджет пустого списка
     * @param emptyView виджет пустого списка
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * Отображает виджет пустого списка
     * @param isShow отображается ли виджет
     */
    private void showEmptyView(boolean isShow) {
        if (emptyView != null) {
            emptyView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        } else {
            return;
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isShow ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Возвращает список выделенных записей
     * @return список выделенных записей
     */
    public List<Long> getSelectedIds() {
        List<Long> result = new ArrayList<>();
        for (int pos : multiSelector.getSelectedPositions()) {
            clipCursor.moveToPosition(pos);
            result.add(clipCursor.getID());
        }
        return result;
    }

    /**
     * Перепривязывает данные в адаптере
     * @param cursor новые данные
     */
    public void setCursor(Cursor cursor) {
        resetSelectMode();
        clipCursor = new ClipCursor(cursor);
        notifyDataSetChanged();
        getItemCount();
    }

    /**
     * Изменяет значение "isFavorite" записи в базе данных
     * @param id         идентификатор записи
     * @param isFavorite флаг избранности
     */
    private void setIsFavorite(long id, boolean isFavorite) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Clip.COLUMN_IS_FAVORITE, isFavorite);

        context.getContentResolver().update(Clip.buildClipUri(id), contentValues, null, null);
    }

    /** Выключает режим множественного выделения и сбрасывает выделения элементов */
    public void resetSelectMode() {
        multiSelector.setSelectable(false);
        multiSelector.clearSelections();

        clickListener.onSelectedChange(false, 0);
    }

    /** Удаляет из базы данных выбранные множественным выделением записи */
    public void deleteSelectedItems() {
        for (long id : getSelectedIds()) {
            Uri uri = DatabaseDescription.Clip.buildClipUri(id);
            context.getContentResolver().delete(uri, null, null);
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

        for (long id : getSelectedIds()) {
            Uri uri = DatabaseDescription.Clip.buildClipUri(id);

            ClipCursor cursor = new ClipCursor(context.getContentResolver().query(uri, null, null, null, null));
            if (cursor.moveToFirst()) {
                if (newClipText.equals("")) {
                    newClipText += cursor.getContent();
                } else {
                    newClipText += splitChar + cursor.getContent();
                }
            }
            if (deleteOld) {
                context.getContentResolver().delete(uri, null, null);
            }
        }

        return newClipText;
    }

    /**
     * Объединяет содержимое выделенных записей в одну строку и создает на ее основе новую запись
     * @param splitChar Строка-разделитель между записями
     * @param deleteOld Требуется ли удалить объединенные записи
     */
    public void splitItems(String splitChar, boolean deleteOld) {
        if (multiSelector.getSelectedPositions().size() > 1) {
            String newClipText = getSplitItemsText(splitChar, deleteOld);

            Uri uriInsert = Clip.CONTENT_URI;
            ContentValues contentValues = Clip.getDefaultContentValues();
            contentValues.put(Clip.COLUMN_CONTENT, newClipText);
            context.getContentResolver().insert(uriInsert, contentValues);

            resetSelectMode();
            Toast.makeText(context, R.string.splited, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.select_cancel, Toast.LENGTH_SHORT).show();
        }
    }

    /** Объединяет содержимое выделенных записей и отравляет в письме */
    public void shareItems() {
        String shareText = getSplitItemsText(UtilPreferences.getSplitChar(context), false);
        Util.shareText(context, shareText);
    }

    /**
     * Изменяет категорию выделенных записей
     * @param categoryId идентификатор категории
     */
    public void changeCategory(long categoryId) {
        for (long id : getSelectedIds()) {
            Uri uri = DatabaseDescription.Clip.buildClipUri(id);

            ContentValues contentValues = new ContentValues();
            contentValues.put(Clip.COLUMN_CATEGORY_ID, categoryId);
            context.getContentResolver().update(uri, contentValues, null, null);
        }
    }

    /** Интерфейс слушателя нажатий */
    public interface ClipClickListener {

        /**
         * Нажатие на элемент списка
         * @param clipId идентификатор заметки
         */
        void onClick(long clipId);

        /**
         * Включение выключение режима множественного выделения
         * @param isSelectedMode включено ли множественное выделение
         * @param selectedCount  количество выделений
         */
        void onSelectedChange(boolean isSelectedMode, int selectedCount);
    }

    /** Холдер основного элемента списка */
    public class ViewHolder extends SwappingHolder {

        /** Идентификатор заметки */
        long _id;
        /** Идентификатор заметки */
        private TextView tvId;
        /** Заголовок заметки */
        private TextView tvTitle;
        /** Содержимое заметки */
        private TextView tvContent;
        /** Дата заметки */
        private TextView tvDate;
        /** Категория заметки */
        private TextView tvCategoryId;
        /** Признак удаленной записи */
        private TextView tvIsDeleted;
        /** Иконка "скопировать" */
        private ImageView ivCopy;
        /** Иконка избранности */
        private ImageView ivFavorite;

        /**
         * Конструктор
         * @param itemView виджет элемента списка
         */
        ViewHolder(final View itemView) {
            super(itemView, multiSelector);
            setSelectionModeBackgroundDrawable(context.getResources().getDrawable(R.drawable.selection_drawable));

            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvCategoryId = (TextView) itemView.findViewById(R.id.tvCategoryId);
            tvIsDeleted = (TextView) itemView.findViewById(R.id.tvIsDeleted);
            ivCopy = (ImageView) itemView.findViewById(R.id.ivCopy);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);

            if (UtilPreferences.isShowMetaInAdapter(context)) {
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
                    if (!multiSelector.tapSelection(ViewHolder.this)) {
                        if (clickListener != null) {
                            clickListener.onClick(_id);
                        }
                    } else {
                        if (multiSelector.getSelectedPositions().size() == 0) {
                            resetSelectMode();
                        } else {
                            clickListener.onSelectedChange(true, multiSelector.getSelectedPositions().size());
                        }
                    }
                }
            });

            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!multiSelector.isSelectable()) { // (3)
                        multiSelector.setSelectable(true); // (4)
                        multiSelector.setSelected(ViewHolder.this, true); // (5)
                        if (clickListener != null) {
                            clickListener.onSelectedChange(true, 1);
                        }
                        return true;
                    }
                    return false;
                }
            });

            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipUtils.copyToClipboard(context, tvContent.getText().toString());
                    notifyDataSetChanged();
                }
            });

            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clipCursor.moveToPosition(getAdapterPosition());
                    setIsFavorite(_id, !clipCursor.isFavorite());
                }
            });
        }

        /**
         * Привязывает данные к виджету списка
         * @param cursor данные
         */
        public void bindView(ClipCursor cursor) {
            _id = cursor.getID();
            tvId.setText(String.valueOf(cursor.getID()));
            tvTitle.setText(cursor.getTitle());
            tvContent.setText(cursor.getContent());
            //tvDate.setText(cursor.getDate());
            tvDate.setText(Util.getTimeInString(cursor.getDate()));
            tvCategoryId.setText(String.valueOf(cursor.getCategoryId()));
            tvIsDeleted.setText(String.valueOf(cursor.isDeleted()));

            ivFavorite.setImageResource(cursor.isFavorite() ?
                    R.drawable.ic_star : R.drawable.ic_star_border);

            boolean clipInClipboard = cursor.getContent().equals(ClipUtils.getClipboardText(context));
            tvContent.setTypeface(null, clipInClipboard ? Typeface.BOLD : Typeface.NORMAL);
        }
    }
}

