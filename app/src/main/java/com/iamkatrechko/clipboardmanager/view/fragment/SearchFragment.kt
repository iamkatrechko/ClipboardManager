package com.iamkatrechko.clipboardmanager.view.fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.domain.loader.callback.ClipsSearchLoaderCallback
import com.iamkatrechko.clipboardmanager.view.activity.ClipEditActivity
import com.iamkatrechko.clipboardmanager.view.adapter.ClipsAdapter
import com.jakewharton.rxbinding2.widget.RxTextView

/**
 * Фрагмент экрана поиска заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class SearchFragment : Fragment() {

    /** Адаптер списка заметок  */
    private var clipsAdapter: ClipsAdapter = ClipsAdapter(object : ClipsAdapter.ClipClickListener {

        override fun onClick(clipId: Long) {
            val i = Intent(activity, ClipEditActivity::class.java)
            i.putExtra("URI", DatabaseDescription.Clip.buildClipUri(clipId))
            startActivity(i)
        }

        override fun onSelectedChange(isSelectedMode: Boolean, selectedCount: Int) {}
    })

    /** Виджет списка заметок  */
    private lateinit var recyclerView: RecyclerView
    /** Текстовое поле поискового запроса  */
    private lateinit var etSearch: EditText
    /** Кнопка применения запроса поиска  */
    private lateinit var ibSearch: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        etSearch = view.findViewById(R.id.etSearch) as EditText
        ibSearch = view.findViewById(R.id.ibSeacrh) as ImageButton
        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView

        clipsAdapter.setEmptyView(view.findViewById(R.id.linearEmpty))
        recyclerView.layoutManager = LinearLayoutManager(activity.baseContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = clipsAdapter

        RxTextView.textChanges(etSearch)
                .subscribe { charSequence -> searchOnText(charSequence.toString()) }
        ibSearch.setOnClickListener { searchOnText(etSearch.text.toString()) }
        return view
    }

    /**
     * Производит поиск по заметкам
     * @param query текст запроса
     */
    private fun searchOnText(query: String) {
        val bundle = Bundle()
        bundle.putString(ClipsSearchLoaderCallback.KEY_LOADER_QUERY_TEXT, query)
        loaderManager.restartLoader<Cursor>(ClipsSearchLoaderCallback.SEARCH_CLIPS_LOADER, bundle,
                ClipsSearchLoaderCallback(context, object : ClipsSearchLoaderCallback.OnDataPreparedListener {
                    override fun onPrepared(clipsList: List<Clip>) {
                        clipsAdapter.setClips(clipsList)
                    }
                }))
    }

    companion object {

        /**
         * Возвращает новый экземпляр фрагмента
         * @return новый экземпляр фрагмента
         */
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}
