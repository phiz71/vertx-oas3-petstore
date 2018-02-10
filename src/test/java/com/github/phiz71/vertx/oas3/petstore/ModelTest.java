package com.github.phiz71.vertx.oas3.petstore;

import com.github.phiz71.vertx.oas3.petstore.model.Error;
import com.github.phiz71.vertx.oas3.petstore.model.NewPet;
import com.github.phiz71.vertx.oas3.petstore.model.Pet;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * addPet Test
 */
public class ModelTest extends BaseTest {
  
  @Test
  public void testError() {
    Error errorToTest = new Error().setCode(1).setMessage("foo");
    Assert.assertEquals(1, errorToTest.getCode().intValue());
    Assert.assertEquals("foo", errorToTest.getMessage());
    
    errorToTest = new Error(1, "foo");
    Assert.assertEquals(1, errorToTest.getCode().intValue());
    Assert.assertEquals("foo", errorToTest.getMessage());
    
    errorToTest = new Error(new JsonObject().put("code", 1).put("message", "foo"));
    Assert.assertEquals(1, errorToTest.getCode().intValue());
    Assert.assertEquals("foo", errorToTest.getMessage());
  }
  
  @Test
  public void testNewPet() {
    NewPet newPetToTest = new NewPet().setName("Rex").setTag("myTag");
    Assert.assertEquals("Rex", newPetToTest.getName());
    Assert.assertEquals("myTag", newPetToTest.getTag());
    
    newPetToTest = new NewPet("Rex", "myTag");
    Assert.assertEquals("Rex", newPetToTest.getName());
    Assert.assertEquals("myTag", newPetToTest.getTag());
    
    newPetToTest = new NewPet(new JsonObject().put("name", "Rex").put("tag", "myTag"));
    Assert.assertEquals("Rex", newPetToTest.getName());
    Assert.assertEquals("myTag", newPetToTest.getTag());
    
  }
  
  @Test
  public void testPet() {
    Pet petToTest = new Pet().setId(1L).setName("Rex").setTag("myTag");
    Assert.assertEquals(1L, petToTest.getId().longValue());
    Assert.assertEquals("Rex", petToTest.getName());
    Assert.assertEquals("myTag", petToTest.getTag());
    
    petToTest = new Pet(1L, "Rex", "myTag");
    Assert.assertEquals(1L, petToTest.getId().longValue());
    Assert.assertEquals("Rex", petToTest.getName());
    Assert.assertEquals("myTag", petToTest.getTag());
    
    petToTest = new Pet(new JsonObject().put("id", 1L).put("name", "Rex").put("tag", "myTag"));
    Assert.assertEquals(1L, petToTest.getId().longValue());
    Assert.assertEquals("Rex", petToTest.getName());
    Assert.assertEquals("myTag", petToTest.getTag());
    
    petToTest = new Pet(new NewPet("Rex", "myTag"));
    Assert.assertNull(petToTest.getId());
    Assert.assertEquals("Rex", petToTest.getName());
    Assert.assertEquals("myTag", petToTest.getTag());
  }
}