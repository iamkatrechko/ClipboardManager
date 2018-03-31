package com.iamkatrechko.clipboardmanager.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.view.adapter.navigation.NavGroups

/**
 * Адаптер списка бокового меню
 * @author iamkatrechko
 *         Date: 03.11.2017
 */
class NavigationMenuAdapter : BaseExpandableListAdapter() {

    /** Список категорий */
    private var categories = ArrayList<Category>()

    override fun getGroupCount(): Int {
        return NavGroups.values().size
    }

    override fun getChildrenCount(groupPos: Int): Int {
        return if (getGroup(groupPos) == NavGroups.CATEGORIES) {
            categories.size + 1
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
            val iconGroup = view.findViewById<ImageView>(R.id.image_view_icon) as ImageView
            val textGroup = view.findViewById<TextView>(R.id.text_view_title) as TextView

            iconGroup.setImageResource(group.iconResId)
            textGroup.text = context.getString(group.nameResId)
        }

        val imageViewIndicator = view!!.findViewById<ImageView>(R.id.image_view_indicator) as ImageView
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

    override fun getChildView(groupPosition: Int, childPos: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.exp_list_child_view, null)
        }

        val textChild = view!!.findViewById<TextView>(R.id.textChild) as TextView
        val imageView = view.findViewById<ImageView>(R.id.imageView) as ImageView

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

    override fun isChildSelectable(groupPos: Int, childPosition: Int): Boolean {
        return true
    }

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