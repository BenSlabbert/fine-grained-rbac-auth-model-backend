/* Licensed under Apache-2.0 2026. */
package org.example.utilities;

import org.example.list.LinkedList;

public class StringUtils {
  public static String join(LinkedList source) {
    return JoinUtils.join(source);
  }

  public static LinkedList split(String source) {
    return SplitUtils.split(source);
  }
}
