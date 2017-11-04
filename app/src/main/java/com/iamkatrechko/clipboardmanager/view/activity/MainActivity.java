package com.iamkatrechko.clipboardmanager.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.CategoryCursor;
import com.iamkatrechko.clipboardmanager.view.adapter.NavigationMenuAdapter;
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavGroups;
import com.iamkatrechko.clipboardmanager.view.fragment.ClipsListFragment;
import com.iamkatrechko.clipboardmanager.view.loader.CategoriesLoader;

/**
 * Основаня активность экрана со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class MainActivity extends AppCompatActivity {

    /** Тег для логирования */
    private static final String TAG = MainActivity.class.getSimpleName();

    /** Идентификатор загрузчика категорий */
    public static final int CATEGORIES_LOADER = 1;

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
                    .replace(R.id.container, ClipsListFragment.Companion.newInstance(1L))
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

        getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, new CategoriesLoader(this, new CategoriesLoader.OnDataPreparedListener() {
            @Override
            public void onPrepared(Cursor data) {
                cursor = new CategoryCursor(data);
                for (int i = 0; i < data.getCount(); i++) {
                    cursor.moveToPosition(i);
                }
                adapter.setOfChildren(cursor);
                adapter.notifyDataSetChanged();
            }
        }));
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
                    case ALL_CLIPS:
                        showByCategory(-1L);
                        break;
                    case CATEGORIES:
                        break;
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
                        cursor.moveToPosition(childPos);
                        showByCategory(cursor.getID());
                        return true;
                }
                return true;
            }
        });
    }

    /**
     * Отобржает список записей по категории
     * @param categoryId идентификатор категории
     */
    private void showByCategory(Long categoryId) {
        ClipsListFragment clipsFragment = (ClipsListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        clipsFragment.showClipsByCategoryId(categoryId);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
