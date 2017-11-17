package com.github.phiz71.vertx.oas3.petstore;

import com.github.phiz71.vertx.oas3.petstore.model.NewPet;
import com.github.phiz71.vertx.oas3.petstore.util.BaseTest;
import com.github.phiz71.vertx.oas3.petstore.util.TestMongoClientVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * addPet Test
 */
@RunWith(VertxUnitRunner.class)
public class AddPetTest extends BaseTest {
  
  @Before
  public void before(TestContext context) {
    super.before(context);
  }
  
  @After
  public void after(TestContext context) {
    super.after(context);
  }
  
  @Test
  public void test200WithEmptyBody(TestContext test) {
    Async async = test.async();
    apiClient.addPetWithEmptyBody((AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(400, ar.result().statusCode());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
  
  @Test
  public void test200WithBody(TestContext test) {
    Async async = test.async(2);
    NewPet body = new NewPet("Rex", null);
    apiClient.addPetWithJson(body, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(200, ar.result().statusCode());
        List<JsonObject> result = TestMongoClientVerticle.db.get(fakeMongoDbName);
        test.assertEquals(5, result.size());
        JsonObject bodyPet = result.get(0);
        test.assertEquals("Rex", bodyPet.getString("name"));
        Object id = bodyPet.getValue("id");
        test.assertNotNull(id);
        test.assertTrue(id instanceof Long);
        async.countDown();
        
      } else {
        test.fail("Request failed");
      }
      async.countDown();
    });
  }
  
  @Test
  public void testDefault(TestContext test) {
    Async async = test.async();
    JsonObject body = new JsonObject();
    body.put("name", "ERROR");
    apiClient.addPetWithJson(body, testDefaultError(test, async));
  }
  
  
}