package com.vb.anonymous.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.vb.anonymous.R
import com.vb.anonymous.databinding.ActivityMainBinding
import com.vb.anonymous.domain.repository.BoardRepo
import com.vb.anonymous.ui.factory.MainVMFactory
import com.vb.anonymous.ui.main.dialog.ResourcesDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var boardRepo: BoardRepo

    val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var refreshingItem: MenuItem? = null

    private val mainVM: MainVM by viewModels { MainVMFactory(boardRepo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.mainToolbar)
        initRefreshGesture()
        initKeyboardSearch()
        with(mainBinding.mainWebView) {
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.mediaPlaybackRequiresUserGesture = false
            scrollIndicators = View.SCROLL_INDICATOR_RIGHT
            var timeForClick = 0L
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN)
                    timeForClick = System.currentTimeMillis()
                val hr = (v as WebView).hitTestResult
                if (hr.type == 8 && hr.extra != null && event.action == MotionEvent.ACTION_UP
                    && System.currentTimeMillis() - timeForClick < 100
                ) {
                    v.performClick()
                    ResourcesDialog(hr.extra).show(supportFragmentManager, "resDialog")
                }
                false
            }
            initUpDownScrollImg()

            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (url != null) if (mainVM.currentUrl != url) mainVM.totalThreadMessages = 0
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (url != null) {
                        if (url.length > 25) {
                            val pathSegments = Uri.parse(url).pathSegments
                            mainVM.currentUrl = url
                            if (pathSegments.size == 3) subscribeToThreadPosts(
                                pathSegments[0],
                                pathSegments[2].split(".")[0]
                            )
                        }
                    }
                    if (mainBinding.mainCircularPb.visibility != View.GONE)
                        mainBinding.mainCircularPb.visibility = View.GONE
                    if (mainBinding.mainRefreshLayout.isRefreshing) mainBinding.mainRefreshLayout.isRefreshing =
                        false
                    if (refreshingItem != null) {
                        if (refreshingItem!!.actionView != null) {
                            refreshingItem!!.actionView.animation.cancel()
                            refreshingItem!!.actionView = null
                        }
                    }
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (mainVM.currentThreadObserver != null) mainVM.currentThreadObserver!!.removeObservers(
                        this@MainActivity
                    )
                    return false
                }

            }
            loadUrl("https://2ch.hk")
        }
    }

    private fun subscribeToBoards() = mainVM.getBoards().observe(this) { initAutoCompleteTV(it) }

    private fun subscribeToThreadPosts(board: String, threadId: String) {
        mainVM.currentThreadObserver = mainVM.getThreadPosts(board, threadId)
        mainVM.currentThreadObserver!!.observe(this) {
            if (it.postsNumber - mainVM.totalThreadMessages > 0) {
                Snackbar.make(
                    mainBinding.mainCoordinator,
                    "Новых сообщений в треде: ${it.postsNumber - mainVM.totalThreadMessages}",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.orange_200))
                    setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.black))
                }.show()
                mainVM.totalThreadMessages = it.postsNumber
            }
        }
    }

    private fun initAutoCompleteTV(boardsList: MutableList<String>) {
        mainBinding.mainActv.setAdapter(
            ArrayAdapter(
                this, android.R.layout.simple_list_item_1, boardsList
            )
        )
        mainBinding.mainActv.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (v.text.length < 5) {
                        mainBinding.mainWebView.loadUrl("https://2ch.hk/${v.text}")
                        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                            mainBinding.mainCoordinator.windowToken, 0
                        )
                    }
                    true
                }
                else -> false
            }
        }
        mainBinding.mainActv.setOnItemClickListener { parent, _, position, _ ->
            mainBinding.mainWebView.loadUrl(
                "https://2ch.hk/${parent.adapter.getItem(position)}"
            )
            (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                mainBinding.mainCoordinator.windowToken, 0
            )
        }
    }

    private fun initUpDownScrollImg() {
        mainBinding.mainUpImg.setOnClickListener { mainBinding.mainWebView.pageUp(true) }
        mainBinding.mainDownImg.setOnClickListener { mainBinding.mainWebView.pageDown(true) }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_item -> {
                if (mainBinding.mainSearchContainer.visibility != View.VISIBLE)
                    mainBinding.mainSearchContainer.visibility = View.VISIBLE
                else {
                    mainBinding.mainSearchContainer.visibility = View.INVISIBLE
                    mainBinding.mainWebView.findAllAsync("")
                }
                true
            }
            R.id.refresh_item -> {
                if (item.actionView != null) return true
                if (refreshingItem == null) refreshingItem = item
                mainBinding.mainWebView.reload()
                item.actionView = ImageView(this).apply {
                    setImageDrawable(item.icon)
                    animation =
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.refreshing_anim)
                            .also {
                                it.repeatCount = Animation.INFINITE
                                it.start()
                            }
                }
                true
            }
            R.id.vpn_item -> {
                startActivity(Intent(Settings.ACTION_VPN_SETTINGS))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        subscribeToBoards()
    }

    private fun initRefreshGesture() {
        mainBinding.mainRefreshLayout.setOnRefreshListener {
            mainBinding.mainWebView.reload()
        }
    }

    override fun onBackPressed() {
        if (mainVM.currentThreadObserver != null) mainVM.currentThreadObserver!!.removeObservers(
            this@MainActivity
        )
        if (mainBinding.mainWebView.canGoBack()) mainBinding.mainWebView.goBack()
        else super.onBackPressed()
    }

    private fun initKeyboardSearch() {
        mainBinding.mainSearchEdit.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mainBinding.mainWebView.findAllAsync(v.text.toString())
                (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    mainBinding.mainCoordinator.windowToken, 0
                )
                true
            } else false
        }
    }

    fun onSearchBtnClicked(view: View) {
        if (mainBinding.mainSearchEdit.text.toString().isNotEmpty()) {
            mainBinding.mainWebView.findAllAsync(mainBinding.mainSearchEdit.text.toString())
        }
    }

    fun onDownSearchImgClicked(view: View) {
        mainBinding.mainWebView.findNext(true)
    }

    fun onUpSearchImgClicked(view: View) {
        mainBinding.mainWebView.findNext(false)
    }
}