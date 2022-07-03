package com.janustech.helpsaap.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemCategoryListBinding
import com.janustech.helpsaap.databinding.ItemCategoryManageListBinding
import com.janustech.helpsaap.model.CategoryDataModel
import com.janustech.helpsaap.model.CompanyDataModel

class CategoryManageListAdapter internal constructor(private val context: Context, private val mData: List<CategoryDataModel>, val viewClickListener: (model: CategoryDataModel, position: Int) -> Unit): RecyclerView.Adapter<CategoryManageListAdapter.ViewHolder>() {

    inner class ViewHolder(itemCategoryManageListBinding: ItemCategoryManageListBinding) : RecyclerView.ViewHolder(itemCategoryManageListBinding.root) {
        private var itemCategoryManageListBinding: ItemCategoryManageListBinding? = null
        fun bind(obj: Any?, position: Int) {
            itemCategoryManageListBinding?.setVariable(BR.model, obj)
            itemCategoryManageListBinding?.executePendingBindings()

            itemCategoryManageListBinding?.itemRemove?.setOnClickListener {
                viewClickListener(obj as CategoryDataModel, position)
            }

            itemCategoryManageListBinding?.bgView?.apply {
                if ((obj as CategoryDataModel).type == "0"){
                    setBackgroundResource(R.drawable.rounded_rect_green_filled_less_radii)
                }else{
                    setBackgroundResource(R.drawable.rounded_rect_orange_filled_less_radii)
                }
            }
        }

        init {
            this.itemCategoryManageListBinding = itemCategoryManageListBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCategoryManageListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_category_manage_list, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position], position)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}