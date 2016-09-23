package com.android.launcher3;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.android.launcher3.WallpaperPickerActivity.ResourceWallpaperInfo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.app.WallpaperManager;

public class WallpaperTypeActivity extends Activity implements
        View.OnClickListener {
    static final String TAG = "WallpaperTypeActivity";
    public static final String KEYGUARD_WALLPAPER = "keyguard_wallpaper";

    public static final String GALLERY = "gallery";
    public static final String LIVE_WALLPAPER = "live";
    public static final String VIDEO_WALLPAPER = "vlw";
    private final int REQUEST_SET_KEYGUARD_WALLPAPER = 100;
    private final int REQUEST_SET_HOME_WALLPAPER = 200;
    protected static ArrayList<ResourceWallpaperInfo> homeWallpapers = null;
    protected static ArrayList<ResourceWallpaperInfo> keyguardWallpapers = null;

    private View mItemLockScreen;
    private View mItemHomeScreen;
    private ImageView mIconLockScreen;
    private ImageView mIconHomeScreen;
    private LinearLayout typeBar;
    private LinearLayout barLayout;
    private TextView barTitle;

    private TextView mWallpaperItem;
    private TextView mLiveWallpaperItem;
    private TextView mGallaryItem;
    private TextView mVideoWallpaperItem;

    private List<ResolveInfo> rList = null;
    private PackageManager pm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //A:DWYYL-1696 wangjian 20150619 {
        if(getRomAvailMemory(getContentResolver())){
            Toast.makeText(this, R.string.no_space_left_to_setas, Toast.LENGTH_SHORT).show();
            finish();
        }
        //
        // TODO Auto-generated method stub
        initViews();
//        homeWallpapers = findBundledWallpapers();
//        keyguardWallpapers = findBundledKeyguardWallpapers();
//        getCurrentWallpaper();
        super.onCreate(savedInstanceState);
    }

    private void initViews() {

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // android.R.id.home will be triggered in onOptionsItemSelected()
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(R.string.selece_wallpaper_from);
        }
        setContentView(R.layout.wallpaper_type);
//        mItemLockScreen = findViewById(R.id.lock_screen_type);
//        mIconLockScreen = (ImageView) findViewById(R.id.lock_screen_icon);
//        mItemHomeScreen = findViewById(R.id.home_screen_type);
//        mIconHomeScreen = (ImageView) findViewById(R.id.home_screen_icon);
//        mItemLockScreen.setOnClickListener(this);
//        mItemHomeScreen.setOnClickListener(this);
        mWallpaperItem = (TextView)findViewById(R.id.wallpaper_item);
        mLiveWallpaperItem = (TextView)findViewById(R.id.live_wallpaper_item);
        mGallaryItem = (TextView)findViewById(R.id.gallery_item);
        mVideoWallpaperItem = (TextView)findViewById(R.id.video_wallpaper_item);

        mWallpaperItem.setOnClickListener(this);
        mLiveWallpaperItem.setOnClickListener(this);
        mGallaryItem.setOnClickListener(this);
        mVideoWallpaperItem.setOnClickListener(this);
        mVideoWallpaperItem.setVisibility(View.INVISIBLE);
        typeBar = (LinearLayout) findViewById(R.id.typebar);
        barTitle = (TextView) typeBar.findViewById(R.id.bartitle);
        barTitle.setText(R.string.selece_wallpaper_from);
        barLayout = (LinearLayout) typeBar.findViewById(R.id.barlayout);
        barLayout.setOnClickListener(this);

    }

    private void getKeyguardWallpaper() {
//        WallpaperManager wm = WallpaperManager.getInstance(this);
//        Drawable lockscreen = wm.peekDrawableForKeyguard();
//        if (lockscreen == null) {
//            Resources sysRes = Resources.getSystem();
//            int res_id = sysRes.getIdentifier("keyguard_wallpaper", "drawable",
//                    "android");
//            lockscreen = getResources().getDrawable(res_id);
//        }
//        mIconLockScreen.setBackgroundDrawable(lockscreen);
    }

    private void getHomeWallpaper() {
        WallpaperManager wm = WallpaperManager.getInstance(this);
        //modify by wangjian for keyguard wallpaper start
        if (wm.getWallpaperInfo() != null) {
                Drawable homescreen = getThumb(wm.getWallpaperInfo());
                mIconHomeScreen.setBackgroundDrawable(homescreen);
        }else {
            Drawable homescreen = wm.getDrawable();
            mIconHomeScreen.setBackgroundDrawable(homescreen);
        }
        //modufy by wangjian for keyguard wallpaper start
    }

    private void getCurrentWallpaper() {
        getKeyguardWallpaper();
        getHomeWallpaper();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        int id = arg0.getId();
        Intent intent = null;
        switch (id) {
        case R.id.wallpaper_item:
            intent = new Intent(this, WallpaperItemPickerActivity.class);
            //intent.putExtra(KEYGUARD_WALLPAPER, isKeyguardWallpaper);
            //intent.putExtra(WALLPAPER_ITEM_PICK, WALLPAPER_ITEM);
            //startActivityForResult(intent, REQUEST_SET_KEYGUARD_WALLPAPER);
            startActivity(intent);
            break;
        case R.id.live_wallpaper_item:
            intent = getLiveWallpaperIntent();
            if(intent != null) startActivity(intent);//startActivityForResult(intent, REQUEST_SET_HOME_WALLPAPER);
            break;
        case R.id.gallery_item:
            intent = getGalleryIntent();
            if(intent != null) {
//                if(isKeyguardWallpaper){
//                    intent.putExtra(KEYGUARD_WALLPAPER,isKeyguardWallpaper);
//                    startActivityForResult(intent, REQUEST_SET_KEYGUARD_WALLPAPER);
//                }else {
//                    startActivityForResult(intent, REQUEST_SET_HOME_WALLPAPER);
//                }
                startActivity(intent);
            }
            break;
        case R.id.video_wallpaper_item:
            intent = getVideoWallpaperIntent();
            if(intent != null) startActivity(intent);//startActivityForResult(intent, REQUEST_SET_HOME_WALLPAPER);
            break;
        case R.id.barlayout:
            finish();
            break;
        default:
            break;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //getCurrentWallpaper();
    }

    @Override
    protected void onActivityResult(final int requestCode,
            final int resultCode, final Intent data) {
//        if (RESULT_OK == resultCode) {
//            if (REQUEST_SET_KEYGUARD_WALLPAPER == requestCode) {
//                getKeyguardWallpaper();
//            } else if (REQUEST_SET_HOME_WALLPAPER == requestCode) {
//                getHomeWallpaper();
//            }
//        }
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
    private Drawable getThumb(WallpaperInfo info) {
        final PackageManager packageManager = getPackageManager();
        final Resources res = getResources();
        BitmapDrawable galleryIcon = (BitmapDrawable) res
                .getDrawable(R.drawable.livewallpaper_placeholder);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        Canvas canvas = new Canvas();
        // / M: moved the size computing out of the for loop. @{
        int thumbWidth = res
                .getDimensionPixelSize(R.dimen.live_wallpaper_thumbnail_width);
        int thumbHeight = res
                .getDimensionPixelSize(R.dimen.live_wallpaper_thumbnail_height);

        // / M: fix the thumbnail image null bug. @{
        BitmapDrawable thumb = (BitmapDrawable) info
                .loadThumbnail(packageManager);
        BitmapDrawable thumbNew;
        if (thumb != null) {
            Bitmap bitmap = Bitmap.createScaledBitmap(thumb.getBitmap(),
                    thumbWidth, thumbHeight, false);
            thumbNew = new BitmapDrawable(res, bitmap);
        } else {
            Bitmap thumbnail = Bitmap.createBitmap(thumbWidth, thumbHeight,
                    Bitmap.Config.ARGB_8888);

            paint.setColor(res
                    .getColor(R.color.live_wallpaper_thumbnail_background));
            canvas.setBitmap(thumbnail);
            canvas.drawPaint(paint);

            galleryIcon.setBounds(0, 0, thumbWidth, thumbHeight);
            galleryIcon.setGravity(Gravity.CENTER);
            galleryIcon.draw(canvas);

            String title = info.loadLabel(packageManager).toString();

            paint.setColor(res
                    .getColor(R.color.live_wallpaper_thumbnail_text_color));
            paint.setTextSize(res
                    .getDimensionPixelSize(R.dimen.live_wallpaper_thumbnail_text_size));

            canvas.drawText(
                    title,
                    (int) (thumbWidth * 0.5),
                    thumbHeight
                            - res.getDimensionPixelSize(R.dimen.live_wallpaper_thumbnail_text_offset),
                    paint);
            thumbNew = new BitmapDrawable(res, thumbnail);
        }
        return thumbNew;
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

    public Pair<ApplicationInfo, Integer> getKeyguardWallpaperArrayResourceId() {
//        final String packageName = getResources().getResourcePackageName(
//                R.array.keyguard_wallpapers);
//        try {
//            ApplicationInfo info = getPackageManager().getApplicationInfo(
//                    packageName, 0);
//            return new Pair<ApplicationInfo, Integer>(info,
//                    R.array.keyguard_wallpapers);
//        } catch (PackageManager.NameNotFoundException e) {
//            return null;
//        }
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

    public Pair<ApplicationInfo, Integer> getWallpaperArrayResourceId() {
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
                String label = info.activityInfo.packageName;
                if(label.contains(GALLERY)){
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
                String label = info.activityInfo.packageName;
                if(label.contains(LIVE_WALLPAPER)){
                    Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                    intent.setComponent(new ComponentName(
                            info.activityInfo.packageName, info.activityInfo.name));
                    return intent;
                }
            }
        }
        return null;
    }

    private Intent getVideoWallpaperIntent() {
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
                String label = info.activityInfo.packageName;
                if(label.contains(VIDEO_WALLPAPER)){
                    Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                    intent.setComponent(new ComponentName(
                            info.activityInfo.packageName, info.activityInfo.name));
                    return intent;
                }
            }
        }
        return null;
    }

    //A:DWYYL-1696 wangjian 20150619 {
        public static boolean getRomAvailMemory(ContentResolver mResolver){
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long availableSize = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            if(availableSize < getMemThreshold(mResolver)){
                return true;
            } else{
                return false;
            }
        }
        private static final int DEFAULT_THRESHOLD_PERCENTAGE = 10;
        private static final int DEFAULT_THRESHOLD_MAX_BYTES = 50*1024*1024; // 50MB
        private static long getMemThreshold(ContentResolver mResolver) {
            long value = Settings.Global.getInt(
                                  mResolver,
//                                  Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE,
                                   "sys_storage_threshold_percentage",
                                  DEFAULT_THRESHOLD_PERCENTAGE);
            value = (value*getRomMemory())/100;
            long maxValue = Settings.Global.getInt(
                    mResolver,
//                    Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES,
                    "sys_storage_threshold_max_bytes",
                    DEFAULT_THRESHOLD_MAX_BYTES);
            return value < maxValue ? value : maxValue;
        }
        private static long getRomMemory(){
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return blockSize * totalBlocks;
        }
        //A}
}
