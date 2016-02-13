/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skyfire.hipda.lib.html;

import java.util.HashMap;
import java.util.Locale;

/**
 * The Color class defines methods for creating and converting color ints.
 * Colors are represented as packed ints, made up of 4 bytes: alpha, red,
 * green, blue. The values are unpremultiplied, meaning any transparency is
 * stored solely in the alpha component, and not in the color components. The
 * components are stored as follows (alpha << 24) | (red << 16) |
 * (green << 8) | blue. Each component ranges between 0..255 with 0
 * meaning no contribution for that component, and 255 meaning 100%
 * contribution. Thus opaque-black would be 0xFF000000 (100% opaque but
 * no contributions from red, green, or blue), and opaque-white would be
 * 0xFFFFFFFF
 */
public class Color {
  public static final int BLACK = 0xFF000000;
  public static final int DKGRAY = 0xFF444444;
  public static final int GRAY = 0xFF888888;
  public static final int LTGRAY = 0xFFCCCCCC;
  public static final int WHITE = 0xFFFFFFFF;
  public static final int RED = 0xFFFF0000;
  public static final int GREEN = 0xFF00FF00;
  public static final int BLUE = 0xFF0000FF;
  public static final int YELLOW = 0xFFFFFF00;
  public static final int CYAN = 0xFF00FFFF;
  public static final int MAGENTA = 0xFFFF00FF;
  public static final int TRANSPARENT = 0;

  /**
   * Converts an HTML color (named or numeric) to an integer RGB value.
   *
   * @param color Non-null color string.
   * @return A color value, or {@code -1} if the color string could not be interpreted.
   * @hide
   */
  public static int getHtmlColor(String color) {
    Integer i = sColorNameMap.get(color.toLowerCase(Locale.ROOT));
    if (i != null) {
      return i;
    } else {
      try {
        return XmlUtils.convertValueToInt(color, -1);
      } catch (NumberFormatException nfe) {
        return -1;
      }
    }
  }

  private static final HashMap<String, Integer> sColorNameMap;

  static {
    sColorNameMap = new HashMap<String, Integer>();
    sColorNameMap.put("black", BLACK);
    sColorNameMap.put("darkgray", DKGRAY);
    sColorNameMap.put("gray", GRAY);
    sColorNameMap.put("lightgray", LTGRAY);
    sColorNameMap.put("white", WHITE);
    sColorNameMap.put("red", RED);
    sColorNameMap.put("green", GREEN);
    sColorNameMap.put("blue", BLUE);
    sColorNameMap.put("yellow", YELLOW);
    sColorNameMap.put("cyan", CYAN);
    sColorNameMap.put("magenta", MAGENTA);
    sColorNameMap.put("aqua", 0xFF00FFFF);
    sColorNameMap.put("fuchsia", 0xFFFF00FF);
    sColorNameMap.put("darkgrey", DKGRAY);
    sColorNameMap.put("grey", GRAY);
    sColorNameMap.put("lightgrey", LTGRAY);
    sColorNameMap.put("lime", 0xFF00FF00);
    sColorNameMap.put("maroon", 0xFF800000);
    sColorNameMap.put("navy", 0xFF000080);
    sColorNameMap.put("olive", 0xFF808000);
    sColorNameMap.put("purple", 0xFF800080);
    sColorNameMap.put("silver", 0xFFC0C0C0);
    sColorNameMap.put("teal", 0xFF008080);

  }

}
