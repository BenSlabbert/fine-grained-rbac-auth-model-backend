/* Licensed under Apache-2.0 2026. */
package org.example.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessageUtilsTest {
  @Test
  void testGetMessage() {
    assertThat("Hello      World!").isEqualTo(MessageUtils.getMessage());
  }
}
