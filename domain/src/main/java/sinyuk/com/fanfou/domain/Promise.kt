/*
 *
 *  * Apache License
 *  *
 *  * Copyright [2017] Sinyuk
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package sinyuk.com.fanfou.domain

/**
 * A generic class that holds a value with its loading states.
 * @param <T>
 *
 */
@Suppress("unused")
class Promise<out T>(@Suppress("MemberVisibilityCanBePrivate")
                     val states: States, val data: T?, val message: String?) {
    companion object {
        @Suppress("unused")
        fun <T> success(data: T?): Promise<T> = Promise(States.SUCCESS, data, null)

        @Suppress("unused")
        fun <T> error(msg: String?, data: T?): Promise<T> = Promise(States.ERROR, data, msg)

        @Suppress("unused")
        fun <T> loading(data: T?): Promise<T> = Promise(States.LOADING, data, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val resource = other as Promise<*>?

        if (states !== resource!!.states) {
            return false
        }
        if (if (message != null) message != resource!!.message else resource!!.message != null) {
            return false
        }
        return if (data != null) data == resource.data else resource.data == null
    }

    override fun hashCode(): Int {
        var result = states.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Promise{" +
                "states=" + states +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }
}


/**
 * Status of a promise that is provided to the UI.
 *
 * These are usually created by the Repository classes where they return
 * `LiveData<Promise<T>>` to pass back the latest data to the UI with its fetch states.
 *
 */
enum class States {
    SUCCESS,
    ERROR,
    LOADING
}