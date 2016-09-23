package com.android.launcher3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.launcher3.WallpaperPickerActivity.PickImageInfo;
import com.android.launcher3.WallpaperPickerActivity.ResourceWallpaperInfo;
import com.android.launcher3.WallpaperPickerActivity.UriWallpaperInfo;
import com.android.launcher3.WallpaperPickerActivity.WallpaperTileInfo;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WallpaperItemPickerActivity extends Activity {
	public static final String KEYGUARD_WALLPAPER = "keyguard_wallpaper";
	public static final String WALLPAPER_ITEM_PICK = "wallpaper_item_pick";
	public static final String WALLPAPER_LIST = "wallpaper_list";
	public static final String WALLPAPER_INDEX = "index";
	public static final String GALLERY = "Gallery";
	public static final String LIVE_WALLPAPER = "Live Wallpapers";
	public static final int WALLPAPER_ITEM = 0;
	public static final int LIVE_WALLPAPER_ITEM = 1;
	public static final int GALLERY_ITEM = 2;
	private final int REQUEST_SET_KEYGUARD_WALLPAPER = 100;
	private final int REQUEST_SET_HOME_WALLPAPER = 200;
	protected static int wallpaperItemPick = 0;
	protected static boolean isKeyguardWallpaper = false;
	private GridView wallpaperPreview = null;
	private LinearLayout listBar;
	private LinearLayout barLayout;
	private TextView barTitle;
	static ArrayList<ResourceWallpaperInfo> wallpapers = null;
	protected static ImageAdapter adapter = null;
	private List<ResolveInfo> rList = null;
	private PackageManager pm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		isKeyguardWallpaper = false;
		if (intent != null) {
			if (intent.hasExtra(WallpaperTypeActivity.KEYGUARD_WALLPAPER)) {
				isKeyguardWallpaper = intent.getBooleanExtra(
						WallpaperTypeActivity.KEYGUARD_WALLPAPER, false);
			}
			if (intent.hasExtra(WALLPAPER_ITEM_PICK)) {
				wallpaperItemPick = intent.getIntExtra(WALLPAPER_ITEM_PICK,
						WALLPAPER_ITEM);
			}
		}
		init();
	}

	protected void init() {
		// TODO Auto-generated method stub
		setContentView(R.layout.wallpaper_list);
		wallpaperPreview = (GridView) findViewById(R.id.wallpaper_preview);
//		if (isKeyguardWallpaper) {
//			wallpapers = findBundledKeyguardWallpapers();
//		} else {
//			wallpapers = findBundledWallpapers();
//		}
		wallpapers = findBundledWallpapers();
		adapter = new ImageAdapter();
		wallpaperPreview.setAdapter(adapter);
		wallpaperPreview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						WallpaperItemPickerActivity.this,
						WallpaperPreviewActivity.class);
				intent.putExtra(WALLPAPER_INDEX, arg2);
//				intent.putExtra(KEYGUARD_WALLPAPER, isKeyguardWallpaper);
//				if (isKeyguardWallpaper) {
//					startActivityForResult(intent,
//							REQUEST_SET_KEYGUARD_WALLPAPER);
//				} else {
//					startActivityForResult(intent,
//							REQUEST_SET_HOME_WALLPAPER);
//				}
				startActivity(intent);
			}
		});
		listBar = (LinearLayout) findViewById(R.id.listbar);
		barTitle = (TextView) listBar.findViewById(R.id.bartitle);
		barTitle.setText(R.string.select_wallpaper);
		barLayout = (LinearLayout) listBar.findViewById(R.id.barlayout);
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
				iv = new ImageView(WallpaperItemPickerActivity.this);
			} else {
				iv = (ImageView) convertView;
			}
			iv.setAdjustViewBounds(true);
			iv.setImageDrawable(wallpapers.get(position).getmThumb());

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

	protected ArrayList<ResourceWallpaperInfo> findBundledKeyguardWallpapers() {
		ArrayList<ResourceWallpaperInfo> bundledWallpapers = new ArrayList<ResourceWallpaperInfo>(
				24);

		Pair<ApplicationInfo, Integer> r = getKeyguardWallpaperArrayResourceId();
		if (r != null) {
			try {
				Resources wallpaperRes = getPackageManager()
						.getResourcesForApplication(r.first);
				bundledWallpapers = addWallpapers(wallpaperRes,
						r.first.packageName, r.second);
			} catch (PackageManager.NameNotFoundException e) {
			}
		}

		return bundledWallpapers;
	}

	protected ArrayList<ResourceWallpaperInfo> findBundledWallpapers() {
		ArrayList<ResourceWallpaperInfo> bundledWallpapers = new ArrayList<ResourceWallpaperInfo>(
				24);

		Pair<ApplicationInfo, Integer> r = getWallpaperArrayResourceId();
		if (r != null) {
			try {
				Resources wallpaperRes = getPackageManager()
						.getResourcesForApplication(r.first);
				bundledWallpapers = addWallpapers(wallpaperRes,
						r.first.packageName, r.second);
			} catch (PackageManager.NameNotFoundException e) {
			}
		}

		return bundledWallpapers;
	}

	public Pair<ApplicationInfo, Integer> getKeyguardWallpaperArrayResourceId() {
		// Context.getPackageName() may return the "original" package name,
		// com.android.launcher3; Resources needs the real package name,
		// com.android.launcher3. So we ask Resources for what it thinks the
		// package name should be.
//		final String packageName = getResources().getResourcePackageName(
//				R.array.keyguard_wallpapers);
//		try {
//			ApplicationInfo info = getPackageManager().getApplicationInfo(
//					packageName, 0);
//			return new Pair<ApplicationInfo, Integer>(info,
//					R.array.keyguard_wallpapers);
//		} catch (PackageManager.NameNotFoundException e) {
//			return null;
//		}
		return null;
	}

	private ArrayList<ResourceWallpaperInfo> addWallpapers(Resources res,
			String packageName, int listResId) {
		ArrayList<ResourceWallpaperInfo> bundledWallpapers = new ArrayList<ResourceWallpaperInfo>(
				24);
		final String[] extras = res.getStringArray(listResId);
		for (String extra : extras) {
			int resId = res.getIdentifier(extra, "drawable", packageName);
			if (resId != 0) {
				final int thumbRes = res.getIdentifier(extra + "_small",
						"drawable", packageName);

				if (thumbRes != 0) {
					ResourceWallpaperInfo wallpaperInfo = new ResourceWallpaperInfo(
							res, resId, res.getDrawable(thumbRes));
					bundledWallpapers.add(wallpaperInfo);
					// Log.d(TAG, "add: [" + packageName + "]: " + extra + " ("
					// + res + ")");
				}
			}
		}
		return bundledWallpapers;
	}

	public Pair<ApplicationInfo, Integer> getWallpaperArrayResourceId() {
		// Context.getPackageName() may return the "original" package name,
		// com.android.launcher3; Resources needs the real package name,
		// com.android.launcher3. So we ask Resources for what it thinks the
		// package name should be.
		final String packageName = getResources().getResourcePackageName(
				R.array.wallpapers);
		try {
			ApplicationInfo info = getPackageManager().getApplicationInfo(
					packageName, 0);
			return new Pair<ApplicationInfo, Integer>(info, R.array.wallpapers);
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}

	public static ArrayList<ResourceWallpaperInfo> getWallpapers() {
		return wallpapers;
	}

	public static ImageAdapter getImageAdapter() {
		return adapter;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			setResult(RESULT_OK);
			finish();
		}
	}

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

	private void populateWallpaperTypes() {
		// Search for activities that satisfy the ACTION_SET_WALLPAPER action
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
		pm = getPackageManager();
		rList = pm.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		
	}

	private Intent getGalleryIntent() {
		if(rList == null){
			populateWallpaperTypes();
		}
		for (ResolveInfo info : rList) {
			// begin modify by zhouzhuobin for JLLJ-402 20141211
			if (!"com.google.android.apps.plus"
					.equals(info.activityInfo.packageName)
					&& !"com.google.android.apps.photos.phone.SetWallpaperActivity"
							.equals(info.activityInfo.name)) {
				// end modify by zhouzhuobin for JLLJ-402 20141211
				CharSequence label = info.loadLabel(pm);
        		if (label == null) label = info.activityInfo.packageName;
        		if(label.equals(GALLERY)){
        			Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
        			intent.setComponent(new ComponentName(
    						info.activityInfo.packageName, info.activityInfo.name));
        			return intent;
        		}
			}
		}
		return null;
	}
	private Intent getLiveWallpaperIntent() {
		if(rList == null){
			populateWallpaperTypes();
		}
		for (ResolveInfo info : rList) {
			// begin modify by zhouzhuobin for JLLJ-402 20141211
			if (!"com.google.android.apps.plus"
					.equals(info.activityInfo.packageName)
					&& !"com.google.android.apps.photos.phone.SetWallpaperActivity"
							.equals(info.activityInfo.name)) {
				// end modify by zhouzhuobin for JLLJ-402 20141211
				CharSequence label = info.loadLabel(pm);
        		if (label == null) label = info.activityInfo.packageName;
        		if(label.equals(LIVE_WALLPAPER)){
        			Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
        			intent.setComponent(new ComponentName(
    						info.activityInfo.packageName, info.activityInfo.name));
        			return intent;
        		}
			}
		}
		return null;
	}
}
