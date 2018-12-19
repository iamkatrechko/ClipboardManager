package com.iamkatrechko.clipboardmanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ExpandableListView
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.domain.loader.callback.CategoriesLoaderCallback
import com.iamkatrechko.clipboardmanager.view.adapter.NavigationMenuAdapter
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavGroups
import com.iamkatrechko.clipboardmanager.view.fragment.ClipsListFragment

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
    /** Список категорий  */
    private val categories = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        setSupportActionBar(findViewById(R.id.toolbar))

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        listView = findViewById<ExpandableListView>(R.id.list_view_navigation)

        val fragmentManager = supportFragmentManager
        if (fragmentManager.fragments.isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ClipsListFragment.newInstance(1L))
                    .commit()
        }

        findViewById<View>(R.id.fab).setOnClickListener {
            val i = ClipEditActivity.newIntent(this, null)
            startActivity(i)
        }
        initNavigationView()

        supportLoaderManager.initLoader(CategoriesLoaderCallback.CATEGORIES_LOADER, null, CategoriesLoaderCallback(this, { categories ->
            this.categories.clear()
            this.categories.addAll(categories)
            adapter.setOfChildren(categories)
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
                findViewById<Toolbar>(R.id.toolbar),
                R.string.app_name,
                R.string.app_name)
        drawerLayout.setDrawerListener(toogle)
        toogle.syncState()

        listView.setAdapter(adapter)

        //listView.setItemChecked(0, true)

        listView.setOnGroupClickListener { expandableListView, view, groupPos, groupId ->
            val group = NavGroups.values()[groupPos]
            when (group) {
                NavGroups.ALL_CLIPS -> showByCategory(null)
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
                    if (childPos > categories.size - 1) {
                        // Если нажата кнопка настроек групп
                        startActivity(Intent(applicationContext, EditCategoriesActivity::class.java))
                        return@OnChildClickListener true
                    }
                    showByCategory(categories[childPos].id)
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
        clipsFragment.showClipsByCategoryId(categoryId)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        /** Тег для логирования  */
        private val TAG = MainActivity::class.java.simpleName

    }
}
