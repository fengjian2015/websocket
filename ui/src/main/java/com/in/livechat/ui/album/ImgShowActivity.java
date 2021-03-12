package com.in.livechat.ui.album;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.in.livechat.ui.R;
import com.in.livechat.ui.album.adapter.ImgShowVpAdapter;
import com.in.livechat.ui.album.impl.CustomVpPageChangeListener;
import com.in.livechat.ui.album.model.ImgUrl;
import com.in.livechat.ui.album.utils.AlbumCons;
import com.in.livechat.ui.album.utils.FileUtil;
import com.in.livechat.ui.album.widget.ZoomImageView;
import com.in.livechat.ui.common.base.BaseChatActivity;
import com.in.livechat.ui.common.util.SystemUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 大图展示
 */

public class ImgShowActivity extends BaseChatActivity {

    private ViewPager containerVp;
    private List<ImgUrl> imgList;
    private ImageView backIv;
    private TextView titleTv;
    private int childPosition;
    private FrameLayout downloadFl;
    private boolean showDownload=false;

    @Override
    protected int getLayoutId() {
        return R.layout.live_activity_album_img_show;
    }

    @Override
    protected void initView() {
        containerVp = findViewById(R.id.vp_img_show);
        backIv = findViewById(R.id.iv_img_show_back);
        titleTv = findViewById(R.id.tv_img_show_title);
        downloadFl = findViewById(R.id.fl_message_chat_img_download);
    }

    @Override
    protected void init() {
        childPosition = getIntent().getIntExtra(AlbumCons.IMG_CLICK_POSITION, 0);
        imgList = (List<ImgUrl>) getIntent().getSerializableExtra(AlbumCons.IMG_LIST_KEY);
        showDownload=getIntent().getBooleanExtra(AlbumCons.IMG_DONWLOAD,false);

        titleTv.setText(childPosition + 1 + "/" + imgList.size());
        if (showDownload){
            downloadFl.setVisibility(View.VISIBLE);
        }else {
            downloadFl.setVisibility(View.GONE);
        }
        initViewPager();
    }


    @Override
    protected void setListener() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImgShowActivity.this.finish();
            }
        });

        downloadFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = containerVp.getCurrentItem();
                if (!TextUtils.isEmpty(imgList.get(currentPosition).getFileName())){
                    getImgPathFromCache(imgList.get(currentPosition).getUrl(), FileUtil.getSDDownloadPath(getApplication()), imgList.get(currentPosition).getFileName());
                }
            }
        });

        containerVp.addOnPageChangeListener(new CustomVpPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if (imgList != null) {
                    titleTv.setText(position + 1 + "/" + imgList.size());
                }
            }
        });
    }
    List<View> viewList = new ArrayList<>();
    private void initViewPager() {
        for (int i = 0; i < imgList.size(); i++) {
            final View view = View.inflate(this, R.layout.live_item_album_img_match_parent, null);
            final ZoomImageView imageView = view.findViewById(R.id.iv_img_show);
            Glide.with(this).load(imgList.get(i).getUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imageView.setImageDrawable(resource);
                    return false;
                }
            }).into(imageView);
            viewList.add(imageView);
        }
        containerVp.setAdapter(new ImgShowVpAdapter(viewList));
        containerVp.setCurrentItem(childPosition);
    }

    private File oldFile;
    private void getImgPathFromCache(final String url, final String saveDir, final String fileName) {
        new Thread(){
            @Override
            public void run() {
                try {
                    oldFile= Glide.with(ImgShowActivity.this)
                            .asFile()
                            .load(url)
                            .submit()
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.e("Tag", "path-->" + oldFile.getPath());
                FileUtil.copyFile(oldFile.getPath(),saveDir+ fileName);
                SystemUtil.notifyMediaStoreRefresh(getApplication(), saveDir + fileName);
                Toast.makeText(getHostActivity(),"已保存到相册",Toast.LENGTH_SHORT).show();
            }
        }.start();

    }
}
