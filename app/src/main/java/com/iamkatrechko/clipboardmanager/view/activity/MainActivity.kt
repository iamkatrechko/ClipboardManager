package com.iamkatrechko.clipboardmanager.view.activity

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ExpandableListView
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.CategoryCursor
import com.iamkatrechko.clipboardmanager.view.adapter.NavigationMenuAdapter
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavGroups
import com.iamkatrechko.clipboardmanager.view.fragment.ClipsListFragment
import com.iamkatrechko.clipboardmanager.view.loader.CategoriesLoader

/**
 * Основаня активность экрана со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class MainActivity : AppCompatActivity() {

    /** Виджет бокового меню  */
    private lateinit var drawerLayout: DrawerLayout
    /** Виджет списка бокового меню  */
    private lateinit var listView: ExpandableListView

    /** Адаптер бокового меню  */
    private var adapter = NavigationMenuAdapter()
    /** Курсор со списком категорий  */
    private var cursor: CategoryCursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        listView = findViewById(R.id.list_view_navigation) as ExpandableListView

        val fragmentManager = supportFragmentManager
        if (fragmentManager.fragments.isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ClipsListFragment.newInstance(1L))
                    .commit()
        }

        findViewById(R.id.fab).setOnClickListener {
            val clipsFragment = supportFragmentManager.findFragmentById(R.id.container) as ClipsListFragment
            clipsFragment.addNewClip()
        }
        initNavigationView()

        supportLoaderManager.initLoader(CATEGORIES_LOADER, null, CategoriesLoader(this, object : CategoriesLoader.OnDataPreparedListener {
            override fun onPrepared(data: Cursor) {
                cursor = CategoryCursor(data)
                for (i in 0 until data.count) {
                    cursor!!.moveToPosition(i)
                }
                adapter.setOfChildren(cursor!!)
                adapter.notifyDataSetChanged()
            }
        }))
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val clipsFragment = supportFragmentManager.findFragmentById(R.id.container) as ClipsListFragment
            clipsFragment.onBackPressed()
        }
    }

    private fun initNavigationView() {
        val toogle = ActionBarDrawerToggle(this,
                drawerLayout,
                findViewById(R.id.toolbar) as Toolbar,
                R.string.app_name,
                R.string.app_name)
        drawerLayout.setDrawerListener(toogle)
        toogle.syncState()

        listView.setAdapter(adapter)

        //listView.setItemChecked(0, true)

        listView.setOnGroupClickListener { expandableListView, view, groupPos, groupId ->
            val group = NavGroups.values()[groupPos]
            when (group) {
                NavGroups.ALL_CLIPS -> showByCategory(-1L)
                NavGroups.SETTINGS -> startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }
            false
        }

        listView.setOnChildClickListener(ExpandableListView.OnChildClickListener { expandableListView, view, groupPos, childPos, l ->
            val group = NavGroups.values()[groupPos]
            val index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPos, childPos))
            expandableListView.setItemChecked(index, true)
            when (group) {
                NavGroups.CATEGORIES -> {
                    if (childPos > cursor!!.count - 1) {
                        // Если нажата кнопка настроек групп
                        startActivity(Intent(applicationContext, EditCategoriesActivity::class.java))
                        return@OnChildClickListener true
                    }
                    cursor!!.moveToPosition(childPos)
                    showByCategory(cursor!!.id)
                    return@OnChildClickListener true
                }
            }
            true
        })
    }

    /**
     * Отобржает список записей по категории
     * @param categoryId идентификатор категории
     */
    private fun showByCategory(categoryId: Long?) {
        val clipsFragment = supportFragmentManager.findFragmentById(R.id.container) as ClipsListFragment
        clipsFragment.showClipsByCategoryId(categoryId!!)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        /** Тег для логирования  */
        private val TAG = MainActivity::class.java.simpleName

        /** Идентификатор загрузчика категорий  */
        val CATEGORIES_LOADER = 1
    }
}
