package com.alexander.documents.ui.groups

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_main.*
import com.alexander.documents.R
import com.alexander.documents.api.CitiesRequest
import com.alexander.documents.api.GroupsRequest
import com.alexander.documents.entity.City
import com.alexander.documents.entity.Group
import com.alexander.documents.ui.markets.MarketListActivity

/**
 * author alex
 */
class MainActivity : AppCompatActivity(), CitiesBottomSheetDialogFragment.CityClickListener {

    private val groupsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        GroupsAdapter(::onGroupClick)
    }

    private var currentCityId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            containerView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        if (!VK.isLoggedIn()) {
            VK.login(this, listOf(VKScope.GROUPS, VKScope.MARKET))
        } else {
            init()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                init()
            }

            override fun onLoginFailed(errorCode: Int) {
                showAuthError()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCityClick(city: City) {
        if (currentCityId != city.id) {
            currentCityId = city.id
            requestGroups(currentCityId)
            toolbarTitleView.text = getString(R.string.main_title, city.title)
        }
    }

    private fun init() {
        recyclerViewGroups.adapter = groupsAdapter
        containerView.setOnRefreshListener {
            requestGroups(currentCityId)
        }
        requestGroups(1)
        toolbarTitleView.text = getString(R.string.main_title, "Москва")
        toolbarButton.setOnClickListener { requestCities() }
    }

    private fun requestGroups(cityId: Int) {
        containerView.isRefreshing = true
        VK.execute(GroupsRequest(cityId), object : VKApiCallback<List<Group>> {
            override fun success(result: List<Group>) {
                if (!isFinishing) {
                    containerView.isRefreshing = false
                    groupsAdapter.groups = result.toMutableList()
                }
            }

            override fun fail(error: Exception) {
                showError()
            }
        })
    }

    private fun requestCities() {
        containerView.isRefreshing = true
        VK.execute(CitiesRequest(), object : VKApiCallback<List<City>> {
            override fun success(result: List<City>) {
                if (!isFinishing) {
                    containerView.isRefreshing = false
                    result.find { it.id == currentCityId }?.isSelected = 1
                    CitiesBottomSheetDialogFragment.newInstance(result)
                        .show(supportFragmentManager, null)
                }
            }

            override fun fail(error: Exception) {
                showError()
            }
        })
    }

    private fun showError() {
        containerView.isRefreshing = false
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_message))
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showAuthError() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.error_auth_message))
            .setPositiveButton(R.string.ok) { _, _ -> VK.login(this, arrayListOf(VKScope.GROUPS, VKScope.MARKET)) }
            .setNegativeButton(R.string.cancel) { _, _ -> finish() }
            .show()
    }

    private fun onGroupClick(group: Group) {
        startActivity(
            MarketListActivity.createIntent(
                this,
                group
            )
        )
    }

    companion object {

        fun createIntent(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}
