package com.ds.phonoexplorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.phonoexplorer.utils.ImageLoader;
import com.ds.phonoexplorer.utils.IndexableListView;
import com.ds.phonoexplorer.utils.MediaFile;
import com.ds.phonoexplorer.utils.MimeUtils;
import com.ds.phonoexplorer.utils.SharedDataStore;
import com.ds.phonoexplorer.utils.StringMatcher;
import com.ds.phonoexplorer.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class SearchActivity extends FragmentActivity implements OnItemClickListener,ActionBar.TabListener   {

	private static int searchType;

	private static PackageManager packageManager = null;
	private List<ApplicationInfo> installedAppsList = null;
	ArrayList<ApplicationInfo> applist;
	private static SearchListAdapter adapter;;
	public static ListView listView;
	ArrayList<String> apkNames;

	private static Uri mUri = null;
	private static String mFilename = null;
	private File fdelete;
	private boolean backUpSuccess = false;

	private ImageLoader mImageLoader;
	Drawable drawable;

	private AlertDialog mExDialog = null;
	public EditText mSearchEditText;
	public static boolean isBackSpace;
	//

	private ArrayList<RowItem> mSearchresultAllList = null;
	private ArrayList<RowItem> mSearchresultAppList = null;
	private ArrayList<RowItem> mSearchInstalledAppsList = null;
	private ArrayList<RowItem> mSearchresultContactList = null;
	private ArrayList<RowItem> mSearchresultImageList = null;
	private ArrayList<RowItem> mSearchresultMusicList = null;
	private ArrayList<RowItem> mSearchresultVideoList = null;
	private ArrayList<RowItem> mSearchresultDocumentList = null;
	private ArrayList<RowItem> mSearchresultScreenshotList = null;
	private ArrayList<RowItem> mSearchresultCameraList = null;
	private ArrayList<RowItem> mSearchresultDownloadList = null;

	private static ArrayList<RowItem> origList = null;
	private ArrayList<ApplicationInfo> origAppList = null;
	ArrayList<ApplicationInfo> results = null;
	private AdView mAdView;
	private InterstitialAd mInterstitialAd;

	DataTypeCollectionPagerAdapter mAppSectionsPagerAdapter;
	static ViewPager mViewPager;

	static int mCurrentViewPagerPosition = 0;
	static int mFromDrawer= 0;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	public TextView extratxt;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mContentList;

	public static Context mContext = null;
	public static boolean isDialogOpen = false;
	public static boolean isCloseFromService = false;
	String[] contentSize;
	private WebView mWebview;
	DrawerListAdapter adapterDrawerList;

	public boolean scrollState = false;

	static RadioButton toCheckInOrder;
	static RadioButton toCheckSortBy; 

	static ActionMode mActionMode;
	static boolean isSelectMode;
	static boolean isSelectModeRemoveValid;
	//	ArrayList<RowItem> mSelectedRow;
	HashMap<Integer,RowItem> selectedFiles;
	SparseBooleanArray mSelectedItemsIds;
	int mSelectedPosition;
	Iterator it;

	boolean isRefresh,isSortby = false;

	private final BroadcastReceiver closeActivity = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			isCloseFromService = true;
			finish();                                   
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.overridePendingTransition(R.anim.in, R.anim.out);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.drawer_viewpager_layout);
		registerReceiver(closeActivity, new IntentFilter("close"));
		mContext = this;
		initAd(mContext);
		contentSize = new String[10];
		mTitle = mDrawerTitle = getTitle();
		mContentList = getResources().getStringArray(R.array.content_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		adapterDrawerList = new DrawerListAdapter(this, mContentList, null);

		mDrawerList.setAdapter(adapterDrawerList);
		mDrawerList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		/*		if (savedInstanceState == null) {
			selectItem(0);
		}*/

		adapter = new SearchListAdapter();
		packageManager = getApplication().getPackageManager();

		mAppSectionsPagerAdapter = new DataTypeCollectionPagerAdapter(getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();
		//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				scrollState = true;
				Utils.hideSoftKeyboard(mSearchEditText, mContext);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onPageSelected(int position) {
				if(mActionMode != null)
					mActionMode.finish();
				mCurrentViewPagerPosition = position;
				//				Toast.makeText(getApplicationContext(), "position = " + position, 1).show();
				searchType = position;
				updateState(mCurrentViewPagerPosition);
				adapter.setSearchList(getList());
				adapter.notifyDataSetChanged();
				//				setTitle(mContentList[position]);
				//				onRefreshSortBy();
				adapterDrawerList.notifyDataSetChanged();
				mSearchEditText.setText("");
				//				Utils.setShowInputMethod(mSearchEditText,mContext);
				//				mSearchView.setQuery("", false);
				invalidateOptionsMenu();
			}
		});

		AppRater.app_launched(mContext);

		Utils.mSoundStatus = SharedDataStore.getInstance(mContext).getSoundStatus();
		Utils.mOptionMenuSoundStatus = SharedDataStore.getInstance(mContext).getoptionMenuSoundStatus();
		Utils.mFloatinIconStatus = SharedDataStore.getInstance(mContext).getFloatingIconStatus();
		Utils.mFloatinIconRebootStatus= SharedDataStore.getInstance(mContext).getFloatingIconRebootStatus();
		Utils.mCurrentSortBy = SharedDataStore.getInstance(mContext).getCurrentSortBy();
		Utils.mCurrentInOrder = SharedDataStore.getInstance(mContext).getCurrentInOrder();
		//		mWebview  = new WebView(this);

		//		mWebview.getSettings().setJavaScriptEnabled(true);

		mImageLoader = new ImageLoader(SearchActivity.this, getListPreferredItemHeight()) {
			@Override
			protected Bitmap processBitmap(Uri data) {
				// This gets called in a background thread and passed the data from
				//				ImageLoader.loadImage(data,);
				//				return Utils.loadContactPhotoThumbnail((String) data, getImageSize(),mContext);
				return Utils.loadContactPhotoThumbnailFromContactUri(mContext,data);
			}
		};

		// Set a placeholder loading image for the image loader
		mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);

		// Add a cache to the image loader
		mImageLoader.addImageCache(SearchActivity.this.getSupportFragmentManager(), 0.1f);

		//play background sound
		if(Utils.mSoundStatus){
			Intent svc = new Intent(this, BackgroundSoundService.class);
			startService(svc);
		}

		Thread task = new Thread()
		{
			@Override
			public void run()
			{
				updateSizetemp();
			}
		};

		task.start();
	}

	public class DrawerListAdapter extends ArrayAdapter<String> {

		private final Context context;
		private final String[] itemname;
		private final Integer[] imgid;

		public DrawerListAdapter(Context context, String[] itemname, Integer[] imgid) {
			super(context, R.layout.drawer_list_row, itemname);
			// TODO Auto-generated constructor stub

			this.context=context;
			this.itemname=itemname;
			this.imgid=imgid;
		}

		public View getView(int position,View view,ViewGroup parent) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			View rowView=inflater.inflate(R.layout.drawer_list_row, null,true);

			TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
			extratxt = (TextView) rowView.findViewById(R.id.textView1);

			txtTitle.setText(itemname[position]);

			TypedArray imgs = getResources().obtainTypedArray(R.array.drawerListIcon);
			//get resourceid by index
			imgs.getResourceId(position, -1);
			// or set you ImageView's resource to the id
			imageView.setImageResource(imgs.getResourceId(position, -1));
			imgs.recycle();
			//			imageView.setImageResource(R.drawable.phoneexplorer);

			extratxt.setText(updateSize(position));

			if(position == mCurrentViewPagerPosition){
				txtTitle.setTextColor(Color.parseColor("#000000"));
				rowView.setBackgroundColor(Color.parseColor("#9dc1e0"));
			}else{
				rowView.setBackgroundColor(Color.parseColor("#ffffff"));
			}
			return rowView;

		};
	}




	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		mCurrentViewPagerPosition = position;
		updateState(mCurrentViewPagerPosition);
		adapter.setSearchList(getList());
		adapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(position);
		mDrawerList.setItemChecked(position, true);
		//		getActionBar().setTitle(mContentList[position]);
		//		setTitle(mContentList[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		//		mTitle = title;
		//		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		mViewPager.setCurrentItem(tab.getPosition());

	}


	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		if (mAdView != null) {  //uncomment it
			mAdView.pause();
		}
		super.onPause();
		if(!isDialogOpen){
			if(Utils.mFloatinIconStatus){
				startService(new Intent(this,floatingUIService.class));
				Toast.makeText(mContext,getResources().getString(R.string.onCloseToast), Toast.LENGTH_LONG).show();
			}

			if(Utils.mSoundStatus){
				Intent svc = new Intent(this, BackgroundSoundService.class);
				startService(svc);
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onRestart();

		if (mAdView != null) {//uncomment it
			mAdView.resume();
		}
		isDialogOpen = false;
		stopService(new Intent(this,floatingUIService.class));
		//		Utils.setShowInputMethod(mSearchEditText,mContext);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdView != null) {//uncomment it
			mAdView.destroy();
		}
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
		if(Utils.mFloatinIconStatus && !isCloseFromService){
			startService(new Intent(this,floatingUIService.class));
			Toast.makeText(mContext,getResources().getString(R.string.onCloseToast), Toast.LENGTH_LONG).show();
		}

		if(Utils.mSoundStatus){
			Intent svc = new Intent(this, BackgroundSoundService.class);
			startService(svc);
		}

		/*		mSearchresultAllList = null;
		mSearchresultAppList = null;
		mSearchInstalledAppsList = null;
		mSearchresultContactList = null;
		mSearchresultImageList = null;
		mSearchresultMusicList = null;
		mSearchresultVideoList = null;
		mSearchresultDocumentList = null;
		mSearchresultScreenshotList = null;
		mSearchresultCameraList = null;
		mSearchresultDownloadList = null;
		installedAppsList = null;
		applist = null;
		adapter = null;*/

		unregisterReceiver(closeActivity);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//		super.onBackPressed();

		Utils.backPressDialog(mContext);
		if (mInterstitialAd.isLoaded()) {
			//						mInterstitialAd.show();
		}
	}

	public  void showOptionsForApplication(final int position){

		final ApplicationInfo app =  adapter.getItem(position).appsList.get(position);
		AlertDialog.Builder build = new AlertDialog.Builder(SearchActivity.this);
		String[] itemsResource = {
				getResources().getString(R.string.item_open),//0
				getResources().getString(R.string.item_share),//1
				getResources().getString(R.string.item_backup),//2
				getResources().getString(R.string.item_move_sdcard),//3
				getResources().getString(R.string.item_uninstall),//4
				getResources().getString(R.string.item_notification),//5
				getResources().getString(R.string.item_search_play)//6
		};


		build.setTitle(app.loadLabel(packageManager));
		build.setItems(itemsResource,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				isDialogOpen = true;
				switch (item) {
				case 0: 
					//Launch
					Intent launchIntent;
					PackageManager manager = getPackageManager();
					try {
						launchIntent = manager.getLaunchIntentForPackage(app.packageName);
						//						Toast.makeText(getApplicationContext(), "app.packageName  =  "+app.packageName, Toast.LENGTH_LONG).show();
						if (launchIntent == null)
							throw new PackageManager.NameNotFoundException();
						launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
						startActivityForResult(launchIntent,Utils.SETTING_REQUEST_CODE);
						//						startActivity(launchIntent);
					} catch (PackageManager.NameNotFoundException e) {
						Toast.makeText(getApplicationContext(), getString(R.string.not_installed_error_msg),Toast.LENGTH_SHORT).show();
						e.printStackTrace();
						//						Toast.makeText(getApplicationContext(), "in catch", Toast.LENGTH_LONG).show();
					}

					//					Toast.makeText(MainActivity.this,"Open",Toast.LENGTH_LONG).show();
					break;

				case 1: 
					// share
					try {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_SEND);
						intent.setType("application/zip");
						intent.putExtra(Intent.EXTRA_STREAM,
								Uri.fromFile(new File(
										app.sourceDir)));
						//						Toast.makeText(getApplicationContext(), "app.processName =  " + app.processName, Toast.LENGTH_LONG).show();
						if (null != intent) {
							startActivity(intent);
						}
					} catch (Exception e) {
						Toast.makeText(SearchActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
					}
					break;
				case 2:
					//					Toast.makeText(MainActivity.this,"BackUp",Toast.LENGTH_LONG).show();
					// backup // restore
					new Handler().postDelayed(new Runnable() {
						public void run() {
							try {

								Uri newUri = Uri
										.fromFile(new File(app.sourceDir));
								boolean alreadyArchived = false;

								String sourcePath = Environment.getExternalStorageDirectory()
										+ app.sourceDir;
								File source = new File(sourcePath);
								String destinationPath = Environment.getExternalStorageDirectory() + "/AppsBackUp";

								int index = sourcePath.lastIndexOf("/");
								String fileName = sourcePath.substring(index + 1);
								alreadyArchived = checkForBackup(fileName);
								if (alreadyArchived) {
									Toast.makeText(
											SearchActivity.this,
											"Apk already saved at:"
													+ destinationPath
													+ "/"
													+ fileName
													.toString(),
													Toast.LENGTH_SHORT)
													.show();
									return;
								}
								File folder = new File(destinationPath);
								boolean isFolderExists = true;
								if (!folder.exists()) {
									isFolderExists = folder.mkdir();
								}

								File dest = new File(destinationPath + "/"+ fileName);
								if (!dest.exists()) {
									dest.createNewFile();
								}

								AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
								builder.setMessage("Apk saved at: " + dest)
								.setCancelable(false)
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										//do things
									}
								});
								AlertDialog alert = builder.create();
								alert.show();

								backUpSuccess = saveFile(newUri, dest);
								Toast.makeText(mContext,	"Apk saved at: " + dest,Toast.LENGTH_SHORT).show();

							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}, 50);
					break;
				case 3://MovetoSDCARD
					Intent intentMove = new Intent();
					if(appInstalledOrNot(app.packageName)) {
						if (Utils.apiLevel >= 9) {
							intentMove.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							intentMove.setData(Uri.parse("package:" + app.packageName));
						} else {
							final String appPkgName = (Utils.apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");

							intentMove.setAction(Intent.ACTION_VIEW);
							intentMove.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
							intentMove.putExtra(appPkgName, app.packageName);
						}
						startActivityForResult(intentMove,Utils.SETTING_REQUEST_CODE);
						Intent myIntent = new Intent(SearchActivity.this,MoveToSdcardActivity.class);
						startActivity(myIntent);
					} else {
						try{
							startActivityForResult(intentMove,Utils.SETTING_REQUEST_CODE);
							Intent myIntent = new Intent(SearchActivity.this,MoveToSdcardActivity.class);
							startActivity(myIntent);
							Toast.makeText(getApplicationContext(), getString(R.string.not_installed_error_msg),Toast.LENGTH_SHORT).show();
						}catch(Exception e){
							Toast.makeText(getApplicationContext(), getString(R.string.not_installed_error_msg),Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
					break;

				case 4://uninstall
					Intent intentuninstall = new Intent();
					//					Toast.makeText(mContext, "app.packageName = " + app.packageName.toString(), Toast.LENGTH_LONG).show();
					if(app.packageName.equalsIgnoreCase("com.ds.phonoexplorer")){
						Toast.makeText(mContext, "Are you kidding me?", Toast.LENGTH_LONG).show();
					}else{
						//						intentuninstall = new Intent(Intent.ACTION_DELETE);
						//						intentuninstall.setData(Uri.parse("package:" + app.packageName));
						if(appInstalledOrNot(app.packageName)) {
							/*						if (Utils.apiLevel >= 9) {
							intentuninstall.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							intentuninstall.setData(Uri.parse("package:" + app.packageName));
						} else {
							final String appPkgName = (Utils.apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");

							intentuninstall.setAction(Intent.ACTION_VIEW);
							intentuninstall.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
							intentuninstall.putExtra(appPkgName, app.packageName);
						}*/
							intentuninstall = new Intent(Intent.ACTION_DELETE);
							intentuninstall.setData(Uri.parse("package:" + app.packageName));
							startActivity(intentuninstall);
							//							getList().remove(position);
							//							adapter.notifyDataSetChanged();
							//						startActivityForResult(intentuninstall,Utils.SETTING_REQUEST_CODE);
							//						Intent myIntent = new Intent(mContext,UninstallActivity.class);
							//						startActivity(myIntent);
						} else {
							try{
								startActivityForResult(intentuninstall,Utils.SETTING_REQUEST_CODE);
								//							Intent myIntent = new Intent(mContext,UninstallActivity.class);
								//							startActivity(myIntent);
								Toast.makeText(getApplicationContext(), getString(R.string.not_installed_error_msg),Toast.LENGTH_SHORT).show();
							}catch(Exception e){
								Toast.makeText(getApplicationContext(), getString(R.string.not_installed_error_msg),Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							}

						}
					}
					break;

				case 5: //notification
					Intent intentnotification = new Intent();
					if(appInstalledOrNot(app.packageName)) {
						if (Utils.apiLevel >= 9) {
							intentnotification.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							intentnotification.setData(Uri.parse("package:" + app.packageName));
						} else {
							final String appPkgName = (Utils.apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName");

							intentnotification.setAction(Intent.ACTION_VIEW);
							intentnotification.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
							intentnotification.putExtra(appPkgName, app.packageName);
						}
						startActivityForResult(intentnotification,Utils.SETTING_REQUEST_CODE);
						Intent myIntent = new Intent(SearchActivity.this,NotificationHiderHelperActivity.class);
						startActivity(myIntent);
					} else {
						try{
							startActivityForResult(intentnotification,Utils.SETTING_REQUEST_CODE);
							Intent myIntent = new Intent(SearchActivity.this,NotificationHiderHelperActivity.class);
							startActivity(myIntent);
							Toast.makeText(getApplicationContext(), getString(R.string.not_installed_error_msg),Toast.LENGTH_SHORT).show();
						}catch(Exception e){
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), getString(R.string.not_installed_error_msg),Toast.LENGTH_SHORT).show();
						}
					}
					break;

				case 6: //Search in Play store
					startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.mPlayStoreLink +""+ app.packageName)),Utils.SETTING_REQUEST_CODE);
					//					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPlayStoreLink +""+ app.packageName)));
					break;
				}

				dialog.dismiss();
			}
		});

		mExDialog = build.create();
		mExDialog.setCanceledOnTouchOutside(true);
		mExDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Utils.SETTING_REQUEST_CODE){/*
			if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
				mInterstitialAd.show();
			} else {
				finish();
				this.overridePendingTransition(R.anim.in,R.anim.out);
			}
		 */}
		if(requestCode == Utils.MULTI_DELETE_REQUEST_CODE || requestCode == Utils.MULTI_SHARE_REQUEST_CODE){
			mActionMode.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		if(Utils.isContact/*||Utils.isApplication*/){
			if(menu != null){
				MenuItem item = (MenuItem)menu.findItem(R.id.action_sort_by);
				if(item != null)
					item.setVisible(false);
			}
		}
		if(!Utils.isContact){
			if(menu != null){
				MenuItem item = (MenuItem)menu.findItem(R.id.action_add_contact);
				if(item != null)
					item.setVisible(false);
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(Utils.mOptionMenuSoundStatus)
			Utils.optionMenuSound(mContext);

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			Utils.hideSoftKeyboard(mSearchEditText, mContext);
			return true;
		}

		isDialogOpen = true;

		switch (item.getItemId()) {
		case R.id.action_add_contact:
			final Intent intent = new Intent(Intent.ACTION_INSERT, Contacts.CONTENT_URI);
			startActivity(intent);
			return true;
		case R.id.action_sort_by:
			sortByDialog(mContext);
			return true;
		case R.id.action_about_us:
			Intent intentAbout = new Intent(this, About_Us_Activity.class);
			startActivity(intentAbout);
			return true;
			/*		case R.id.action_help:
			Intent intenthelp = new Intent(this, HelpActivity.class);
			startActivity(intenthelp);
			return true;*/
		case R.id.action_rate_us:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppPlayStoreLink)));
			return true;
		case R.id.action_promo_video:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppVideoLink)));
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this,SettingActivity.class));
			return true;
		case R.id.action_refresh:
			if(Utils.isClickValid()){
				refresh();
				adapter.setSearchList(getList());
				adapter.notifyDataSetChanged();
				return true;	
			}else{
				return false;
			}
		default:
			return false;
		}
	}

	private void refresh() {
		// TODO Auto-generated method stub
		if(!isSortby)
			isRefresh = true;

		if(Utils.isAll){
			mSearchresultAllList.clear();
			initSearchFiles("");

		}else if(Utils.isApplication){
			mSearchresultAppList.clear();
			setupSearchViewForApplication("");

		}else if(Utils.isContact){
			mSearchresultContactList.clear();
			initSearchViewForContact("");

		}else if(Utils.isImage){
			mSearchresultImageList.clear();
			initSearchViewForImage("");

		}else if(Utils.isAudio){
			mSearchresultMusicList.clear();
			initSearchViewForMusic("");

		}else if(Utils.isVideo){
			mSearchresultVideoList.clear();
			initSearchViewForVideo("");

		}else if(Utils.isDocument){
			mSearchresultDocumentList.clear();
			initSearchViewForDocument("");

		}else if(Utils.isCamera){
			mSearchresultCameraList.clear();
			initSearchViewForCamera("");

		}else if(Utils.isDownload){
			mSearchresultDownloadList.clear();
			initSearchViewForDownload("");

		}else if(Utils.isScreenshot){
			mSearchresultScreenshotList.clear();
			initSearchViewForScreenshot("");

		}
		isRefresh = false;
	}

	public void getInstalledApplicationsList(){
		installedAppsList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
	}

	public void setupSearchViewForApplication(String newText) {
		if(installedAppsList == null){
			getInstalledApplicationsList();
		}
		//		Log.d("TEST", "start");
		new LoadApplications().execute(newText);
		//		Log.d("TEST", "END");
		//		adapter.notifyDataSetChanged();

	}

	private class LoadApplications extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String queryText = params[0];
			try {
				mSearchresultAppList = new ArrayList<RowItem>();		
				applist = new ArrayList<ApplicationInfo>();
				if(Utils.mCurrentSortBy == Utils.NAME){
					//					scrollBarCondition(true);
					Collections.sort(installedAppsList, AppNameComparator);
				}else if(Utils.mCurrentSortBy == Utils.TIME){
					//					scrollBarCondition(false);
					Collections.sort(installedAppsList, AppDateComparator);
				}else if(Utils.mCurrentSortBy == Utils.SIZE){
					//					scrollBarCondition(false);
					Collections.sort(installedAppsList, AppSizeComparator);
				}
				for (ApplicationInfo info : installedAppsList) {
					if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {	            	
						if (((String) info.loadLabel(packageManager)).toLowerCase().contains(queryText.toString().toLowerCase())) {
							applist.add(info);
							RowItem r= new RowItem(applist);
							if(r == null){
								continue;
							}
							mSearchresultAppList.add( r );
						}
					}
				}
			}
			//			installedAppsList = applist;
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			//			setListAdapter(listadaptor);
			//			installedAppsList = null;
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			/*if(mSearchresultAppList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else if(mSearchresultAppList.size() == 1) {
				Toast.makeText(mContext, mSearchresultAppList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultAppList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}
			 */
			/*			if(mSearchresultList.size() > 10)
				Utils.mScrollbarShow  = true;
			else*/
			try{
				if(mSearchresultAppList != null && mSearchresultAppList.size() > 6 /*&& Utils.mCurrentSortBy == Utils.NAME*/)
					Utils.mScrollbarShow  = true;
				else
					Utils.mScrollbarShow  = false;
				//				showScrollBar();

				adapter.setSearchList(mSearchresultAppList);
				adapter.notifyDataSetChanged();
				adapterDrawerList.notifyDataSetChanged();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing application list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting application list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Loading application list...");
			}
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	public static Comparator<ApplicationInfo> AppNameComparator = new Comparator<ApplicationInfo>() {

		@Override
		public int compare(ApplicationInfo e1, ApplicationInfo e2) {
			String s1 = e1.loadLabel(packageManager).toString();
			String s2 = e2.loadLabel(packageManager).toString();
			return s1.compareTo(s2);
		}
	};
	public static Comparator<ApplicationInfo> AppSizeComparator = new Comparator<ApplicationInfo>() {

		@Override
		public int compare(ApplicationInfo e1, ApplicationInfo e2) {

			File file1 = new File(e1.sourceDir);
			File file2 = new File(e2.sourceDir);
			if (Utils.mCurrentInOrder == Utils.ASC){
				return Long.valueOf(file1.length()).compareTo(Long.valueOf(file2.length()));
			}else{
				return Long.valueOf(file2.length()).compareTo(Long.valueOf(file1.length()));
			}
		}
	};
	public static Comparator<ApplicationInfo> AppDateComparator = new Comparator<ApplicationInfo>() {

		@Override
		public int compare(ApplicationInfo e1, ApplicationInfo e2) {
			File file1 = new File(e1.sourceDir);
			File file2 = new File(e2.sourceDir);
			long lastmodified1 = file1.lastModified();
			long lastmodified2 = file2.lastModified();

			String s1 =  Objects.toString(lastmodified1, null);
			String s2 = Objects.toString(lastmodified2, null);
			return compareName(s1,s2);
		}
	};

	public void initSearchViewForContact(String newText) {
		String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
				+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("
				+ Contacts.DISPLAY_NAME + " LIKE ?  ))";

		String[] projection = new String[] {
				Contacts._ID,
				Contacts.DISPLAY_NAME,
				Contacts.CONTACT_STATUS,
				Contacts.CONTACT_PRESENCE,
				Contacts.PHOTO_ID,
				Contacts.LOOKUP_KEY,
				Contacts.PHOTO_THUMBNAIL_URI
		};

		mSearchresultContactList = new ArrayList<RowItem>();

		Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, projection, select, new String[]{"%"+ newText+ "%" }, Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC");

		try{
			if(cursor!=null && cursor.getCount()!=0){
				mSearchresultContactList.clear();
				if (cursor.moveToFirst()) {
					do {
						String title = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
						Uri contactIdUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(Contacts._ID))); ;// TODO Find uri from table;
						String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.PHOTO_THUMBNAIL_URI));						final Uri contactUri = Contacts.getLookupUri(
								cursor.getColumnIndexOrThrow(Contacts._ID),
								cursor.getString(cursor.getColumnIndexOrThrow(Contacts.LOOKUP_KEY)));
						RowItem r= new RowItem(title, contactUri, photoUri,contactIdUri); 
						mSearchresultContactList.add( r );
					} while (cursor.moveToNext());

				}
			}
		}catch( IllegalStateException e){
			e.printStackTrace();
		}

		if(mSearchresultContactList.size() > 6)
			Utils.mScrollbarShow  = true;
		else
			Utils.mScrollbarShow  = false;

		adapter.setSearchList(mSearchresultContactList);
		adapter.notifyDataSetChanged();
		adapterDrawerList.notifyDataSetChanged();
	}

	public void initSearchViewForImage(String newText) {

		mSearchresultImageList = new ArrayList<RowItem>();
		new LoadImage().execute(newText);
	}

	public class LoadImage extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String newText = params[0];

			Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, Media.TITLE +" LIKE ? "/* + "OR " + Media.ARTIST + " LIKE ? "*/, new String[]{"%"+ newText+ "%" },
					android.provider.MediaStore.Images.Media.TITLE + " COLLATE NOCASE ASC");

			try{
				if(cursor!=null && cursor.getCount()!=0){
					mSearchresultImageList.clear();
					if (cursor.moveToFirst()) {
						do {
							int id = cursor.getInt((cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
							Uri fileUri = Uri.fromFile(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))));
							String imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));

							if(!Utils.mShowHidden && imageName.startsWith("."))
								continue;
							RowItem r= new RowItem(imageName, fileUri);
							if(r == null){
								continue;
							}
							mSearchresultImageList.add( r );
						} while (cursor.moveToNext());

						onRefreshSortBy();
						//						
						//						if(Utils.mCurrentSortBy == Utils.NAME){
						//							Collections.sort(getList(), NameComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.TIME){
						//							Collections.sort(getList(), ModifiedDateComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.SIZE){
						//							Collections.sort(getList(), SizeComparator);
						//						}
					}
				}
			}catch( IllegalStateException e){
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			if(mSearchresultImageList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultImageList.size() == 1) {
				Toast.makeText(mContext, mSearchresultImageList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultImageList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}
			 */
			if(mSearchresultImageList != null && mSearchresultImageList.size() > 6/* && Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();

			adapter.setSearchList(mSearchresultImageList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing images list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting images list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading images list...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	public void initSearchViewForMusic(String newText) {

		mSearchresultMusicList = new ArrayList<RowItem>();
		new LoadMusic().execute(newText);
	}

	public class LoadMusic extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String newText = params[0];

			//Search for atrist name disable becoz of duplicate entires

			Cursor cursor = getContentResolver().query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, Media.TITLE +" LIKE ? "
					/* + "OR " + Media.ARTIST + " LIKE ? "*/, new String[]{"%"+ newText+ "%" }, android.provider.MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");

			try{
				if(cursor!=null && cursor.getCount()!=0){
					mSearchresultMusicList.clear();
					if (cursor.moveToFirst()) {
						do {
							Uri fileUri = Uri.fromFile(new File(cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))));
							int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
							String track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

							if(!Utils.mShowHidden && track.startsWith("."))
								continue;
							RowItem r= new RowItem(track, fileUri);
							if(r == null){
								continue;
							}
							mSearchresultMusicList.add( r );
						} while (cursor.moveToNext());

						onRefreshSortBy();

						//						if(Utils.mCurrentSortBy == Utils.NAME){
						//							Collections.sort(getList(), NameComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.TIME){
						//							Collections.sort(getList(), ModifiedDateComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.SIZE){
						//							Collections.sort(getList(), SizeComparator);
						//						}
					}
				}
			}catch(IllegalStateException e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			if(mSearchresultMusicList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultMusicList.size() == 1) {
				Toast.makeText(mContext, mSearchresultMusicList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultMusicList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}*/

			if(mSearchresultMusicList.size() > 6 /*&& Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();

			adapter.setSearchList(mSearchresultMusicList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing audio list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting audio list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading audio list...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	public void initSearchViewForVideo(String newText) {
		mSearchresultVideoList = new ArrayList<RowItem>();
		new LoadVideo().execute(newText);
	}

	public class LoadVideo extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String newText = params[0];

			Cursor cursor = getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, Media.TITLE +" LIKE ? ", new String[]{"%"+ newText+ "%" },  android.provider.MediaStore.Video.Media.TITLE + " COLLATE NOCASE ASC");

			try{
				if(cursor!=null && cursor.getCount()!=0){
					mSearchresultVideoList.clear();
					if (cursor.moveToFirst()) {
						do {
							Uri fileUri = Uri.fromFile(new File(cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))));
							String track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));

							String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

							int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

							if(!Utils.mShowHidden && track.startsWith("."))
								continue;
							RowItem r= new RowItem(track, fileUri); 
							if(r == null){
								continue;
							}
							mSearchresultVideoList.add( r );
						} while (cursor.moveToNext());

						onRefreshSortBy();
						//						
						//						if(Utils.mCurrentSortBy == Utils.NAME){
						//							Collections.sort(getList(), NameComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.TIME){
						//							Collections.sort(getList(), ModifiedDateComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.SIZE){
						//							Collections.sort(getList(), SizeComparator);
						//						}
					}
				}
			}catch ( IllegalStateException e){
				e.printStackTrace();
			}



			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			if(mSearchresultVideoList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultVideoList.size() == 1) {
				Toast.makeText(mContext, mSearchresultVideoList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultVideoList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}*/

			if(mSearchresultVideoList.size() > 6 /*&& Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();
			adapter.setSearchList(mSearchresultVideoList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing videos list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting videos list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading videos list...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
		ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
					applist.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return applist;
	}

	public void initSearchViewForDocument(String newText) {

		mSearchresultDocumentList = new ArrayList<RowItem>();
		new LoadDocument().execute(newText);
	}



	public class LoadDocument extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String newText = params[0];
			String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

			//Search for atrist name disable becoz of duplicate entires
			Cursor cursor = getContentResolver().query(android.provider.MediaStore.Files.getContentUri("external"), null,selection + " AND " + MediaStore.Files.FileColumns.TITLE +" LIKE ? "/* + "OR " + Media.ARTIST + " LIKE ? "*/, new String[]{"%"+ newText+ "%" },
					android.provider.MediaStore.Files.FileColumns.TITLE + " COLLATE NOCASE ASC");

			try{
				if(cursor!=null && cursor.getCount()!=0){
					mSearchresultDocumentList.clear();
					if (cursor.moveToFirst()) {
						do {
							File mFile = new File(cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA)));

							if(!MimeUtils.hasMimeType(Utils.getFileExtension(mFile.getPath()))){
								continue;
							}

							int fileType = MediaFile.getFileTypeInt(mFile.getPath());

							if(!MediaFile.isDocumentFileType(fileType) ){
								continue;
							}

							Uri fileUri = Uri.fromFile(mFile);
							int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
							String filename = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));
							String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));

							if(!Utils.mShowHidden && filename.startsWith("."))
								continue;
							RowItem r= new RowItem(filename, fileUri,fileType);
							if(r == null){
								continue;
							}
							mSearchresultDocumentList.add( r );
						} while (cursor.moveToNext());

						onRefreshSortBy();
						//						
						//						if(Utils.mCurrentSortBy == Utils.NAME){
						//							Collections.sort(getList(), NameComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.TIME){
						//							Collections.sort(getList(), ModifiedDateComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.SIZE){
						//							Collections.sort(getList(), SizeComparator);
						//						}
					}
				}
			}catch( IllegalStateException e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			if(mSearchresultDocumentList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultDocumentList.size() == 1) {
				Toast.makeText(mContext, mSearchresultDocumentList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultDocumentList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}*/


			if(mSearchresultDocumentList.size() > 6/* && Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();

			adapter.setSearchList(mSearchresultDocumentList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing documents list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting documents list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading documents list...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	public void initSearchFiles(String newText) {

		mSearchresultAllList = new ArrayList<RowItem>();

		new LoadFiles().execute(newText);
	}

	public class LoadFiles extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {

			String newText = params[0];
			String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE +" OR "
					+ MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " OR "
					+ MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR "
					+ MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

			Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), null,selection + " AND " + MediaStore.Files.FileColumns.TITLE +" LIKE ? ", new String[]{"%"+ newText+ "%" },
					/*MediaStore.Files.FileColumns.TITLE + " COLLATE NOCASE ASC"*/null);

			try{
				if(cursor!=null && cursor.getCount()!=0){
					mSearchresultAllList.clear();

					if (cursor.moveToFirst()) {
						do {
							File mFile = new File(cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA)));

							if(!MimeUtils.hasMimeType(Utils.getFileExtension(mFile.getPath()))){
								continue;
							}

							Uri fileUri = Uri.fromFile(mFile);
							String filename = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));

							if(!Utils.mShowHidden && filename.startsWith("."))
								continue;
							RowItem r= new RowItem(filename, fileUri/*,bitmap*/);
							if(r == null){
								continue;
							}
							mSearchresultAllList.add( r );
						} while (cursor.moveToNext());

						onRefreshSortBy();

						//						if(Utils.mCurrentSortBy == Utils.NAME){
						//							Collections.sort(getList(), NameComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.TIME){
						//							Collections.sort(getList(), ModifiedDateComparator);
						//						}else if(Utils.mCurrentSortBy == Utils.SIZE){
						//							Collections.sort(getList(), SizeComparator);
						//						}
					}
				}
			}catch( IllegalStateException e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			//			progress.dismiss();
			super.onPostExecute(result);
			if(mSearchresultAllList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultAllList.size() == 1) {
				Toast.makeText(mContext, mSearchresultAllList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultAllList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}*/

			if(mSearchresultAllList.size() > 6/* && Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();
			adapter.setSearchList(mSearchresultAllList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing files ...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting files ...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading files ...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}



	public void initSearchViewForCamera(String newText) {

		mSearchresultCameraList = new ArrayList<RowItem>();
		new LoadCamera().execute(newText);
	}



	public class LoadCamera extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String newText = params[0];


			File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			Log.d("TEST", "DCIM = " + dcim.getAbsolutePath());
			File thumbnails = new File(dcim, "/.thumbnails");
			Log.d("TEST", "thumbnails = " + thumbnails.getAbsolutePath());
			File isScreenshot = new File(dcim,"/ScreenShots");
			Log.d("TEST", "isScreenshot = " + isScreenshot.getAbsolutePath());
			File[] listOfImg = dcim.listFiles();
			if (dcim.isDirectory()){

				//for each child in DCIM directory
				for (int i = 0; i < listOfImg.length; ++i){
					//no thumbnails
					if( !listOfImg[i].getAbsolutePath().equals(thumbnails.getAbsolutePath()) && !listOfImg[i].getAbsolutePath().equals(isScreenshot.getAbsolutePath())){
						//only get the directory (100MEDIA, Camera, 100ANDRO, and others)
						if(listOfImg[i].isDirectory()) {
							//is a parent directory, get children
							File[] temp = listOfImg[i].listFiles();
							for(int j = 0; j < temp.length; ++j) {

								if(!Utils.mShowHidden && temp[j].getName().startsWith("."))
									continue;
								Uri fileUri = Uri.fromFile(temp[j]);
								RowItem r= new RowItem(temp[j].getName(), fileUri);
								if(r == null){
									continue;
								}
								mSearchresultCameraList.add( r );
								//		                        f.add(temp[j].getAbsolutePath());
							}
						}else if(listOfImg[i].isFile()){
							//is not a parent directory, get files
							if(!Utils.mShowHidden && listOfImg[i].getName().startsWith("."))
								continue;
							Uri fileUri = Uri.fromFile(listOfImg[i]);
							RowItem r= new RowItem(listOfImg[i].getName(), fileUri);
							if(r == null){
								continue;
							}
							mSearchresultCameraList.add( r );
							//		                    f.add(listOfImg[i].getAbsolutePath());
						}
					}
				}
				onRefreshSortBy();
				//				if(Utils.mCurrentSortBy == Utils.NAME){
				//					Collections.sort(getList(), NameComparator);
				//				}else if(Utils.mCurrentSortBy == Utils.TIME){
				//					Collections.sort(getList(), ModifiedDateComparator);
				//				}else if(Utils.mCurrentSortBy == Utils.SIZE){
				//					Collections.sort(getList(), SizeComparator);
				//				}
			}


			/*
			File fileCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera");
//			String path = Environment.getExternalStorageDirectory().toString()+"/DCIM/Camera";
			if(!fileCamera.exists()){
				fileCamera.mkdir();
			}
			Log.d("Files", "Path: " + fileCamera.getAbsolutePath());
//			File f = new File(path);
			File file[] = fileCamera.listFiles();
			Log.d("Files", "Size: "+ file.length);
			mSearchresultCameraList.clear();
			try{
				for (int i=0; i < file.length; i++)
				{
					Log.d("Files", "FileName:" + file[i].getName());
					if(!Utils.mShowHidden && file[i].getName().startsWith("."))
						continue;
					Uri fileUri = Uri.fromFile(file[i]);
					RowItem r= new RowItem(file[i].getName(), fileUri);
					if(r == null){
						continue;
					}
					mSearchresultCameraList.add( r );
				}
			}catch( Exception e){
				e.printStackTrace();
			}*/

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			if(mSearchresultCameraList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultCameraList.size() == 1) {
				Toast.makeText(mContext, mSearchresultCameraList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultCameraList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}
			 */
			if(mSearchresultCameraList.size() > 6 /*&& Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();
			adapter.setSearchList(mSearchresultCameraList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing camera files list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting camera files list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading camera files...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}


	public void initSearchViewForDownload(String newText) {

		mSearchresultDownloadList = new ArrayList<RowItem>();
		new LoadDownload().execute(newText);
	}



	public class LoadDownload extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String newText = params[0];
			File fileDownload = new File(Environment.getExternalStorageDirectory().toString()+"/Download");

			if(!fileDownload.exists()){
				fileDownload.mkdir();
			}
			Log.d("Files", "Path: " + fileDownload.getAbsolutePath());
			//			File f = new File(path);

			File file[] = fileDownload.listFiles();
			Log.d("Files", "Size: "+ file.length);
			mSearchresultDownloadList.clear();
			try{
				for (int i=0; i < file.length; i++)
				{
					Log.d("Files", "FileName:" + file[i].getName());
					if(!Utils.mShowHidden && file[i].getName().startsWith("."))
						continue;
					Uri fileUri = Uri.fromFile(file[i]);
					RowItem r= new RowItem(file[i].getName(), fileUri);
					if(r == null){
						continue;
					}
					mSearchresultDownloadList.add( r );
				}
				onRefreshSortBy();
				//				if(Utils.mCurrentSortBy == Utils.NAME){
				//					Collections.sort(getList(), NameComparator);
				//				}else if(Utils.mCurrentSortBy == Utils.TIME){
				//					Collections.sort(getList(), ModifiedDateComparator);
				//				}else if(Utils.mCurrentSortBy == Utils.SIZE){
				//					Collections.sort(getList(), SizeComparator);
				//				}
			}catch( Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			if(mSearchresultDownloadList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultDownloadList.size() == 1) {
				Toast.makeText(mContext, mSearchresultDownloadList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultDownloadList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}*/

			if(mSearchresultDownloadList.size() > 6 /*&& Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();
			adapter.setSearchList(mSearchresultDownloadList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing downloaded list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting downloaded list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading downloaded list...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}


	public void initSearchViewForScreenshot(String newText) {

		mSearchresultScreenshotList = new ArrayList<RowItem>();
		new LoadScreenshot().execute(newText);
	}



	public class LoadScreenshot extends AsyncTask<String, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(String... params) {
			String newText = params[0];
			File fileScreenshots = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/ScreenShots");

			if( !fileScreenshots.exists()){
				fileScreenshots = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/ScreenShots");
			}

			if(!fileScreenshots.exists()){
				fileScreenshots.mkdir();
			}

			Log.d("Files", "Path: " + fileScreenshots.getAbsolutePath());
			//			File f = new File(path);  

			File file[] = fileScreenshots.listFiles();
			Log.d("Files", "Size: "+ file.length);
			mSearchresultScreenshotList.clear();
			try{
				for (int i=0; i < file.length; i++)
				{
					Log.d("Files", "FileName:" + file[i].getName());
					if(!Utils.mShowHidden && file[i].getName().startsWith("."))
						continue;
					Uri fileUri = Uri.fromFile(file[i]);
					RowItem r= new RowItem(file[i].getName(), fileUri);
					if(r == null){
						continue;
					}
					mSearchresultScreenshotList.add( r );
				}
				onRefreshSortBy();
				//				if(Utils.mCurrentSortBy == Utils.NAME){
				//					Collections.sort(getList(), NameComparator);
				//				}else if(Utils.mCurrentSortBy == Utils.TIME){
				//					Collections.sort(getList(), ModifiedDateComparator);
				//				}else if(Utils.mCurrentSortBy == Utils.SIZE){
				//					Collections.sort(getList(), SizeComparator);
				//				}
			}catch( Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			try{
				if (progress!=null) {
					if (progress.isShowing()) {
						progress.dismiss();     
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			super.onPostExecute(result);
			if(mSearchresultScreenshotList.isEmpty()){
				Toast.makeText(mContext, "No Result Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}/*else if(mSearchresultScreenshotList.size() == 1) {
				Toast.makeText(mContext, mSearchresultScreenshotList.size() + " Item Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(mContext, mSearchresultScreenshotList.size() + " Items Found, Click on search key to search on internet", Toast.LENGTH_SHORT).show();
			}*/

			if(mSearchresultScreenshotList.size() > 6 /*&& Utils.mCurrentSortBy == Utils.NAME*/)
				Utils.mScrollbarShow  = true;
			else
				Utils.mScrollbarShow  = false;
			//			showScrollBar();
			adapter.setSearchList(mSearchresultScreenshotList);
			adapter.notifyDataSetChanged();
			adapterDrawerList.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			if (isRefresh) {
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Refreshing ScreenShot list...");
			}else if(isSortby){
				progress = ProgressDialog.show(SearchActivity.this, null,
						"Sorting ScreenShot list...");
			}else{
				progress = ProgressDialog.show(SearchActivity.this, null,
						"First time loading ScreenShot list...");
			}

			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}


	public  void showOptionsForFiles(final String title,final Uri fileUri, final String fileType,final int position){
		AlertDialog.Builder build = new AlertDialog.Builder(SearchActivity.this);
		String playOrOpen = null;
		if(Utils.isImage || Utils.isDocument || Utils.isAll || Utils.isCamera || Utils.isScreenshot || Utils.isDownload){
			playOrOpen = getResources().getString(R.string.item_open);
		}else{
			playOrOpen = getResources().getString(R.string.action_play);
		}
		String[] itemsResource = {
				playOrOpen,
				getResources().getString(R.string.action_share),
				getResources().getString(R.string.action_rename),
				getResources().getString(R.string.action_delete),
				getResources().getString(R.string.action_file_location)
		};

		build.setTitle(title);
		build.setItems(itemsResource,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				isDialogOpen = true;
				switch (item) {
				case 0: // Play
					Intent intent = new Intent();
					intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);

					intent.setDataAndType(adapter.getItem(position).uri, fileType);
					//					startActivity(intent);
					startActivityForResult(intent,Utils.SETTING_REQUEST_CODE);
					isDialogOpen = true;
					break;
				case 1: // share
					try {
						Intent intentshare = new Intent();
						intentshare.setAction(Intent.ACTION_SEND);
						intentshare.setType(fileType);
						intentshare.putExtra(Intent.EXTRA_STREAM,fileUri);
						if (null != intentshare) {
							startActivity(intentshare);
						}
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(),
								Toast.LENGTH_LONG).show();
					}
					isDialogOpen = true;
					break;					
				case 2: //rename
					final File mOldFileName = new File(fileUri.getPath());
					//					Utils.renameDialog(mFile, mContext);
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
					alertDialog.setTitle("Rename");
					final String srcFilename = mFilename;

					final EditText input = new EditText(mContext);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					input.setFocusable(true);
					input.setSelectAllOnFocus(true);

					input.setPrivateImeOptions("inputType=PredictionOff;inputType=filename;disableEmoticonInput=true" );
					input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);


					input.setText(srcFilename);
					//					input.setSelection(mOldFileName.getName().length()); // if uncomment rename will not work
					lp.setMargins(30, 20, 30, 0);
					input.setLayoutParams(lp);
					alertDialog.setView(input);					

					alertDialog.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String newFileName = input.getText().toString();
							/*							if (newFileName.equals(srcFilename) ) {
								Toast.makeText(mContext, "Already exists", Toast.LENGTH_LONG).show();
								return;
							}*/

							System.out.println("Old file: "+ mOldFileName);

							File newFile = new File(mOldFileName.getParent(), newFileName + "." + Utils.getFileExtension(mOldFileName.getPath()));

							if (newFile.isDirectory()) {
								Toast.makeText(mContext, "Rename failed", Toast.LENGTH_LONG).show();
								return;
							}

							if(newFile.exists()){
								Toast.makeText(mContext, "Already exists at same path, Try with other name.", Toast.LENGTH_LONG).show();
								return;
							}
							System.out.println("new file: "+newFile.getAbsolutePath());

							if(mOldFileName.renameTo(newFile)){
								Utils.removeMediaThumbnail(mContext,mOldFileName);
								adapter.getItem(position).name = newFileName;
								adapter.getItem(position).uri = Uri.fromFile(newFile);
								adapter.notifyDataSetChanged();
								Toast.makeText(mContext,"Succes! Rename to : " + newFile.getName(),Toast.LENGTH_LONG).show();
								Utils.sendScanFile(mContext, newFile.getAbsolutePath());
							}else{
								Toast.makeText(mContext,"Rename failed",Toast.LENGTH_LONG).show();
							}

						}
					});

					alertDialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					AlertDialog alert1 = alertDialog.create();
					alert1.show();



					break;
				case 3: //delete

					fdelete = new File(fileUri.getPath()+"");
					if (fdelete != null && fdelete.exists()) {

						AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
						builder1.setMessage(" Are you sure, you want to delete ?");
						builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								//									sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fdelete)));
								//								boolean test =  fdelete.delete();
								if (fdelete != null && fdelete.delete()) {
									Utils.removeMediaThumbnail(mContext,fdelete);
									getList().remove(position);
									adapter.notifyDataSetChanged();
									Toast.makeText(getApplicationContext(), "File deleted", Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(getApplicationContext(), "File not exist in Phone Memory, Unable to delete File.", Toast.LENGTH_LONG).show();
								}
							}
						});
						builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							} });

						AlertDialog alert11 = builder1.create();
						alert11.show();

					} else {
						Toast.makeText(getApplicationContext(), "File not exist, Unable to delete", Toast.LENGTH_LONG).show();
					}
					break;

				case 4: //details
					File file = new File(fileUri.getPath());
					long lastmodified = file.lastModified();
					String lastmodifiedtime = Utils.getDateFormatByFormatSetting(mContext, lastmodified * 1000);

					AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
					builder.setMessage(Html.fromHtml("<b>" + "File Name : " + "</b>" + mFilename +"<br><br>" +
							"<b>" + "Size : " + "</b>" + Utils.getFileSize(fileUri) + "<br><br>" +
							"<b>" + "Last Modified Time : " + "</b>" + lastmodifiedtime  +"<br><br>" +
							"<b>" + "File Location : " + "</b>" + fileUri.getPath() + "."))
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									//do things
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
					break;
				}

				dialog.dismiss();
			}
		});

		mExDialog = build.create();
		mExDialog.setCanceledOnTouchOutside(true);
		mExDialog.show();
	}


	public void getScreenShot(){
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/ScreenShots";
		Log.d("Files", "Path: " + path);
		File f = new File(path);        
		File file[] = f.listFiles();
		Log.d("Files", "Size: "+ file.length);
		for (int i=0; i < file.length; i++)
		{
			Log.d("Files", "FileName:" + file[i].getName());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//		Toast.makeText(SearchActivity.this, adapter.getItem(position).uri +" ", Toast.LENGTH_SHORT).show();		
	}


	protected boolean deleteInFileSystem(final File file, int position) throws Exception {

		if (file == null || !file.exists()) {

			return false;
		}

		try {

			if (!file.delete()) {
				File[] subFiles = file.listFiles();

				if (subFiles == null || subFiles.length == 0) {
					return false;
				} else {
					for (File sf : subFiles) {
						deleteInFileSystem(sf,position);
					}
				}
				file.delete(); // file is empty directory
				Utils.removeMediaThumbnail(mContext,file);
				getList().remove(position);
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "File deleted", Toast.LENGTH_LONG).show();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}


	// Adapter work

	public class RowItem{

		String name;
		Uri uri;
		Uri contactId;
		String photoUri;
		Bitmap thumbnail;
		List<ApplicationInfo> appsList;
		int filetype;

		public RowItem(String filename , Uri uri) {
			this.name = filename;
			this.uri = uri;
		}
		public RowItem(String filename , Uri uri,int filetype) {
			this.name = filename;
			this.uri = uri;
			this.filetype = filetype;
		}
		public RowItem(List<ApplicationInfo> appsList) {
			this.appsList = appsList;
		}
		public RowItem(String filename , Uri uri,Bitmap albumArtImage) {
			this.name = filename;
			this.uri = uri;

			if( albumArtImage != null ){

				this.thumbnail = albumArtImage;

			}else{
				try{
					int tempDrawable;
					if(Utils.isImage){
						tempDrawable = R.drawable.images;
					}else if(Utils.isAudio){
						tempDrawable = R.drawable.audio;
					}else if (Utils.isVideo) {
						tempDrawable = R.drawable.video;
					}else if (Utils.isApplication) {
						tempDrawable = R.drawable.application;
					}else{
						tempDrawable = R.drawable.phoneexplorer;
					}
					this.thumbnail = BitmapFactory.decodeResource(getResources(), tempDrawable);
					//															this.thumbnail = scaledBitmapForSmallImage(BitmapFactory.decodeResource(getResources(), tempDrawable));

				}catch(OutOfMemoryError e){

					System.gc();
					e.printStackTrace();

				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}

		}
		//contact
		public RowItem(String filename , Uri uri, String photoUri, Uri contactIdUri) {
			this.name = filename;
			this.uri = uri;
			this.photoUri = photoUri;
			this.contactId = contactIdUri;
		}
		public char charAt(int i) {
			// TODO Auto-generated method stub

			if(Utils.isApplication){
				ApplicationInfo data = appsList.get(i);
				return data.loadLabel(packageManager).charAt(0);
			}else{
				return this.name.charAt(0);	
			}
		}
	}

	public static void setListView( ListView mlistView ) {

		listView = mlistView;
	}


	public ListView getListView() {

		return listView;
	}


	class SearchListAdapter extends BaseAdapter implements SectionIndexer {

		private String mSections1 = "#aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ";
		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		@Override

		public int getPositionForSection(int section) {

			// If there is no item for current section, previous section will be

			// selected

			for (int i = section; i >= 0; i--) {

				for (int j = 0; j < getCount(); j++) {

					if (i == 0) {

						// For numeric section

						for (int k = 0; k <= 9; k++) {

							if (StringMatcher.match(String.valueOf(getItem(j).charAt(0)), String.valueOf(k)))

								return j;

						}

					} else {

						if (StringMatcher.match(String.valueOf(getItem(j).charAt(0)), String.valueOf(mSections.charAt(i))))

							return j;

					}

				}

			}

			return 0;

		}



		@Override

		public int getSectionForPosition(int position) {

			return 0;

		}



		@Override

		public Object[] getSections() {

			String[] sections = new String[mSections.length()];

			for (int i = 0; i < mSections.length(); i++)

				sections[i] = String.valueOf(mSections.charAt(i));

			return sections;

		}

		LayoutInflater inflater;

		public SearchListAdapter() {
			mSelectedItemsIds = new SparseBooleanArray();
			//			mSelectedRow = new ArrayList<SearchActivity.RowItem>();
			selectedFiles = new HashMap<Integer,RowItem>();
			inflater = LayoutInflater.from(SearchActivity.this);
		}


		public void toggleSelection(int position) {
			selectView(position, !mSelectedItemsIds.get(position));
		}

		public void removeSelection() {
			isSelectMode = false;
			mSelectedItemsIds = new SparseBooleanArray();
			//			selectedFiles.clear();
			notifyDataSetChanged();
		}

		public void selectView(int position, boolean value) {
			mSelectedPosition = position;
			if (value){
				mSelectedItemsIds.put(position, value);
				//				mSelectedRow.add(getItem(position));
				selectedFiles.put(position,getItem(position));
			}
			else{
				//				mSelectedRow.remove(position);
				selectedFiles.remove(position);
				mSelectedItemsIds.delete(position);
			}
			adapter.notifyDataSetChanged();
		}

		public int getSelectedCount() {
			return mSelectedItemsIds.size();
		}

		public SparseBooleanArray getSelectedIds() {
			return mSelectedItemsIds;
		}


		private ArrayList<RowItem> mList = new ArrayList<SearchActivity.RowItem>();


		public void setSearchList(ArrayList<RowItem> mList) {
			this.mList = mList;
		}

		@Override
		public int getCount() {
			if(mList == null)
				return 0;
			return mList.size();
		}

		@Override
		public RowItem getItem(int position) {
			if(mList != null){
				if(position > getCount()){
					return mList.get(0);
				}else{
					return mList.get(position);

				}
			}else{
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				if(holder == null){
					holder = new ViewHolder();
					convertView = inflater.inflate(R.layout.app_list_row, null);
					holder.qcb = (QuickContactBadge) convertView.findViewById (R.id.quickContactBadge);
					holder.imageView = (ImageView) convertView.findViewById(R.id.img);
					holder.textViewTitle = (TextView) convertView.findViewById(R.id.title);
					holder.textViewFilePath = (TextView) convertView.findViewById(R.id.filepath);
					holder.textViewFileSize = (TextView) convertView.findViewById(R.id.filesize);
					holder.txtFileThumbnail = (TextView) convertView.findViewById(R.id.file_thumbnail);
					convertView.setTag(holder);
				}
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			if(Utils.isContact){
				holder.qcb.setVisibility(QuickContactBadge.VISIBLE);
				holder.textViewFilePath.setVisibility(TextView.GONE);
				holder.textViewFileSize.setVisibility(TextView.GONE);				
				holder.imageView.setVisibility(ImageView.GONE);
				holder.txtFileThumbnail.setVisibility(ImageView.GONE);
				holder.textViewTitle.setVisibility(TextView.VISIBLE);

				holder.qcb.setMode(ContactsContract.QuickContact.MODE_LARGE);
				if(getItem(position) != null && getItem(position).uri != null && getItem(position).name != null){
					holder.qcb.assignContactUri(getItem(position).uri);

					//					mImageLoader.loadImage(getItem(position).uri, holder.qcb);

					/*					  InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(
					            mContext.getContentResolver(), getItem(position).uri);*/
					//					  if(stream == null){
					mImageLoader.loadImage(getItem(position).uri, holder.qcb);
					//					  }else{
					//						  holder.qcb.setImageBitmap(BitmapFactory.decodeStream(stream));
					//					  }

					holder.textViewTitle.setText(getItem(position).name);
				}
				holder.textViewTitle.setSingleLine(true);
				holder.textViewTitle.setEllipsize(TruncateAt.MIDDLE);

			}else if(Utils.isAudio){
				holder.qcb.setVisibility(QuickContactBadge.GONE);
				holder.textViewFilePath.setVisibility(TextView.VISIBLE);
				holder.textViewFileSize.setVisibility(TextView.VISIBLE);				
				holder.imageView.setVisibility(ImageView.VISIBLE);
				holder.txtFileThumbnail.setVisibility(ImageView.GONE);
				holder.textViewTitle.setVisibility(TextView.VISIBLE);
				if(getItem(position) != null && getItem(position).uri != null && getItem(position).name != null){
					loadThumbnail( holder, getItem(position).uri );

					holder.textViewTitle.setText(getItem(position).name);


					holder.textViewTitle.setSingleLine(true);
					holder.textViewTitle.setEllipsize(TruncateAt.MIDDLE);

					holder.textViewFilePath.setText((getItem(position).uri.getPath()));
					holder.textViewFilePath.setText(getItem(position).uri.getPath());
					holder.textViewFilePath.setSingleLine(false);

					holder.textViewFileSize.setText(Utils.getFileSize(getItem(position).uri));
				}
			}else if(Utils.isVideo){
				holder.qcb.setVisibility(QuickContactBadge.GONE);
				holder.textViewFilePath.setVisibility(TextView.VISIBLE);
				holder.textViewFileSize.setVisibility(TextView.VISIBLE);				
				holder.imageView.setVisibility(ImageView.VISIBLE);
				holder.txtFileThumbnail.setVisibility(ImageView.GONE);
				holder.textViewTitle.setVisibility(TextView.VISIBLE);

				if(getItem(position) != null && getItem(position).uri != null && getItem(position).name != null){
					loadThumbnail( holder, getItem(position).uri );

					holder.textViewTitle.setText(getItem(position).name);
					holder.textViewTitle.setSingleLine(true);
					holder.textViewTitle.setEllipsize(TruncateAt.MIDDLE);

					holder.textViewFilePath.setText((getItem(position).uri.getPath()));
					holder.textViewFilePath.setText(getItem(position).uri.getPath());
					holder.textViewFilePath.setSingleLine(false);

					holder.textViewFileSize.setText(Utils.getFileSize(getItem(position).uri));
				}

			}else if(Utils.isImage || Utils.isCamera || Utils.isScreenshot){
				holder.qcb.setVisibility(QuickContactBadge.GONE);
				holder.textViewFilePath.setVisibility(TextView.VISIBLE);
				holder.textViewFileSize.setVisibility(TextView.VISIBLE);				
				holder.imageView.setVisibility(ImageView.VISIBLE);
				holder.txtFileThumbnail.setVisibility(ImageView.GONE);
				holder.textViewTitle.setVisibility(TextView.VISIBLE);

				if(getItem(position) != null && getItem(position).uri != null && getItem(position).name != null){
					loadThumbnail( holder, getItem(position).uri );

					//				holder.imageView.setImageBitmap(getItem(position).thumbnail);

					holder.textViewTitle.setText(getItem(position).name);
					holder.textViewTitle.setSingleLine(true);
					holder.textViewTitle.setEllipsize(TruncateAt.MIDDLE);

					holder.textViewFilePath.setText((getItem(position).uri.getPath()));
					holder.textViewFilePath.setText(getItem(position).uri.getPath());
					holder.textViewFilePath.setSingleLine(false);

					holder.textViewFileSize.setText(Utils.getFileSize(getItem(position).uri));
				}

			}else if(Utils.isApplication){
				holder.qcb.setVisibility(QuickContactBadge.GONE);
				holder.textViewFilePath.setVisibility(TextView.GONE);
				holder.textViewFileSize.setVisibility(TextView.VISIBLE);				
				holder.imageView.setVisibility(ImageView.VISIBLE);
				holder.txtFileThumbnail.setVisibility(ImageView.GONE);
				holder.textViewTitle.setVisibility(TextView.VISIBLE);
				try{
					if(getItem(position) != null && applist !=null && getItem(position).appsList != null){
						ApplicationInfo data = getItem(position).appsList.get(position);
						if(data != null){
							holder.textViewTitle.setText(data.loadLabel(packageManager));
							holder.imageView.setImageDrawable(data.loadIcon(packageManager));
							holder.textViewFileSize.setText(Utils.getFileSize(Uri.fromFile(new File(data.sourceDir))));
						}
						holder.textViewTitle.setSingleLine(true);
						holder.textViewTitle.setEllipsize(TruncateAt.MIDDLE);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if(Utils.isWebsearch){
				holder.qcb.setVisibility(QuickContactBadge.GONE);
				holder.textViewFilePath.setVisibility(TextView.GONE);
				holder.textViewFileSize.setVisibility(TextView.GONE);				
				holder.imageView.setVisibility(ImageView.GONE);
				holder.txtFileThumbnail.setVisibility(ImageView.GONE);
				holder.textViewTitle.setVisibility(TextView.GONE);

			}else if(Utils.isDocument || Utils.isAll || Utils.isDownload){

				holder.qcb.setVisibility(QuickContactBadge.GONE);
				holder.textViewFilePath.setVisibility(TextView.VISIBLE);
				holder.textViewFileSize.setVisibility(TextView.VISIBLE);				
				holder.imageView.setVisibility(ImageView.GONE);
				holder.txtFileThumbnail.setVisibility(ImageView.VISIBLE);
				holder.textViewTitle.setVisibility(TextView.VISIBLE);
				String mimetype,colorCode;
				if(getItem(position) != null && getItem(position).uri != null && getItem(position).name != null){
					try{
						mimetype = Utils.getMimeType(getItem(position).uri.getPath());
						colorCode = MediaFile.getColorCodeFormMimeType(mimetype);

						Log.d("Phone_Explorer", "Color code = " + colorCode + "  Color.parseColor() = "  + Color.parseColor("#"+colorCode));
						holder.txtFileThumbnail.setBackgroundColor(Color.parseColor("#"+colorCode));
						//					holder.txtFileThumbnail.setBackgroundColor(Color.parseColor("#FF0000"));
					}catch(Exception e){
						e.printStackTrace();
					}

					try{
						String txt = Utils.getFileExtension(getItem(position).uri.getPath().toUpperCase());
						if(txt == null){
							holder.txtFileThumbnail.setText(getItem(position).name.substring(0, 1).toUpperCase());
						}else{
							holder.txtFileThumbnail.setText(txt);
						}
						//					holder.txtFileThumbnail.setText(getItem(position).name.substring(0, 1).toUpperCase());
					}catch(Exception ex){
						ex.printStackTrace();
					}


					holder.textViewTitle.setText(getItem(position).name);
					holder.textViewTitle.setSingleLine(true);
					holder.textViewTitle.setEllipsize(TruncateAt.MIDDLE);

					holder.textViewFilePath.setText((getItem(position).uri.getPath()));
					holder.textViewFilePath.setText(getItem(position).uri.getPath());
					holder.textViewFilePath.setSingleLine(false);

					holder.textViewFileSize.setText(Utils.getFileSize(getItem(position).uri));
				}
			}

			//			if(!Utils.isContact){
			if (mSelectedItemsIds.get(position) && isSelectMode) {
				convertView.setBackgroundColor(Color.parseColor("#"+"2967A0"));
				//				convertView.setBackground(mContext.getResources().getDrawable(android.R.drawable.list_selector_background));
			}else{
				convertView.setBackgroundColor(Color.parseColor("#"+"ffffff"));
			}
			//				}
			return convertView;
		}



		private void loadThumbnail(ViewHolder holder, Uri uri) {
			// TODO Auto-generated method stub
			loadThumbnail(holder, uri.getPath(), -1);
		}



		private void loadThumbnail(ViewHolder vh, String path, int i) {

			Drawable thumbnail = com.ds.phonoexplorer.utils.MediaLoader.getThumbnailDrawableWithoutMakeCache( path );

			int fileType = MediaFile.getFileTypeInt( path, mContext );

			if ( thumbnail == null ) {

				int defaultIconResId;

				if(Utils.isImage || Utils.isCamera || Utils.isScreenshot){
					defaultIconResId = R.drawable.images;
				}else if(Utils.isAudio){
					defaultIconResId = R.drawable.audio;
				}else if (Utils.isVideo) {
					defaultIconResId = R.drawable.video;
				}else if (Utils.isApplication) {
					defaultIconResId = R.drawable.application;
				}else{
					defaultIconResId = R.drawable.phoneexplorer;
				}


				if ( vh.imageView != null ) {

					vh.imageView.setImageResource( defaultIconResId );
				}

				Handler h = com.ds.phonoexplorer.utils.ThumbnailLoader.sThumbnailLoader.mBackThreadHandler;

				com.ds.phonoexplorer.utils.ThumbnailLoader.ThumbnailInfo info = new com.ds.phonoexplorer.utils.ThumbnailLoader.ThumbnailInfo();


				info.mThumbnailInfoContext = mContext;

				if (vh.imageView != null) {

					info.mIconImageView = vh.imageView;

					info.mIconImageView.setTag( path );
				}

				info.mFileType = fileType;

				info.mPath = path;

				if(h != null) {
					h.sendMessageAtFrontOfQueue( h.obtainMessage( 0, info ) );
				}

			} else {

				if ( vh.imageView != null ) {

					vh.imageView.setTag( path );
				}

				if ( vh.imageView != null ) {

					vh.imageView.setImageDrawable( thumbnail );
				}
			}
		}
	}
	static class ViewHolder {
		private TextView textViewTitle;
		private TextView textViewFilePath;
		private TextView textViewFileSize;
		private ImageView imageView;
		private QuickContactBadge qcb;
		private TextView txtFileThumbnail;
	}



	private int getListPreferredItemHeight() {
		final TypedValue typedValue = new TypedValue();

		SearchActivity.this.getTheme().resolveAttribute(
				android.R.attr.listPreferredItemHeight, typedValue, true);
		final DisplayMetrics metrics = new android.util.DisplayMetrics();
		SearchActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return (int) typedValue.getDimension(metrics);
	}

	private Bitmap scaledBitmapForSmallImage(Bitmap bitmap) {

		if (bitmap != null) {

			Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, 400, 
					400, true);
			return resizeBitmap;
		}

		return null;
	}

	public static Bitmap getAlbumartBitmap( Context context, int albumID ) {

		Bitmap bitmap = null;

		final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

		sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		sBitmapOptions.inDither = false;

		final Uri artworkUri = Uri.parse( "content://media/external/audio/albumart" );

		int width = 128;

		int height = 128;

		ParcelFileDescriptor fileDescriptor = null;

		try {

			Uri uri = ContentUris.withAppendedId( artworkUri, albumID );

			fileDescriptor = context.getContentResolver().openFileDescriptor( uri, "r" );

			int sampleSize = 1;

			sBitmapOptions.inJustDecodeBounds = true;

			BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, sBitmapOptions);

			if (sBitmapOptions.outHeight > height || sBitmapOptions.outWidth > width) {

				if (sBitmapOptions.outWidth > sBitmapOptions.outHeight) {

					sampleSize = Math.round(sBitmapOptions.outHeight / (float) height);

				} else {

					sampleSize = Math.round(sBitmapOptions.outWidth / (float) width);
				}
			}

			sBitmapOptions.inSampleSize = sampleSize;

			sBitmapOptions.inJustDecodeBounds = false;

			Bitmap bm = BitmapFactory.decodeFileDescriptor(
					fileDescriptor.getFileDescriptor(), null, sBitmapOptions );

			if (bm != null) {

				if ( sBitmapOptions.outWidth != width || sBitmapOptions.outHeight != height ) {

					bitmap = Bitmap.createScaledBitmap(bm, width, height, true);

					bm.recycle();

				} else {

					bitmap = bm;

				}

			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			throw e;

		} finally {

			if (fileDescriptor != null) {


				try {

					fileDescriptor.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

				if (null == bitmap) {

					//bitmap = getDefaultArtwork(album_id);
				}
			}
		}

		return bitmap;
	}

	public static Bitmap getVideoBitmap( Context context, String path ) {

		Bitmap b = null;

		BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

		int sampleSize = 8;

		sBitmapOptions.inSampleSize = sampleSize;

		sBitmapOptions.inDither = false;

		ContentResolver contentResolver = context.getContentResolver();

		if ( contentResolver != null ) {

			Cursor c = null;

			try {

				c = contentResolver.query( MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
						MediaStore.Video.Media.DATA + "=" + "\"" + path + "\"", null, null );

				if ( c != null ) {

					if ( c.moveToFirst() ) {

						int index = c.getColumnIndex( MediaStore.Video.Media._ID );

						long id = c.getLong( index );

						b = MediaStore.Video.Thumbnails.getThumbnail( contentResolver, id,
								MediaStore.Video.Thumbnails.MICRO_KIND, sBitmapOptions );
					}

				}

			} catch ( Exception e ) {

				e.printStackTrace();
			} finally {
				if(c != null) {
					c.close();
				}
			}
		}

		if ( b == null ) {

			File file = new File( path );

			b = getVideoThumbBitmap( file );
		}

		return b;
	}


	private static Bitmap getVideoThumbBitmap( File file ) {

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();

		Bitmap bitmap = null;

		try {


			retriever.setDataSource( file.getPath() );

			//            retriever.setVideoSize( 190, 140, false, true );

			bitmap = retriever.getFrameAtTime( 15000000 );

			if ( bitmap == null ) {

				retriever.release();

				return null;
			}

			float scale = 0.0f;

			if ( bitmap.getWidth() < bitmap.getHeight() ) {

				scale = 190 / (float) bitmap.getWidth();

			} else {

				scale = 140 / (float) bitmap.getHeight();
			}

			Matrix matrix = new Matrix();

			matrix.setScale(scale, scale);

			retriever.release();

			return transform( matrix, bitmap, 190, 140, false );

		} catch ( RuntimeException e ) {
			e.printStackTrace();
		}

		retriever.release();

		return bitmap;
	}


	public static Bitmap transform( Matrix scaler, Bitmap source, int targetWidth, int targetHeight, boolean scaleUp ) {

		if ( source == null ) {

			return null;
		}

		float scale;

		if ( source.getWidth() < source.getHeight() ) {

			scale = targetWidth / (float) source.getWidth();

		} else {

			scale = targetHeight / (float) source.getHeight();
		}

		Bitmap tmpBmp;

		if ( scaler != null ) {

			scaler.setScale( scale, scale );

			tmpBmp = Bitmap.createBitmap( source, 0, 0, source.getWidth(), source.getHeight(), scaler, true );

		} else {

			tmpBmp = source;
		}

		int dx1 = Math.max( 0, tmpBmp.getWidth() - targetWidth );

		int dy1 = Math.max( 0, tmpBmp.getHeight() - targetHeight );

		Bitmap retBmp = Bitmap.createBitmap( tmpBmp, dx1 / 2, dy1 / 2, targetWidth, targetHeight );

		if ( tmpBmp.equals( source ) ) {

			tmpBmp.recycle();
		}

		return retBmp;
	}





	public void launchApplication(String packagename){
		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packagename);
		startActivity( LaunchIntent );
	}

	public void extractApk(){
		new Handler().postDelayed(new Runnable() {
			public void run() {
				try {

					Uri newUri = Uri
							.fromFile(new File("application path"));
					boolean alreadyArchived = false;

					String sourcePath = Environment.getExternalStorageDirectory() + ""
							/* + app.sourceDir*/;
					File source = new File(sourcePath);
					String destinationPath = Environment.getExternalStorageDirectory() + "/AppsBackUp";
					int index = sourcePath.lastIndexOf("/");
					String fileName = sourcePath.substring(index + 1);
					alreadyArchived = checkForBackup(fileName);

					if (alreadyArchived) {
						Toast.makeText(
								SearchActivity.this,
								"Apk already saved at:"
										+ destinationPath
										+ "/"
										+ fileName
										.toString(),
										Toast.LENGTH_SHORT)
										.show();
						return;
					}
					File dest = new File(destinationPath + "/"+ fileName);
					if (!dest.exists()) {
						dest.createNewFile();
					}
					saveFile(newUri, dest);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 50);
	}

	public boolean checkForBackup(String newFile) {
		File[] files;
		ArrayList<String> fileNames;
		files = GetFiles(Environment.getExternalStorageDirectory()
				+ "/ApkBackup");
		fileNames = getFileNames(files);
		ArrayList<String> backFileNames = new ArrayList<String>();
		String[] splitName;
		if (fileNames == null)
			return false;
		for (String name : fileNames) {
			splitName = name.split("-");
			backFileNames.add(splitName[0]);

		}
		splitName = newFile.split("-");
		return backFileNames.contains(splitName[0]);

	}
	public File[] GetFiles(String DirectoryPath) {
		File f = new File(DirectoryPath);
		f.mkdirs();
		File[] file = f.listFiles();
		return file;
	}

	private boolean saveFile(Uri sourceUri, File destination) {
		File source;
		FileChannel src;
		FileChannel dst;
		try {
			source = new File(sourceUri.getPath());
			src = new FileInputStream(source).getChannel();
			dst = new FileOutputStream(destination).getChannel();
			long transfer = dst.transferFrom(src, 0, src.size());

			src.close();
			dst.close();
			if (transfer > 0)
				return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public ArrayList<String> getFileNames(File[] file) {
		ArrayList<String> arrayFiles = new ArrayList<String>();
		if (file == null || file.length == 0)
			return null;
		else {
			for (int i = 0; i < file.length; i++)
				arrayFiles.add(file[i].getName());
		}
		return arrayFiles;
	}

	public boolean appInstalledOrNot(String app){
		PackageManager pm = getPackageManager();
		boolean app_installed = false;
		try
		{
			pm.getPackageInfo(app, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			app_installed = false;
		}
		return app_installed ;
	}
	void dataUsageSetting(){

	}

	void notificationSetting(){

	}

	void stopApplication(){

	}

	public void performWebSearch(String searchText)
	{
		try {
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, searchText);
			startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				final FilterResults oReturn = new FilterResults();
				mSearchInstalledAppsList = new ArrayList<SearchActivity.RowItem>();
				try{
					if(Utils.isApplication){
						final ArrayList<ApplicationInfo> results = new ArrayList<ApplicationInfo>();
						origAppList = applist;
						if (constraint != null) {
							if (origAppList != null && origAppList.size() > 0) {
								for (final ApplicationInfo g : origAppList) {
									if (((String) g.loadLabel(packageManager)).toLowerCase().contains(constraint.toString().toLowerCase())){
										results.add(g);
										RowItem r= new RowItem(results); 
										mSearchInstalledAppsList.add( r );
									}
								}
							}
							oReturn.values = results;
							oReturn.count = results.size();
						}
					}else{
						final ArrayList<RowItem> results = new ArrayList<RowItem>();
						origList = getList();
						if (constraint != null) {
							if (origList != null && origList.size() > 0) {
								for (RowItem g : origList) {
									if(g.name != null)
										if (g.name.toLowerCase().contains(constraint.toString().toLowerCase()))
											results.add(g);
								}
							}
							oReturn.values = results;
							oReturn.count = results.size();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				return oReturn;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				try{
					ArrayList<RowItem> mSearchresultList = null;
					if(Utils.isApplication)
						mSearchresultList = mSearchInstalledAppsList;
					else
						mSearchresultList = (ArrayList<RowItem>) results.values;
					/*					if(mSearchresultList.size() != 0)
							Toast.makeText(mContext, mSearchresultList.size() + " Items Found", Toast.LENGTH_SHORT).show();*/
					adapter.setSearchList(mSearchresultList);
					adapter.notifyDataSetChanged();
				}catch(Exception e){
					e.printStackTrace();
				}

			}
		};
	}

	public ArrayList<RowItem> getList(){

		if(Utils.isAll){

			return mSearchresultAllList;

		}else if(Utils.isApplication){

			return mSearchresultAppList;

		}else if(Utils.isContact){

			return mSearchresultContactList;

		}else if(Utils.isImage){

			return mSearchresultImageList;

		}else if(Utils.isAudio){

			return mSearchresultMusicList;

		}else if(Utils.isVideo){

			return mSearchresultVideoList;

		}else if(Utils.isDocument){

			return mSearchresultDocumentList;

		}else if(Utils.isCamera){

			return mSearchresultCameraList;

		}else if(Utils.isDownload){

			return mSearchresultDownloadList;

		}else if(Utils.isScreenshot){

			return mSearchresultScreenshotList;

		}else{

			return null;
		}
	}
	public void initAd(Context mContext){//uncomment it

		//		mInterstitialAd = new InterstitialAd(mContext);
		//		mInterstitialAd.setAdUnitId(mContext.getString(R.string.Interstitial_ad_ad_unit_id));
		//
		//		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		//		mInterstitialAd.loadAd(adRequestBuilder.build());

		//For testing 
		mInterstitialAd = new InterstitialAd(mContext);
		mInterstitialAd.setAdUnitId("ca-app-pub-123456789/123456789");

		// Request for Ads

		AdRequest adRequest = new AdRequest.Builder()

		// Add a test device to show Test Ads
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		.addTestDevice("6F34A77DBC1CE047184CBCBF1EF48D04")
		.build();
		mInterstitialAd.loadAd(adRequest);

	}

	/*	public static class WebSearchFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	 *//** Inflating the layout for this fragment **//*
			((SearchActivity) mContext).mWebview.setWebViewClient(new WebViewClient() {
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					Toast.makeText(mContext, description, Toast.LENGTH_SHORT).show();
				}
			});

			((SearchActivity) mContext).mWebview .loadUrl("http://www.google.com");
			return ((SearchActivity) mContext).mWebview;
		}
	}
	  */
	public static class DataFragment extends Fragment {
		/*
		public DataFragment() {
			// Empty constructor required for fragment subclasses
		}*/

		public static DataFragment newInstance(String text,Context context) {
			mContext = context;
			DataFragment f = new DataFragment();
			Bundle b = new Bundle();
			b.putString("msg", text);

			f.setArguments(b);

			return f;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.data_fragment, container, false);

			//            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
			//            getActivity().setTitle(planet);
			((SearchActivity) mContext).mSearchEditText = (EditText)rootView.findViewById(R.id.inputSearch);
			listView = (IndexableListView)rootView.findViewById(R.id.fragment_listview);
			setListView(listView);
			((SearchActivity) mContext).mSearchEditText.setPrivateImeOptions("inputType=PredictionOff;inputType=filename;disableEmoticonInput=true" );
			((SearchActivity) mContext).mSearchEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

			((SearchActivity) mContext).mSearchEditText.setOnKeyListener(new OnKeyListener() {                 
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					//You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
					if(keyCode == KeyEvent.KEYCODE_DEL){  
						//this is for backspace
						isBackSpace = true;
					}
					else{
						isBackSpace = false;
					}
					return false;       
				}
			});

			((SearchActivity) mContext).mAdView = (AdView)rootView.findViewById(R.id.adView); //uncomment it

			// Request for Ads
			//			AdRequest adRequest = new AdRequest.Builder().build();

			// Add a test device to show Test Ads
			AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.addTestDevice("6F34A77DBC1CE047184CBCBF1EF48D04")
			.build();


			//			    	mInterstitialAd.loadAd(adRequest);
			((SearchActivity) mContext).mAdView.loadAd(adRequest);  //uncomment it

			listView.setFastScrollEnabled(true);
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					return true;
				}
			});
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode arg0) {
					// TODO Auto-generated method stub
					adapter.removeSelection();
					isSelectMode =false;
				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					// TODO Auto-generated method stub
					if(Utils.isApplication || Utils.isContact){
						return false;
					}
					mode.getMenuInflater().inflate(R.menu.action_mode, menu);
					mActionMode = mode;
					isSelectMode = true;
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					// TODO Auto-generated method stub
					switch (item.getItemId()) {
					case R.id.action_delete:

						((SearchActivity) mContext).DeleteMultipleFiles();
						// Calls getSelectedIds method from ListViewAdapter Class
						/*						SparseBooleanArray selected = listviewadapter
								.getSelectedIds();
						// Captures all selected ids with a loop
						for (int i = (selected.size() - 1); i >= 0; i--) {
							if (selected.valueAt(i)) {
								WorldPopulation selecteditem = listviewadapter
										.getItem(selected.keyAt(i));
								// Remove selected items following the ids
								listviewadapter.remove(selecteditem);
							}
						}*/
						// Close CAB
						//						mode.finish();
						return true;
					case R.id.action_share:
						((SearchActivity) mContext).ShareMultipleFiles();

						mode.finish();

						return true;
					default:
						return false;
					}
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position,
						long id, boolean checked) {
					// TODO Auto-generated method stub
					// Capture total checked items
					adapter.toggleSelection(position);
					final int checkedCount = adapter.getSelectedCount();
					// Set the CAB title according to total checked items
					mode.setTitle(checkedCount + " Selected");
					// Calls toggleSelection method from ListViewAdapter Class

				}
			});

			if(mCurrentViewPagerPosition == 0 ){
				Utils.isContact = true;
				Utils.isImage = false;
				Utils.isAudio = false;
				Utils.isVideo = false;
				Utils.isDocument = false;
				Utils.isApplication = false;
				Utils.isDownload = false;
				Utils.isScreenshot = false;
				Utils.isCamera = false;
				Utils.isAll = false;
				((SearchActivity) mContext).initSearchViewForContact("");
			}

			listView.setAdapter(adapter);
			listView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
					//					listView.setFastScrollEnabled(false);
				}
			});
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long arg3) {
					// TODO Auto-generated method stub

					((SearchActivity) mContext).mUri = adapter.getItem(position).uri;
					((SearchActivity) mContext).mFilename = adapter.getItem(position).name;
					String filetype = null;
					Intent i2;

					switch (mCurrentViewPagerPosition) {



					case 0://contacts
						QuickContactBadge qcb = (QuickContactBadge) ((SearchActivity) mContext).findViewById(R.id.quickContactBadge);
						qcb.setMode(ContactsContract.QuickContact.MODE_LARGE); 
						qcb.assignContactUri(adapter.getItem(position).uri);
						//						((SearchActivity) mContext).mImageLoader.loadImage(adapter.getItem(position).photoUri,qcb);
						qcb.performClick();
						isDialogOpen = true;
						break;
					case 1://Image
						//					filetype = "image/*";
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "image/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						break;

					case 2://Music

						//					filetype = "audio/*";
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "audio/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						break;

					case 3://Video
						//					filetype = "video/*";
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "video/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						break;

					case 4://Documents
						//					filetype = "text/*";
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "text/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						//					Toast.makeText(getApplicationContext(), "spinner1.getSelectedItem() = " + spinner1.getSelectedItem(), Toast.LENGTH_LONG).show();
						break;
					case 5://application

						//					Toast.makeText(getApplicationContext(), "spinner1.getSelectedItem() = " + spinner1.getSelectedItem(), Toast.LENGTH_LONG).show();
						((SearchActivity) mContext).showOptionsForApplication(position);
						break;
					case 6://Download
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "*/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						break;


					case 7://screenshot
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "*/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						break;
					case 8://Camera
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "*/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						break;
					case 9://All
						filetype = MimeUtils.guessMimeTypeFromExtension(Utils.getFileExtension(mUri.getPath()));
						if(filetype == null)
							filetype = "*/*";
						((SearchActivity) mContext).showOptionsForFiles(mFilename,mUri,filetype,position);
						break;
					default:
						break;

					}
				}
			});

			((SearchActivity) mContext).mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						isDialogOpen = true;
						((SearchActivity) mContext).performWebSearch(((SearchActivity) mContext).mSearchEditText.getText().toString());
						return true;
					}
					return false;
				}
			});

			((SearchActivity) mContext).mSearchEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					if(s.length() == 0){
						//						((SearchActivity) mContext).mSearchEditText.setText("");
						//												adapter.notifyDataSetChanged();
						return;
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					switch (mCurrentViewPagerPosition) {
					case 0://contact
						((SearchActivity) mContext).initSearchViewForContact(s.toString().trim());
						break;
					case 1:
						if( ((SearchActivity) mContext).mSearchresultImageList == null){
							((SearchActivity) mContext).initSearchViewForImage("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;
					case 2:
						if(((SearchActivity) mContext).mSearchresultMusicList == null){
							((SearchActivity) mContext).initSearchViewForMusic("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;
					case 3:
						if(((SearchActivity) mContext).mSearchresultVideoList == null){
							((SearchActivity) mContext).initSearchViewForVideo("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;

					case 4:
						if(((SearchActivity) mContext).mSearchresultDocumentList == null){
							((SearchActivity) mContext).initSearchViewForDocument("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;
					case 5:
						if(((SearchActivity) mContext).mSearchresultAppList == null || (s.length() == 0 /*&& !isBackSpace*/)){
							((SearchActivity) mContext).setupSearchViewForApplication(s.toString().trim());
						}else {
							//							((SearchActivity) mContext).setupSearchViewForApplicationf(s.toString().trim());
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;
					case 6:
						if(((SearchActivity) mContext).mSearchresultDownloadList == null || s.length() == 0){
							((SearchActivity) mContext).initSearchViewForDownload("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;


					case 7:
						if(((SearchActivity) mContext).mSearchresultScreenshotList == null){
							((SearchActivity) mContext).initSearchViewForScreenshot("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;
					case 8:
						if(((SearchActivity) mContext).mSearchresultCameraList == null){
							((SearchActivity) mContext).initSearchViewForCamera("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;
					case 9:
						if(((SearchActivity) mContext).mSearchresultAllList == null){
							((SearchActivity) mContext).initSearchFiles("");
						}else {
							((SearchActivity) mContext).getFilter().filter(s);
						}
						break;

					default:
						break;
					}
				}
			});


			return rootView;
		}
	}


	public static class DataTypeCollectionPagerAdapter extends FragmentStatePagerAdapter {

		public DataTypeCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new DataFragment();
			Bundle args = new Bundle();
			fragment.setArguments(args);
			return fragment;
			//			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			//			return 11;
			return 10;
		}
		@Override
		public CharSequence getPageTitle(int position) {

			switch (position) {
			case 0: return "Contacts";
			case 1: return "Images";
			case 2: return "Audio";
			case 3: return "Videos";
			case 4: return "Documents";
			case 5: return "Applications";
			case 6: return "Downloads";
			case 7: return "Screenshots";
			case 8: return "Camera";
			case 9: return "All Files";
			//			case 10: return "Web Search";
			default:
				return null;
			}
		}
	}

	public String updateSize(int position){
		switch (position) {
		case 0:
			if(mSearchresultContactList != null)
				return mSearchresultContactList.size() + " Contacts";
			else
				return "calculating...";
		case 1: 
			if(mSearchresultImageList != null)
				return mSearchresultImageList.size() + " of size " + contentSize[1];
			else
				return contentSize[1];
		case 2:
			if(mSearchresultMusicList != null)
				return mSearchresultMusicList.size() + " of size " + contentSize[2];
			else
				return contentSize[2];
		case 3:
			if(mSearchresultVideoList != null)
				return mSearchresultVideoList.size() + " of size " + contentSize[3];
			else
				return contentSize[3];
		case 4:
			if(mSearchresultDocumentList != null)
				return mSearchresultDocumentList.size() + " of size " + contentSize[4];
			else
				return contentSize[4];
		case 5:
			if(installedAppsList != null)
				return installedAppsList.size() + " Apps installed";
			else
				return "calculating...";
		case 6:
			if(mSearchresultDownloadList != null)
				return mSearchresultDownloadList.size() + " of size " + contentSize[6];
			else
				return  contentSize[6];
		case 7:
			if(mSearchresultScreenshotList !=null)
				return mSearchresultScreenshotList.size() + " of size "  + contentSize[7];
			else
				return contentSize[7];
		case 8:
			if(mSearchresultCameraList != null)
				return mSearchresultCameraList.size() + " Images of " + contentSize[8];
			else
				return contentSize[8];
		case 9:
			if(mSearchresultAllList != null)
				return mSearchresultAllList.size() + " Files";
			else
				return "calculating...";
		default:
			return "calculating...";
		}
	}

	public void updateSizetemp(){
		if( mContext == null ){
			mContext = getApplicationContext();
		}
		contentSize[0] = "calculating...";
		contentSize[1] = Utils.getImageFilesSize(mContext);
		contentSize[2] = Utils.getMusicFilesSize(mContext);
		contentSize[3] = Utils.getVideoFilesSize(mContext);
		contentSize[4] = Utils.getDocumentFilesSize(mContext);
		contentSize[5] = "calculating...";

		File fileDownload = new File(Environment.getExternalStorageDirectory().toString()+"/Download");
		if( !fileDownload.exists()){
			fileDownload.mkdir();
		}
		contentSize[6] = Utils.returnSize(Utils.folderSize(fileDownload));


		File fileScreenshots = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/ScreenShots");
		if( !fileScreenshots.exists()){
			fileScreenshots = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/ScreenShots");
		}
		if(!fileScreenshots.exists()){
			fileScreenshots.mkdir();
		}
		contentSize[7] = Utils.returnSize(Utils.folderSize(fileScreenshots));

		File fileCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera");
		if( !fileCamera.exists()){
			fileCamera.mkdir();
		}
		contentSize[8] = Utils.returnSize(Utils.folderSize(fileCamera));

		contentSize[9] = "calculating...";
	}

	public void updateState(int position){
		switch (position) {
		case 0:
			Utils.isContact = true;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 1:
			Utils.isContact = false;
			Utils.isImage = true;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 2:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = true;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 3:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = true;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 4:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = true;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 5:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = true;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 6:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = true;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 7:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = true;
			Utils.isCamera = false;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 8:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = true;
			Utils.isAll = false;
			Utils.isWebsearch = false;
			break;
		case 9:
			Utils.isContact = false;
			Utils.isImage = false;
			Utils.isAudio = false;
			Utils.isVideo = false;
			Utils.isDocument = false;
			Utils.isApplication = false;
			Utils.isDownload = false;
			Utils.isScreenshot = false;
			Utils.isCamera = false;
			Utils.isAll = true;
			Utils.isWebsearch = false;
			break;
		default:
			Toast.makeText(mContext, "Enter text and click on keyboard search key.", Toast.LENGTH_LONG).show();
			break;
		}
		selectedFiles.clear();
	}

	public void sortByDialog(final Context mContext){
		AlertDialog.Builder dialog = new AlertDialog.Builder( mContext );
		dialog.setTitle("Sort By");
		isSortby = true;
		final View custom = ((Activity) mContext).getLayoutInflater().inflate( R.layout.sort_by_layout, null );

		final RadioGroup sortByRadioGroup = (RadioGroup) custom.findViewById( R.id.sort_by_menu );

		int currentSortByRadio = R.id.sort_by_name;

		switch ( Utils.mCurrentSortBy ) {

		case Utils.NAME:
			currentSortByRadio = R.id.sort_by_name;
			break;

		case Utils.TIME:
			currentSortByRadio = R.id.sort_by_time;
			break;

		case Utils.SIZE:
			currentSortByRadio = R.id.sort_by_size;
			break;
		}

		sortByRadioGroup.check( currentSortByRadio );

		sortbyRadioClick(sortByRadioGroup);

		sortByRadioGroup.setOnCheckedChangeListener( new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				int sortBy = Utils.mCurrentSortBy;

				switch ( checkedId ) {

				case R.id.sort_by_time:
					sortBy = Utils.TIME;
					break;

				case R.id.sort_by_name:
					sortBy = Utils.NAME;
					break;

				case R.id.sort_by_size:
					sortBy = Utils.SIZE;
					break;
				}

				if ( Utils.mCurrentSortBy != sortBy ) {

					Utils.mCurrentSortBy = sortBy;
				}
			}
		});

		final RadioGroup inOrderRadioGroup = (RadioGroup) custom.findViewById( R.id.in_order_menu );

		orderbyRadioClick(inOrderRadioGroup);

		int currentinOrderRadio = R.id.sort_by_ascending;

		switch ( Utils.mCurrentInOrder ) {

		case Utils.ASC:
			currentinOrderRadio = R.id.sort_by_ascending;
			break;

		case Utils.DESC:
			currentinOrderRadio = R.id.sort_by_descending;
			break;
		}

		inOrderRadioGroup.check(currentinOrderRadio);

		inOrderRadioGroup.setOnCheckedChangeListener( new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				int inOrder = Utils.mCurrentInOrder;

				switch ( checkedId ) {

				case R.id.sort_by_ascending:
					inOrder = Utils.ASC;
					break;

				case R.id.sort_by_descending:
					inOrder = Utils.DESC;
					break;
				}

				if ( Utils.mCurrentInOrder != inOrder ) {

					Utils.mCurrentInOrder = inOrder;
				}
			}
		});
		//
		toCheckSortBy = (RadioButton) custom.findViewById(currentSortByRadio);

		toCheckInOrder = (RadioButton) custom.findViewById(currentinOrderRadio);

		dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedDataStore sharedDataStore = SharedDataStore.getInstance(mContext);
				toCheckSortBy = (RadioButton) custom.findViewById(sortByRadioGroup.getCheckedRadioButtonId());

				toCheckInOrder = (RadioButton) custom.findViewById(inOrderRadioGroup.getCheckedRadioButtonId());

				sharedDataStore.setCurrentSortBy( Utils.mCurrentSortBy );

				sharedDataStore.setCurrentInOrder( Utils.mCurrentInOrder );

				//                getAdapterManager().setSortBy( mCurrentSortBy, mCurrentInOrder );

				refresh();
				isSortby = false;
				dialog.dismiss();
			}
		});

		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				try {

					dialog.dismiss();

				} catch (IllegalArgumentException e) {

					e.printStackTrace();
				}
			}
		});

		dialog.setView(custom);        
		dialog.show();  
	}

	public static void sortbyRadioClick(RadioGroup radioGroup){

		RadioButton nameRadio = (RadioButton) radioGroup.findViewById(R.id.sort_by_name);
		RadioButton timeRadio = (RadioButton) radioGroup.findViewById(R.id.sort_by_time);
		RadioButton sizeRadio = (RadioButton) radioGroup.findViewById(R.id.sort_by_size);
		nameRadio.setOnClickListener(mRadioClickListener);
		timeRadio.setOnClickListener(mRadioClickListener);
		sizeRadio.setOnClickListener(mRadioClickListener);
	}
	public static void orderbyRadioClick(RadioGroup radioGroup){

		RadioButton ascendingRadio = (RadioButton) radioGroup.findViewById(R.id.sort_by_ascending);
		RadioButton descendingRadio = (RadioButton) radioGroup.findViewById(R.id.sort_by_descending);
		ascendingRadio.setOnClickListener(mRadioClickListener);
		descendingRadio.setOnClickListener(mRadioClickListener);
	}

	public static OnClickListener mRadioClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		}
	};

	protected void onRefreshSortBy() {
		// TODO Auto-generated method stub
		if(Utils.mCurrentSortBy == Utils.NAME){
			//			scrollBarCondition(true);
			if(getList() != null)
				Collections.sort(getList(), NameComparator);
		}else if(Utils.mCurrentSortBy == Utils.TIME){
			//			scrollBarCondition(false);
			if(getList() != null)
				Collections.sort(getList(), ModifiedDateComparator);
		}else if(Utils.mCurrentSortBy == Utils.SIZE){
			//			scrollBarCondition(false);
			if(getList() != null)
				Collections.sort(getList(), SizeComparator);
		}
	}

	public static Comparator<RowItem> NameComparator = new Comparator<RowItem>() {

		@Override
		public int compare(RowItem e1, RowItem e2) {
			String s1 = e1.name.toString();
			String s2 = e2.name.toString();
			return compareName(s1,s2);
		}


	};
	public static Comparator<RowItem> ModifiedDateComparator = new Comparator<RowItem>() {

		@Override
		public int compare(RowItem e1, RowItem e2) {
			File file1 = new File(e1.uri.getPath());
			long lastmodified1 = file1.lastModified();
			File file2 = new File(e2.uri.getPath());
			long lastmodified2 = file2.lastModified();
			//			String lastmodifiedtime = Utils.getDateFormatByFormatSetting(mContext, lastmodified * 1000);

			String s1 =  Objects.toString(lastmodified1, null);
			String s2 = Objects.toString(lastmodified2, null);
			return compareName(s1,s2);
		}
	};
	public static Comparator<RowItem> SizeComparator = new Comparator<RowItem>() {

		@Override
		public int compare(RowItem e1, RowItem e2) {
			File file1 = new File(e1.uri.getPath());
			//			double size1 = Double.parsed(String.valueOf(file1.length()));
			File file2 = new File(e2.uri.getPath());
			//			double size2 = Double.parseInt(String.valueOf(file2.length()));

			//			String s1 =  Objects.toString(Utils.getTotalSize(file1), null);
			//			String s2 = Objects.toString(Utils.getTotalSize(file2), null);
			if (Utils.mCurrentInOrder == Utils.ASC){
				return Long.valueOf(file1.length()).compareTo(Long.valueOf(file2.length()));
			}else{
				return Long.valueOf(file2.length()).compareTo(Long.valueOf(file1.length()));
			}
		}
	};

	public static int compareName(String entry1, String entry2) {
		Collator collator = java.text.Collator.getInstance();
		collator.setStrength(java.text.Collator.SECONDARY);
		if (Utils.mCurrentInOrder == Utils.ASC)
			return collator.compare(entry1.toLowerCase(), entry2.toLowerCase());
		else if (Utils.mCurrentInOrder == Utils.DESC) {
			return collator.compare(entry2.toLowerCase(), entry1.toLowerCase());
		} else {
			return 0;
		}
	}
	/*	
    private Comparator<ApplicationInfo> mAppNameComparator = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {

            String entryLocaleName1 = "";
            String entryLocaleName2 = "";


                return compareName(lhs.label, rhs.label);

            //return mCurrentInOrder == Constant.ASC ? result : -result;
        }


        public int compareName(String entry1, String entry2) {
            Collator collator = java.text.Collator.getInstance();
            collator.setStrength(java.text.Collator.SECONDARY);
            if (mCurrentInOrder == Constant.ASC)
                return collator.compare(entry1.toLowerCase(), entry2.toLowerCase());
            else if (mCurrentInOrder == Constant.DESC) {
                return collator.compare(entry2.toLowerCase(), entry1.toLowerCase());
            } else {
                return 0;
            }
        }
    };


    private Comparator<ApplicationInfo> mAppSizeComparator = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(AppEntry lhs, AppEntry rhs) {

            int result = (int)( lhs.size - rhs.size );

            return mCurrentInOrder == Constant.ASC ? result : -result;
        }
    };


    private Comparator<ApplicationInfo> mAppTimeComparator = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(AppEntry lhs, AppEntry rhs) {

            int result = (int)( lhs.date - rhs.date );

            return mCurrentInOrder == Constant.ASC ? result : - result;
        }
    };*/

	public void ShareMultipleFiles(){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND_MULTIPLE);
		intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
		intent.setType("*/*"); /* This example is sharing jpeg images. */

		ArrayList<Uri> files = new ArrayList<Uri>();
		it = selectedFiles.keySet().iterator();

		while(it.hasNext()) {
			final int position = (Integer) it.next();
			files.add(selectedFiles.get(position).uri);
		}

		/*		 for(String path : fileUri  List of the files you want to send ) {
		     File file = new File(path);
		     Uri uri = Uri.fromFile(file);
		     files.add(uri);
		 }*/

		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
		isDialogOpen = true;
		mContext.startActivity(intent);
	}

	public void DeleteMultipleFiles(){
		AlertDialog.Builder builder1;

		builder1 = new AlertDialog.Builder(mContext);
		builder1.setMessage(" Are you sure, you want to delete ?");
		builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				delete();
			}
		});
		builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			} });

		AlertDialog alert11 = builder1.create();
		alert11.show();

	}

	public void delete(){
		it = selectedFiles.keySet().iterator();
		while(it.hasNext()) {
			final int position = (Integer) it.next();
			File tempFile;
			tempFile =  new File(selectedFiles.get(position).uri.getPath());
			if (tempFile != null && tempFile.exists()) {
				if (tempFile != null && tempFile.delete()) {
					Utils.removeMediaThumbnail(mContext,tempFile);
				}else{
					Toast.makeText(mContext, "Some files not exist in Phone Memory, Unable to delete that Files.", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(mContext, "File not exist, Unable to delete", Toast.LENGTH_LONG).show();
			}
		}
		/*		ArrayList<Integer> pos = new ArrayList<Integer>();
		it = selectedFiles.keySet().iterator();
		while(it.hasNext()) {
			final int position = (Integer) it.next();
			pos.add(position);
			fdelete = new File(selectedFiles.get(position).getPath()+"");
			System.out.println(position + " - " + fdelete.getAbsolutePath()) ;
			if (fdelete != null && fdelete.exists()) {
				if (fdelete != null && fdelete.delete()) {
					Utils.removeMediaThumbnail(mContext,fdelete);
//					getList().remove(position);
					Toast.makeText(mContext, "File deleted", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mContext, "File not exist in Phone Memory, Unable to delete File.", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(mContext, "File not exist, Unable to delete", Toast.LENGTH_LONG).show();
			}
		}*/
		/*		File tempFile;
		for(int i=0; i < mSelectedRow.size();i++){
			tempFile =  new File(mSelectedRow.get(i).uri.getPath());
			if (tempFile != null && tempFile.exists()) {
				if (tempFile != null && tempFile.delete()) {
					Utils.removeMediaThumbnail(mContext,tempFile);
				}else{
					Toast.makeText(mContext, "Some files not exist in Phone Memory, Unable to delete that Files.", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(mContext, "File not exist, Unable to delete", Toast.LENGTH_LONG).show();
			}
		}*/

		//		getList().removeAll(selectedFiles);
		removeItemFromList();

	}

	public void removeItemFromList(){
		it = selectedFiles.keySet().iterator();
		while(it.hasNext()) {
			final int position = (Integer) it.next();
			getList().remove(selectedFiles.get(position));

		}
		adapter.setSearchList(getList());
		adapter.notifyDataSetChanged();
		adapterDrawerList.notifyDataSetChanged();
		selectedFiles.clear();
		mActionMode.finish();
	}

	/*	public void scrollBarCondition(boolean value){
		if(getList() != null && getList().size() > 6 && value)
			Utils.mScrollbarShow  = true;
		else
			Utils.mScrollbarShow  = false;
	}
	public void showScrollBar(){
		if(getList() != null && getList().size() > 6 && Utils.mCurrentSortBy == Utils.NAME)
			Utils.mScrollbarShow  = true;
		else
			Utils.mScrollbarShow  = false;
	}*/

}
