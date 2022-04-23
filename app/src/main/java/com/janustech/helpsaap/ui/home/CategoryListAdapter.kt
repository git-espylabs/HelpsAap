package com.janustech.helpsaap.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemCategoryListBinding
import com.janustech.helpsaap.databinding.ItemLocationListBinding
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.model.LocationDataModel

class CategoryListAdapter internal constructor(private val mData: List<CategoryDataModel>): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    inner class ViewHolder(itemCategoryListBinding: ItemCategoryListBinding) : RecyclerView.ViewHolder(itemCategoryListBinding.root) {
        private var itemCategoryListBinding: ItemCategoryListBinding? = null
        fun bind(obj: Any?) {
            itemCategoryListBinding?.setVariable(BR.model, obj)
            itemCategoryListBinding?.executePendingBindings()
        }

        init {
            this.itemCategoryListBinding = itemCategoryListBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCategoryListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_category_list, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}