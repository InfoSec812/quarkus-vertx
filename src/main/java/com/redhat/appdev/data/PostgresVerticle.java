package com.redhat.appdev.data;

import io.reactiverse.axle.pgclient.PgClient;
import io.reactiverse.axle.pgclient.PgPool;
import io.reactiverse.axle.pgclient.PgRowSet;
import io.reactiverse.axle.pgclient.Row;
import io.reactiverse.pgclient.PgPoolOptions;
import io.vertx.axle.core.Vertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.stream.Stream;

@Singleton
public class PostgresVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(PostgresVerticle.class);
  public static final String DB_TEST_ADDR = "db.test";

  private PgPool client;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    LOG.warn("Starting PostgresVerticle");
    PgPoolOptions options = new PgPoolOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("quarkus")
      .setUser("quarkus")
      .setPassword("quarkus")
      .setMaxSize(10);

    LOG.warn("Creating PostgreSQL client");
    try {
      client = PgClient.pool(Vertx.newInstance(vertx), options);
    } catch (Throwable t) {
      LOG.error("Caught Exception", t);
    }

    LOG.warn("Setting up eventbus listener");
    vertx.eventBus().localConsumer(DB_TEST_ADDR, msg -> {
      client.query("SELECT * FROM people").thenAccept(res -> {
        PgRowSet rows = res.value();
        JsonArray rowList = new JsonArray();
        for (Row row : rows) {
          rowList.add(new JsonObject()
                            .put("given_name", row.getString("given_name"))
                            .put("family_name", row.getString("family_name")));
        }
        msg.reply(rowList);
      }).exceptionally(throwable -> {
        LOG.error("Error executing query", throwable);
        msg.fail(1, throwable.getLocalizedMessage());
        return null;
      });
    });

    LOG.warn("PostgresVerticle loaded");
    startFuture.complete();
  }
}