/*
 * Copyright 2017 Nimrod Dayan CodePond.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codepond.daggersample.presentation.main.di;

import org.codepond.daggersample.presentation.main.MainActivity;
import org.codepond.daggersample.presentation.main.mvp.MainActivityPresenter;
import org.codepond.daggersample.presentation.main.mvp.MainActivityView;


import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Feature level module holds all the bindings needed for this feature.
 */
@Module
public abstract class MainActivityModule {

//    @Binds abstract MainActivityView provideFeatureView(MainActivity mainActivity);
//    @Provides @Named("someId") static String provideSomeId(MainActivity mainActivity) {
//        return mainActivity.someId;
//    }
}
