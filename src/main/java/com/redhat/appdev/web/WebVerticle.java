package com.redhat.appdev.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.redhat.appdev.data.PostgresVerticle.DB_TEST_ADDR;

@Singleton
public class WebVerticle extends AbstractVerticle {

  private static final int INTERNAL_SERVER_ERROR = 500;

  @Inject
  private Router router;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    router.get("/hello").handler(ctx -> {
      ctx.response().end("Hello World!");
    });

    router.get("/dbtest").handler(ctx -> {
      vertx.eventBus().send(DB_TEST_ADDR, new JsonObject(), reply -> {
        if (reply.failed()) {
          ctx.response().setStatusMessage("SERVER ERROR").setStatusCode(INTERNAL_SERVER_ERROR).end(reply.cause().getLocalizedMessage());
        } else {
          String response = ((JsonArray)(JsonArray)reply.result().body()).encodePrettily();
          ctx.response().end(response);
        }
      });
    });

    vertx.createHttpServer().requestHandler(router).listen(8080);
    startFuture.complete();
  }
}