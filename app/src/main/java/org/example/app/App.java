/* Licensed under Apache-2.0 2026. */
package org.example.app;

import static org.example.app.MessageUtils.getMessage;
import static org.example.utilities.StringUtils.join;
import static org.example.utilities.StringUtils.split;

import org.example.list.LinkedList;

public class App {
  public static void main(String[] args) {
    LinkedList tokens;
    tokens = split(getMessage());
    String result = join(tokens);
    System.out.println(result);
  }
}
