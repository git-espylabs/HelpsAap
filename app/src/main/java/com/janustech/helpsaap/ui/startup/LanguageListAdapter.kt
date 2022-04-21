package com.janustech.helpsaap.ui.startup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.ItLanguageBinding
import com.janustech.helpsaap.model.LanguageDataModel


class LanguageListAdapter internal constructor(private val mData: List<LanguageDataModel>, val languageSelectListener: (model: LanguageDataModel) -> Unit): RecyclerView.Adapter<LanguageListAdapter.ViewHolder>() {

    inner class ViewHolder(itLanguageBinding: ItLanguageBinding) : RecyclerView.ViewHolder(itLanguageBinding.root) {
        private var itLanguageBinding: ItLanguageBinding? = null
        fun bind(obj: Any?, languageSelectListener: (model: LanguageDataModel) -> Unit) {
            itLanguageBinding?.setVariable(BR.model, obj)
            itLanguageBinding?.executePendingBindings()

            itLanguageBinding?.root?.setOnClickListener {
                languageSelectListener(obj as LanguageDataModel)
            }
        }

        init {
            this.itLanguageBinding = itLanguageBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItLanguageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.it_language, parent, false
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