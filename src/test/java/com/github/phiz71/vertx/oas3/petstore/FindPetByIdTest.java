package com.github.phiz71.vertx.oas3.petstore;

import com.github.phiz71.vertx.oas3.petstore.model.Error;
import com.github.phiz71.vertx.oas3.petstore.model.Pet;
import com.github.phiz71.vertx.oas3.petstore.util.BaseTest;
import io.vertx.core.AsyncResult;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * find pet by id Test
 */
@RunWith(VertxUnitRunner.class)
public class FindPetByIdTest extends BaseTest {
  
  @Before
  public void before(TestContext context) {
    super.before(context);
  }
  
  @After
  public void after(TestContext context) {
    super.after(context);
  }
  
  @Test
  public void test200(TestContext test) {
    Async async = test.async();
    Long id = petToFind.getLong("id");
    
    apiClient.findPetById(id, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(200, ar.result().statusCode());
        Pet foundPet = new Pet(ar.result().bodyAsJsonObject());
        test.assertNotNull(foundPet);
        test.assertEquals(petToFind, foundPet);
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
  
  @Test
  public void testDefaultTooManyResult(TestContext test) {
    Async async = test.async();
    Long id = anotherPetToFind.getLong("id");
    
    apiClient.findPetById(id, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(500, ar.result().statusCode());
        Error error = new Error(ar.result().bodyAsJsonObject());
        test.assertNotNull(error);
        test.assertEquals(500, error.getCode());
        test.assertEquals("More than one item with this id : " + id, error.getMessage());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
    
  }
  
  @Test
  public void testDefaultNoPetFound(TestContext test) {
    Async async = test.async();
    Long id = 10L;
    
    apiClient.findPetById(id, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(404, ar.result().statusCode());
        Error error = new Error(ar.result().bodyAsJsonObject());
        test.assertNotNull(error);
        test.assertEquals(404, error.getCode());
        test.assertEquals("No pet found with the id : " + id, error.getMessage());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
    
  }
  
  @Test
  public void testDefaultError(TestContext test) {
    Async async = test.async();
    Long id = -1L;
    
    apiClient.findPetById(id, testDefaultError(test, async));
    
  }
}