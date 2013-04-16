package com.xdxf.dictionary.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/31/13
 * Time: 11:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static SimpleDateFormat DATEFORMAT_NUMERIC = new SimpleDateFormat("ddMMyyyyhhmmss");
    public static SimpleDateFormat DATEFORMAT_STRING = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    public static SimpleDateFormat DATEFORMAT_ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String toISODateFormat(SimpleDateFormat from, String date) {
        return convertDateFormat(from, DATEFORMAT_ISO8601FORMAT, date);
    }

    public static String toLongDateFormat(SimpleDateFormat from, String date) {
        return convertDateFormat(from, DATEFORMAT_STRING, date);
    }

    public static String convertDateFormat(SimpleDateFormat from, SimpleDateFormat to, String date) {
        try {
            Date d = from.parse(date);
            return to.format(d);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return date;
    }
    private static long[] toPrimitives(Long[] longs) {
        long[] primitives = new long[longs.length];
        for (int i = 0; i < longs.length; i++)
            primitives[i] = longs[i].longValue();

        return primitives;
    }
    public static Long[] toObjects(long... longs) {

        Long[] objects = new Long[longs.length];
        for (int i = 0; i < longs.length; i++)
            objects[i] = longs[i];

        return objects;
    }

    public static boolean contains(long[] longs, long value) {
        boolean b = false;
        for (long l : longs) {
            if (l == value) {
                b = true;
                break;
            }
        }
        return b;
    }

    public static void hideSoftKeyboard(Activity activity, IBinder token) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    public static int getItemPositionByAdapterId(ListAdapter adapter, final long id) {
        int position = -1;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItemId(i) == id)
            {
                position = i;
                break;
            }
        }
        return position;
    }

    public static long[] remove(long[] longs, long value) {
        if (contains(longs, value)) {
            long[] _longs = new long[longs.length - 1];
            int index = 0;
            for (long l : longs) {
                if (l != value) {
                    _longs[index] = l;
                    index++;
                }
            }
            return _longs;
        }
        return longs;
    }

    public static class Color {
        public static int generateRandomBlueShade() {
            return generateRandomColor(0x0000ff);//light blue #87CEFA
        }

        // http://stackoverflow.com/questions/43044/algorithm-to-randomly-generate-an-aesthetically-pleasing-color-palette
        public static int generateRandomColor(int mix) {
            Random random = new Random();
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);

            // mix the color
            red = (red + android.graphics.Color.red(mix)) / 2;
            green = (green + android.graphics.Color.green(mix)) / 2;
            blue = (blue + android.graphics.Color.blue(mix)) / 2;

            int color = android.graphics.Color.rgb(red, green, blue);
            return color;
        }

        public static String toHexColor(int color) {
            return String.format("#%06X", 0xFFFFFF & color);
        }
    }

    public static class File {

        /**
         * http://stackoverflow.com/a/12949705/722965
         *
         * @return
         */
        public static boolean isStorageWritable() {
            String state = Environment.getExternalStorageState();
            boolean mExternalStorageAvailable = false;
            boolean mExternalStorageWriteable = false;

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
            } else {
                // Something else is wrong. It may be one of many other states, but
                // all we need
                // to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
            }

            if (mExternalStorageAvailable == true
                    && mExternalStorageWriteable == true) {
                return true;
            } else {
                return false;
            }
        }

        public static String getPath(Context context, Uri uri) {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {"_data"};
                Cursor cursor = null;

                try {
                    cursor = context.getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor
                            .getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }
    }

    public static class Service {
        public static boolean isServiceRunning(Activity activity, String serviceName) {
            ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }


    }

    public static class Csv {

        // ----------------- Following code copied from Apache Harmony (Character class)
        private static final String QUOTE = "\"";
        private static final String ESCAPED_QUOTE = "\"\"";
        private static char[] CHARACTERS_THAT_MUST_BE_QUOTED = {',', '"', '\n'};

        /**
         * Indicates whether {@code ch} is a high- (or leading-) surrogate code
         * unit that is used for representing supplementary characters in UTF-16
         * encoding.
         *
         * @param ch the character to test.
         * @return {@code true} if {@code ch} is a high-surrogate code unit;
         *         {@code false} otherwise.
         */
        static boolean isHighSurrogate(char ch) {
            return ('?' <= ch && '?' >= ch);
        }

        public static int indexOfAny(String str, char[] searchChars) {

            int csLen = str.length();
            int csLast = csLen - 1;
            int searchLen = searchChars.length;
            int searchLast = searchLen - 1;
            for (int i = 0; i < csLen; i++) {
                char ch = str.charAt(i);
                for (int j = 0; j < searchLen; j++) {
                    if (searchChars[j] == ch) {
                        if (i < csLast && j < searchLast && isHighSurrogate(ch)) {
                            // ch is a supplementary character
                            if (searchChars[j + 1] == str.charAt(i + 1)) {
                                return i;
                            }
                        } else {
                            return i;
                        }
                    }
                }
            }
            return -1;
        }

        public static String Escape(String s) {
            if (s.contains(QUOTE)) {
                s = s.replace(QUOTE, ESCAPED_QUOTE);
            }

            if (indexOfAny(s, CHARACTERS_THAT_MUST_BE_QUOTED) > -1) {
                s = QUOTE + s + QUOTE;
            }

            return s;
        }

        public static String Unescape(String s) {
            if (s.startsWith(QUOTE) && s.endsWith(QUOTE)) {
                s = s.substring(1, s.length() - 2);

                if (s.contains(ESCAPED_QUOTE)) {
                    s = s.replace(ESCAPED_QUOTE, QUOTE);
                }
            }

            return s;
        }

        public static String toCsvString(long[] l) {
            StringBuilder sb = new StringBuilder();
            boolean isFirstSelected = true;
            final int checkedItemsCount = l.length;
            for (int i = 0; i < checkedItemsCount; ++i) {
                if (!isFirstSelected) {
                    sb.append(", ");
                }
                sb.append(l[i]);
                isFirstSelected = false;
            }
            return sb.toString();
        }

        public static String fromCsvStringToWhereClause(String commaSeparatedString, String column) {
            StringBuilder sb = new StringBuilder();
            String[] items = commaSeparatedString.split(",");
            boolean isFirstSelected = true;
            final int itemCount = items.length;
            for (int i = 0; i < itemCount; ++i) {
                if (!isFirstSelected) {
                    sb.append(" OR ");
                }
                sb.append(column).append(" = ").append(items[i]);
                isFirstSelected = false;
            }
            return sb.toString();
        }

        public static long[] toLongArray(String commaSeparatedString) {
            String[] items = commaSeparatedString.split(",");
            final int itemCount = items.length;
            ArrayList<Long> l = new ArrayList<Long>();
            for (int i = 0; i < itemCount; ++i) {
               String item = items[i];
                if(item.isEmpty())
                    continue;
                l.add(Long.parseLong(items[i].trim()));
            }
            return Utils.toPrimitives(l.toArray(new Long[l.size()]));
        }
    }


}
