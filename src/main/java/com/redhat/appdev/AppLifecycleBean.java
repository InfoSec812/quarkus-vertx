package com.redhat.appdev;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.redhat.appdev.web.WebVerticle;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.axle.core.Vertx;

@ApplicationScoped
public class AppLifecycleBean {

  @Inject
  private Vertx vertx;

  void onStart(@Observes StartupEvent ev) {
    vertx.deployVerticle(new WebVerticle());
  }

  void onStop(@Observes ShutdownEvent ev) {

  }
}