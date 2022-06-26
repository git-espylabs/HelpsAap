package com.janustech.helpsaap.ui.startup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItLanguageBinding
import com.janustech.helpsaap.databinding.RowItemChangeLanguageBinding
import com.janustech.helpsaap.model.LanguageDataModel

class LanguageChangeAdpater(private val context: Context, private val mData: List<LanguageDataModel>, val languageSelectListener: (model: LanguageDataModel) -> Unit): RecyclerView.Adapter<LanguageChangeAdpater.ViewHolder>() {

    inner class ViewHolder(rowItemChangeLanguageBinding: RowItemChangeLanguageBinding) : RecyclerView.ViewHolder(rowItemChangeLanguageBinding.root) {
        private var rowItemChangeLanguageBinding: RowItemChangeLanguageBinding? = null
        fun bind(obj: Any?, languageSelectListener: (model: LanguageDataModel) -> Unit) {
            rowItemChangeLanguageBinding?.setVariable(BR.model, obj)
            rowItemChangeLanguageBinding?.executePendingBindings()



            rowItemChangeLanguageBinding?.root?.setOnClickListener {
                val animation: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.click
                )
                it?.startAnimation(animation)
                languageSelectListener(obj as LanguageDataModel)
            }
        }

        init {
            this.rowItemChangeLanguageBinding = rowItemChangeLanguageBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowItemChangeLanguageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_change_language, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData[position], languageSelectListener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}