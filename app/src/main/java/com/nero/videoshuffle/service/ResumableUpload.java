/*
 * Copyright (c) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nero.videoshuffle.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.Sleeper;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.nero.videoshuffle.R;
import com.nero.videoshuffle.activity.MainActivity;
import com.nero.videoshuffle.fragment.UploadFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;


/**
 * @author Ibrahim Ulukaya <ulukaya@google.com>
 *         <p/>
 *         YouTube Resumable Upload controller class.
 */
public class ResumableUpload {
    /**
     * Assigned to the upload
     */
    public static final String[] DEFAULT_KEYWORDS = {"MultiSquash", "Game"};
    /**
     * Indicates that the video is fully processed, see https://www.googleapis.com/discovery/v1/apis/youtube/v3/rpc
     */
    private static final String SUCCEEDED = "succeeded";
    private static final String TAG = "ResumableUpload";
    private static int UPLOAD_NOTIFICATION_ID = 1001;
    private static int PLAYBACK_NOTIFICATION_ID = 1002;
    /*
     * Global instance of the format used for the video being uploaded (MIME type).
     */
    private static String VIDEO_FILE_FORMAT = "video/*";

    /**
     * Uploads user selected video in the project folder to the user's YouTube account using OAuth2
     * for authentication.
     */

    public static String upload(YouTube youtube, final InputStream fileInputStream,
                                final long fileSize, final Uri mFileUri, final String path, final Context context,
                                final OnUploadCallback callback) {
        final NotificationManager notifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setData(mFileUri);
        notificationIntent.setAction(Intent.ACTION_VIEW);
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MICRO_KIND);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentTitle(context.getString(R.string.youtube_upload))
                .setContentText(context.getString(R.string.youtube_upload_started))
                .setSmallIcon(R.drawable.ic_stat_device_access_video)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(thumbnail));
        notifyManager.notify(UPLOAD_NOTIFICATION_ID, builder.build());

        String videoId = null;
        try {
            // Add extra information to the video before uploading.
            Video videoObjectDefiningMetadata = new Video();

      /*
       * Set the video to public, so it is available to everyone (what most people want). This is
       * actually the default, but I wanted you to see what it looked like in case you need to set
       * it to "unlisted" or "private" via API.
       */
            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("unlisted");
            videoObjectDefiningMetadata.setStatus(status);

            // We set a majority of the metadata with the VideoSnippet object.
            VideoSnippet snippet = new VideoSnippet();

      /*
       * The Calendar instance is used to create a unique name and description for test purposes, so
       * you can see multiple files being uploaded. You will want to remove this from your project
       * and use your own standard names.
       */
            final Calendar cal = Calendar.getInstance();
            if (!TextUtils.isEmpty(path)) {
                int nameIndex = path.lastIndexOf(File.separator);
                if (nameIndex >= 0 && nameIndex < path.length()) {
                    snippet.setTitle(path.substring(nameIndex + 1));
                }
            } else {
                snippet.setTitle("Video shuffle Upload on " + cal.getTime());
            }
            snippet.setDescription("Video shuffle uploaded via YouTube Data API V3  "
                    + "on " + cal.getTime());

            // Set your keywords.
            // snippet.setTags(Arrays.asList(Constants.DEFAULT_KEYWORD, Upload.generateKeywordFromPlaylistId(Constants.UPLOAD_PLAYLIST)));

            // Set completed snippet to the video object.
            videoObjectDefiningMetadata.setSnippet(snippet);

            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, new BufferedInputStream(fileInputStream));
            mediaContent.setLength(fileSize);

      /*
       * The upload command includes: 1. Information we want returned after file is successfully
       * uploaded. 2. Metadata we want associated with the uploaded video. 3. Video file itself.
       */
            YouTube.Videos.Insert videoInsert = youtube.videos().insert("snippet,statistics,status",
                    videoObjectDefiningMetadata, mediaContent);

            // Set the upload type and add event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();


      /*
       * Sets whether direct media upload is enabled or disabled. True = whole media content is
       * uploaded in a single request. False (default) = resumable media upload protocol to upload
       * in data chunks.
       */
            uploader.setDirectUploadEnabled(false);

            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                    switch (uploader.getUploadState()) {
                        case INITIATION_STARTED:
                            builder.setContentText(context.getString(R.string.initiation_started)).setProgress((int) fileSize,
                                    (int) uploader.getNumBytesUploaded(), false);
                            notifyManager.notify(UPLOAD_NOTIFICATION_ID, builder.build());
                            break;
                        case INITIATION_COMPLETE:
//                            builder.setContentText(context.getString(R.string.initiation_completed)).setProgress((int) fileSize,
//                                    (int) uploader.getNumBytesUploaded(), false);
//                            notifyManager.notify(UPLOAD_NOTIFICATION_ID, builder.build());
                            if (callback != null) {
                                callback.onStart(uploader.getNumBytesUploaded(), fileSize);
                            }
                            break;
                        case MEDIA_IN_PROGRESS:
                            if (callback != null) {
                                callback.onProgress(uploader.getNumBytesUploaded(), fileSize);
                            }
//                            builder.setContentTitle(context.getString(R.string.youtube_upload) +
//                                    (int) (uploader.getProgress() * 100) + "%")
//                                    .setContentText(context.getString(R.string.upload_in_progress))
//                                    .setProgress((int) fileSize, (int) uploader.getNumBytesUploaded(), false);
//                            notifyManager.notify(UPLOAD_NOTIFICATION_ID, builder.build());
                            break;
                        case MEDIA_COMPLETE:
                            builder.setContentTitle(context.getString(R.string.yt_upload_completed))
                                    .setContentText(context.getString(R.string.upload_completed))
                                            // Removes the progress bar
                                    .setProgress(0, 0, false);
                            notifyManager.notify(UPLOAD_NOTIFICATION_ID, builder.build());
                        case NOT_STARTED:
                            if (callback != null) {
                                callback.onFailed(UploadError.Unknown, "");
                            }
                            Log.d(this.getClass().getSimpleName(), context.getString(R.string.upload_not_started));
                            break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);
            uploader.setChunkSize(1 * 1024 * 1024);
            Video returnedVideo = videoInsert.execute();

            Log.d(TAG, "Video upload completed");
            videoId = returnedVideo.getId();
            if (callback != null) {
                callback.onComplete(videoId);
            }
            Log.d(TAG, String.format("videoId = [%s]", videoId));
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            Log.e(TAG, "GooglePlayServicesAvailabilityIOException", availabilityException);
            notifyFailedUpload(context, context.getString(R.string.cant_access_play), notifyManager, builder);
        } catch (UserRecoverableAuthIOException userRecoverableException) {
            Log.i(TAG, String.format("UserRecoverableAuthIOException: %s",
                    userRecoverableException.getMessage()));
            requestAuth(context, userRecoverableException);
        } catch (GoogleJsonResponseException jsonResponseException) {
              /*    com.google.api.client.googleapis.json.GoogleJsonResponseException: 401 Unauthorized
               http://stackoverflow.com/questions/14453505/youtube-data-api-v3-video-upload-403-forbidden-youtubesignuprequired
               http://m.youtube.com/create_channel
                {
                    "code": 401,
                        "errors": [
                    {
                        "domain": "youtube.header",
                            "location": "Authorization",
                            "locationType": "header",
                            "message": "Unauthorized",
                            "reason": "youtubeSignupRequired"
                    }
                    ],
                    "message": "Unauthorized"
                }

                 {
                    "code": 403,
                    "errors": [
                      {
                        "domain": "usageLimits",
                        "message": "Access Not Configured. The API (YouTube Data API) is not enabled for your project. Please use the Google Developers Console to update your configuration.",
                        "reason": "accessNotConfigured",
                        "extendedHelp": "https://console.developers.google.com"
                      }
                    ],
                    "message": "Access Not Configured. The API (YouTube Data API) is not enabled for your project. Please use the Google Developers Console to update your configuration."
                  }
             */

            String errorMsg = jsonResponseException.getMessage();
            Log.i(TAG, String.format("GoogleJsonResponseException: %s", errorMsg));
            Intent intent = new Intent(UploadFragment.REQUEST_UNLINK_ACCOUNT_ACTION);
            intent.putExtra(UploadFragment.REQUEST_ERROR_MESSAGE, errorMsg);
            sendBroadcast(context, intent);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            notifyFailedUpload(context, context.getString(R.string.please_try_again), notifyManager, builder);
        }
        return videoId;
    }

    private static void requestAuth(Context context,
                                    UserRecoverableAuthIOException userRecoverableException) {
        Intent authIntent = userRecoverableException.getIntent();
        Intent runReqAuthIntent = new Intent(UploadFragment.REQUEST_AUTHORIZATION_ACTION);
        runReqAuthIntent.putExtra(UploadFragment.REQUEST_AUTHORIZATION_ACTION_PARAM, authIntent);
        sendBroadcast(context, runReqAuthIntent);
    }

    private static void sendBroadcast(Context context, Intent intent) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.sendBroadcast(intent);
        Log.d(TAG, String.format("Sent broadcast %s", intent.getAction()));
    }

    private static void notifyFailedUpload(Context context, String message, NotificationManager notifyManager,
                                           NotificationCompat.Builder builder) {
        builder.setContentTitle(context.getString(R.string.yt_upload_failed))
                .setContentText(message);
        notifyManager.notify(UPLOAD_NOTIFICATION_ID, builder.build());
        Log.e(ResumableUpload.class.getSimpleName(), message);
    }

    public static void showSelectableNotification(String videoId, Context context) {
//        Log.d(TAG, String.format("Posting selectable notification for video ID [%s]", videoId));
//        final NotificationManager notifyManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        Intent notificationIntent = new Intent(context, PlayActivity.class);
//        notificationIntent.putExtra(MainActivity.YOUTUBE_ID, videoId);
//        notificationIntent.setAction(Intent.ACTION_VIEW);
//
//        URL url;
//        try {
//            url = new URL("https://i1.ytimg.com/vi/" + videoId + "/mqdefault.jpg");
//            Bitmap thumbnail = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            PendingIntent contentIntent = PendingIntent.getActivity(context,
//                    0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//            builder.setContentTitle(context.getString(R.string.watch_your_video))
//                    .setContentText(context.getString(R.string.see_the_newly_uploaded_video)).setContentIntent(contentIntent).setSmallIcon(R.drawable.ic_stat_device_access_video).setStyle(new NotificationCompat.BigPictureStyle().bigPicture(thumbnail));
//            notifyManager.notify(PLAYBACK_NOTIFICATION_ID, builder.build());
//            Log.d(TAG, String.format("Selectable notification for video ID [%s] posted", videoId));
//        } catch (MalformedURLException e) {
//            Log.e(TAG, e.getMessage());
//        } catch (IOException e) {
//            Log.e(TAG, e.getMessage());
//        }
    }


    /**
     * @return url of thumbnail if the video is fully processed
     */
    public static boolean checkIfProcessed(String videoId, YouTube youtube) {
        try {
            YouTube.Videos.List list = youtube.videos().list("processingDetails");
            list.setId(videoId);
            VideoListResponse listResponse = list.execute();
            List<Video> videos = listResponse.getItems();
            if (videos.size() == 1) {
                Video video = videos.get(0);
                String status = video.getProcessingDetails().getProcessingStatus();
                Log.e(TAG, String.format("Processing status of [%s] is [%s]", videoId, status));
                if (status.equals(SUCCEEDED)) {
                    return true;
                }
            } else {
                // can't find the video
                Log.e(TAG, String.format("Can't find video with ID [%s]", videoId));
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching video metadata", e);
        }
        return false;
    }

    public enum UploadError {
        Unknown,
        Unauthorized,
        Forbidden
    }

    public interface OnUploadCallback {
        void onStart(long pos, long total);

        void onProgress(long pos, long total);

        void onFailed(UploadError errorType, String errorMsg);

        void onComplete(String videoId);
    }
}
