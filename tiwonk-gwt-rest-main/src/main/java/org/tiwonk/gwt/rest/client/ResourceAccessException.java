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
package org.tiwonk.gwt.rest.client;

import com.google.gwt.http.client.Response;

/**
 * An exception that signifies that a failure occurred as
 * indicated by the status code returned from an HTTP request.
 * @author gmcfall
 *
 */
public class ResourceAccessException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private Response response;

  public ResourceAccessException(Response response) {
    this.response = response;
  }

  /**
   * Returns the response from the HTTP request.
   */
  public Response getResponse() {
    return response;
  }
  
  
  

}
