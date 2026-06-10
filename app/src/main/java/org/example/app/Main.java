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
    long start = System.currentTimeMillis();
    int code = new Main(args).launch();
    long time = System.currentTimeMillis() - start;
    log.info("launch successful ? {} time {}ms", 0 == code, time);
  }

  private Main(String[] args) {
    super(args, new CustomApplicationHooks());
  }
}
