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
package org.tiwonk.rest.example.person.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;

import org.tiwonk.gwt.rest.client.ClientResource;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PersonClientResource extends ClientResource {
//  
//  @POST
//  @Consumes("application/vnd.example.v1.Person+json")
//  void create1(Person person, AsyncCallback<String> callback);
  
  @POST
  @Consumes("application/vnd.example.v1.Person+json")
  @Produces("application/vnd.example.v1.PersonRef+json")
  void create(Person person, AsyncCallback<Person> callback);
  
//  @POST
//  @Consumes("application/vnd.example.v1.Person+json")
//  void create3(Person person, AsyncCallback<Void> callback);
  
  @GET
  @Produces("application/vnd.example.v1.Person+json")
  void retrieve(AsyncCallback<Person> callback);
  
  @PUT
  @Consumes("application/vnd.example.v1.Person+json")
  void update(Person person, AsyncCallback<Void> callback);
  
}
