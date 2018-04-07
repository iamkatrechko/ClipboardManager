package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.CategoryTable
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.domain.loader.callback.CategoriesLoaderCallback
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.adapter.CategoriesCursorAdapter
import com.iamkatrechko.clipboardmanager.view.adapter.ItemDivider

/**
 * Фрагмент экрана со списком категорий
 * @author iamkatrechko
 * Date: 01.11.2016
 */
class CategoriesListFragment : Fragment() {

    /** Адаптер списка категорий заметок  */
    private lateinit var categoriesAdapter: CategoriesCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoriesAdapter = CategoriesCursorAdapter(object : CategoriesCursorAdapter.MyClickListener {

            override fun onEditClick(categoryId: Long) {
                DialogManager.showDialogCategoryEdit(this@CategoriesListFragment, categoryId)
            }

            override fun onDeleteClick(categoryId: Long) {
                DialogManager.showDialogCategoryDelete(this@CategoriesListFragment, categoryId)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_categories, container, false)

        /* Виджет списка категорий */
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = categoriesAdapter
        recyclerView.addItemDecoration(ItemDivider(context))

        loaderManager.initLoader(CategoriesLoaderCallback.CATEGORIES_LOADER, null, CategoriesLoaderCallback(context!!) { categories ->
            categoriesAdapter.setCursor(categories)
        })
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (DialogManager.DIALOG_EDIT == requestCode) {
            val categoryId = data.getLongExtra("categoryId", 1)
            val newName = data.getStringExtra("newName")
            renameCategory(categoryId, newName)
        }
        if (DialogManager.DIALOG_ADD == requestCode) {
            createCategory(data.getStringExtra("newName"))
        }
        if (DialogManager.DIALOG_DELETE == requestCode) {
            val deleteCategoryId = data.getLongExtra("deleteCategoryId", -1)
            val newCategoryId = data.getLongExtra("newCategoryId", 1)
            moveClips(deleteCategoryId, newCategoryId)
        }
    }

    /** Отображает диалог создания категории  */
    fun showDialogAdd() {
        DialogManager.showDialogCategoryAdd(this)
    }

    /** Создает новую категорию */
    private fun createCategory(name: String) {
        val uri = CategoryTable.CONTENT_URI
        val contentValues = ContentValues()
        contentValues.put(CategoryTable.COLUMN_TITLE, name)
        activity!!.contentResolver.insert(uri, contentValues)
    }

    /** Переименовывает категорию */
    private fun renameCategory(categoryId: Long, newName: String) {
        val uri = CategoryTable.buildClipUri(categoryId)
        val contentValues = ContentValues()
        contentValues.put(CategoryTable.COLUMN_TITLE, newName)
        activity!!.contentResolver.update(uri, contentValues, null, null)
    }

    /** Перемещает записи из одной категории в другую */
    private fun moveClips(oldCategoryId: Long, newCategoryId: Long) {
        val uriMove = DatabaseDescription.ClipsTable.CONTENT_URI
        val contentValues = ContentValues()
        contentValues.put(ClipsTable.COLUMN_CATEGORY_ID, newCategoryId)

        // Перемещение записей из удаляемой категории в новую
        activity!!.contentResolver.update(uriMove,
                contentValues,
                DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID + "=" + oldCategoryId, null)

        // Удаление категории (теперь уже пустой)
        val uriDelete = CategoryTable.buildClipUri(oldCategoryId)
        activity!!.contentResolver.delete(uriDelete, null, null)
    }
}
