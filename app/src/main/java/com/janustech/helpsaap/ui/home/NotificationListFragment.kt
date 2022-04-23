package com.janustech.helpsaap.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentNotificationBinding
import com.janustech.helpsaap.map.toNotificationDataModel
import com.janustech.helpsaap.model.NotificationDataModel
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationListFragment : BaseFragmentWithBinding<FragmentNotificationBinding>(R.layout.fragment_notification) {

    private val appHomeViewModel: AppHomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appHomeViewModel
        }

        setObserver()
        appHomeViewModel.getNotification()

    }

    private fun setObserver(){
        appHomeViewModel.notificationsListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppHomeActivity).hideProgress()
                    val dataList = it.data?.data
                    setNotificationList(dataList?.map { notify -> notify.toNotificationDataModel() }?: listOf())
                }
                Status.LOADING -> {
                    (activity as AppHomeActivity).showProgress()
                }
                else ->{
                    (activity as AppHomeActivity).hideProgress()
                    (activity as AppHomeActivity).showAlertDialog(it.message?:"Invalid Server Response")
                }
            }
        }
    }

    private fun setNotificationList(notificationList: List<NotificationDataModel>?){
        if (notificationList != null && notificationList.isNotEmpty()){
            binding.notificationAdapter = NotificationListAdapter(notificationList)
        }
    }
}