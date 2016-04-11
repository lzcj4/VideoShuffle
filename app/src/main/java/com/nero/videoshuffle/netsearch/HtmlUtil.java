package com.nero.videoshuffle.netsearch;

import android.text.TextUtils;

/**
 * Created by nlang on 4/11/2016.
 */
public class HtmlUtil {

    /// <summary>
    /// Get subString (include the startFlag ,exclude the  endFlag)
    /// </summary>
    /// <param name="html"></param>
    /// <param name="startFlag"></param>
    /// <param name="endFlag"></param>
    /// <returns></returns>
    public static String getSubString(String html, String startFlag, String endFlag) {
        return getSubString(html, 0, startFlag, endFlag);
    }

    /// <summary>
    /// Get subString (include the startFlag ,exclude the endFlag)
    /// </summary>
    /// <param name="html"></param>
    /// <param name="startIndex"></param>
    /// <param name="startFlag"></param>
    /// <param name="endFlag"></param>
    /// <returns></returns>
    public static String getSubString(String html, int startIndex, String startFlag, String endFlag) {
        if (TextUtils.isEmpty(html) || startIndex < 0 ||
                TextUtils.isEmpty(startFlag) || TextUtils.isEmpty(endFlag)) {
            throw new IllegalArgumentException();
        }

        String result = null;
        int subStartIndex = html.indexOf(startFlag, startIndex);
        if (subStartIndex < 0) {
            return result;
        }
        int endIndex = html.indexOf(endFlag, subStartIndex);
        result = getSubString(html, subStartIndex, endIndex);
        return result;
    }


    private static String getSubString(String html, int startIndex, int endIndex) {
        if (startIndex < 0 ||
                endIndex < 0 ||
                endIndex > html.length() ||
                endIndex < startIndex) {
            throw new IllegalArgumentException("getSubString invalid argument");
        }

        String result = null;
        result = html.substring(startIndex, endIndex);
        return result;
    }

    /// <summary>
    /// Get subString (include the startFlag ,but no endFlag)
    /// </summary>
    /// <param name="html"></param>
    /// <param name="startFlag"></param>
    /// <param name="endFlag"></param>
    /// <returns></returns>
    public static String getLastSubString(String html, String startFlag, String endFlag) {
        if (TextUtils.isEmpty(html) ||
                TextUtils.isEmpty(startFlag) || TextUtils.isEmpty(endFlag)) {
            throw new IllegalArgumentException();
        }

        String result = null;
        int subStartIndex = html.lastIndexOf(startFlag);
        if (subStartIndex < 0) {
            return result;
        }
        int endIndex = html.indexOf(endFlag, subStartIndex);
        result = getSubString(html, subStartIndex, endIndex);
        return result;
    }

    /// <summary>
    /// get html property value
    /// </summary>
    /// <param name="html"></param>
    /// <param name="startFlag"></param>
    /// <param name="endFlag"></param>
    /// <param name="propName"></param>
    /// <returns></returns>
    public static String getProperty(String html, String startFlag, String endFlag, String propName) {
        String result = "";
        String newHtml = getSubString(html, startFlag, endFlag);
        if (TextUtils.isEmpty(html)) {
            return result;
        }

        // <div class="image image-user">

        int startIndex = newHtml.indexOf(propName);
        if (startIndex >= 0) {
            startIndex += propName.length();
            startIndex = newHtml.indexOf("\"", startIndex) >= 0 ? newHtml.indexOf("\"", startIndex) : newHtml.indexOf("'", startIndex);

            //" or '
            startIndex += "'".length();
            int endIndex = newHtml.indexOf("\"", startIndex) >= 0 ? newHtml.indexOf("\"", startIndex) : newHtml.indexOf("'", startIndex);

            if (endIndex > startIndex && endIndex >= 0) {
                result = getSubString(newHtml, startIndex, endIndex);
            }
        }
        return result;
    }


    /// <summary>
    ///  get html value
    /// </summary>
    /// <param name="html"></param>
    /// <param name="startFlag"></param>
    /// <param name="endFlag"></param>
    /// <returns></returns>
    public static String getValue(String html, String startFlag, String endFlag) {
        String result = null;
        String newHtml = getSubString(html, startFlag, endFlag);
        if (TextUtils.isEmpty(html)) {
            return result;
        }

        //  <span title="August 24, 2014 at 3:58:33 AM UTC">2 years ago</span>

        int startIndex = newHtml.indexOf(">") + 1;
        if (startIndex >= 0 && startIndex < newHtml.length()) {
            result = newHtml.substring(startIndex);
        }
        return result;
    }
}
