package com.iamkatrechko.clipboardmanager.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.CategoryCursor;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavGroups;
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavigationMenuAdapter;
import com.iamkatrechko.clipboardmanager.view.fragment.ClipsListFragment;

/**
 * Основаня активность экрана со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Тег для логирования */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** Идентификатор загрузчика категорий */
    private static final int CATEGORIES_LOADER = 1;

    /** Виджет бокового меню */
    private DrawerLayout drawerLayout;

    /** Виджет списка бокового меню */
    private ExpandableListView listView;
    /** Адаптер бокового меню */
    private NavigationMenuAdapter adapter;
    /** Курсор со списком категорий */
    private CategoryCursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments().isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ClipsListFragment.newInstance())
                    .commit();
        }

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipsListFragment clipsFragment = (ClipsListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                clipsFragment.addNewClip();
            }
        });

        initNavigationView();


        getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ClipsListFragment clipsFragment = (ClipsListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            clipsFragment.onBackPressed();
        }
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ExpandableListView) findViewById(R.id.list_view_navigation);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this,
                drawerLayout,
                (Toolbar) findViewById(R.id.toolbar),
                R.string.app_name,
                R.string.app_name);
        drawerLayout.setDrawerListener(toogle);
        toogle.syncState();

        adapter = new NavigationMenuAdapter();
        listView.setAdapter(adapter);

        /*listView.setItemChecked(NAV_MENU_POS_PROJECTS + 1, true);*/

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPos, long groupId) {
                NavGroups group = NavGroups.values()[groupPos];
                switch (group) {
                    case CATEGORIES:
                        /*getFragment(NAV_MENU_POS_TODAY);*/
                        break;
                    /*case NAV_MENU_POS_WEEK:
                        getFragment(NAV_MENU_POS_WEEK);
                        break;*/
                    case SETTINGS:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                }
                return false;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPos, int childPos, long l) {
                NavGroups group = NavGroups.values()[groupPos];
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPos, childPos));
                expandableListView.setItemChecked(index, true);
                switch (group) {
                    case CATEGORIES:
                        if (childPos > cursor.getCount() - 1) {
                            // Если нажата кнопка настроек групп
                            startActivity(new Intent(getApplicationContext(), EditCategoriesActivity.class));
                            return true;
                        }
                        ClipsListFragment clipsFragment = (ClipsListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                        cursor.moveToPosition(childPos);
                        clipsFragment.showClipsByCategoryId(cursor.getID());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CATEGORIES_LOADER:
                Log.d(TAG, "onCreateLoader");
                return new CursorLoader(this,
                        DatabaseDescription.Category.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        null); // сортировка
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished");
        cursor = new CategoryCursor(data);
        for (int i = 0; i < data.getCount(); i++) {
            cursor.moveToPosition(i);
        }
        adapter.setOfChildren(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
