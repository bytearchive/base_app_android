/*
 * Copyright 2016 FuckBoilerplate
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

package app.presentation.foundation.views;

import android.app.Activity;

import com.trello.rxlifecycle.FragmentEvent;

import rx.Observable;

public interface BaseViewFragment {
    void showToast(Observable<String> oTitle);
    void showSnackBar(Observable<String> oTitle);
    void showLoading();
    void hideLoading();
    Observable<FragmentEvent> lifeCycle();
    Activity getActivity();
}