package com.iamkatrechko.clipboardmanager.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.iamkatrechko.clipboardmanager.DialogManager;
import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.activity.ClipEditActivity;
import com.iamkatrechko.clipboardmanager.activity.DeveloperActivity;
import com.iamkatrechko.clipboardmanager.activity.SearchActivity;
import com.iamkatrechko.clipboardmanager.adapter.ClipsCursorAdapter;
import com.iamkatrechko.clipboardmanager.util.UtilPreferences;

import static com.iamkatrechko.clipboardmanager.data.DatabaseDescription.Clip;

/**
 * Основной фрагмент экрана со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ClipsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Идентификатор загрузчика заметок по категории */
    private static final int CLIPS_BY_CATEGORY_LOADER = 1;

    /** Адаптер списка заметок */
    private ClipsCursorAdapter clipsCursorAdapter;
    /** Виджет списка заметок */
    private RecyclerView recyclerView;

    /** Текущая выбранная категория */
    private long currentCategoryId = 2;
    /** Включен ли режим выделения */
    private boolean isContextMenu;
    /** Количество выделенных элементов */
    private int selectedCount = 0;

    /**
     * Возвращает новый экземпляр фрагмента
     * @return новый экземпляр фрагмента
     */
    public static ClipsListFragment newInstance() {
        return new ClipsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);

        clipsCursorAdapter = new ClipsCursorAdapter(getActivity(), new ClipsCursorAdapter.ClipClickListener() {

            @Override
            public void onClick(long clipId) {
                Intent i = ClipEditActivity.newIntent(getActivity(),
                        Clip.buildClipUri(clipId));
                startActivity(i);
            }

            @Override
            public void onSelectedChange(boolean isSelectedMode, int selectedCount) {
                isContextMenu = isSelectedMode;
                getActivity().invalidateOptionsMenu();
                ClipsListFragment.this.selectedCount = selectedCount;
            }
        });
        clipsCursorAdapter.setEmptyView(v.findViewById(R.id.linearEmpty));
        recyclerView.setAdapter(clipsCursorAdapter);

        showClipsByCategoryId(2);
        return v;
    }

    /**
     * Отображает список заметок по категории
     * @param categoryId идентификатор категории
     */
    public void showClipsByCategoryId(long categoryId) {
        clipsCursorAdapter.resetSelectMode();
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        currentCategoryId = categoryId;
        getLoaderManager().restartLoader(CLIPS_BY_CATEGORY_LOADER, bundle, this);
    }

    /** Открывает экран создания новой заметки */
    public void addNewClip() {
        Intent i = ClipEditActivity.newIntent(getActivity(), null);
        startActivity(i);
    }

    /**
     * Меняет значок ToolBar"а (звездочку) в зависимости от того,
     * отображаются ли только избранные записи, или нет
     * @param itemStar           Элемент ToolBar'а
     * @param isOnlyFavoriteShow Отображаются только избранные записи
     */
    private void changeToolbarItemIcon(MenuItem itemStar, boolean isOnlyFavoriteShow) {
        if (isContextMenu) return;
        if (isOnlyFavoriteShow) {
            itemStar.setIcon(R.drawable.ic_star_white);
        } else {
            itemStar.setIcon(R.drawable.ic_star_border_white);
        }
    }

    /** Нажатие кнопки "назад" */
    public void onBackPressed() {
        if (isContextMenu) {
            clipsCursorAdapter.resetSelectMode();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("Fragment", "onCreateLoader");

        switch (id) {
            /*case CLIPS_LOADER:
                Log.d("Fragment", "onCreateLoader2");
                return new CursorLoader(getActivity(),
                        Clip.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        Clip._ID + " DESC"); // сортировка*/
            case CLIPS_BY_CATEGORY_LOADER:
                String orderType = UtilPreferences.getOrderType(getActivity());
                String orderQuery = null;
                if (orderType.equals("3")) {
                    orderQuery = Clip.COLUMN_DATE;
                }
                if (orderType.equals("2")) {
                    orderQuery = Clip.COLUMN_DATE + " DESC";
                }
                if (orderType.equals("1")) {
                    orderQuery = Clip._ID;
                }
                long categoryId = args.getLong("categoryId");
                boolean isOnlyFavoriteShow = UtilPreferences.isShowOnlyFavorite(getActivity());
                String onlyFavorite = isOnlyFavoriteShow ? " and " + Clip.COLUMN_IS_FAVORITE + " = 1" : "";
                return new CursorLoader(getActivity(),
                        Clip.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        Clip.COLUMN_CATEGORY_ID + "=?" + onlyFavorite, // все записи
                        new String[]{String.valueOf(categoryId)}, // без аргументов
                        orderQuery); // сортировка
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        clipsCursorAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clipsCursorAdapter.setCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(!isContextMenu ? R.menu.menu_main : R.menu.menu_main_context, menu);
        if (selectedCount == 0) {
            getActivity().setTitle(R.string.app_name);
        } else {
            getActivity().setTitle("" + selectedCount);
            menu.findItem(R.id.action_split).setVisible(selectedCount > 1);
        }

        boolean showOnlyFavorite = UtilPreferences.isShowOnlyFavorite(getActivity());
        MenuItem itemStar = menu.findItem(R.id.action_show_favorites);
        changeToolbarItemIcon(itemStar, showOnlyFavorite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Окно поиска
            case R.id.action_search:
                getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            // Настройка сортировки
            case R.id.action_set_order:
                DialogManager.showDialogSetOrderType(this);
                break;
            // Показывать только избранные
            case R.id.action_show_favorites:
                boolean isOnly = UtilPreferences.isShowOnlyFavorite(getActivity());
                changeToolbarItemIcon(item, !isOnly);
                UtilPreferences.setShowOnlyFavorite(getActivity(), !isOnly);
                showClipsByCategoryId(currentCategoryId);
                break;
            // Меню разработчика
            case R.id.action_developer_menu:
                Intent i = new Intent(getActivity(), DeveloperActivity.class);
                getActivity().startActivity(i);
                break;
            // Удалить выделенные записи
            case R.id.action_delete:
                DialogManager.showDialogDeleteConfirm(this);
                break;
            // Соединить выделенные записи
            case R.id.action_split:
                DialogManager.showDialogSplitClips(this);
                break;
            // Поделиться выделенными записями
            case R.id.action_share:
                clipsCursorAdapter.shareItems();
                break;
            // Сменить категорию выделенных записей
            case R.id.action_change_category:
                DialogManager.showDialogChangeCategory(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_SPLIT_CLIPS) {
            String splitChar = data.getStringExtra("splitChar");
            boolean deleteOldClips = data.getBooleanExtra("deleteOldClips", false);

            clipsCursorAdapter.splitItems(splitChar, deleteOldClips);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_CHANGE_CATEGORY) {
            long categoryId = data.getLongExtra("categoryId", 0);

            clipsCursorAdapter.changeCategory(categoryId);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_DELETE_CONFIRM) {
            boolean delete = data.getBooleanExtra("delete", false);
            if (delete)
                clipsCursorAdapter.deleteSelectedItems();
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_SET_ORDER_TYPE) {
            String orderType = data.getStringExtra("orderType");
            UtilPreferences.setOrderType(getActivity(), orderType);
            showClipsByCategoryId(currentCategoryId);
        }
    }
}
