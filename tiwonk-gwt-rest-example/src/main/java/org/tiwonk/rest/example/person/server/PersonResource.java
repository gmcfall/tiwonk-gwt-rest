/*******************************************************************************
 * Copyright 2012 Gregory McFall
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.tiwonk.rest.example.person.server;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.google.inject.Inject;

@Path("/persons/")
public class PersonResource {
  
  private PersonDao dao;
  
  @Inject
  public PersonResource(PersonDao dao) {
    this.dao = dao;
  }
  
  @GET
  @Path("{guid}")
  public Person get(@PathParam("guid") String guid) {
    return dao.findByGuid(guid);
  }

  @POST
  @Consumes("application/vnd.example.v1.Person+json; charset=UTF-8")
  @Produces("application/vnd.example.v1.PersonRef+json")
  public PersonRef post(Person person, @Context HttpServletRequest request, @Context HttpServletResponse response) {
   
    String guid = UUID.randomUUID().toString();
    person.setGuid(guid);
    StringBuffer buffer = request.getRequestURL();
    buffer.append(guid);
    String location = buffer.toString();
    dao.save(person);
    response.setHeader("Location", location);
    
    PersonRef ref = new PersonRef();
    ref.setUri(location);
    
    return ref;
  }

  @PUT
  @Path("{guid}")
  @Consumes("application/vnd.example.v1.Person+json; charset=UTF-8")
  public void put(
    Person person, 
    @PathParam("guid") String guid, 
    @Context HttpServletRequest request, 
    @Context HttpServletResponse response
  ) {
   
    person.setGuid(guid);
    dao.save(person);
    
  }
}
