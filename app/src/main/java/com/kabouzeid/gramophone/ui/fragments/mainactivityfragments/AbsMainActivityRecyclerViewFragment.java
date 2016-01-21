package com.kabouzeid.gramophone.ui.fragments.mainactivityfragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.kabouzeid.gramophone.R;
import com.kabouzeid.gramophone.interfaces.MusicServiceEventListener;
import com.kabouzeid.gramophone.util.ColorUtil;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public abstract class AbsMainActivityRecyclerViewFragment<A extends RecyclerView.Adapter, LM extends RecyclerView.LayoutManager> extends AbsMainActivityFragment implements OnOffsetChangedListener, MusicServiceEventListener {

    public static final String TAG = AbsMainActivityRecyclerViewFragment.class.getSimpleName();

    @Bind(R.id.container)
    View container;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @Bind(android.R.id.empty)
    TextView empty;

    private A adapter;
    private LM layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().addOnAppBarOffsetChangedListener(this);
        getMainActivity().addMusicServiceEventListener(this);

        setUpRecyclerView();

        checkIsEmpty();
    }

    private void setUpRecyclerView() {
        if (recyclerView instanceof FastScrollRecyclerView) {
            int accentColor = ThemeSingleton.get().positiveColor.getDefaultColor();
            ((FastScrollRecyclerView) recyclerView).setPopupBgColor(accentColor);
            ((FastScrollRecyclerView) recyclerView).setPopupTextColor(ColorUtil.getPrimaryTextColorForBackground(getActivity(), accentColor));
            ((FastScrollRecyclerView) recyclerView).setThumbColor(accentColor);
            ((FastScrollRecyclerView) recyclerView).setTrackColor(ColorUtil.getColorWithAlpha(0.12f, ColorUtil.resolveColor(getContext(), R.attr.colorControlNormal)));
        }
        invalidateLayoutManager();
        invalidateAdapter();
    }

    protected void invalidateAdapter() {
        adapter = createAdapter();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIsEmpty();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    protected void invalidateLayoutManager() {
        layoutManager = createLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
    }

    protected A getAdapter() {
        return adapter;
    }

    protected LM getLayoutManager() {
        return layoutManager;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        container.setPadding(container.getPaddingLeft(), container.getPaddingTop(), container.getPaddingRight(), getMainActivity().getTotalAppBarScrollingRange() + i);
    }

    @Override
    public void onPlayingMetaChanged() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public void onShuffleModeChanged() {

    }

    @Override
    public void onRepeatModeChanged() {

    }

    @Override
    public void enableViews() {
        super.enableViews();
        recyclerView.setEnabled(true);
    }

    @Override
    public void disableViews() {
        super.disableViews();
        recyclerView.setEnabled(false);
    }

    private void checkIsEmpty() {
        if (empty != null) {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter != null) {
                empty.setText(getEmptyMessage());
                empty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @StringRes
    protected int getEmptyMessage() {
        return R.string.empty;
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.fragment_main_activity_recycler_view;
    }

    protected abstract LM createLayoutManager();

    protected abstract A createAdapter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getMainActivity().removeOnAppBarOffsetChangedListener(this);
        getMainActivity().removeMusicServiceEventListener(this);
        ButterKnife.unbind(this);
    }
}
