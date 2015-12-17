
package com.nero.videoshuffle.fragment;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.nero.videoshuffle.R;
import com.nero.videoshuffle.activity.PhotoPreviewActivity;
import com.nero.videoshuffle.activity.UploadActivity;
import com.nero.videoshuffle.service.UploadService;
import com.nero.videoshuffle.util.NetworkSingleton;

import java.util.Arrays;

/*
 * YouTube video upload fragment
 */
public class UploadFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener {

    private final String TAG = this.getClass().getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private ImageLoader mImageLoader;

    public final int REQUEST_SIGN_IN_REQUIRED = 0;
    public final int REQUEST_GMS_ERROR_DIALOG = 1;
    public final int REQUEST_ACCOUNT_PICKER = 2;
    public static final int REQUEST_AUTHORIZATION = 3;
    public final int REQUEST_CREATE_YOUTUBE_CHANNEL = 4;

    public static final String[] SCOPES = {Scopes.PROFILE, YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_UPLOAD};
    public static final String CLIENT_ID_KEY = "developer.google.oauth2.clientid";
    public static final String GOOGLE_ACCOUNT_KEY = "google_account";

    public static final String URI_YOUTUBE_WATCH_PREFIX = "http://www.youtube.com/watch?v=";
    public static final Uri URI_YOUTUBE_CREATE_CHANNEL = Uri.parse("http://m.youtube.com/create_channel");

    public static final String REQUEST_AUTHORIZATION_ACTION = "com.google.developer.oauth2.RequestAuth";
    public static final String REQUEST_AUTHORIZATION_ACTION_PARAM = REQUEST_AUTHORIZATION_ACTION + ".param";
    public static final String REQUEST_ERROR_MESSAGE = "com.google.developer.error.message";
    public static final String REQUEST_UNLINK_ACCOUNT_ACTION = "com.google.developer.unlink.account";
    public static final String REQUEST_UPLOAD_RESULT_RECEIVER = "com.nero.videoshuffle.upload.resultreceiver";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        getGoogleAccount();
        setHasOptionsMenu(true);
    }

    NetworkImageView mImgIcon;
    TextView mTextAccount;
    ImageView mImgPreview;
    ProgressBar mProgressAccount, mProgressUpload;
    Button mBtnUpload, mBtnStop;
    WebView mWebView;
    Uri mUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.upload_fragment, container, false);
        mImgIcon = (NetworkImageView) rootView.findViewById(R.id.avatar);
        mTextAccount = (TextView) rootView.findViewById(R.id.display_name);
        mImgPreview = (ImageView) rootView.findViewById(R.id.img_Video_Preview);
        mProgressAccount = (ProgressBar) rootView.findViewById(R.id.progress_account_loading);
        mProgressUpload = (ProgressBar) rootView.findViewById(R.id.progress_upload_loading);
        mBtnUpload = (Button) rootView.findViewById(R.id.btn_upload);
        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpload();
            }
        });
        mBtnStop = (Button) rootView.findViewById(R.id.btn_stop);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mWebView.loadUrl(URI_YOUTUBE_WATCH_PREFIX);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mUploadIntent) {
                    boolean isStopped = UploadFragment.this.getActivity().stopService(mUploadIntent);
                    if (isStopped) {
                    }
                }
            }
        });

        Bundle bundle = getArguments();
        mUri = bundle.getParcelable(PhotoPreviewActivity.CURRENT_SELECTED_INDEX);
        long id = ContentUris.parseId(mUri);

        Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(getContext().getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);
        mImgPreview.setImageBitmap(thumbnail);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProfileInfo();
    }

    public void setProfileInfo() {
        //not sure if mGoogleapiClient.isConnect is appropriate...
        boolean isConnected = mGoogleApiClient.isConnected();
        if (!isConnected) {
            mImgIcon.setImageDrawable(null);
            mTextAccount.setText(R.string.not_signed_in);
            mAccountName = null;
            return;
        }
        String account = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        mAccountName = account;
        mTextAccount.setText(account);
        String selectedAccount = mCredential.getSelectedAccountName();
        if (TextUtils.isEmpty(selectedAccount) || selectedAccount != account) {
            mCredential.setSelectedAccountName(account);
            saveAccount();
        }
        if (null != currentPerson && currentPerson.hasImage()) {
            // Set the URL of the image that should be loaded into this view, and
            // specify the ImageLoader that will be used to make the request.
            mImgIcon.setImageUrl(currentPerson.getImage().getUrl(), mImageLoader);
        }
        if (null != currentPerson && currentPerson.hasDisplayName()) {
            mTextAccount.setText(currentPerson.getDisplayName());
        }
    }

    private ResultReceiver mResultReceiver = new UploadResultReceiver(new Handler(Looper.myLooper()));

    UploadService mService;
    private boolean mIsStartUpload = false;
    private Intent mUploadIntent;

    private void startUpload() {
        if (!mGoogleApiClient.isConnected() || TextUtils.isEmpty(mCredential.getSelectedAccountName())) {
            Toast toast = Toast.makeText(getContext(), R.string.hint_upload_initialed_not_ready, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            return;
        }

        mUploadIntent = new Intent(this.getActivity(), UploadService.class);
        mUploadIntent.setData(mUri);
        Bundle bundle = new Bundle();
        bundle.putString(UploadFragment.GOOGLE_ACCOUNT_KEY, mAccountName);
        bundle.putParcelable(REQUEST_UPLOAD_RESULT_RECEIVER, mResultReceiver);

        mUploadIntent.putExtras(bundle);
        this.getActivity().startService(mUploadIntent);

        setProgressUploadVisibility(0);
        if (!mIsStartUpload) {
            this.getActivity().bindService(mUploadIntent, mUploadServiceConnnect, 0);
            mIsStartUpload = true;
        }
    }

    private ServiceConnection mUploadServiceConnnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UploadService.UploadBinder myBinder = (UploadService.UploadBinder) service;
            mService = myBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        setProgressAccountVisibility();
        IntentFilter intentFilter = new IntentFilter(UploadFragment.REQUEST_AUTHORIZATION_ACTION);
        intentFilter.addAction(UploadFragment.REQUEST_UNLINK_ACCOUNT_ACTION);
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(mYouTubeUploadReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        setProgressAccountGone();
        mGoogleApiClient.disconnect();
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mYouTubeUploadReceiver);
        if (mIsStartUpload) {
            this.getActivity().unbindService(mUploadServiceConnnect);
            mIsStartUpload = false;
        }
    }

    private void setProgressAccountVisibility() {
        mProgressAccount.setVisibility(View.VISIBLE);
    }

    private void setProgressAccountGone() {
        mProgressAccount.setVisibility(View.GONE);
        mImgPreview.setAlpha(1.0f);
        mProgressUpload.setProgress(0);
        mProgressUpload.setSecondaryProgress(0);
    }

    private void setProgressUploadVisibility(int pos) {
        mProgressUpload.setVisibility(View.VISIBLE);
        mProgressUpload.setProgress(pos);
        mImgPreview.setAlpha(0.5f);
    }

    private void setProgressUploadGone() {
        mProgressUpload.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SIGN_IN_REQUIRED:
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                    // hasGooglePlayService();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (!TextUtils.isEmpty(accountName)) {
                        mAccountName = accountName;
                        mCredential.setSelectedAccountName(mAccountName);
                        saveAccount();
                        mGoogleApiClient.reconnect();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
            case REQUEST_CREATE_YOUTUBE_CHANNEL:
                if (resultCode == Activity.RESULT_OK) {
                    //startUpload();
                }
            default:
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        setProgressAccountGone();
        setProfileInfo();
        String name = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Log.d(TAG, name);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            //Toast.makeText(this.getContext(), "Connection to Play Service failed", Toast.LENGTH_LONG).show();

            Log.e(TAG, String.format("Connection to Play Services Failed, error: %d, reason: %s",
                    connectionResult.getErrorCode(),
                    connectionResult.toString()));
            try {
                connectionResult.startResolutionForResult(getActivity(), REQUEST_SIGN_IN_REQUIRED);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.toString(), e);
            }
        } else {
            showGooglePlayServicesAvailablilityErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mImageLoader = NetworkSingleton.getInstance(getContext()).getImageLoader();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mImageLoader = null;
    }

    GoogleAccountCredential mCredential;
    String mAccountName;

    public static String getOAuth2ClientId(Context context) {
        String clientId = null;

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle metaData = appInfo.metaData;
            clientId = metaData.getString(CLIENT_ID_KEY, null);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return clientId;
    }

    private String getAccountName() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String result = pref.getString(GOOGLE_ACCOUNT_KEY, null);
        return result;
    }

    private void saveAccount() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        pref.edit().putString(GOOGLE_ACCOUNT_KEY, mAccountName).apply();
    }

    public void getGoogleAccount() {
        mCredential = GoogleAccountCredential.usingOAuth2(getContext(), Arrays.asList(SCOPES));
        mCredential.setBackOff(new ExponentialBackOff());

        mAccountName = getAccountName();
        mCredential.setSelectedAccountName(mAccountName);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_switch_account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menu_account) {
            chooseGoogleAccount();
        }
        return true;
    }

    private void hasGooglePlayService() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseGoogleAccount();
        }
    }

    private void chooseGoogleAccount() {
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean checkGooglePlayServicesAvailable() {
        final int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        boolean result = true;
        if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
            showGooglePlayServicesAvailablilityErrorDialog(statusCode);
            result = false;
        }
        return result;
    }

    private void showGooglePlayServicesAvailablilityErrorDialog(final int connectionStatusCode) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dlg = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, UploadFragment.this.getActivity(), REQUEST_SIGN_IN_REQUIRED);
                dlg.show();
            }
        });
    }

    boolean isNeedChannelPoped = false;
    private BroadcastReceiver mYouTubeUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UploadFragment.REQUEST_AUTHORIZATION_ACTION)) {
                Intent newIntent = intent.getParcelableExtra(UploadFragment.REQUEST_AUTHORIZATION_ACTION_PARAM);
                UploadFragment.this.startActivityForResult(newIntent, UploadFragment.REQUEST_AUTHORIZATION);
            } else if (intent.getAction().equals(UploadFragment.REQUEST_UNLINK_ACCOUNT_ACTION) && !isNeedChannelPoped) {
                DialogFragment dlg = new ConfirmDialogFragmen();
                dlg.show(UploadFragment.this.getFragmentManager(), dlg.getClass().getSimpleName());
                isNeedChannelPoped = true;
            }
        }
    };

    private class ConfirmDialogFragmen extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog dlg = new AlertDialog.Builder(UploadFragment.this.getContext())
                    .setMessage(R.string.dialog_content_youtube_channel)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isNeedChannelPoped = false;
                            Intent browerIntent = new Intent(Intent.ACTION_VIEW);
                            browerIntent.setData(URI_YOUTUBE_CREATE_CHANNEL);
                            UploadFragment.this.startActivityForResult(browerIntent, REQUEST_CREATE_YOUTUBE_CHANNEL);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isNeedChannelPoped = false;
                        }
                    }).create();
            return dlg;
        }
    }


    public class UploadResultReceiver extends ResultReceiver {
        public static final int RESULT_UPLOAD_START = 0;
        public static final int RESULT_UPLOAD_PROGRESS = 1;
        public static final int RESULT_UPLOAD_FAILED = 2;
        public static final int RESULT_UPLOAD_COMPLETED = 3;

        private static final int UPLOAD_NOTIFICATION_ID = 4;

        public static final String RESULT_UPLOAD_POSITION = "upload_position";
        public static final String RESULT_UPLOAD_TOTAL = "upload_total";
        public static final String RESULT_UPLOAD_ERROR = "upload_error";
        public static final String RESULT_UPLOAD_RESULT_VIDEO_ID = "upload_video_id";

        private NotificationCompat.Builder mBuilder;
        private NotificationManager mNotificationMgr;

        public UploadResultReceiver(Handler handler) {
            super(handler);
        }

        private NotificationCompat.Builder getNotificationBuilder() {
            if (mBuilder != null) {
                return mBuilder;
            }
            Context context = UploadFragment.this.getContext();
            mNotificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context);

            Intent notificationIntent = new Intent(context, UploadActivity.class);
            notificationIntent.setData(UploadFragment.this.mUri);
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(UploadFragment.this.mUri.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentTitle(context.getString(R.string.youtube_upload))
                    .setContentText(context.getString(R.string.youtube_upload_started))
                    .setSmallIcon(R.drawable.ic_stat_device_access_video)
                    .setContentIntent(contentIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(thumbnail));
            return mBuilder;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            Context context = UploadFragment.this.getContext();
            NotificationCompat.Builder builder;
            long pos = resultData.getLong(RESULT_UPLOAD_POSITION);
            long size = resultData.getLong(RESULT_UPLOAD_TOTAL);
            switch (resultCode) {
                case RESULT_UPLOAD_START:
                    builder = getNotificationBuilder().setContentText(context.getString(R.string.initiation_started))
                            .setProgress((int) size, (int) pos, false);
                    mNotificationMgr.notify(UPLOAD_NOTIFICATION_ID, builder.build());
                    setProgressUploadVisibility(0);
                    break;
                case RESULT_UPLOAD_PROGRESS:
                    builder = getNotificationBuilder().setContentTitle(context.getString(R.string.youtube_upload))
                            .setContentText(context.getString(R.string.youtube_upload))
                            .setProgress((int) size, (int) pos, false);
                    mNotificationMgr.notify(UPLOAD_NOTIFICATION_ID, builder.build());

                    if (size > 0) {
                        setProgressUploadVisibility((int) (100.0 * pos / size));
                    }

                    break;
                case RESULT_UPLOAD_FAILED:
                    break;
                case RESULT_UPLOAD_COMPLETED:
                    String videoId = resultData.getString(RESULT_UPLOAD_RESULT_VIDEO_ID);
                    builder = getNotificationBuilder().setContentTitle(context.getString(R.string.yt_upload_completed))
                            .setContentText(context.getString(R.string.upload_completed) + ",Id=" + videoId)
                            .setProgress((int) size, (int) pos, false);

                    mNotificationMgr.notify(UPLOAD_NOTIFICATION_ID, builder.build());
                    Toast toast = Toast.makeText(context, "New upload video id:" + videoId, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(URI_YOUTUBE_WATCH_PREFIX + videoId));
                    startActivity(intent);
                    setProgressUploadGone();
                    break;
                default:
                    break;

            }
        }
    }
}
