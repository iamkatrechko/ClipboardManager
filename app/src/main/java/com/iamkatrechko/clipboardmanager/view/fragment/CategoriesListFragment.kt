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
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.CategoryTable
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.domain.loader.callback.CategoriesLoaderCallback
import com.iamkatrechko.clipboardmanager.domain.repository.ICategoryRepository
import com.iamkatrechko.clipboardmanager.domain.use_case.MoveClipsUseCase
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.adapter.CategoriesCursorAdapter
import com.iamkatrechko.clipboardmanager.view.adapter.common.ItemDivider
import com.iamkatrechko.clipboardmanager.view.dialog.DialogCategoryDelete
import com.iamkatrechko.clipboardmanager.view.dialog.DialogCategoryEdit

/**
 * Фрагмент экрана со списком категорий
 * @author iamkatrechko
 * Date: 01.11.2016
 */
class CategoriesListFragment : Fragment() {

    /** Репозиторий список категорий */
    private val repository: ICategoryRepository = CategoryRepository.getInstance()
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
            val categoryId = data.getLongExtra(DialogCategoryEdit.KEY_CATEGORY_ID, 1)
            val newName = data.getStringExtra(DialogCategoryEdit.KEY_NEW_CATEGORY_ID)
            renameCategory(categoryId, newName)
        }
        if (DialogManager.DIALOG_ADD == requestCode) {
            val newName = data.getStringExtra(DialogCategoryEdit.KEY_NEW_CATEGORY_ID)
            createCategory(newName)
        }
        if (DialogManager.DIALOG_DELETE == requestCode) {
            val deleteCategoryId = data.getLongExtra(DialogCategoryDelete.KEY_DELETE_CATEGORY_ID, -1)
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
        repository.addCategory(name)
    }

    /** Переименовывает категорию */
    private fun renameCategory(categoryId: Long, newName: String) {
        val uri = CategoryTable.buildClipUri(categoryId)
        val contentValues = ContentValues()
        contentValues.put(CategoryTable.COLUMN_TITLE, newName)
        activity!!.contentResolver.update(uri, contentValues, null, null)
    }

    /** Перемещает записи из одной категории в другую */
    private fun moveClips(fromCategoryId: Long, toCategoryId: Long) {
        val params = MoveClipsUseCase.Params(context!!, fromCategoryId, toCategoryId)
        MoveClipsUseCase().execute(params)
    }
}
