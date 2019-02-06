package com.example.ooduberu.chatapp.utility;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.example.ooduberu.chatapp.ChatApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

public class TimeDateUtils {
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;

    private static final SimpleDateFormat[] ACCEPTED_TIMESTAMP_FORMATS = {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a Z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    };

    private static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String getCurrentGMTTimestamp() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(new Date());
    }

    public static String getUTCTimestamp(Date date) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a Z", Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(date);
    }

    public static String getTimeAgo(Date date) {
        long time = date.getTime();
        return getTimeAgo(time);
    }

    public static String getTimeAgo2(Date date) {
        long time = date.getTime() - 3600000;
        return getTimeAgo(time);
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000; // if timestamp given in seconds, convert to millis
        }

        long now = getCurrentTime();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;

        if(diff < 10 * SECOND){
            return "just now";
        } else if(diff < 50 * SECOND){
            return diff / SECOND + " secs ago";
        } else if (diff < 2 * MINUTE) {
            return "a min ago";
        } else if (diff < 50 * MINUTE) {
            return diff / MINUTE + " mins ago";
        } else if (diff < 90 * MINUTE) {
            return "an hr ago";
        } else if (diff < 24 * HOUR) {
            return diff / HOUR + " hrs ago";
        } else if (diff < 48 * HOUR) {
            return "yesterday";
        } else {
            //return diff / DAY + " days ago";
            return formatShortDate(new Date(time));
        }
    }

    public static String getTimeInFuture(Date date) {
        long time = date.getTime();
        return getTimeInFuture(time);
    }

    public static String getTimeInFuture(long time) {
        // TODO: use DateUtils methods instead
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = getCurrentTime();
        if (time < now || time <= 0) {
            return null;
        }

        final long diff = time - now;

        return diff/DAY+"";
    }

    private static final SimpleDateFormat VALID_IFMODIFIEDSINCE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    public static boolean isValidFormatForIfModifiedSinceHeader(String timestamp) {
        try {
            return VALID_IFMODIFIEDSINCE_FORMAT.parse(timestamp)!=null;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String formatShortDate(Date date) {
        Context context = ChatApplication.getInstance();

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR,
                TimeZone.getDefault().getID()).toString();
    }

    public static Date parseTimestamp(String timestamp) {
        for (SimpleDateFormat format : ACCEPTED_TIMESTAMP_FORMATS) {
            // format.setTimeZone(TimeZone.getTimeZone("GMT"));
            // format.setTimeZone(TimeZone.getTimeZone("UTC"));
            format.setTimeZone(TimeZone.getDefault());
            try {
                return format.parse(timestamp);
            } catch (ParseException ex) {
                continue;
            }
        }

        // All attempts to parse have failed
        return null;
    }

    public static String getGMTTime(String dateStr) {
        if(TextUtils.isEmpty(dateStr)) return "";
        String formattedDate;

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(dateStr);

            //DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
            //format.setTimeZone(TimeZone.getTimeZone("GMT"));
            //format.format(date);

            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);

        }catch (Exception e){
            e.printStackTrace();
            formattedDate = "";
        }

        return formattedDate;
    }

    public static long timestampToMillis(String timestamp, long defaultValue) {
        if (TextUtils.isEmpty(timestamp)) {
            return defaultValue;
        }
        Date d = parseTimestamp(timestamp);
        return d == null ? defaultValue : d.getTime();
    }

    public static String formatShortTime(Date time) {
        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
        TimeZone tz = TimeZone.getDefault();
        if (tz != null) {
            format.setTimeZone(tz);
        }
        return format.format(time);
    }

    /**
     * Returns "Today", "Tomorrow", "Yesterday", or a short date format.
     */
    public static String formatHumanFriendlyShortDate(long timestamp) {
        long localTimestamp, localTime;
        long now = getCurrentTime();

        TimeZone defaultTz = TimeZone.getDefault();

        localTimestamp = timestamp + defaultTz.getOffset(timestamp);
        localTime = now + defaultTz.getOffset(now);

        long dayOrd = localTimestamp / 86400000L;
        long nowOrd = localTime / 86400000L;

        if (dayOrd == nowOrd) {
            return "Today";
        } else if (dayOrd == nowOrd - 1) {
            return "Yesterday";
        } else if (dayOrd == nowOrd + 1) {
            return "Tomorrow";
        } else {
            return formatShortDate(new Date(timestamp));
        }
    }
}
