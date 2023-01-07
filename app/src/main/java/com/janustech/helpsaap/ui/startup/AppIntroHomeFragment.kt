package com.janustech.helpsaap.ui.startup

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.janustech.helpsaap.R
import com.janustech.helpsaap.databinding.FragmentAppIntroHomeBinding
import com.janustech.helpsaap.extension.launchActivity
import com.janustech.helpsaap.map.toAdsDataModel
import com.janustech.helpsaap.map.toCategoryDataModel
import com.janustech.helpsaap.map.toDealsOfDayDataModel
import com.janustech.helpsaap.map.toLanguageDataModel
import com.janustech.helpsaap.model.*
import com.janustech.helpsaap.network.Status
import com.janustech.helpsaap.preference.AppPreferences
import com.janustech.helpsaap.ui.base.BaseFragmentWithBinding
import com.janustech.helpsaap.ui.home.AppHomeActivity
import com.janustech.helpsaap.ui.home.EditLocationBottomSheetDialogFragment
import com.janustech.helpsaap.ui.profile.LoginActivity
import com.janustech.helpsaap.utils.EditLocationListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class AppIntroHomeFragment: BaseFragmentWithBinding<FragmentAppIntroHomeBinding>(R.layout.fragment_app_intro_home), EditLocationListener {

    private val appIntroViewModel: AppIntroViewModel by activityViewModels()

    lateinit var categoriesListAdapter: ArrayAdapter<Any>
    private var categoriesSuggestionList = listOf<CategoryDataModel>()
    private var autoCompleteTextHandler: Handler? = null

    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300

    private var timerAdsList: Timer? = null
    private var currentPageAdsList: Int = 0
    private val handlerAdsList = Handler(Looper.getMainLooper())

    private var timerDeals: Timer? = null
    private var currentPageDeals: Int = 0
    private val handlerDeals = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = appIntroViewModel
            btnLogin.setOnClickListener { activity?.launchActivity<LoginActivity>()}
            btnRegister.setOnClickListener {
                (activity as AppIntroActivity).showProgress()
                activity?.launchActivity<SignupActivity>()
            }
            tvLocation.setOnClickListener {
                EditLocationBottomSheetDialogFragment(appIntroViewModel, this@AppIntroHomeFragment).show(
                    childFragmentManager,
                    "EditLocationFragment"
                )
            }

            btnProfileIco.setOnClickListener {
                showPopup(it)
            }

            if (AppPreferences.userId.isNotEmpty()){
                btnLogin.visibility = View.INVISIBLE
                btnLogin.isEnabled = false

                btnProfileIco.visibility = View.VISIBLE
                btnProfileIco.isEnabled = true

                groupSignupPrompt.visibility = View.INVISIBLE
                btnProfile.visibility = View.VISIBLE
            }else{
                btnLogin.visibility = View.VISIBLE
                btnLogin.isEnabled = true

                btnProfileIco.visibility = View.INVISIBLE
                btnProfileIco.isEnabled = false

                groupSignupPrompt.visibility = View.VISIBLE
                btnProfile.visibility = View.INVISIBLE
            }

            helplineLay.setOnClickListener {
                HelplineContactBottomSheetFragment().show(
                    childFragmentManager,
                    "HelpLineContactFragment"
                )
            }

            btnProfile.setOnClickListener {
                activity?.launchActivity<AppHomeActivity>()
                activity?.finish()
            }

            tvLangName.also {
                val str = appIntroViewModel.userLanguage
                val content = SpannableString(str)
                content.setSpan(UnderlineSpan(), 0, str.length, 0)
                it.text = str
                it.setOnClickListener {
                    appIntroViewModel.getEditLanguages()
                }
            }

            tvSettings.setOnClickListener {

                showSettingsPopup(it)

            }

        }

        setObserver()
        setSearchList()
    }

    private fun showSettingsPopup(view: View) {
        val popup = PopupMenu(activity!!, view)
        popup.inflate(R.menu.header_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.privacy_policy -> {
                    showSettingsPopDialog("Privacy Policy","privacypolicy")
                }
                R.id.terms -> {
                    showSettingsPopDialog("Terms and Conditions","terms")
                }
                R.id.refund_policy -> {
                    showSettingsPopDialog("Refund Policy","refundpolicy")
                }
                R.id.products_pricing -> {
                    showSettingsPopDialog("Products and Pricing","productpricing")
                }
                R.id.about_us -> {

                    showSettingsPopDialog("About Us","aboutus")
                }
                R.id.contact_us -> {
                    showSettingsPopDialog("Contact Us","contactus")
                }
            }

            true
        }

        popup.show()
    }

    override fun onPause() {
        super.onPause()

        timerAdsList?.apply {
            cancel()
            purge()
        }

        timerDeals?.apply {
            cancel()
            purge()
        }

        timerAdsList = null
        timerDeals = null
    }

    override fun onResume() {
        super.onResume()
        if (appIntroViewModel.userSelectedCategory.isNotEmpty()){
            appIntroViewModel.getDealsOfTheDay(appIntroViewModel.userSelectedCategory)

        }
    }

    override fun onStop() {
        super.onStop()
        appIntroViewModel._categoriesReceiver.value = null
        appIntroViewModel._languageEditListReceiver.value = null
        (activity as AppIntroActivity).hideProgress()
    }

    override fun onLocationSelected(location: LocationDataModel) {
        location.let {
            appIntroViewModel.userLocationName = it.toString()
            appIntroViewModel.userLocationId = it.id
            binding.tvLocation.text = it.toString()

            if (appIntroViewModel.userSelectedCategory.isNotEmpty()){
                appIntroViewModel.getDealsOfTheDay(appIntroViewModel.userSelectedCategory)

            }
        }
    }

    private fun setLanguage(languageModel: LanguageDataModel){
        binding.apply {
            tvLangName.also {
                val str = languageModel.lang
                val content = SpannableString(str)
                content.setSpan(UnderlineSpan(), 0, str.length, 0)
                it.text = str
            }
        }
    }

    private fun setObserver(){

        lifecycleScope.launch {
            appIntroViewModel._langugaeUpdatedFlow.collect {
                setLanguage(it)
            }
        }

        appIntroViewModel.dealsOfDay.observe(viewLifecycleOwner){
            if(it!=null) {
                when (it.status) {
                    Status.SUCCESS -> {
                        (activity as AppIntroActivity).hideProgress()
                        val dataList = it.data?.data
                        setDealsOfDay(
                            dataList?.map {
                                    dOd -> dOd.toDealsOfDayDataModel() }
                            ?: listOf())
                        appIntroViewModel.getAdsList(appIntroViewModel.userSelectedCategory)

                    }
                    Status.LOADING -> {
                        (activity as AppIntroActivity).showProgress()
                    }
                    else -> {
                        (activity as AppIntroActivity).hideProgress()
                        setDealsOfDay(listOf())
                        appIntroViewModel.getAdsList(appIntroViewModel.userSelectedCategory)
                    }
                }
            }
        }

        appIntroViewModel.adsListReceiver.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    (activity as AppIntroActivity).hideProgress()
                    val dataList = it.data?.data
                    setAdsList(dataList?.map { adls -> adls.toAdsDataModel() } ?: listOf())
                }
                Status.LOADING -> {
                    (activity as AppIntroActivity).showProgress()
                }
                else ->{
                    (activity as AppIntroActivity).hideProgress()
                    setAdsList(listOf())
                }
            }
        }

        appIntroViewModel.categoriesReceiver?.observe(viewLifecycleOwner){ result ->
            result?.let {
                when(it.status){
                    Status.SUCCESS ->{
                        (activity as AppIntroActivity).hideProgress()
                        val dataList = it.data?.data
                        categoriesSuggestionList = dataList?.map { dat -> dat.toCategoryDataModel() } ?: listOf()
                        categoriesListAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            categoriesSuggestionList
                        )
                        binding.actSearch.apply {
                            setAdapter(categoriesListAdapter)
                            showDropDown()
                        }
                    }
                    Status.LOADING -> {
                        (activity as AppIntroActivity).showProgress()
                    }
                    else ->{
                        (activity as AppIntroActivity).hideProgress()
//                        (activity as AppIntroActivity).showAlertDialog(it.message?:"Invalid Server Response")
                    }
                }
            }
        }

        appIntroViewModel.languageEditListReceiver?.observe(viewLifecycleOwner){ result ->
            result?.let {
                when(it.status){
                    Status.SUCCESS ->{
                        (activity as AppIntroActivity).hideProgress()
                        val dataList = it.data?.data?.map { lang -> lang.toLanguageDataModel() }
                        dataList?.let { languageDataList ->
                            if (languageDataList.isNotEmpty()){
                                ChangeLanguageBottomSheetFragment(appIntroViewModel, languageDataList).show(
                                    childFragmentManager,
                                    "ChangeLanguageFragment"
                                )
                            }
                        }

                    }
                    Status.LOADING -> {
                        (activity as AppIntroActivity).showProgress()
                    }
                    else ->{
                        (activity as AppIntroActivity).hideProgress()
                    }
                }
            }
        }

    }

    private fun setSearchList(){
        binding.ivClearSearch.setOnClickListener {
            binding.actSearch.setText("")
        }

        binding.actSearch.apply {

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    binding.apply {
                        if (s.toString().isNotEmpty()){
                            binding.ivClearSearch.visibility = View.VISIBLE
                        }else{
                            binding.ivClearSearch.visibility = View.GONE
                        }
                    }
                    if (s.toString().isNotEmpty() && s.toString().length >= 2) {
                        autoCompleteTextHandler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                        autoCompleteTextHandler?.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY)
                    }
                }
            })

            onItemClickListener =
                OnItemClickListener { _, _, pos, _ ->
                    val catData = (categoriesListAdapter.getItem(pos) as CategoryDataModel)

                    catData.let {
                        appIntroViewModel.userSelectedCategory = it.id
                        appIntroViewModel.userSelectedCategoryName = it.category
                        (activity as AppIntroActivity).hideKeyboard()
                        setText("")
                        findNavController().navigate(AppIntroHomeFragmentDirections.actionAppIntroHomeToAppIntroSearchList())
                    }
                }

            autoCompleteTextHandler = Handler(Looper.getMainLooper()) { msg ->
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(text)) {
                        appIntroViewModel.getCategories(text.toString())
                    }
                }
                false
            }
        }
    }

    private fun setDealsOfDay(dOdList: List<DealOfDayDataModel>?){
        if (dOdList != null && dOdList.isNotEmpty()){
            binding.apply {
                tvPromptDeals.visibility = View.VISIBLE
                rvDealOfDay.visibility = View.VISIBLE
                val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        currentPageDeals = position
                    }
                }

                rvDealOfDay.adapter = DealsListPagerAdapter(requireContext(), dOdList){ userId ->
                    findNavController().navigate(AppIntroHomeFragmentDirections.actionAppIntroHomeToDealOrAdOwnerDetailsFragment(userId?:"0"))
                }


                rvDealOfDay.registerOnPageChangeCallback(slidingCallback)
                startAutoSwipeDeals(dOdList.size)
            }
        }else{
            binding.apply {
                tvPromptDeals.visibility = View.GONE
                rvDealOfDay.visibility = View.GONE
            }
        }

    }

    private fun setAdsList(adsList: List<AdsDataModel>?){
        if (adsList != null && adsList.isNotEmpty()){
            binding.apply {
                tvPromptAds.visibility = View.VISIBLE
                rvAds.visibility = View.VISIBLE
                val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        currentPageAdsList = position
                    }
                }
                rvAds.adapter = AdsListPagerAdapter(requireContext(), adsList){ userId ->
                    findNavController().navigate(AppIntroHomeFragmentDirections.actionAppIntroHomeToDealOrAdOwnerDetailsFragment(userId?:"0"))
                }
                rvAds.registerOnPageChangeCallback(slidingCallback)
                startAutoSwipeAds(adsList.size)
            }

        }else{
            binding.apply {
                tvPromptAds.visibility = View.GONE
                rvAds.visibility = View.GONE
            }
        }
    }

    private fun startAutoSwipeAds(pageSize:Int) {
        val update = Runnable {
            if (currentPageAdsList == pageSize) {
                currentPageAdsList = 0
            }

            binding.rvAds.setCurrentItem(currentPageAdsList++, true)
        }

        timerAdsList = timerAdsList ?: Timer()
        timerAdsList?.schedule(object : TimerTask() {
            override fun run() {
                handlerAdsList.post(update)
            }
        }, 4000L, 4000L)
    }

    private fun startAutoSwipeDeals(pageSize:Int) {
        val update = Runnable {
            if (currentPageDeals == pageSize) {
                currentPageDeals = 0
            }

            binding.rvDealOfDay.setCurrentItem(currentPageDeals++, true)
        }

        timerDeals = timerDeals ?: Timer()
        timerDeals?.schedule(object : TimerTask() {
            override fun run() {
                handlerDeals.post(update)
            }
        }, 4000L, 4000L)
    }

    private fun showPopup(v : View){
        val popup = PopupMenu(requireActivity(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_sub_home, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.actionLogout-> {
                    Handler(Looper.getMainLooper()).post {
                        AppPreferences.clearAll()
                        findNavController().navigate(AppIntroHomeFragmentDirections.actionAppIntroHomeToSelectLocationFragment())
                    }
                }
                R.id.actionProfile ->{
                    activity?.launchActivity<AppHomeActivity>()
                    activity?.finish()
                }
            }
            true
        }
        popup.show()
    }



    private fun showSettingsPopDialog(header :String?,page: String?) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fragment_terms_conditions)
        val title = dialog.findViewById(R.id.title) as TextView
        if(TextUtils.equals(page,"privacypolicy")) {
            title.text = header
        }
        else if(TextUtils.equals(page,"terms")) {
            title.text = header
        }
        else if(TextUtils.equals(page,"refundpolicy")) {
            title.text = header
        }
        else if(TextUtils.equals(page,"productpricing")) {
            title.text = header
        }
        else if(TextUtils.equals(page,"aboutus")) {
            title.text = header
        }
        else if(TextUtils.equals(page,"contactus")) {
            title.text = header
        }
        val tv_close = dialog.findViewById(R.id.tv_close) as TextView
        val webView = dialog.findViewById(R.id.webView) as WebView
        val layoutParams = dialog.window!!.attributes
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.attributes = layoutParams
        var tc_url=""

        if(TextUtils.equals(page,"privacypolicy")) {
            tc_url = "https://helpsaap.com/privacy"
        }
        else if(TextUtils.equals(page,"terms")) {
            tc_url = "http://helpsaap.com/terms"
        }
        else if(TextUtils.equals(page,"refundpolicy")) {
            tc_url = "https://helpsaap.com/refund"
        }
        else if(TextUtils.equals(page,"productpricing")) {
            tc_url = "https://helpsaap.com/product_pricing"
        }
        else if(TextUtils.equals(page,"aboutus")) {
            tc_url = "https://helpsaap.com/aboutus"
        }
        else if(TextUtils.equals(page,"contactus")) {
            tc_url = "https://helpsaap.com/contact"
        }

        webView.apply {
            loadUrl(tc_url)
            settings.also {
                it.loadsImagesAutomatically = true
                it.javaScriptEnabled = true;
            }
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    /*if (activity is AppHomeActivity) {
                        (activity as AppHomeActivity).hideProgress()
                    } else if (activity is SignupActivity) {
                        (activity as SignupActivity).hideProgress()
                    }*/
                }
            }
        }
        tv_close.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }


}