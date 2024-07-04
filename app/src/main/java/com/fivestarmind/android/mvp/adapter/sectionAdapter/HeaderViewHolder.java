package com.fivestarmind.android.mvp.adapter.sectionAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fivestarmind.android.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    final View rootView;

    final TextView tvTitle;
    final TextView tvSeeAll;

    HeaderViewHolder(@NonNull final View view) {
        super(view);
        rootView = view;
        tvTitle = view.findViewById(R.id.tvHeader);
        tvSeeAll = view.findViewById(R.id.tvSeeAll);
    }
}
