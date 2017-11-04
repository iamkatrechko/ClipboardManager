package com.iamkatrechko.clipboardmanager.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.CategoryCursor
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavGroups

/**
 * Адаптер списка бокового меню
 * @author iamkatrechko
 *         Date: 03.11.2017
 */
class NavigationMenuAdapter : BaseExpandableListAdapter() {

    /** Список категорий */
    private var categories: CategoryCursor? = null

    override fun getGroupCount(): Int {
        return NavGroups.values().size
    }

    override fun getChildrenCount(groupPos: Int): Int {
        return if (getGroup(groupPos) == NavGroups.CATEGORIES) {
            (categories?.count ?: 0) + 1
        } else {
            0
        }
    }

    override fun getGroup(groupPos: Int): NavGroups {
        return NavGroups.values()[groupPos]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        // В моем случае не актуально, т.к. в getChildView
        // я сразу обращаюсь к массиву со списком.
        return null
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return groupPosition.toLong() * childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPos: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View? {
        val group = getGroup(groupPos)
        val context = parent.context
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.exp_list_group_view, null)
            val iconGroup = view.findViewById(R.id.image_view_icon) as ImageView
            val textGroup = view.findViewById(R.id.text_view_title) as TextView

            iconGroup.setImageResource(group.iconResId)
            textGroup.text = context.getString(group.nameResId)
        }

        val imageViewIndicator = view!!.findViewById(R.id.image_view_indicator) as ImageView
        if (getChildrenCount(groupPos) == 0) {
            imageViewIndicator.visibility = View.GONE
        } else {
            if (isExpanded) {
                imageViewIndicator.setImageResource(R.drawable.ic_arrow_up)
            } else {
                imageViewIndicator.setImageResource(R.drawable.ic_arrow_down)
            }
        }

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.exp_list_child_view, null)
        }

        val textChild = view!!.findViewById(R.id.textChild) as TextView
        val imageView = view.findViewById(R.id.imageView) as ImageView

        if (isLastChild) {
            textChild.text = context.getString(R.string.setting_categories)
            imageView.setImageResource(R.drawable.ic_settings)
            return view
        }

        categories?.let {
            it.moveToPosition(childPosition)
            imageView.setImageResource(R.drawable.ic_label)
            textChild.text = it.title
        }

        return view
    }

    override fun isChildSelectable(groupPos: Int, childPosition: Int): Boolean {
        return true
    }

    fun setOfChildren(categoryCursor: CategoryCursor) {
        categories = categoryCursor
    }
}