package com.fivestarmind.android.mvp.adapter.sectionAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fivestarmind.android.R;
import com.fivestarmind.android.interfaces.Constants;
import com.fivestarmind.android.interfaces.SectionedRecyclerViewListener;
import com.fivestarmind.android.mvp.model.response.FilesItem;
import com.fivestarmind.android.retrofit.ApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class ListSectionAdapter extends Section {
    private final Context context;
    private final String title;
    private final int productId;
    private final String productName;
    private final String seeAll;
    private final ArrayList<FilesItem> filesItemlist;
    private final SectionedRecyclerViewListener clickListener;
    String TAG = ListSectionAdapter.class.getName();
    FilesItem mData;
    // private FilesItem data;

    public ListSectionAdapter(Context context, @NonNull final String title, String seeAll, @NonNull final ArrayList<FilesItem> list, int productId, String productName,
                              @NonNull final SectionedRecyclerViewListener clickListener) {

        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_my_favorite)
                .headerResourceId(R.layout.menu_detail_header)
                //.footerResourceId(R.layout.section_ex2_footer)
                .build());

        this.context = context;
        this.title = title;
        this.seeAll = seeAll;
        this.filesItemlist = list;
        this.productId = productId;
        this.productName = productName;
        this.clickListener = clickListener;


    }

    /**
     * here convertMillieToHMmSs
     *
     * @param millie
     * @return
     */
    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }

    /**
     * here get Video Duration from video url
     *
     * @param url
     * @return
     */
    public String getVideoDuration(String url) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(url, new HashMap<String, String>());
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        String duration = convertMillieToHMmSs(timeInMillisec); //use this duration

        Log.d(TAG, "Videos_duration: " + duration);

        return duration;
    }

   /* public void add(final int position, @NonNull final FilesItem filesItem) {
        filesItemlist.add(position, filesItem);

       // notify();
    }*/

    @Override
    public int getContentItemsTotal() {
        return filesItemlist.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        FilesItem data = filesItemlist.get(position);
        //mData =  data;
        //itemHolder.ivFavProgram.setImageURI(data.getImage());

        if (data.getType().contains("pdf") || data.getType().contains("image")) {
            itemHolder.ivPlayVideo.setVisibility(View.GONE);
            itemHolder.tvTimeDuration.setVisibility(View.GONE);
            itemHolder.pbProgram.setVisibility(View.GONE);
        } else {
            itemHolder.ivPlayVideo.setVisibility(View.VISIBLE);
            itemHolder.tvTimeDuration.setVisibility(View.VISIBLE);
            itemHolder.pbProgram.setVisibility(View.VISIBLE);

            if (data.getDuration() != null) {

                itemHolder.tvTimeDuration.setText(getTimeFromat(data.getDuration()));
            } else {

                //itemHolder.tvTimeDuration.setText(" ");
                itemHolder.tvTimeDuration.setVisibility(View.GONE);
            }


        }

        if (data.getType().contains("video") || data.getType().contains("audio")) {
            if (data.getVideoView() == null || data.getVideoView().getDuration() == null) {
                if (data.getDuration() != null) {
                    itemHolder.pbProgram.setMax(Math.toIntExact(getProgressDuration(data.getDuration())));
                }

                itemHolder.pbProgram.setProgress(Math.toIntExact(getProgressDuration("00:00:00")));
            } else {
                if (data.getDuration() != null) {

                    itemHolder.pbProgram.setMax(Math.toIntExact(getProgressDuration(data.getDuration())));
                }
                if (data.getVideoView() != null) {
                    itemHolder.pbProgram.setProgress(Math.toIntExact(getProgressDuration(data.getVideoView().getDuration())));
                }

                //itemHolder.pbProgram.setProgress(Math.toIntExact(getProgressDuration(data.getVideoView().getDuration())));
            }
        }

        itemHolder.tvFavProgramName.setText(data.getTitle());
        if (data.getDescription() != null) {
            itemHolder.tvDiscription.setText(htmlToStringFilter(data.getDescription()));
        }

        //  itemHolder.pbProgram.setProgress(35);

        itemHolder.clBookmarks.setSelected(data.isFavourite());

        if (data.getType().contains("video") || data.getType().contains("pdf") || data.getType().contains("audio")) {

            int drawableIcon = 0;
            if (data.getType().contains("pdf")) {
                drawableIcon = R.drawable.ic_pdf;
            } else if (data.getType().contains("video")) {
                drawableIcon = R.drawable.ic_video;
            } else if (data.getType().contains("audio")) {
              //  drawableIcon = R.drawable.ic_audio;
                drawableIcon = R.drawable.ic_mp3;
            } else if (data.getType().contains("image")) {
                drawableIcon = R.drawable.ic_image_placeholder;
            }

            if (data.getThumbpath() != null) {

                Picasso.get().load(ApiClient.BASE_URL_MEDIA + data.getThumbpath()).placeholder(drawableIcon)
                        .into(itemHolder.ivFavProgram, new Callback() {
                            @Override
                            public void onSuccess() {
                                itemHolder.pbImage.setVisibility(View.GONE);

                            }

                            @Override
                            public void onError(Exception e) {
                                itemHolder.pbImage.setVisibility(View.GONE);

                            }
                        });

            } else {

                itemHolder.ivFavProgram.setImageDrawable(context.getResources().getDrawable(drawableIcon));

            }
        } else {
            Picasso.get().load(ApiClient.BASE_URL_MEDIA + data.getImage()).placeholder(R.drawable.ic_image_placeholder)
                    .into(itemHolder.ivFavProgram, new Callback() {
                        @Override
                        public void onSuccess() {
                            itemHolder.pbImage.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {
                            itemHolder.pbImage.setVisibility(View.GONE);

                        }
                    });

        }

        itemHolder.clBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickListener.onItemRootViewClicked(ListSectionAdapter.this, data, itemHolder.getAdapterPosition() - 1, Constants.App.TYPE_BOOKMARK);
                // itemHolder.clBookmarks.setImageResource(R.drawable.ic_select_bookmark);
                //  selectOrUnselectBookMark(data.isFavourite(), itemHolder.clBookmarks);

            }
        });

        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.getType().contains("video")) {
                    //"playVideo"
                    clickListener.onItemRootViewClicked(ListSectionAdapter.this, data, itemHolder.getAdapterPosition() - 1, Constants.App.TYPE_PLAY_VIDEO);

                } else if (data.getType().contains("pdf")) {

                    clickListener.onItemRootViewClicked(ListSectionAdapter.this, data, itemHolder.getAdapterPosition() - 1, Constants.App.TYPE_PDF);

                } else if (data.getType().contains("audio")) {

                    clickListener.onItemRootViewClicked(ListSectionAdapter.this, data, itemHolder.getAdapterPosition() - 1, Constants.App.TYPE_AUDIO);

                } else if (data.getType().contains("image")) {

                    clickListener.onItemRootViewClicked(ListSectionAdapter.this, data, itemHolder.getAdapterPosition() - 1, Constants.App.TYPE_IMAGE);

                }

            }
        });

    }

    /**
     * set time format in UI
     *
     * @param duration
     * @return
     */
    private String getTimeFromat(String duration) {

        String time;

        String newStrg = duration;
        String[] mTime = newStrg.split(":");

        if (mTime[0].equals("00")) {
            time = mTime[1] + ":" + mTime[2];

        } else {

            time = duration;
        }

        return time;

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {

        HeaderViewHolder headerViewHolder = new HeaderViewHolder(view);
        headerViewHolder.tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               clickListener.onHeaderViewClicked(NewsSection.this, mData, headerViewHolder.getAdapterPosition());
                clickListener.onHeaderViewClicked(ListSectionAdapter.this, productId, productName,headerViewHolder.getAdapterPosition());
                Log.d("NewSection", "onBindHeaderViewHolder: productId: " + productId);
            }
        });

        return headerViewHolder;
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvTitle.setText(title);
        headerHolder.tvSeeAll.setText(seeAll);
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(final View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(final RecyclerView.ViewHolder holder) {
        final FooterViewHolder footerHolder = (FooterViewHolder) holder;
        //  footerHolder.rootView.setOnClickListener(v -> clickListener.onFooterRootViewClicked(this, footerHolder.getAdapterPosition()));
    }

    /**
     * here remove HTML tags from a String
     */
    public String htmlToStringFilter(String textToFilter) {
        return Html.fromHtml(textToFilter).toString();
    }


    private Long getProgressDuration(String duration) {
        String videoDuration = "";
        if (duration != null && duration.isEmpty()) {
            videoDuration = "00:00:00";
        } else {
            videoDuration = duration;
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormat.parse(videoDuration);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime() / 1000L;
    }


    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    class BackgroundImageTask extends AsyncTask<Void, Void, Bitmap> {
        ImageView iv;
        String temUrl;
        ProgressBar progressBar;

        BackgroundImageTask(ImageView imageView, ProgressBar pb, String url) {
            iv = imageView;
            temUrl = url;
            progressBar = pb;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bit1 = null;

            try {
                bit1 = retriveVideoFrameFromVideo(temUrl);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            /*try {

                bit1 = BitmapFactory.decodeStream((InputStream)new URL(postList.get(pos).thumbnail).getContent());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            return bit1;
        }

        @Override
        protected void onPostExecute(Bitmap bit) {
            progressBar.setVisibility(View.GONE);
            iv.setImageBitmap(bit);
        }
    }


}


