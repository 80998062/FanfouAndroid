/*
 *   Copyright 2081 Sinyuk
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sinyuk.com.fanfou.ui.sign

import android.Manifest
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.Dimension
import android.support.annotation.VisibleForTesting
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import kotlinx.android.synthetic.main.signin_view.*
import permissions.dispatcher.*
import sinyuk.com.fanfou.R
import sinyuk.com.fanfou.domain.States
import sinyuk.com.fanfou.ext.obtainViewModel
import sinyuk.com.fanfou.ext.start
import sinyuk.com.fanfou.injectors.Injectable
import sinyuk.com.fanfou.ui.FanfouViewModelFactory
import sinyuk.com.fanfou.ui.ViewTooltip
import sinyuk.com.fanfou.ui.base.AbstractFragment
import sinyuk.com.fanfou.ui.home.HomeActivity
import sinyuk.com.fanfou.utils.showRationaleDialog
import javax.inject.Inject


/**
 * Created by sinyuk on 2018/5/4.
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│        _______. __  .__   __. ____    ____  __    __   __  ___   │
│       /       ||  | |  \ |  | \   \  /   / |  |  |  | |  |/  /   │
│      |   (----`|  | |   \|  |  \   \/   /  |  |  |  | |  '  /    │
│       \   \    |  | |  . `  |   \_    _/   |  |  |  | |    <     │
│   .----)   |   |  | |  |\   |     |  |     |  `--'  | |  .  \    │
│   |_______/    |__| |__| \__|     |__|      \______/  |__|\__\   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
 */
@RuntimePermissions
class SignInView : AbstractFragment(), Injectable {
    override fun layoutId() = R.layout.signin_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    @Inject
    lateinit var factory: FanfouViewModelFactory

    private val signViewModel by lazy { obtainViewModel(factory, SignViewModel::class.java) }

    private fun setup() {
        onFormChanged()
        togglePasswordVisibility(false)

        skip.setOnClickListener { start(HomeActivity::class) }

        account.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onFormChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        passwordToggle.isChecked = false
        passwordToggle.setOnClickListener {
            passwordToggle.toggle()
            togglePasswordVisibility(passwordToggle.isChecked)
        }

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                onFormChanged()
                passwordToggle.visibility = if (s?.isBlank() != false) View.INVISIBLE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        account.setOnFocusChangeListener { _, hasFocus ->
            accountIcon.isChecked = hasFocus
        }

        password.setOnFocusChangeListener { _, hasFocus ->
            passwordIcon.isChecked = hasFocus
        }

        account.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                password.requestFocus()
            }
            return@setOnEditorActionListener false
        }

        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) performSigningIn()
            return@setOnEditorActionListener false
        }

        button.setOnClickListener { performSigningIn() }
    }

    private fun onFormChanged() {
        val accountText = account.text.toString()
        val passwordText = password.text.toString()

        val enable = accountText.isNotBlank()
        password.isFocusableInTouchMode = enable
        password.isEnabled = enable

        button.isEnabled = (accountText.isNotBlank() && passwordText.isNotBlank())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun togglePasswordVisibility(visible: Boolean) {
        if (visible) {
            password.transformationMethod = PasswordTransformationMethod.getInstance()
            password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password.inputType = InputType.TYPE_CLASS_TEXT
        }
        val text = password.text
        password.text = text
        password.setSelection(text.length)
    }


    @Suppress("MemberVisibilityCanBePrivate")
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @NeedsPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun performSigningIn() {
        val accountText = account.text.toString()
        val passwordText = password.text.toString()
        signViewModel.signIn(accountText, passwordText)
                .observe(this@SignInView, Observer {
                    onProcessingSignIn(it?.states == States.LOADING)
                    when (it?.states) {
                        States.LOADING -> {
                        }
                        States.ERROR -> {
                            onSignInFailed(it.message)
                        }
                        States.SUCCESS -> start(HomeActivity::class)
                    }
                })
    }

    private fun onSignInFailed(message: String?) {
        ViewTooltip.on(button)
                .autoHide(true, 5000)
                .clickToHide(true)
                .align(ViewTooltip.ALIGN.CENTER)
                .position(ViewTooltip.Position.LEFT)
                .text(message ?: getString(R.string.hint_login_failed))
                .textColor(Color.WHITE)
                .textSize(Dimension.SP, 12f)
                .color(ContextCompat.getColor(context!!, R.color.colorWarning))
                .show()
    }

    @Suppress("unused")
    @OnShowRationale(Manifest.permission.ACCESS_NETWORK_STATE)
    fun showRationalesForNetworkState(request: PermissionRequest) {
        showRationaleDialog(context!!, R.string.permission_network_state, request)
    }

    @Suppress("unused")
    @OnNeverAskAgain(Manifest.permission.ACCESS_NETWORK_STATE)
    fun showNeverAskForCamera() {
        Toast.makeText(context!!, R.string.permission_neverask, Toast.LENGTH_SHORT).show()
    }

    private fun onProcessingSignIn(freeze: Boolean) {
        if (freeze) KeyboardUtil.hideKeyboard(button) // this will clear focus
        button.isEnabled = freeze
        progress.isIndeterminate = freeze
    }

    fun clearFocus() {
        account.clearFocus()
        password.clearFocus()
    }


}