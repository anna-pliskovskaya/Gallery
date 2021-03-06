package com.sec.android.gallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.sec.android.gallery.interfaces.Image;
import com.sec.android.gallery.interfaces.ImageProvider;
import com.sec.android.gallery.interfaces.Receiver;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Ganna Pliskovska(g.pliskovska@samsung.com)
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private final ImageCacheManager imageCacheManager = new ImageCacheManager();
    private ProgressDialog mProgressDialog;
    private RadioGroup columns;

    /**
     * Grid view holding the images.
     */
    private GridView imagesGridView;
    /**
     * List view holding the images.
     */
    private ListView imagesListView;
    /**
     * Image adapter for the grid view.
     */
    private ImageAdapter imageGridAdapter;
    /**
     * Image adapter for the list view.
     */
    private ImageAdapter imageListAdapter;

    ImageProvider mImageProvider;
    Receiver<Collection<Image>> mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // Request progress bar
        setContentView(R.layout.main);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

        setupViews();

        columns = (RadioGroup) findViewById(R.id.toggle_group);
        findViewById(R.id.toggle_two_columns).setOnClickListener(this);
        findViewById(R.id.toggle_three_columns).setOnClickListener(this);
        findViewById(R.id.toggle_five_columns).setOnClickListener(this);

        ((ToggleButton) findViewById(R.id.modes)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imagesGridView.setSelection(imagesListView.getFirstVisiblePosition());
                    columns.setVisibility(View.VISIBLE);
                    imagesGridView.setVisibility(View.VISIBLE);
                    imagesListView.setVisibility(View.GONE);
                } else {
                    imagesListView.setSelection(imagesGridView.getFirstVisiblePosition());
                    columns.setVisibility(View.GONE);
                    imagesGridView.setVisibility(View.GONE);
                    imagesListView.setVisibility(View.VISIBLE);
                }
            }
        });
        columns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //if one button is checked, uncheck all others
                for (int j = 0; j < group.getChildCount(); j++) {
                    final ToggleButton view = (ToggleButton) group.getChildAt(j);
                    view.setChecked(view.getId() == checkedId);
                }
            }
        });

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int length = Toast.LENGTH_LONG;
                Toast.makeText(getApplicationContext(), ((Image) imageGridAdapter.getItem(position)).getName() + "\n" +
                        ((Image) imageGridAdapter.getItem(position)).getDescription(), length).show();
            }
        };
        imagesGridView.setOnItemClickListener(onItemClickListener);
        imagesListView.setOnItemClickListener(onItemClickListener);

        mReceiver = new Receiver<Collection<Image>>() {
            @Override
            public void receive(Collection<Image> data) {
                imageGridAdapter.addPhotoList(new ArrayList<Image>(data));
                imageGridAdapter.notifyDataSetChanged();
                imageListAdapter.addPhotoList(new ArrayList<Image>(data));
                imageListAdapter.notifyDataSetChanged();
                mProgressDialog.hide();
            }
        };
    }

    /**
     * Setup the grid view.
     */
    private void setupViews() {
        imagesGridView = (GridView) findViewById(R.id.gridView);
        imagesListView = (ListView) findViewById(R.id.listView);

        imagesGridView.setClipToPadding(false);
        imagesListView.setClipToPadding(false);

        imageGridAdapter = new ImageAdapter(this, R.layout.gridview_item);
        imagesGridView.setAdapter(imageGridAdapter);

        imageListAdapter = new ImageAdapter(this, R.layout.listview_item);
        imagesListView.setAdapter(imageListAdapter);
        imagesListView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        columns.clearCheck();
        columns.check(v.getId());
        switch (v.getId()) {
            case R.id.toggle_two_columns:
                retainPositionOfGridView(2);
                break;
            case R.id.toggle_three_columns:
                retainPositionOfGridView(3);
                break;
            case R.id.toggle_five_columns:
                retainPositionOfGridView(5);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        imagesGridView.setAdapter(null);
        imagesListView.setAdapter(null);
        imageGridAdapter.clean();
        imageGridAdapter.notifyDataSetChanged();
        imageListAdapter.clean();
        imageListAdapter.notifyDataSetChanged();
        imageCacheManager.memoryCache.evictAll();
        super.onDestroy();
    }

    private void retainPositionOfGridView(int countColumns) {
        int index = imagesGridView.getFirstVisiblePosition();
        imagesGridView.setNumColumns(countColumns);
        imagesGridView.setSelection(index);
    }
}






