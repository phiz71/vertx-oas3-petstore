package com.github.phiz71.vertx.oas3.petstore;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * findPets Test
 */
@RunWith(VertxUnitRunner.class)
public class FindPetsTest extends BaseTest {
  
  @Before
  public void before(TestContext context) {
    super.before(context);
  }
  
  @After
  public void after(TestContext context) {
    super.after(context);
  }
  
  @Test
  public void test200WithoutFilterWithoutLimit(TestContext test) {
    List<Object> tags = null;
    Integer limit = null;
    testFindPets(test, tags, limit, 4);
  }
  
  @Test
  public void test200WithTagFilter(TestContext test) {
    List<Object> tags = null;
    Integer limit = null;
    
    tags = new ArrayList<>();
    tags.add("customTag");
    limit = null;
    testFindPets(test, tags, limit, 3);
    
    tags.clear();
    tags.add("anotherTag");
    testFindPets(test, tags, limit, 1);
    
    tags.clear();
    tags.add("customTag");
    tags.add("anotherTag");
    testFindPets(test, tags, limit, 4);
    
    tags.clear();
    tags.add("unexistingTag");
    Async async = test.async();
    apiClient.findPets(tags, limit, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(200, ar.result().statusCode());
        test.assertEquals("No pet found", ar.result().statusMessage());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
  
  @Test
  public void test200WithLimitFilter(TestContext test) {
    List<Object> tags = null;
    Integer limit = null;
    
    limit = 1;
    testFindPets(test, tags, limit, 1);
    
    limit = 2;
    testFindPets(test, tags, limit, 2);
    
    limit = 3;
    testFindPets(test, tags, limit, 3);
  }
  
  @Test
  public void testDefault(TestContext test) {
    Async async = test.async();
    List<Object> tags = null;
    Integer limit = -1;
    apiClient.findPets(tags, limit, testDefaultError(test, async));
  }
  
  
  private void testFindPets(TestContext test, List<Object> tags, Integer limit, int expectedSize) {
    Async async = test.async();
    apiClient.findPets(tags, limit, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(200, ar.result().statusCode());
        JsonArray pets = ar.result().bodyAsJsonArray();
        test.assertNotNull(pets);
        test.assertEquals(expectedSize, pets.size());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
}