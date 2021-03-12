package com.in.livechat.ui.album.pop;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.in.livechat.ui.R;
import com.in.livechat.ui.album.adapter.CommonAdapter;
import com.in.livechat.ui.album.model.ImageFolder;
import com.in.livechat.ui.album.utils.ViewHolder;

import java.util.List;


public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFolder> {

    private ListView mListDir;

    public ListImageDirPopupWindow(int width, int height, List<ImageFolder> dataList,
                                   View convertView) {
        super(convertView, width, height, true, dataList);
    }

    @Override
    public void initViews() {
        mListDir = (ListView) findViewById(R.id.id_list_dir);
        mListDir.setAdapter(new CommonAdapter<ImageFolder>(context, mDataList,
                R.layout.live_item_album_list_dir) {
            @Override
            public void convert(ViewHolder helper, ImageFolder item) {
                helper.setText(R.id.id_dir_item_name, item.getName());
                helper.setImageByUrl(R.id.id_dir_item_image,item.getFirstImagePath());
                helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
            }
        });
    }

    public interface OnImageDirSelected {
        void selected(ImageFolder folder);
    }

    private OnImageDirSelected mImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents() {
        mListDir.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mImageDirSelected != null) {
                    mImageDirSelected.selected(mDataList.get(position));
                }
            }
        });
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {
        // TODO Auto-generated method stub
    }

}
