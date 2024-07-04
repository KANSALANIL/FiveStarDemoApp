package com.fivestarmind.android.mvp.adapter.sectionAdapter;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class News {

    final String header;
    final String date;
    @DrawableRes
    final int icon;

    News(@NonNull final String header, @NonNull final String date, @DrawableRes final int icon) {
        this.header = header;
        this.date = date;
        this.icon = icon;
    }
}
