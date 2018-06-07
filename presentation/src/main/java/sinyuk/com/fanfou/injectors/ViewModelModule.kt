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

package sinyuk.com.fanfou.injectors

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import sinyuk.com.fanfou.ui.FanfouViewModelFactory
import sinyuk.com.fanfou.ui.player.PlayerViewModel
import sinyuk.com.fanfou.ui.sign.SignViewModel
import sinyuk.com.fanfou.ui.status.StatusesViewModel

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
@Module
abstract class ViewModelModule {
    @Suppress("unused")
    @Binds
    @IntoMap
    @ViewModelKey(SignViewModel::class)
    abstract fun signViewModel(viewModel: SignViewModel): ViewModel

    @Suppress("unused")
    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun playerViewModel(viewModel: PlayerViewModel): ViewModel


    @Suppress("unused")
    @Binds
    @IntoMap
    @ViewModelKey(PhotoViewModel::class)
    abstract fun photoViewModel(viewModel: PhotoViewModel): ViewModel

    @Suppress("unused")
    @Binds
    @IntoMap
    @ViewModelKey(StatusesViewModel::class)
    abstract fun statusesViewModel(viewModel: StatusesViewModel): ViewModel

    @Suppress("unused")
    @Binds
    abstract fun bindViewModelFactory(factory: FanfouViewModelFactory): ViewModelProvider.Factory
}