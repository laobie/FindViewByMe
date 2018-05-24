package com.jaeger.findviewbyme.util;

/**
 * Created by Jaeger on 2016/12/31.
 * <p>
 * Email: chjie.jaeger@gamil.com
 * GitHub: https://github.com/laobie
 */
public class TextUtils {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
    public static String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<RelativeLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    android:id=\"@+id/base_relative_layout\"\n" +
            "    android:layout_width=\"match_parent\"\n" +
            "    android:layout_height=\"70dp\"\n" +
            "    android:gravity=\"bottom\"\n" +
            "    android:background=\"@drawable/background_color\">\n" +
            "\n" +
            "    <RelativeLayout\n" +
            "        android:id=\"@+id/rl_title\"\n" +
            "        android:layout_width=\"match_parent\"\n" +
            "        android:layout_height=\"50dp\"\n" +
            "        android:layout_alignParentBottom=\"true\">\n" +
            "\n" +
            "        <LinearLayout\n" +
            "            android:id=\"@+id/ll_left\"\n" +
            "            android:layout_width=\"45dp\"\n" +
            "            android:layout_height=\"match_parent\"\n" +
            "            android:layout_centerVertical=\"true\"\n" +
            "            android:orientation=\"horizontal\">\n" +
            "\n" +
            "            <TextView\n" +
            "                android:id=\"@+id/tv_left\"\n" +
            "                android:layout_width=\"wrap_content\"\n" +
            "                android:layout_height=\"match_parent\"\n" +
            "                android:layout_gravity=\"center_vertical\"\n" +
            "                android:layout_marginRight=\"5dp\"\n" +
            "                android:gravity=\"center\"\n" +
            "                android:paddingLeft=\"15dp\"\n" +
            "                android:paddingRight=\"15dp\"\n" +
            "                android:textColor=\"@color/white\"\n" +
            "                android:textSize=\"16dp\"\n" +
            "                android:visibility=\"gone\" />\n" +
            "\n" +
            "            <ImageView\n" +
            "                android:id=\"@+id/iv_left\"\n" +
            "                android:layout_width=\"45dp\"\n" +
            "                android:layout_height=\"50dp\"\n" +
            "                android:paddingLeft=\"14dp\"\n" +
            "                android:paddingRight=\"14dp\"\n" +
            "                android:layout_gravity=\"center_vertical\"\n" +
            "                android:src=\"@drawable/back_white\" />\n" +
            "\n" +
            "        </LinearLayout>\n" +
            "\n" +
            "        <TextView\n" +
            "            android:id=\"@+id/title_name\"\n" +
            "            android:layout_width=\"wrap_content\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:layout_centerInParent=\"true\"\n" +
            "            android:ellipsize=\"marquee\"\n" +
            "            android:focusable=\"true\"\n" +
            "            android:focusableInTouchMode=\"true\"\n" +
            "            android:gravity=\"center\"\n" +
            "            android:marqueeRepeatLimit=\"marquee_forever\"\n" +
            "            android:maxWidth=\"200dp\"\n" +
            "            android:singleLine=\"true\"\n" +
            "            android:textColor=\"@color/bg_white\"\n" +
            "            android:textSize=\"18dp\" />\n" +
            "\n" +
            "        <LinearLayout\n" +
            "            android:id=\"@+id/ll_right\"\n" +
            "            android:layout_width=\"wrap_content\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:layout_alignParentRight=\"true\"\n" +
            "            android:layout_centerVertical=\"true\"\n" +
            "            android:minWidth=\"20dp\"\n" +
            "            android:orientation=\"horizontal\">\n" +
            "\n" +
            "\n" +
            "            <TextView\n" +
            "                android:id=\"@+id/tv_leftright\"\n" +
            "                android:layout_width=\"wrap_content\"\n" +
            "                android:layout_height=\"wrap_content\"\n" +
            "                android:paddingBottom=\"5dp\"\n" +
            "                android:paddingLeft=\"5dp\"\n" +
            "                android:paddingRight=\"10dp\"\n" +
            "                android:paddingTop=\"5dp\"\n" +
            "                android:textColor=\"@color/white\"\n" +
            "                android:textSize=\"16dp\"\n" +
            "                android:visibility=\"gone\" />\n" +
            "\n" +
            "            <TextView\n" +
            "                android:id=\"@+id/tv_right\"\n" +
            "                android:layout_width=\"wrap_content\"\n" +
            "                android:layout_height=\"wrap_content\"\n" +
            "                android:paddingBottom=\"5dp\"\n" +
            "                android:paddingLeft=\"5dp\"\n" +
            "                android:paddingRight=\"10dp\"\n" +
            "                android:paddingTop=\"5dp\"\n" +
            "                android:text=\"提交\"\n" +
            "                android:textColor=\"@color/white\"\n" +
            "                android:textSize=\"16dp\"\n" +
            "                android:visibility=\"gone\" />\n" +
            "\n" +
            "            <ImageView\n" +
            "                android:id=\"@+id/iv_right\"\n" +
            "                android:layout_width=\"wrap_content\"\n" +
            "                android:layout_height=\"wrap_content\"\n" +
            "                android:paddingBottom=\"5dp\"\n" +
            "                android:paddingLeft=\"5dp\"\n" +
            "                android:paddingRight=\"10dp\"\n" +
            "                android:paddingTop=\"5dp\"\n" +
            "                android:src=\"@drawable/caidan\"\n" +
            "                android:visibility=\"gone\" />\n" +
            "\n" +
            "            <LinearLayout\n" +
            "                android:id=\"@+id/ll_updateclose\"\n" +
            "                android:layout_width=\"wrap_content\"\n" +
            "                android:layout_height=\"wrap_content\"\n" +
            "                android:orientation=\"horizontal\"\n" +
            "                android:visibility=\"gone\">\n" +
            "\n" +
            "                <ImageView\n" +
            "                    android:id=\"@+id/iv_update\"\n" +
            "                    android:layout_width=\"34dp\"\n" +
            "                    android:layout_height=\"49dp\"\n" +
            "                    android:layout_marginLeft=\"5dp\"\n" +
            "                    android:paddingBottom=\"15dp\"\n" +
            "                    android:paddingRight=\"10dp\"\n" +
            "                    android:paddingTop=\"15dp\"\n" +
            "                    android:src=\"@drawable/shuaxin\" />\n" +
            "\n" +
            "                <ImageView\n" +
            "                    android:id=\"@+id/iv_close\"\n" +
            "                    android:layout_width=\"34dp\"\n" +
            "                    android:layout_height=\"49dp\"\n" +
            "                    android:paddingBottom=\"15dp\"\n" +
            "                    android:paddingRight=\"15dp\"\n" +
            "                    android:paddingTop=\"15dp\"\n" +
            "                    android:src=\"@drawable/guanbi\" />\n" +
            "            </LinearLayout>\n" +
            "        </LinearLayout>\n" +
            "    </RelativeLayout>\n" +
            "\n" +
            "    <RelativeLayout\n" +
            "        android:id=\"@+id/rl_search\"\n" +
            "        android:layout_width=\"match_parent\"\n" +
            "        android:layout_height=\"50dp\"\n" +
            "        android:visibility=\"gone\">\n" +
            "\n" +
            "        <TextView\n" +
            "            android:id=\"@+id/txt_cancel\"\n" +
            "            android:layout_width=\"wrap_content\"\n" +
            "            android:layout_height=\"match_parent\"\n" +
            "            android:paddingLeft=\"15dp\"\n" +
            "            android:paddingRight=\"15dp\"\n" +
            "            android:layout_centerVertical=\"true\"\n" +
            "            android:gravity=\"center\"\n" +
            "            android:text=\"取消\"\n" +
            "            android:textSize=\"16dp\"\n" +
            "            android:textColor=\"@color/white\"\n" +
            "            android:layout_alignParentRight=\"true\"/>\n" +
            "\n" +
            "        <RelativeLayout\n" +
            "            android:id=\"@+id/ll_search\"\n" +
            "            android:layout_width=\"match_parent\"\n" +
            "            android:layout_height=\"match_parent\"\n" +
            "            android:layout_centerVertical=\"true\"\n" +
            "            android:layout_marginLeft=\"15dp\"\n" +
            "            android:layout_marginBottom=\"7dp\"\n" +
            "            android:layout_marginTop=\"7dp\"\n" +
            "            android:layout_toLeftOf=\"@+id/txt_cancel\"\n" +
            "            android:background=\"@drawable/title_search_bg\">\n" +
            "\n" +
            "            <ImageView\n" +
            "                android:id=\"@+id/img_delete\"\n" +
            "                android:layout_width=\"wrap_content\"\n" +
            "                android:layout_height=\"match_parent\"\n" +
            "                android:layout_alignParentRight=\"true\"\n" +
            "                android:layout_centerVertical=\"true\"\n" +
            "                android:paddingLeft=\"10dp\"\n" +
            "                android:paddingRight=\"10dp\"\n" +
            "                android:src=\"@drawable/search_delete\"/>\n" +
            "\n" +
            "            <EditText\n" +
            "                android:id=\"@+id/edt_search\"\n" +
            "                android:layout_width=\"match_parent\"\n" +
            "                android:layout_height=\"match_parent\"\n" +
            "                android:layout_centerVertical=\"true\"\n" +
            "                android:layout_marginLeft=\"10dp\"\n" +
            "                android:background=\"@null\"\n" +
            "                android:gravity=\"center_vertical\"\n" +
            "                android:inputType=\"phone\"\n" +
            "                android:maxLength=\"11\"\n" +
            "                android:layout_toLeftOf=\"@+id/img_delete\"\n" +
            "                android:textSize=\"14dp\"\n" +
            "                android:hint=\"@string/please_enter_phone_number\"\n" +
            "                android:textColorHint=\"@color/color_C2C7CE\"\n" +
            "                android:textColor=\"@color/front_gray_2\"/>\n" +
            "        </RelativeLayout>\n" +
            "    </RelativeLayout>\n" +
            "</RelativeLayout>\n";
}
