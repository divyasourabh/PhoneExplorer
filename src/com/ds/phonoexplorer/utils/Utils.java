package com.ds.phonoexplorer.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Collections;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ds.phonoexplorer.R;
import com.ds.phonoexplorer.SearchActivity;

public class Utils {

	public static final int apiLevel = Build.VERSION.SDK_INT;
	public static final String prefName= "PhoneExplorer";

	public final static String APP_TITLE = "Phone Explorer";
	public final static String APP_PNAME = "com.ds.phonoexplorer";

	public static boolean isAll,isContact,isAudio,isVideo,isDocument,isImage,isApplication,isCamera,isScreenshot,isDownload,isWebsearch = false;

	public static boolean mSoundStatus;
	public static boolean mOptionMenuSoundStatus;
	public static boolean mFloatinIconStatus;
	public static boolean mFloatinIconRebootStatus;
	public static boolean mFirstTimeLaunch;
	public static boolean mShowHidden;

	public static boolean mScrollbarShow = false;

	public static int SETTING_REQUEST_CODE = 26;
	public static int MULTI_SHARE_REQUEST_CODE = 27;
	public static int MULTI_DELETE_REQUEST_CODE = 28;
	public static final String myAppPlayStoreLink = "https://play.google.com/store/apps/details?id=com.ds.phonoexplorer";
	public static final String myAppVideoLink = "https://www.youtube.com/watch?v=UWHfk6HOn7Q&feature=youtu.be";	
	public static final String mPlayStoreLink = "https://play.google.com/store/apps/details?id=com.ds.phonoexplorer";

	public static final String myAppPlayStoreLinks[] = {"https://play.google.com/store/apps/details?id=com.ds.phonoexplorer",
		"https://play.google.com/store/apps/details?id=com.ds.shareapps",
		"https://play.google.com/store/apps/details?id=com.ds.appmanager",
		"https://play.google.com/store/apps/details?id=com.ds.phonoexplorer",
	"https://play.google.com/store/apps/details?id=com.ds.phonoexplorer"};

	public final static String mInternalRoot = Environment.getExternalStorageDirectory().getAbsolutePath();

	public static final String mDataFolder = Environment.getDataDirectory().getAbsolutePath();

	public static String newFileName = null;
	public static String mOldFileName = null;
	public static String mFileExtension = null;

	private final static String INVALID_CHAR[] = {

		"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "\n","[","]","(",")","@","#","!","$","%","^","&","-","~"

	};

	private static long mLastClickTime = 0;
	public static final long MIN_CLICK_INTERVAL = 2000;

    /**
     *  sort by 
     */
	public static final int NAME = 0;
    public static final int TIME = 1;
    public static final int SIZE = 2;
    
    public static final int ASC = 0;
    public static final int DESC = 1;
    
    public static int mCurrentSortBy;
    public static int mCurrentInOrder;
    
    static RadioButton toCheckInOrder;
    static RadioButton toCheckSortBy; 
    
	public static void optionMenuSound(Context mContext) {
		if(mSoundStatus || mOptionMenuSoundStatus){
			MediaPlayer player;
			player = MediaPlayer.create(mContext, R.raw.click);
			player.setVolume(100,100);
			player.start();
		}
	}

	public static void renameDialog(final File mFile, Context mContext){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("Rename");

		final EditText input = new EditText(mContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialog.setView(input);

		mOldFileName = mFile.getAbsolutePath();
		mFileExtension = getFileExtension(mFile.getPath());

		alertDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				newFileName = input.getText().toString();		             

				System.out.println("Old file: "+ mOldFileName);
				File oldFile = new File(mOldFileName);
				File newFile = new File(mFile.getParent(), newFileName + "." + mFileExtension);

				System.out.println("new file: "+newFile.getAbsolutePath());

				if(oldFile.renameTo(newFile)){
					System.out.println("Succes! Name changed to: " + mFile.getName());
				}else{
					System.out.println("failed");
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
	}
	public static void outSideTouchDialog(final Context mContext){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
		builder1.setMessage("Do you want to close App ??");
		builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				((Activity) mContext).finish();
				((Activity) mContext).overridePendingTransition(R.anim.in,R.anim.out);
			}
		});
		builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			} });

		AlertDialog alert1 = builder1.create();
		alert1.show();
	}

	public static void backPressDialog(final Context mContext){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
		builder1.setMessage("Do you want to retain list, press" + " yes"+" else " + "close"+".");
		builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				((Activity) mContext).startActivity(startMain);
				((Activity) mContext).overridePendingTransition(R.anim.in,R.anim.out);
			}
		});
		builder1.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				((Activity) mContext).finish();
				((Activity) mContext).overridePendingTransition(R.anim.in,R.anim.out);
			} });

		AlertDialog alert1 = builder1.create();
		alert1.show();
	}
	public static void helpDialog(final Context mContext){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
		builder1.setMessage(mContext.getResources().getString(R.string.helptoast));
		builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		AlertDialog alert1 = builder1.create();
		alert1.show();
	}



	public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
		final Dialog dialog = new Dialog(mContext);
		dialog.setTitle("Rate " + APP_TITLE);

		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(mContext);
		tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
		tv.setWidth(240);
		tv.setPadding(4, 0, 4, 10);
		ll.addView(tv);

		Button b1 = new Button(mContext);
		b1.setText("Rate " + APP_TITLE);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
				dialog.dismiss();
			}
		});        
		ll.addView(b1);

		Button b2 = new Button(mContext);
		b2.setText("Remind me later");
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll.addView(b2);

		Button b3 = new Button(mContext);
		b3.setText("No, thanks");
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);        
		dialog.show();        
	}
    
	public static void setShowInputMethod(final EditText mSearchEditText,final Context mContext) {
		mSearchEditText.postDelayed( new Runnable() {
			@Override
			public void run() {

				if ( mContext != null ) {

					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(mSearchEditText, 0);

				}
			}
		}, 300 );
	}

	public static void hideSoftKeyboard(final EditText mSearchEditText,final Context mContext) {
		mSearchEditText.postDelayed( new Runnable() {
			@Override
			public void run() {
				if ( mContext != null ) {
					InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
				}
			}
		}, 100 );
	}

	public static void addMedia(Context c, File f) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(Uri.fromFile(f));
		c.sendBroadcast(intent);
	}

	public static void removeMediaThumbnail(Context c, File f) {
		ContentResolver resolver = c.getContentResolver();
		if(isImage || isCamera || isScreenshot)
			resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA + "=" + "\"" + f.getAbsolutePath() + "\"", null);
		else if(isAudio)
			resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA + "=" + "\"" + f.getAbsolutePath() + "\"", null);
		else if(isVideo)
			resolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA + "=" + "\"" + f.getAbsolutePath() + "\"", null);
		else if (isDocument || isAll || isDownload)
			resolver.delete(Files.getContentUri("external"), Files.FileColumns.DATA + "=?", new String[] { f.getAbsolutePath() });
		else
			Toast.makeText(c, "File deleted, but list refreshing failed", Toast.LENGTH_LONG).show();
	}


	public static Bitmap loadContactPhotoThumbnail(String photoData, int imageSize,Context mContext) {
		if ( mContext == null) {
			return null;
		}
		AssetFileDescriptor afd = null;

		try {
			Uri thumbUri;
			final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);
			thumbUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
			afd = mContext.getContentResolver().openAssetFileDescriptor(thumbUri, "r");
			FileDescriptor fileDescriptor = afd.getFileDescriptor();

			if (fileDescriptor != null) {
				return ImageLoader.decodeSampledBitmapFromDescriptor(
						fileDescriptor, imageSize, imageSize);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (afd != null) {
				try {
					afd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static Bitmap loadContactPhotoThumbnailFromContactUri(Context mContext,Uri photoUri) {
		if ( mContext == null) {
			return null;
		}
		  InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(
		            mContext.getContentResolver(), photoUri);
		return BitmapFactory.decodeStream(stream);
	}

	public static String getFileSize( Uri fileUri){

		File filenew = new File(fileUri.getPath());
		int size = Integer.parseInt(String.valueOf(filenew.length()));
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

    public static long getTotalSize(File file) {
        long totalSize = 0;
        if(file != null && file.exists()) {
            if(file.isDirectory()) {
                File[] fileList = file.listFiles();
                if(fileList != null)
                    for(File content : fileList) {
                        if(content.isDirectory())
                            totalSize += getTotalSize(content);
                        else {
                            totalSize += content.length();
                        }
                    }
            } else {
                return file.length();
            }
        }
        return totalSize;
    }

	public static long folderSize(File directory) {
		long length = 0;
		if (directory == null) {
			return length;
		}
		try{
			for (File file : directory.listFiles()) {
				if ( file != null ){
					if (file.isFile())
						length += file.length();
					else
						length += folderSize(file);
				}
			}
		}catch( Exception e){
			e.printStackTrace();
		}
		return length;
	}

	public static String getMimeType(String url)
	{
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

	public static String getMIMEcategory(String aMIMEtype) {

		if (aMIMEtype != null) {

			aMIMEtype = aMIMEtype.substring(0, aMIMEtype.lastIndexOf("/", aMIMEtype.length() - 1)) + "/*";

		} else {

			aMIMEtype = "application/*";
		}

		return aMIMEtype;
	}

	public static String getFileType() {

		return null;
	}
	public static String getFileExtension(String filePath) {
		int index = filePath.lastIndexOf(".");
		if (index == -1){
			return null;
		}
		return filePath.substring(index+1);

	}

	private static java.text.DateFormat shortDateFormat = null;

	private static java.text.DateFormat TimeFormat = null;

	private static StringBuffer mDateString = null;

	public static void resetDateFormatStrings() {

		shortDateFormat = null;

		TimeFormat = null;

	}

	public static String getDateFormatByFormatSetting(final Context context, long date) {

		// String dateString = "";

		if(context == null) {

			return null;
		}

		Time time = new Time();

		time.setToNow();
		long now = time.toMillis(true);
		if(now - date < 0) {
			date /= 1000;
		}
		if ( shortDateFormat == null )
			shortDateFormat = DateFormat.getDateFormat( context );
		if ( TimeFormat == null )
			TimeFormat = DateFormat.getTimeFormat( context );
		if ( mDateString != null )
			mDateString = null;
		mDateString = new StringBuffer( shortDateFormat.format( date ) );
		mDateString.append( ' ' ).append(
				TimeFormat.format( date ) );
		return mDateString.toString();
	}

	public static String getSmallFormatedDateFromLong(final Context context, long date) {
		if ( context == null ) {
			return "";
		}
		java.text.DateFormat shortDateFormat = null;
		shortDateFormat = DateFormat.getDateFormat( context );
		if ( mDateString != null )
			mDateString = null;
		mDateString = new StringBuffer( shortDateFormat.format( date ) );
		return mDateString.toString();
	}

	public static void sendScanFile( Context context, String filePath ) {

		//Intent intent = new Intent( Intent.ACTION_MEDIA_MOUNTED, Uri.parse( "file://" + filePath ) );
		Intent intent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse( "file://" + filePath ) );

		context.sendBroadcast( intent );
	}

	public static boolean checkIfFilesNameIsValid(String fName) {
		if (fName.length() <= 0 || fName.indexOf('\\') >= 0 || fName.indexOf('/') >= 0 || fName.indexOf(':') >= 0
				|| fName.indexOf('?') >= 0 || fName.indexOf('<') >= 0 || fName.indexOf('>') >= 0
				|| fName.indexOf('"') >= 0 || fName.indexOf('|') >= 0)
			return false;
		else
			return true;
	}




	public static String getImageFilesSize( Context context) {

		if ( context == null ) {

			return 0+"";
		}

		long totalSize = 0;

		String where = null;

		String[] whereArgs = null;

		where = Images.ImageColumns.DATA + " LIKE ?";
		whereArgs = new String[]{ mInternalRoot + "%" };

		Cursor cursor = context.getContentResolver().query( Images.Media.EXTERNAL_CONTENT_URI,
				new String[]{ "SUM(" + Images.ImageColumns.SIZE + ")" },
				where,
				whereArgs,
				null );

		if ( cursor != null ) {
			if ( cursor.moveToFirst() ) {
				totalSize = cursor.getLong( 0 );
			}
			cursor.close();
		}
		return returnSize(totalSize);
	}


	public static String getVideoFilesSize( Context context) {

		if ( context == null ) {

			return 0+"";
		}

		long totalSize = 0;

		String where = null;

		String[] whereArgs = null;

		where = Video.VideoColumns.DATA + " LIKE ?";
		whereArgs = new String[]{ mInternalRoot + "%" };

		Cursor cursor = context.getContentResolver().query( Video.Media.EXTERNAL_CONTENT_URI,
				new String[]{ "SUM(" + Video.VideoColumns.SIZE + ")" },
				where,
				whereArgs,
				null );

		if ( cursor != null ) {

			if ( cursor.moveToFirst() ) {
				totalSize = cursor.getLong( 0 );
			}

			cursor.close();
		}

		return returnSize(totalSize);
	}


	public static String getMusicFilesSize( Context context) {

		if ( context == null ) {

			return 0+"";
		}

		long totalSize = 0;

		String where = null;

		String[] whereArgs = null;

		where = Audio.AudioColumns.DATA + " LIKE ?";
		whereArgs = new String[]{ mInternalRoot + "%" };

		Cursor cursor = context.getContentResolver().query( Audio.Media.EXTERNAL_CONTENT_URI,
				new String[]{ "SUM(" + Audio.AudioColumns.SIZE + ")" },
				where,
				whereArgs,
				null );

		if ( cursor != null ) {

			if ( cursor.moveToFirst() ) {
				totalSize = cursor.getLong( 0 );
			}

			cursor.close();
		}

		return returnSize(totalSize);
	}

	public static String getDocumentFilesSize( Context context) {

		if ( context == null ) {

			return 0 + "";
		}

		long totalSize = 0;

		StringBuilder sb = new StringBuilder();

		String where = null;

		String[] whereArgs = MediaFile.getDocumentExtensions();

		for ( int i = 0; i < whereArgs.length; i++ ) {

			whereArgs[ i ] = "%." + whereArgs[ i ];
		}

		where = Files.FileColumns.DATA + " LIKE '" + mInternalRoot + "%' AND ";

		if ( !TextUtils.isEmpty( where ) ) {

			sb.append( where );
		}

		sb.append( "(" );

		for ( int i = 0; i < whereArgs.length; i++ ) {

			if ( i < whereArgs.length - 1 ) {

				sb.append( Files.FileColumns.DATA + " LIKE ? OR " );

			} else {

				sb.append( Files.FileColumns.DATA + " LIKE ?" );
			}
		}

		sb.append( ")" );

		where = sb.toString();


		// query
		Cursor cursor = context.getContentResolver().query( Files.getContentUri( "external" ),
				new String[]{ "SUM(" + Files.FileColumns.SIZE + ")" },
				where,
				whereArgs,
				null );

		if ( cursor != null ) {

			if ( cursor.moveToFirst() ) {

				totalSize = cursor.getLong( 0 );
			}

			cursor.close();
		}

		return returnSize(totalSize);
	}

	public static String returnSize(long size){
		if(size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		try{
			return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
		}catch (Exception e){
			e.printStackTrace();	
		}
		return "Calculating ...";
	}
	public static boolean isClickValid() {

		Long newClickTime = SystemClock.elapsedRealtime();

		if (newClickTime - mLastClickTime < MIN_CLICK_INTERVAL) {

			Log.d("Phone Explorer","invalid click - time diff = "
					+ (newClickTime - mLastClickTime));
			return false;

		} else {

			mLastClickTime = newClickTime;
			return true;
		}
	}
}
