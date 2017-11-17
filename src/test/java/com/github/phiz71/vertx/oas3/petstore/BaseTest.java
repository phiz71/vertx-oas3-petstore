package com.github.phiz71.vertx.oas3.petstore;

import com.github.phiz71.vertx.oas3.petstore.model.Error;
import com.github.phiz71.vertx.oas3.petstore.model.Pet;
import io.vertx.core.*;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.client.HttpResponse;

public abstract class BaseTest {
  
  private static Vertx vertx;
  protected String fakeMongoDbName = "pets";
  protected Pet petToFind = new Pet(1L, "Rex", "customTag");
  protected Pet anotherPetToFind = new Pet().setId(2L).setName("Foo").setTag("customTag");
  protected Pet yetAnotherPetToFind = new Pet().setId(2L).setName("Bar").setTag("anotherTag");
  protected Pet petToDelete = new Pet().setId(4L).setName("Rex").setTag("customTag");
  protected Error defaultError = new Error().setCode(500).setMessage("Error raised");
  protected ApiClient apiClient;
  
  
  public void before(TestContext context) {
    Async async = context.async();
    vertx = Vertx.vertx(new VertxOptions().setMaxEventLoopExecuteTime(Long.MAX_VALUE));
      vertx.deployVerticle(new MainVerticle(), res -> {
        if (res.succeeded()) {
          apiClient = new ApiClient(vertx, "localhost", 8080);
          async.complete();
        } else {
          context.fail(res.cause());
        }
      });
      initPetStore();
  }
  
  private void initPetStore() {
    PetStoreVerticle.petStoreList.add(petToFind);
    PetStoreVerticle.petStoreList.add(anotherPetToFind);
    PetStoreVerticle.petStoreList.add(yetAnotherPetToFind);
    PetStoreVerticle.petStoreList.add(petToDelete);
  }
  
  protected Handler<AsyncResult<HttpResponse>> testDefaultError(TestContext test, Async async) {
    return (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(500, ar.result().statusCode());
        Error error = new Error(ar.result().bodyAsJsonObject());
        test.assertNotNull(error);
        test.assertEquals(defaultError, error);
      } else {
        test.fail("Request failed");
      }
      async.complete();
    };
  }
  
  public void after(TestContext context) {
    apiClient.close();
    PetStoreVerticle.petStoreList.clear();
    vertx.close(context.asyncAssertSuccess());
  }
  
  
}
