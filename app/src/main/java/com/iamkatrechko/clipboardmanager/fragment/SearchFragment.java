package com.iamkatrechko.clipboardmanager.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.activity.ClipEditActivity;
import com.iamkatrechko.clipboardmanager.adapter.ClipsCursorAdapter;

import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.Clip;

/**
 * Фрагмент экрана поиска заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Идентификатор загрузчика найденых заметок */
    private static final int SEARCH_CLIPS_LOADER = 1;

    /** Адаптер списка заметок */
    private ClipsCursorAdapter clipsCursorAdapter;
    /** Виджет списка заметок */
    private RecyclerView recyclerView;

    /** Текстовое поле поискового запроса */
    private EditText etSearch;
    /** Кнопка применения запроса поиска */
    private ImageButton ibSearch;

    /**
     * Возвращает новый экземпляр фрагмента
     * @return новый экземпляр фрагмента
     */
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = (EditText) v.findViewById(R.id.etSearch);
        ibSearch = (ImageButton) v.findViewById(R.id.ibSeacrh);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);

        clipsCursorAdapter = new ClipsCursorAdapter(getActivity(), new ClipsCursorAdapter.ClipClickListener() {

            @Override
            public void onClick(long clipId) {
                Intent i = new Intent(getActivity(), ClipEditActivity.class);
                i.putExtra("URI", Clip.buildClipUri(clipId));
                startActivity(i);
            }

            @Override
            public void onSelectedChange(boolean isSelectedMode, int selectedCount) {
            }
        });
        clipsCursorAdapter.setEmptyView(v.findViewById(R.id.linearEmpty));
        recyclerView.setAdapter(clipsCursorAdapter);

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnText(etSearch.getText().toString());
            }
        });
        return v;
    }

    /**
     * Производит поиск по заметкам
     * @param query текст запроса
     */
    private void searchOnText(String query) {
        if (query.length() == 0) return;
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        getLoaderManager().restartLoader(SEARCH_CLIPS_LOADER, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SEARCH_CLIPS_LOADER:
                //TODO Добавить реализацию пустого результата (View)
                String query = args.getString("query");
                return new CursorLoader(getActivity(),
                        Clip.CONTENT_URI,
                        null,
                        Clip.COLUMN_TITLE + " LIKE '%" + query + "%' OR " +
                                Clip.COLUMN_CONTENT + " LIKE '%" + query + "%'",
                        null,
                        Clip._ID + " DESC");
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
    }
}
