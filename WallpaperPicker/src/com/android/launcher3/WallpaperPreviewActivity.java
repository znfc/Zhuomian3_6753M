package com.android.launcher3;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.launcher3.WallpaperPickerActivity.ResourceWallpaperInfo;
import com.android.launcher3.WallpaperPickerActivity.WallpaperTileInfo;
import com.android.photos.BitmapRegionTileSource;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
//import android.app.ActivityThread;
//import android.content.pm.IPackageManager;

public class WallpaperPreviewActivity extends Activity {

	public static final String WALLPAPER_LIST = "wallpaper_list";
	public static final String WALLPAPER_INDEX = "index";
	protected static boolean isKeyguardWallpaper = false;
	protected static int wallpaperIndex = 0;
	protected static ArrayList<ResourceWallpaperInfo> wallpapers = null;
	private Gallery wallpaperPreview = null;
	private Button setWallpaperButton = null;
	private ImageAdapter adapter = null;
	private WallpaperManager wallpaperManager = null;
	private LinearLayout previewBar;
	private LinearLayout barLayout;
	private TextView barTitle;
	private TextView setHomeWallpaper;
	private TextView setKeyguardWallpaper;
	private TextView setBothWallpaper;
	private Dialog setDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		wallpaperManager = WallpaperManager
				.getInstance(WallpaperPreviewActivity.this);
		Intent intent = getIntent();
		isKeyguardWallpaper = false;
		if (intent != null) {
			if (intent.hasExtra(WallpaperTypeActivity.KEYGUARD_WALLPAPER)) {
				isKeyguardWallpaper = intent.getBooleanExtra(
						WallpaperTypeActivity.KEYGUARD_WALLPAPER, false);
			}
			if (intent.hasExtra(WALLPAPER_INDEX)) {
				wallpaperIndex = intent.getIntExtra(WALLPAPER_INDEX, 0);
			}
		}
		setContentView(R.layout.wallpaper_preview);
		setDialog = setWallpaperDialog();
		wallpapers = WallpaperItemPickerActivity.getWallpapers();
		wallpaperPreview = (Gallery) findViewById(R.id.wallpaper_preview);
		adapter = new ImageAdapter();
		wallpaperPreview.setAdapter(adapter);
		wallpaperPreview.setSelection(wallpaperIndex);
		setWallpaperButton = (Button) findViewById(R.id.set_wallpaper);
		setWallpaperButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (isStorageLow()) {
//					Toast.makeText(WallpaperPreviewActivity.this,
//							R.string.storage_low, Toast.LENGTH_LONG).show();
//				} else {
//					try {
//						ResourceWallpaperInfo info = (ResourceWallpaperInfo) wallpapers
//								.get(wallpaperPreview.getSelectedItemPosition());
//						if (isKeyguardWallpaper) {
//							wallpaperManager.setKeyguardResource(info.getmResId(),
//									wallpaperPreview.getSelectedItemPosition());
//							//wallpaperManager.setStreamForKeyguard(mInStream);
//						} else {
//							wallpaperManager.setResource(info.getmResId(),
//									wallpaperPreview.getSelectedItemPosition());
//							//wallpaperManager.setStream(mInStream);
//						}
//						Toast.makeText(WallpaperPreviewActivity.this,
//								R.string.set_wallpaper_successfull, Toast.LENGTH_LONG).show();
//						setResult(RESULT_OK);
//						finish();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//					}
//					setDialog.show();
//				}
			}
		});
		previewBar = (LinearLayout) findViewById(R.id.previewbar);
		barTitle = (TextView) previewBar.findViewById(R.id.bartitle);
		barTitle.setText(R.string.select_wallpaper);
		barLayout = (LinearLayout) previewBar.findViewById(R.id.barlayout);
		barLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // android.R.id.home will be triggered in onOptionsItemSelected()
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(R.string.select_wallpaper);
        }

	}
	

	public class ImageAdapter extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView iv;
			if (convertView == null) {
				iv = new ImageView(WallpaperPreviewActivity.this);
			} else {
				iv = (ImageView) convertView;
			}
			iv.setAdjustViewBounds(true);
			iv.setScaleType(ImageView.ScaleType.CENTER);
			iv.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			iv.setImageResource(wallpapers.get(position).getmResId());
			return iv;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return wallpapers.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wallpapers.size();
		}
	}

	// add by sunjie for JSLEL-1016 begin
//	private boolean isStorageLow() {
//		final IPackageManager pm = ActivityThread.getPackageManager();
//		try {
//			if (pm != null && pm.isStorageLow()) {
//				return true;
//			}
//		} catch (RemoteException e) {
//		}
//		return false;
//	}
	// add by sunjie for JSLEL-1016 end
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
        case android.R.id.home:
            finish();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private Dialog setWallpaperDialog() {
		String[] names = new String[] {
				getResources().getString(R.string.set_home_wallpaper),
				getResources().getString(R.string.set_keyguard_wallpaper),
				getResources().getString(R.string.set_both_wallpaper)};
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < names.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("personName", names[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = (SimpleAdapter) new SimpleAdapter(this,
				listItems, R.layout.set_wallpaper_dialog_row,
				new String[] { "personName" }, new int[] { R.id.name });
		TextView title = new TextView(this);
		title.setHeight(130);
		title.setTextSize(20);
		title.setTextColor(Color.GREEN);
        title.setText(getResources().getString(R.string.choose_from_wallpaper));
        title.setGravity(Gravity.CENTER);

		Dialog dialog = new AlertDialog.Builder(this)
							.setCustomTitle(title)
							.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									setWallpaper(which);
									dialog.dismiss();
								}
							}).create();
		return dialog;
	}
	
	private void setWallpaper(int id) {
		ResourceWallpaperInfo info = (ResourceWallpaperInfo) wallpapers
				.get(wallpaperPreview.getSelectedItemPosition());
		switch (id) {
		case 0:
			try {
				wallpaperManager.setResource(info.getmResId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			try {
//				wallpaperManager.setKeyguardResource(info.getmResId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				wallpaperManager.setResource(info.getmResId());
//				wallpaperManager.setKeyguardResource(info.getmResId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		Toast.makeText(WallpaperPreviewActivity.this,
				R.string.set_wallpaper_successfull, Toast.LENGTH_LONG).show();
	}
}
