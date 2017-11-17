package com.github.phiz71.vertx.oas3.petstore;

import com.github.phiz71.vertx.oas3.petstore.model.Error;
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
 * deletePet Test
 */
@RunWith(VertxUnitRunner.class)
public class DeletePetTest extends BaseTest {
  
  @Before
  public void before(TestContext context) {
    super.before(context);
  }
  
  @After
  public void after(TestContext context) {
    super.after(context);
  }
  
  @Test
  public void test204WithSecurity(TestContext test) {
    Async async = test.async();
    Long id = petToDelete.getLong("id");
    apiClient.deletePet(id, "0123-4567-89AB-CDEF", (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(204, ar.result().statusCode());
        test.assertEquals(3, PetStoreVerticle.petStoreList.size());
        test.assertFalse(PetStoreVerticle.petStoreList.contains(petToDelete));
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
  
  @Test
  public void test204WithoutSecurity(TestContext test) {
    Async async = test.async();
    Long id = 1L;
    apiClient.deletePet(id, null, (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(401, ar.result().statusCode());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
  
  @Test
  public void test204WithBadSecurity(TestContext test) {
    Async async = test.async();
    Long id = 1L;
    apiClient.deletePet(id, "BAD_CREDENTIAL", (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(401, ar.result().statusCode());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
  
  @Test
  public void testDefault(TestContext test) {
    Async async = test.async();
    Long id = -1L;
    apiClient.deletePet(id, "0123-4567-89AB-CDEF", testDefaultError(test, async));
  }
  
  @Test
  public void testDefaultPetNotFound(TestContext test) {
    Async async = test.async();
    Long id = 10L;
    apiClient.deletePet(id, "0123-4567-89AB-CDEF", (AsyncResult<HttpResponse> ar) -> {
      if (ar.succeeded()) {
        test.assertEquals(500, ar.result().statusCode());
        Error error = new Error(ar.result().bodyAsJsonObject());
        test.assertNotNull(error);
        test.assertEquals("pet not found :" + id, error.getMessage());
        test.assertEquals(500, error.getCode());
      } else {
        test.fail("Request failed");
      }
      async.complete();
    });
  }
  
  
}