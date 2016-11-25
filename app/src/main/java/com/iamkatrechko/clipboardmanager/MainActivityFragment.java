package com.iamkatrechko.clipboardmanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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

import static com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CLIPS_LOADER = 0;
    private static final int CLIPS_BY_CATEGORY_LOADER = 1;

    private ClipsCursorAdapter mCursorAdapter;
    private RecyclerView recyclerView;

    private long currentCategoryId = 2;
    private boolean isContextMenu = false;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
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

        mCursorAdapter = new ClipsCursorAdapter(getActivity(), new ClipsCursorAdapter.ClipClickListener(){
            @Override
            public void onClick(long clipId) {
                Intent i = new Intent(getActivity(), ClipEditActivity.class);
                i.putExtra("URI", Clip.buildClipUri(clipId));
                startActivity(i);
            }

            @Override
            public void onSelectedChange(boolean isSelectedMode) {
                isContextMenu = isSelectedMode;
                getActivity().invalidateOptionsMenu();
            }
        }, getActivity());
        recyclerView.setAdapter(mCursorAdapter);

        showClipsByCategoryId(2);
        return v;
    }

    public void showClipsByCategoryId(long categoryId){
        mCursorAdapter.resetSelectMode();
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        currentCategoryId = categoryId;
        getLoaderManager().restartLoader(CLIPS_BY_CATEGORY_LOADER, bundle, this);
    }

    public void addNewClip(){
        Intent i = new Intent(getActivity(), ClipEditActivity.class);
        startActivity(i);
    }

    /**
     * Меняет значок ToolBar"а (звездочку) в зависимости от того,
     * отображаются ли только избранные записи, или нет
     * @param itemStar Элемент ToolBar'а
     * @param isOnlyFavoriteShow Отображаются только избранные записи
     */
    private void changeToolbarItemIcon(MenuItem itemStar, boolean isOnlyFavoriteShow){
        if (isContextMenu) return;
        if (isOnlyFavoriteShow){
            itemStar.setIcon(R.drawable.ic_star_white);
        }else{
            itemStar.setIcon(R.drawable.ic_star_border_white);
        }
    }

    public void onBackPressed(){
        if (isContextMenu){
            mCursorAdapter.resetSelectMode();
        }else{
            getActivity().finish();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // События /////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("Fragment", "onCreateLoader");

        switch (id) {
            case CLIPS_LOADER:
                Log.d("Fragment", "onCreateLoader2");
                return new CursorLoader(getActivity(),
                        Clip.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        Clip._ID + " DESC"); // сортировка
            case CLIPS_BY_CATEGORY_LOADER:
                long categoryId = args.getLong("categoryId");
                boolean isOnlyFavoriteShow = UtilPrefences.isShowOnlyFavorite(getActivity());
                String onlyFavorite = isOnlyFavoriteShow ? " and " + Clip.COLUMN_IS_FAVORITE + " = 1" : "";
                return new CursorLoader(getActivity(),
                        Clip.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        Clip.COLUMN_CATEGORY_ID + "=?" + onlyFavorite, // все записи
                        new String[]{String.valueOf(categoryId)}, // без аргументов
                        Clip._ID + " DESC"); // сортировка
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.setCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(!isContextMenu ? R.menu.menu_main : R.menu.menu_main_context, menu);
        getActivity().setTitle(!isContextMenu ? R.string.app_name : R.string.select_records);

        boolean showOnlyFavorite = UtilPrefences.isShowOnlyFavorite(getActivity());
        MenuItem itemStar = menu.findItem(R.id.action_show_favorites);
        changeToolbarItemIcon(itemStar, showOnlyFavorite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            // Показывать только избранные
            case R.id.action_show_favorites:
                boolean isOnly = UtilPrefences.isShowOnlyFavorite(getActivity());
                changeToolbarItemIcon(item, !isOnly);
                UtilPrefences.setShowOnlyFavorite(getActivity(), !isOnly);
                showClipsByCategoryId(currentCategoryId);
                break;
            // Меню разработчика
            case R.id.action_developer_menu:
                Intent i = new Intent(getActivity(), DeveloperActivity.class);
                getActivity().startActivity(i);
                break;
            // Удалить выделенные записи
            case R.id.action_delete:
                mCursorAdapter.deleteSelectedItems();
                break;
            // Соединить выделенные записи
            case R.id.action_split:
                DialogManager.showDialogSplitClips(this);
                break;
            // Поделиться выделенными записями
            case R.id.action_share:
                mCursorAdapter.shareItems();
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
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_SPLIT_CLIPS){
            String splitChar = data.getStringExtra("splitChar");
            boolean deleteOldClips = data.getBooleanExtra("deleteOldClips", false);

            mCursorAdapter.splitItems(splitChar, deleteOldClips);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_CHANGE_CATEGORY){
            long categoryId = data.getLongExtra("categoryId", 0);

            mCursorAdapter.changeCategory(categoryId);
        }
    }
}
