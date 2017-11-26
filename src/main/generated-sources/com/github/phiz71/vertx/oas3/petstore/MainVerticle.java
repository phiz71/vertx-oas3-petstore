package com.github.phiz71.vertx.oas3.petstore;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

public class MainVerticle extends AbstractVerticle {
  
  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
  private HttpServer server;
  
  @Override
  public void start(Future future) {
    
    ConfigStoreOptions fileStore = new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "my-config.json"));
    ConfigStoreOptions verticleConfigStore = new ConfigStoreOptions().setType("json").setConfig(vertx.getOrCreateContext().config());
    ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(fileStore).addStore(verticleConfigStore));
    retriever.getConfig(ar -> {
      if (ar.failed()) {
        future.fail(ar.cause());
      } else {
        JsonObject config = ar.result();
        OpenAPI3RouterFactory.createRouterFactoryFromURL(this.vertx, config.getString("openapi.url"), openAPI3RouterFactoryAsyncResult -> {
          if (openAPI3RouterFactoryAsyncResult.succeeded()) {
            OpenAPI3RouterFactory routerFactory = openAPI3RouterFactoryAsyncResult.result();
            
            // Enable automatic response when ValidationException is thrown
            routerFactory.enableValidationFailureHandler(true);
            
            // Add routes handlers
            configHandlers(future, config, routerFactory);
            
            // Add security handlers
            configSecurityHandlers(future, config, routerFactory);
            
            //deploy util Verticles
            deployUtilVerticles(future, config);
            
            // Generate the router
            Router router = routerFactory.getRouter();
            server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"));
            server.requestHandler(router::accept).listen();
            future.complete();
          } else {
            // Something went wrong during router factory initialization
            future.fail(openAPI3RouterFactoryAsyncResult.cause());
          }
        });
      }
    });
  }
  
  private void deployUtilVerticles(Future future, JsonObject config) {
    config.getJsonArray("verticles").forEach(verticle ->
      vertx.deployVerticle(verticle.toString(), result -> {
        if (result.succeeded())
          logger.info(verticle.toString() + " has been deployed");
        else
          future.fail(result.cause());
      })
    );
  }
  
  @SuppressWarnings("unchecked")
  private void configSecurityHandlers(Future future, JsonObject config, OpenAPI3RouterFactory routerFactory) {
    config.getJsonObject("securityHandlers").forEach(securityHandler -> {
      try {
        Class<Handler<RoutingContext>> securityHandlerImpl = (Class<Handler<RoutingContext>>) Class.forName(securityHandler.getValue().toString());
        routerFactory.addSecurityHandler(securityHandler.getKey(), securityHandlerImpl.newInstance());
      } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
        future.fail(e);
      }
    });
  }
  
  @SuppressWarnings("unchecked")
  private void configHandlers(Future future, JsonObject config, OpenAPI3RouterFactory routerFactory) {
    config.getJsonObject("handlers").forEach(handler -> {
      try {
        Class<Handler<RoutingContext>> handlerImpl = (Class<Handler<RoutingContext>>) Class.forName(handler.getValue().toString());
        routerFactory.addHandlerByOperationId(handler.getKey(), handlerImpl.newInstance());
      } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
        future.fail(e);
      }
    });
  }
  
  @Override
  public void stop() {
    this.server.close();
  }
  
}
