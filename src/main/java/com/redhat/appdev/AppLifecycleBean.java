package com.redhat.appdev;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.redhat.appdev.data.PostgresVerticle;
import com.redhat.appdev.web.WebVerticle;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.axle.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AppLifecycleBean {

  private static final Logger LOG = LoggerFactory.getLogger(AppLifecycleBean.class);

  @Inject
  private Vertx vertx;

  @Inject
  private PostgresVerticle pgVerticle;

  @Inject
  private WebVerticle webVerticle;

  void onStart(@Observes StartupEvent ev) {
    vertx.deployVerticle(pgVerticle).whenComplete((res, err) -> {
      LOG.warn("PostgreVerticle deployed");
      vertx.deployVerticle(webVerticle);
    });
    vertx.deployVerticle(pgVerticle)
        .thenCompose(res -> vertx.deployVerticle(webVerticle))
        .exceptionally(err -> {
          LOG.error("Error deploying verticles", err);
          return "";
        });
  }

  void onStop(@Observes ShutdownEvent ev) {

  }
}