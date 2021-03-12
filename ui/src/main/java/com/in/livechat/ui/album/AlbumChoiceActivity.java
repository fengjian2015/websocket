package com.in.livechat.ui.album;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.in.livechat.socket.util.LogUtil;
import com.in.livechat.ui.R;
import com.in.livechat.ui.album.adapter.AlbumGvAdapter;
import com.in.livechat.ui.album.model.ImageFolder;
import com.in.livechat.ui.album.model.ImgUrl;
import com.in.livechat.ui.album.pop.ListImageDirPopupWindow;
import com.in.livechat.ui.album.utils.AlbumCons;
import com.in.livechat.ui.album.utils.ImgScanUtil;
import com.in.livechat.ui.common.base.BaseChatActivity;
import com.in.livechat.ui.common.util.ImgUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 多图选择
 */
public class AlbumChoiceActivity extends BaseChatActivity implements ListImageDirPopupWindow.OnImageDirSelected {

    //图片数量最多的文件夹
    private File mMostImgDir;
    //所有的图片
    private List<String> mImgList = new ArrayList<>();
    private GridView containerGv;
    //扫描拿到所有的图片文件夹
    private List<ImageFolder> mImageFolderList = new ArrayList<>();
    private ArrayList<String> selectImgList = new ArrayList<>();
    private ListImageDirPopupWindow mListImageDirPopupWindow;
    private RelativeLayout mBottomLy;
    private TextView mChooseDir;
    private int mScreenHeight;
    private ImageView backIv;
    private Button confirmBtn;
    private int maxImgNum;
    private int albumOpenCode;


    @Override
    protected int getLayoutId() {
        return R.layout.live_activity_album;
    }

    @Override
    protected void initView() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        containerGv = findViewById(R.id.id_gridView);
        mChooseDir = findViewById(R.id.id_choose_dir);
        mBottomLy = findViewById(R.id.id_bottom_ly);
        backIv = findViewById(R.id.btn_cancel);
        confirmBtn = findViewById(R.id.btn_ok);
    }

    @Override
    protected void init() {
        maxImgNum = getIntent().getIntExtra(AlbumCons.ALBUM_SELECT_MAX_NUM, 9);
        //-1:多选; 其他:单选
        albumOpenCode = getIntent().getIntExtra(AlbumCons.ALBUM_OPEN_KEY, 0);
        LogUtil.i("maxImgNum", maxImgNum);
        LogUtil.i("albumOpenCode", albumOpenCode);

        ImgScanUtil imgScanUtil = new ImgScanUtil(this);
        imgScanUtil.setOnImgScanListener(new ImgScanUtil.OnImgScanListener() {
            @Override
            public void imgScan(List<ImageFolder> imageFolders, int picSize, File imgDir) {
                mImageFolderList = imageFolders;
                mMostImgDir = imgDir;
                // 为View绑定数据
                data2View();
                // 初始化文件夹pop
                initListDirPop();
            }
        });
        imgScanUtil.getImages();
    }

    @Override
    protected void setListener() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntentResult(selectImgList);
                AlbumChoiceActivity.this.finish();
            }
        });
        //为底部的布局设置点击事件，弹出popupWindow
        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }

    private void setIntentResult(ArrayList<String> pathList) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(AlbumCons.IMG_SELECT_PATH_LIST, pathList);
        setResult(RESULT_OK, intent);
    }

    //为View绑定数据
    private void data2View() {
        if (mMostImgDir == null) {
            Toast.makeText(getApplicationContext(),"没扫描到图片",Toast.LENGTH_SHORT).show();
            return;
        }
        mImgList.clear();
        mImgList.addAll(Arrays.asList(mMostImgDir.list()));
        for (int i = mImgList.size() - 1; i >= 0; i--) {
            if (!mImgList.get(i).endsWith(".jpg") && !mImgList.get(i).endsWith(".png")
                    && !mImgList.get(i).endsWith(".jpeg")){
                mImgList.remove(i);
            }
        }
        setAdapterData();
    }

    private void setAdapterData() {
        //可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
        final ArrayList<ImgUrl> mDataList = new ArrayList<>();
        for (String mImg : mImgList) {
            mDataList.add(new ImgUrl(mMostImgDir.getAbsolutePath() + "/" + mImg));
        }
        if (maxImgNum <= 0) {
            maxImgNum = 9;
        }
        AlbumGvAdapter gvAdapter = new AlbumGvAdapter(this, mDataList, maxImgNum, albumOpenCode);
        containerGv.setAdapter(gvAdapter);

        gvAdapter.setOnImgClickListener(new AlbumGvAdapter.OnImgClickListener() {
            @Override
            public void onClick(int position) {
                if (albumOpenCode != -1) {
                    //单选则跳转图片裁剪
                    String path = mMostImgDir.getAbsolutePath() + "/" + mImgList.get(position);
                    ImgUtil.cropPic(AlbumChoiceActivity.this, path, AlbumCons.IMG_CROP_SIZE_1);

                } else {
                    //多选则跳转图片展示
                    Intent intent = new Intent(getApplicationContext(), ImgShowActivity.class);
                    intent.putExtra(AlbumCons.IMG_CLICK_POSITION, position);
                    intent.putExtra(AlbumCons.IMG_LIST_KEY, mDataList);
                    startActivity(intent);
                }
            }
        });

        gvAdapter.setOnCbClickListener(new AlbumGvAdapter.OnCbClickListener() {
            @Override
            public void onCbClick(View v, int position) {
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {
                    if (selectImgList.size() < maxImgNum) {
                        selectImgList.add(mDataList.get(position).getUrl());
                    } else {
                        cb.setChecked(false);
                        Toast.makeText(getApplicationContext(), "最多选" + maxImgNum + "张",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    selectImgList.remove(mDataList.get(position).getUrl());
                }
                confirmBtn.setText( "确认(" + selectImgList.size() + "/9)");
            }
        });
    }

    //初始化展示文件夹的popupWindow
    private void initListDirPop() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFolderList, View.inflate(getApplicationContext(),
                R.layout.live_pop_album_list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    public void selected(ImageFolder folder) {
        mMostImgDir = new File(folder.getDir());
        mImgList = Arrays.asList(mMostImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg") || filename.endsWith(".gif");
            }
        }));
        setAdapterData();
        mChooseDir.setText(folder.getName());
        mListImageDirPopupWindow.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == AlbumCons.REQUEST_CROP) {
            selectImgList.add(ImgUtil.cropOutputPath);
            setIntentResult(selectImgList);
            AlbumChoiceActivity.this.finish();
        }
    }

}