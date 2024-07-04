package com.fivestarmind.android.mvp.adapter.sectionAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fivestarmind.android.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    final View rootView;
    final ImageView ivFavProgram,ivPlayVideo;
    final ImageView ivBookMark;
    final ConstraintLayout clBookmarks;
    final TextView tvFavProgramName;
    final TextView tvDiscription;
    final TextView tvTimeDuration;
    final ProgressBar pbProgram,pbImage;

    ItemViewHolder(@NonNull final View view) {
        super(view);

        rootView = view;
        ivFavProgram = rootView.findViewById(R.id.ivFavProgram);
        ivBookMark = rootView.findViewById(R.id.ivBookMark);
        clBookmarks = rootView.findViewById(R.id.clBookmarks);
        tvFavProgramName = rootView.findViewById(R.id.tvFavProgramName);
        tvDiscription = rootView.findViewById(R.id.tvDiscription);
        tvTimeDuration = rootView.findViewById(R.id.tvTimeDuration);
        pbProgram = rootView.findViewById(R.id.pbProgram);
        pbImage = rootView.findViewById(R.id.pbImage);
        ivPlayVideo = rootView.findViewById(R.id.ivPlayVideo);
    }
}