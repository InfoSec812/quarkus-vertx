package com.redhat.appdev.web;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

public class WebVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Router router = Router.router(vertx);

    router.get("/hello").handler(ctx -> {
      ctx.response().end("Hello World!");
    });

    vertx.createHttpServer().requestHandler(router).listen(8080);
  }
}