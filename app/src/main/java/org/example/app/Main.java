/* Licensed under Apache-2.0 2026. */
package org.example.app;

import github.benslabbert.vdw.codegen.launcher.CustomApplicationHooks;
import io.vertx.launcher.application.VertxApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends VertxApplication {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    log.info("Starting");
    int code = new Main(args).launch();
    log.info("launch successful ? {}", 0 == code);
  }

  private Main(String[] args) {
    super(args, new CustomApplicationHooks());
  }
}
