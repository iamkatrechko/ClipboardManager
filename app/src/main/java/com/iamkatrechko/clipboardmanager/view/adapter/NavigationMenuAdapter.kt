package com.iamkatrechko.clipboardmanager.view.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavGroups
import com.iamkatrechko.clipboardmanager.view.extension.inflate
import com.iamkatrechko.clipboardmanager.view.extension.setGone

/**
 * Адаптер списка бокового меню
 * @author iamkatrechko
 *         Date: 03.11.2017
 */
class NavigationMenuAdapter : BaseExpandableListAdapter() {

    /** Список категорий */
    private var categories = ArrayList<Category>()

    override fun getGroupCount() = NavGroups.values().size

    override fun getChildrenCount(groupPos: Int) = if (getGroup(groupPos) == NavGroups.CATEGORIES) {
        categories.size + 1
    } else {
        0
    }

    override fun getGroup(groupPos: Int) = NavGroups.values()[groupPos]

    override fun getChild(groupPosition: Int, childPosition: Int) = null

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return groupPosition.toLong() * childPosition.toLong()
    }

    override fun hasStableIds() = true

    override fun getGroupView(groupPos: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View? {
        val group = getGroup(groupPos)
        val context = parent.context
        var view = convertView
        if (view == null) {
            view = parent.inflate(R.layout.exp_list_group_view)
            val iconGroup = view.findViewById(R.id.image_view_icon) as ImageView
            val textGroup = view.findViewById(R.id.text_view_title) as TextView

            iconGroup.setImageResource(group.iconResId)
            textGroup.text = context.getString(group.nameResId)
        }

        val imageViewIndicator = view.findViewById(R.id.image_view_indicator) as ImageView
        if (getChildrenCount(groupPos) == 0) {
            imageViewIndicator.setGone(true)
        } else {
            if (isExpanded) {
                imageViewIndicator.setImageResource(R.drawable.ic_arrow_up)
            } else {
                imageViewIndicator.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        return view
    }

    override fun getChildView(groupPosition: Int, childPos: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        var view = convertView
        if (view == null) {
            view = parent.inflate(R.layout.exp_list_child_view)
        }

        val textChild = view.findViewById(R.id.textChild) as TextView
        val imageView = view.findViewById(R.id.imageView) as ImageView

        if (isLastChild) {
            textChild.text = context.getString(R.string.setting_categories)
            imageView.setImageResource(R.drawable.ic_settings)
            return view
        }

        val category = categories[childPos]
        imageView.setImageResource(R.drawable.ic_label)
        textChild.text = category.title

        return view
    }

    override fun isChildSelectable(groupPos: Int, childPosition: Int) = true

    /**
     * Устанавливает список категорий
     * @param [list] список категорий
     */
    fun setOfChildren(list: List<Category>) {
        categories.clear()
        categories.addAll(list)
        notifyDataSetChanged()
    }
}