/* Licensed under Apache-2.0 2026. */
package org.example.utilities;

import org.example.list.LinkedList;

class JoinUtils {
  public static String join(LinkedList source) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < source.size(); ++i) {
      if (result.length() > 0) {
        result.append(" ");
      }
      result.append(source.get(i));
    }

    return result.toString();
  }
}
