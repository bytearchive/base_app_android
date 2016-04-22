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

package app.presentation.foundation;

import android.support.annotation.StringRes;

import com.trello.rxlifecycle.RxLifecycle;

import org.base_app_android.BuildConfig;
import org.base_app_android.R;

import app.presentation.foundation.views.BaseViewFragment;
import app.presentation.sections.Wireframe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.CompositeException;
import rx.schedulers.Schedulers;

public abstract class PresenterFragment<V extends BaseViewFragment> {
    protected V view;
    protected final Wireframe wireframe;

    protected PresenterFragment(Wireframe wireframe) {
        this.wireframe = wireframe;
    }

    public void attachView(V view) {
        this.view = view;
    }

    public void back() {
        wireframe.popCurrentScreen();
    }

    protected <T> Observable.Transformer<T, T> safely() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindFragment(view.lifeCycle()));
    }

    protected <T> Observable.Transformer<T, T> safelyReport() {
        return observable -> observable.compose(safely())
                .doOnError(throwable -> view.showToast(parseException(throwable)))
                .onErrorResumeNext(throwable -> Observable.empty());
    }

    protected <T> Observable.Transformer<T, T> safelyReportSnackbar() {
        return observable -> observable.compose(safely())
                .doOnError(throwable -> {
                    if (BuildConfig.DEBUG) view.showToast(parseException(throwable));
                    else view.showSnackBar(parseException(throwable));
                })
                .onErrorResumeNext(throwable -> Observable.empty());
    }

    protected <T> Observable.Transformer<T, T> applyLoading() {
        return observable -> observable.doOnSubscribe(() -> view.showLoading())
                .doOnCompleted(() -> view.hideLoading());
    }

    public Observable<String> parseException(Throwable throwable) {
        if (!BuildConfig.DEBUG) return Observable.just(getString(R.string.errors_happen));

        String message = throwable.getMessage();

        if(throwable.getCause() != null) message += System.getProperty("line.separator") + throwable.getCause().getMessage();

        if (throwable instanceof CompositeException) {
            message += System.getProperty("line.separator");
            CompositeException compositeException = (CompositeException) throwable;

            for (Throwable exception : compositeException.getExceptions()) {
                String exceptionName = exception.getClass().getSimpleName();
                String exceptionMessage = exception.getMessage() != null ? exception.getMessage() : "";
                message += exceptionName + " -> " + exceptionMessage + System.getProperty("line.separator");
            }
        }

        return Observable.just(message);
    }


    protected String getString(@StringRes int resId) {
        return view.getActivity().getString(resId);
    }
}