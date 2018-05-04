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

package sinyuk.com.fanfou.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * Created by sinyuk on 2017/11/28.
 *
 */


fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelFactory: ViewModelProvider.Factory, viewModelClass: Class<T>) = ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)


fun <T : ViewModel> Fragment.obtainViewModelFromActivity(viewModelFactory: ViewModelProvider.Factory, viewModelClass: Class<T>) =
        ViewModelProviders.of(this.activity as AppCompatActivity, viewModelFactory).get(viewModelClass)


fun <T : ViewModel> Fragment.obtainViewModel(viewModelFactory: ViewModelProvider.Factory, viewModelClass: Class<T>) =
        ViewModelProviders.of(this, viewModelFactory).get(viewModelClass)

fun AppCompatActivity.addFragment(@IdRes res: Int, fragment: Fragment, addToBackStack: Boolean = false) {
    if (addToBackStack) {
        supportFragmentManager.beginTransaction()
                .add(res, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
                .commit()
    } else {
        supportFragmentManager.beginTransaction()
                .add(res, fragment, fragment.javaClass.simpleName)
                .disallowAddToBackStack()
                .commit()
    }
}