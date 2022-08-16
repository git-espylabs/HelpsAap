package com.janustech.helpsaap.ui.startup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItemCompanyListBinding
import com.janustech.helpsaap.databinding.ItemDealOfDayBinding
import com.janustech.helpsaap.model.CompanyDataModel

class CompanyListAdapter internal constructor(private val mData: List<CompanyDataModel>, val viewClickListener: (model: CompanyDataModel, action: String) -> Unit): RecyclerView.Adapter<CompanyListAdapter.ViewHolder>() {

    inner class ViewHolder(itemCompanyListBinding: ItemCompanyListBinding) : RecyclerView.ViewHolder(itemCompanyListBinding.root) {
        private var itemCompanyListBinding: ItemCompanyListBinding? = null
        fun bind(obj: Any?, viewClickListener: (model: CompanyDataModel, action: String) -> Unit) {
            itemCompanyListBinding?.setVariable(BR.model, obj)
            itemCompanyListBinding?.executePendingBindings()

            itemCompanyListBinding?.ivCall?.setOnClickListener {
                viewClickListener(obj as CompanyDataModel, "call")
            }

            itemCompanyListBinding?.ivShare?.setOnClickListener {
                viewClickListener(obj as CompanyDataModel, "share")
            }

            itemCompanyListBinding?.tvLocation?.setOnClickListener {
                viewClickListener(obj as CompanyDataModel, "loc")
            }

            itemCompanyListBinding?.ivWeb?.setOnClickListener {
                viewClickListener(obj as CompanyDataModel, "web")
            }

            itemCompanyListBinding?.ivWhatsap?.setOnClickListener {
                viewClickListener(obj as CompanyDataModel, "whatsap")
            }

        }

        init {
            this.itemCompanyListBinding = itemCompanyListBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCompanyListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_company_list, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position], viewClickListener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}