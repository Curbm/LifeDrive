package com.skyfire.hipda.misc;

import android.text.style.ImageSpan;
import android.util.SparseIntArray;
import com.skyfire.hipda.R;

/**
 * Created by Neil
 */
public class SmileyHelper {

  public static ImageSpan getSmileySpan(int id) {
    int resId = RES_ID_MAP.get(id);

    return resId == 0 ? null : new ImageSpan(App.getInstance(), resId);
  }

  static final SparseIntArray RES_ID_MAP = new SparseIntArray();

  static {
    RES_ID_MAP.put(1, R.drawable.smiley_1);
    RES_ID_MAP.put(2, R.drawable.smiley_2);
    RES_ID_MAP.put(3, R.drawable.smiley_3);
    RES_ID_MAP.put(5, R.drawable.smiley_5);
    RES_ID_MAP.put(6, R.drawable.smiley_6);
    RES_ID_MAP.put(7, R.drawable.smiley_7);
    RES_ID_MAP.put(8, R.drawable.smiley_8);
    RES_ID_MAP.put(9, R.drawable.smiley_9);
    RES_ID_MAP.put(26, R.drawable.smiley_26);
    RES_ID_MAP.put(27, R.drawable.smiley_27);
    RES_ID_MAP.put(34, R.drawable.smiley_34);
    RES_ID_MAP.put(35, R.drawable.smiley_35);
    RES_ID_MAP.put(36, R.drawable.smiley_36);
    RES_ID_MAP.put(37, R.drawable.smiley_37);
    RES_ID_MAP.put(38, R.drawable.smiley_38);
    RES_ID_MAP.put(39, R.drawable.smiley_39);
    RES_ID_MAP.put(40, R.drawable.smiley_40);
    RES_ID_MAP.put(41, R.drawable.smiley_41);
    RES_ID_MAP.put(42, R.drawable.smiley_42);
    RES_ID_MAP.put(43, R.drawable.smiley_43);
    RES_ID_MAP.put(44, R.drawable.smiley_44);
    RES_ID_MAP.put(45, R.drawable.smiley_45);
    RES_ID_MAP.put(46, R.drawable.smiley_46);
    RES_ID_MAP.put(47, R.drawable.smiley_47);
    RES_ID_MAP.put(48, R.drawable.smiley_48);
    RES_ID_MAP.put(49, R.drawable.smiley_49);
    RES_ID_MAP.put(50, R.drawable.smiley_50);
    RES_ID_MAP.put(51, R.drawable.smiley_51);
    RES_ID_MAP.put(52, R.drawable.smiley_52);
    RES_ID_MAP.put(53, R.drawable.smiley_53);
    RES_ID_MAP.put(54, R.drawable.smiley_54);
    RES_ID_MAP.put(55, R.drawable.smiley_55);
    RES_ID_MAP.put(56, R.drawable.smiley_56);
    RES_ID_MAP.put(57, R.drawable.smiley_57);
    RES_ID_MAP.put(58, R.drawable.smiley_58);
    RES_ID_MAP.put(59, R.drawable.smiley_59);
    RES_ID_MAP.put(60, R.drawable.smiley_60);
    RES_ID_MAP.put(61, R.drawable.smiley_61);
    RES_ID_MAP.put(62, R.drawable.smiley_62);
    RES_ID_MAP.put(63, R.drawable.smiley_63);
    RES_ID_MAP.put(64, R.drawable.smiley_64);
    RES_ID_MAP.put(65, R.drawable.smiley_65);
    RES_ID_MAP.put(66, R.drawable.smiley_66);
    RES_ID_MAP.put(67, R.drawable.smiley_67);
    RES_ID_MAP.put(68, R.drawable.smiley_68);
    RES_ID_MAP.put(69, R.drawable.smiley_69);
    RES_ID_MAP.put(70, R.drawable.smiley_70);
    RES_ID_MAP.put(71, R.drawable.smiley_71);
    RES_ID_MAP.put(72, R.drawable.smiley_72);
    RES_ID_MAP.put(73, R.drawable.smiley_73);
    RES_ID_MAP.put(74, R.drawable.smiley_74);
    RES_ID_MAP.put(75, R.drawable.smiley_75);
    RES_ID_MAP.put(76, R.drawable.smiley_76);
    RES_ID_MAP.put(77, R.drawable.smiley_77);
    RES_ID_MAP.put(78, R.drawable.smiley_78);
    RES_ID_MAP.put(79, R.drawable.smiley_79);
    RES_ID_MAP.put(80, R.drawable.smiley_80);
    RES_ID_MAP.put(81, R.drawable.smiley_81);
    RES_ID_MAP.put(82, R.drawable.smiley_82);
    RES_ID_MAP.put(83, R.drawable.smiley_83);
    RES_ID_MAP.put(84, R.drawable.smiley_84);
  }

}
