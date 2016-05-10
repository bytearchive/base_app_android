/*
 * Copyright 2015 RefineriaWeb
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

package app.presentation.sections.user_demo.list;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.base_app_android.R;

import app.domain.user_demo.User;
import app.presentation.foundation.views.BaseFragment;
import app.presentation.foundation.views.LayoutResFragment;
import app.presentation.foundation.views.RecyclerViewPaginated;
import app.presentation.sections.user_demo.UserViewGroup;
import butterknife.Bind;
import library.recycler_view.OkRecyclerViewAdapter;


@LayoutResFragment(R.layout.users_fragment)
public class UsersFragment extends BaseFragment<UsersPresenter>  {
    @Bind(R.id.rv_users) protected SuperRecyclerView rv_users;
    private OkRecyclerViewAdapter<User, UserViewGroup> adapter;
    private RecyclerViewPaginated<User, UserViewGroup> recyclerViewPaginated;

    @Override protected void injectDagger() {
        getApplicationComponent().inject(this);
    }

    @Nullable
    @Override protected String getScreenNameForGoogleAnalytics() {
        return this.getClass().getSimpleName();
    }

    @Override protected void initViews() {
        super.initViews();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        //gridLayoutManager.setReverseLayout(true);
        rv_users.setLayoutManager(gridLayoutManager);

        adapter = new OkRecyclerViewAdapter<User, UserViewGroup>() {
            @Override protected UserViewGroup onCreateItemView(ViewGroup parent, int viewType) {
                return new UserViewGroup(getActivity());
            }
        };

        adapter.setOnItemClickListener((user, userViewGroup) -> {
            presenter.dataForNextScreen(user)
                    .compose(safelyReport())
                    .subscribe(_I -> wireframe.userScreen());
        });

        recyclerViewPaginated = new RecyclerViewPaginated(rv_users, adapter);
        recyclerViewPaginated.setLoaderPager(user -> presenter.nextPage(user).compose(safelyReport()));
        recyclerViewPaginated.setResetPager(() -> presenter.refreshList().compose(safelyReport()));
    }

    @Override protected void onSyncScreen() {
        super.onSyncScreen();
    }
}
