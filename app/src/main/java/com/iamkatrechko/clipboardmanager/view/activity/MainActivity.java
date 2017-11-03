package com.iamkatrechko.clipboardmanager.view.activity;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.CategoryCursor;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.view.fragment.ClipsListFragment;

import java.util.ArrayList;

/**
 * Основаня активность экрана со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Идентификатор загрузчика категорий */
    private static final int CATEGORIES_LOADER = 1;

    /** Виджет бокового меню */
    private DrawerLayout drawerLayout;

    /** Виджет списка бокового меню */
    private ExpandableListView listView;
    /** Адаптер бокового меню */
    private ExpListAdapter adapter;
    /** Курсор со списком категорий */
    private CategoryCursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmain);
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
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this,
                drawerLayout,
                (Toolbar) findViewById(R.id.toolbar),
                R.string.app_name,
                R.string.app_name);
        drawerLayout.setDrawerListener(toogle);
        toogle.syncState();

        // Находим наш list
        listView = (ExpandableListView) findViewById(R.id.exListView);
        String[] names = getResources().getStringArray(R.array.nav_draw_names);
        String[] icons = getResources().getStringArray(R.array.nav_draw_icons);

        ArrayList<String> listGroup = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            listGroup.add(String.valueOf(i));
        }

        adapter = new ExpListAdapter(getApplicationContext(), names, icons);
        listView.setAdapter(adapter);

        /*listView.setItemChecked(NAV_MENU_POS_PROJECTS + 1, true);*/

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        Toast.makeText(getApplicationContext(), "onGroupClick", Toast.LENGTH_SHORT).show();

                    /*getFragment(NAV_MENU_POS_TODAY);*/
                        break;
                    /*case NAV_MENU_POS_WEEK:
                        getFragment(NAV_MENU_POS_WEEK);
                        break;*/
                    case 4:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
                return false;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, i1));
                expandableListView.setItemChecked(index, true);
                Log.d("MainActivity", "CheckedIndex: " + index);
                switch (i) {
                    case 1:
                        if (i1 > cursor.getCount() - 1) {
                            Intent intent = new Intent(MainActivity.this, EditCategoriesActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        ClipsListFragment clipsFragment =
                                (ClipsListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                        cursor.moveToPosition(i1);
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
                Log.d("MainActivity", "onCreateLoader");
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
        Log.d("MainActivity", "onLoadFinished");
        cursor = new CategoryCursor(data);
        for (int i = 0; i < data.getCount(); i++) {
            cursor.moveToPosition(i);
        }
        adapter.setChilders(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class ExpListAdapter extends BaseExpandableListAdapter {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private String[] aGroupNames;
        private String[] aIconNames;
        private Context mContext;
        private CategoryCursor aCursorCategories;

        public ExpListAdapter(Context context, String[] names, String[] icons) {
            mContext = context;
            aGroupNames = names;
            aIconNames = icons;
        }

        @Override
        public int getGroupCount() {
            return aGroupNames.length + 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 1) {
                if (aCursorCategories != null) {
                    return aCursorCategories.getCount() + 1;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Возвращает дочерний объект.
            // В моем случае не актуально, т.к. в getChildView
            // я сразу обращаюсь к массиву со списком.
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (groupPosition == 0) {
                    convertView = inflater.inflate(R.layout.navigation_view_header, null);
                } else {
                    convertView = inflater.inflate(R.layout.exp_list_group_view, null);
                }
            }

            if (groupPosition == 0) {
                return convertView;
            }

            ImageView imageViewIndicator = (ImageView) convertView.findViewById(R.id.imageViewIndicator);
            if (groupPosition != 1) {
                imageViewIndicator.setVisibility(View.GONE);
            } else {
                if (isExpanded) {
                    //Изменяем что-нибудь, если текущая Group раскрыта
                    imageViewIndicator.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    //Изменяем что-нибудь, если текущая Group скрыта
                    imageViewIndicator.setImageResource(R.drawable.ic_arrow_down);
                }
            }

            ImageView iconGroup = (ImageView) convertView.findViewById(R.id.iconGroup);
            iconGroup.setImageResource(mContext.getResources().getIdentifier(aIconNames[groupPosition - 1], "drawable", mContext.getPackageName()));

            TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
            textGroup.setText(aGroupNames[groupPosition - 1]);

            /*if (groupPosition == NAV_MENU_POS_HEADER) {
                //Настройка шапки
                //convertView.findViewById(R.id.linearMain).setBackgroundColor(color);
            }else{
                ImageView imageViewIndicator = (ImageView) convertView.findViewById(R.id.imageViewIndicator);

                //Показ индикатора при наличие подсписка
                if (groupPosition == NAV_MENU_POS_PROJECTS ||
                        groupPosition == NAV_MENU_POS_FILTERS ||
                        groupPosition == NAV_MENU_POS_TAGS) {
                    imageViewIndicator.setVisibility(View.VISIBLE);
                }
            }*/

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.exp_list_child_view, null);
            }

            TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

            if (isLastChild) {
                textChild.setText("Настроить категории");
                imageView.setImageResource(R.drawable.ic_settings);
                return convertView;
            }

            if (aCursorCategories != null) {
                aCursorCategories.moveToPosition(childPosition);
                imageView.setImageResource(R.drawable.ic_label);
                textChild.setText(aCursorCategories.getTitle());
            }

            /*switch (groupPosition){
                case NAV_MENU_POS_PROJECTS:
                    textChild.setText(mGroups.get(groupPosition).getList().get(childPosition).getTitle());

                    ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    imageView.setColorFilter(mGroups.get(groupPosition).getList().get(childPosition).getColor());
                    break;
                case NAV_MENU_POS_FILTERS:
                    textChild.setText(mGroups.get(groupPosition).getListFilters()[childPosition]);

                    //ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    //imageView.setColorFilter(mGroups.get(groupPosition).getList().get(childPosition).getColor());
                    break;
                case NAV_MENU_POS_TAGS:
                    textChild.setText(mGroups.get(groupPosition).getListTags().get(childPosition).getTitle());
                    break;
            }*/

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;
            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

        public void setChilders(CategoryCursor cursor) {
            aCursorCategories = cursor;
        }
    }
}
