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

package sinyuk.com.common.realm

import android.arch.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

class RealmLiveData<T : RealmModel>(private val realmResults: RealmResults<T>)
    : LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { results -> value = results }

    override fun onActive() {
        realmResults.addChangeListener(listener)
    }

    override fun onInactive() {
        realmResults.removeChangeListener(listener)
    }
}