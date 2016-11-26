package com.iamkatrechko.clipboardmanager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.iamkatrechko.clipboardmanager.data.DatabaseDescription;

import static com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

public class SearchActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int SEARCH_CLIPS_LOADER = 1;

    private ClipsCursorAdapter mCursorAdapter;
    private RecyclerView recyclerView;

    private EditText etSearch;
    private ImageButton ibSearch;

    public static SearchActivityFragment newInstance() {
        return new SearchActivityFragment();
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

        mCursorAdapter = new ClipsCursorAdapter(getActivity(), new ClipsCursorAdapter.ClipClickListener(){
            @Override
            public void onClick(long clipId) {
                Intent i = new Intent(getActivity(), ClipEditActivity.class);
                i.putExtra("URI", Clip.buildClipUri(clipId));
                startActivity(i);
            }

            @Override
            public void onSelectedChange(boolean isSelectedMode) {
                /*isContextMenu = isSelectedMode;
                getActivity().invalidateOptionsMenu();*/
            }
        }, getActivity());
        mCursorAdapter.setEmptyView(v.findViewById(R.id.linearEmpty));
        recyclerView.setAdapter(mCursorAdapter);

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnText(etSearch.getText().toString());
            }
        });
        return v;
    }

    private void searchOnText(String query){
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
                        null, // все столбцы
                        Clip.COLUMN_TITLE + " LIKE '%" + query + "%' OR " +
                        Clip.COLUMN_CONTENT + " LIKE '%" + query + "%'",
                        null, // без аргументов
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

    }
}
