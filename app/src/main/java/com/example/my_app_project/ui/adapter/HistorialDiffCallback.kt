package com.example.my_app_project.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.my_app_project.domain.model.HistorialItem

class HistorialDiffCallback(
    private val oldList: List<HistorialItem>,
    private val newList: List<HistorialItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
