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
package org.tiwonk.gwt.rest.generator;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;

import org.tiwonk.gwt.rest.client.AbstractClientResource;
import org.tiwonk.gwt.rest.client.ResourceAccessException;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class ClientResourceGenerator extends Generator {
  

  @Override
  public String generate(TreeLogger logger, GeneratorContext context,  String typeName) 
  throws UnableToCompleteException {
   
    TypeOracle typeOracle = context.getTypeOracle();
    JClassType clientInterfaceType = typeOracle.findType(typeName);
    
    String apiPackage = clientInterfaceType.getPackage().getName();
    String implPackage = apiPackage + ".impl";
    String implClassSimpleName = clientInterfaceType.getSimpleSourceName() + "Impl";
    
    
    GeneratorInfo info = new GeneratorInfo(logger, clientInterfaceType);
    
    ImportAnalyzer importAnalyzer = new ImportAnalyzer(implPackage);
    importAnalyzer.analyze(clientInterfaceType);
    
    ClassSourceFileComposerFactory composerFactory =
        new ClassSourceFileComposerFactory(implPackage, implClassSimpleName);

    importAnalyzer.add(JSONValue.class.getName());
    importAnalyzer.add(JSONObject.class.getName());
    importAnalyzer.add(JSONParser.class.getName());
    importAnalyzer.add(Request.class.getName());
    importAnalyzer.add(Response.class.getName());
    importAnalyzer.add(RequestException.class.getName());
    importAnalyzer.add(RequestCallback.class.getName());
    importAnalyzer.add(JavaScriptObject.class.getName());
    importAnalyzer.add(AbstractClientResource.class.getName());
    importAnalyzer.add(ResourceAccessException.class.getName());
    importAnalyzer.add(RequestBuilder.class.getName());
    importAnalyzer.addImports(composerFactory);
    
    composerFactory.setSuperclass(AbstractClientResource.class.getSimpleName());
    composerFactory.addImplementedInterface(clientInterfaceType.getSimpleSourceName());
    
    PrintWriter printWriter = context.tryCreate(logger, implPackage, implClassSimpleName);
    if (printWriter != null) {
      SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
      
      addMethods(info, sourceWriter, clientInterfaceType);
      
      sourceWriter.commit(logger);
    } 
    
    
    return implPackage + "." + implClassSimpleName;
  }


  private void addMethods(GeneratorInfo info, SourceWriter sourceWriter, JClassType interfaceType) throws UnableToCompleteException {
    
    JMethod[] methodArray = interfaceType.getMethods();
    for (int i=0; i<methodArray.length; i++) {
      addMethod(info, sourceWriter, interfaceType, methodArray[i]);
    }
    
  }

  private void addMethod(GeneratorInfo info, SourceWriter writer, JClassType interfaceType, JMethod method) throws UnableToCompleteException {
    
    String returnType = method.getReturnType().getSimpleSourceName();
    String methodName = method.getName();
    writer.print("public " + returnType + " " + methodName + "(");
    analyzeHttpMethod(info, method);
    addParameters(info, writer, method.getParameters());
    writer.println(") {");
    addMethodBody(info, writer, method);
    writer.println("}");
    
  }

  private void addMethodBody(GeneratorInfo info, SourceWriter writer, JMethod method) 
  throws UnableToCompleteException {
    
    if (info.httpMethod == RequestBuilder.GET) {
      buildGetMethod(info, writer, method);
    } else if (info.httpMethod == RequestBuilder.POST) {
      buildPostMethod(info, writer, method);
    } else if (info.httpMethod == RequestBuilder.PUT) {
      buildPutMethod(info, writer, method);
    }
    
  }

  private void buildPostMethod(GeneratorInfo info, SourceWriter writer, JMethod method) 
  throws UnableToCompleteException {
    

    if (
        info.asyncCallbackParam == null || 
        info.resourceParam == null || 
        info.asyncType==null
    ) {
      
      info.logger.log(TreeLogger.ERROR, 
          "A POST method must have parameters of the form (T resource, AsyncCallback<R> callback)\n" +
          "where T is the type of object posted to the server and R is the return type.\n" +
          "If R is a string, then the callback will receive the value of the 'Location' header from the response.\n" +
          "Otherwise, the callback will receive an object of type R parsed from the response text.\n" +
          "The return type R may be Void, in which case the callback receives a null value.");
      
      throw new UnableToCompleteException();
    }
    
    if ("java.lang.String".equals(info.asyncType.getQualifiedSourceName())) {
      saveMethodReturnsLocation(info, writer, method);
      
    } else if ("java.lang.Void".equals(info.asyncType.getQualifiedSourceName())) {
      saveMethodReturnsVoid(info, writer, method);
      
    } else {
      saveMethodReturnsEntity(info, writer, method);
    }
    
  }
  
  private void buildPutMethod(GeneratorInfo info, SourceWriter writer, JMethod method) 
      throws UnableToCompleteException {
        

        if (
            info.asyncCallbackParam == null || 
            info.resourceParam == null || 
            info.asyncType==null
        ) {
          
          info.logger.log(TreeLogger.ERROR, 
              "A PUT method must have parameters of the form (T resource, AsyncCallback<R> callback)\n" +
              "where T is the type of object posted to the server and R is the return type.\n" +
              "The return type R may be Void, in which case the callback receives a null value." +
              "Otherwise, the callback will receive an object of type R parsed from the response text.\n" +
              "The return type R may not be a String (unlike a POST method)");
          
          throw new UnableToCompleteException();
        }
        
       if ("java.lang.Void".equals(info.asyncType.getQualifiedSourceName())) {
          saveMethodReturnsVoid(info, writer, method);
          
        } else {
          saveMethodReturnsEntity(info, writer, method);
        }
  }


  private void saveMethodReturnsLocation(GeneratorInfo info, SourceWriter writer, JMethod method) throws UnableToCompleteException {
   
    String callbackParam = info.asyncCallbackParam.getName();
    String resourceParam = info.resourceParam.getName();
    String methodName = info.httpMethod.toString();
        
    // RequestBuilder request = new RequestBuilder(RequestBuilder.POST, getResourceURL());
    writer.println();
    writer.indent();
    writer.println("RequestBuilder request = new RequestBuilder(RequestBuilder." + methodName + ", getResourceURL());");
    
    // request.setHeader("Content-Type", "application/vnd.example.v1.person");
    
    String consumes = info.consumes(method);
    if (consumes != null) {
      writer.print("request.setHeader(\"Content-Type\", \"");
      writer.print(consumes);
      writer.println("\");");
    }
    
    String produces = info.produces(method);
    if (produces != null) {
      writer.print("request.setHeader(\"Accept\", \"");
      writer.print(produces);
      writer.println("\");");
    }
    
    
    // JSONObject json = new JSONObject(person);
    // String payload = json.toString();
    writer.println();
    writer.print("JSONObject json = new JSONObject(");
    writer.print(resourceParam);
    writer.println(");");
    writer.println("String payload = json.toString();");

//    try {
//     request.sendRequest(payload, new RequestCallback() {
    writer.println();
    writer.println("try {");
    writer.indent();
    writer.println("request.sendRequest(payload, new RequestCallback() {");
    writer.indent();
    
//    @Override
//    public void onResponseReceived(Request request, Response response) {
//      
//      if (
//        response.getStatusCode() != Response.SC_CREATED &&
//        response.getStatusCode() != Response.SC_OK
//      ) {
//        onError(request, new ResourceAccessException(response));
//        return;
//      }
//      
//      String location = response.getHeader("Location");
//      callback.onSuccess(location);
//      
//    }
    writer.println();
    writer.println("@Override");
    writer.println("public void onResponseReceived(Request request, Response response) {");
    writer.indent();
    writer.println("if (");
    writer.println("  response.getStatusCode() != Response.SC_CREATED &&");
    writer.println("  response.getStatusCode() != Response.SC_OK");
    writer.println(") {");
    writer.println("  onError(request, new ResourceAccessException(response));");
    writer.println("  return;");
    writer.println("}");
    writer.println();
    writer.println("String location = response.getHeader(\"Location\");");
    writer.print(callbackParam);
    writer.println(".onSuccess(location);");
    writer.outdent();
    writer.println("}");

//    @Override
//    public void onError(Request request, Throwable e) {
//      callback.onFailure(e);
//    }
//  });
    writer.println();
    writer.println("@Override");
    writer.println("public void onError(Request request, Throwable e) {");
    writer.print("  ");
    writer.print(callbackParam);
    writer.println(".onFailure(e);");
    writer.println("}");
    writer.outdent();
    writer.println("});");
    writer.outdent();

//  } catch (RequestException e) {
//    callback.onFailure(e);
//  }
    writer.println();
    writer.println("} catch (RequestException e) {");
    writer.print("  ");
    writer.print(callbackParam);
    writer.println(".onFailure(e);");
    writer.println("}");
    writer.outdent();
    
    
  }

  private void saveMethodReturnsEntity(GeneratorInfo info, SourceWriter writer, JMethod method) throws UnableToCompleteException {
    
    String callbackParam = info.asyncCallbackParam.getName();
    String resourceParam = info.resourceParam.getName();
    String callbackType = info.asyncType.getSimpleSourceName();
    String methodName = info.httpMethod.toString();
    
    
    // RequestBuilder request = new RequestBuilder(RequestBuilder.POST, getResourceURL());
    writer.println();
    writer.indent();
    writer.println("RequestBuilder request = new RequestBuilder(RequestBuilder." + methodName + ", getResourceURL());");
    
    // request.setHeader("Content-Type", "application/vnd.example.v1.person");
    String consumes = info.consumes(method);
    if (consumes != null) {
      writer.print("request.setHeader(\"Content-Type\", \"");
      writer.print(consumes);
      writer.println("\");");
    }
    
    String produces = info.produces(method);
    if (produces != null) {
      writer.print("request.setHeader(\"Accept\", \"");
      writer.print(produces);
      writer.println("\");");
    }
    
    // JSONObject json = new JSONObject(person);
    // String payload = json.toString();
    writer.println();
    writer.print("JSONObject json = new JSONObject(");
    writer.print(resourceParam);
    writer.println(");");
    writer.println("String payload = json.toString();");

//    try {
//     request.sendRequest(payload, new RequestCallback() {
    writer.println();
    writer.println("try {");
    writer.indent();
    writer.println("request.sendRequest(payload, new RequestCallback() {");
    writer.indent();
    
//    @Override
//    public void onResponseReceived(Request request, Response response) {
//      
//      if (
//        response.getStatusCode() != Response.SC_CREATED &&
//        response.getStatusCode() != Response.SC_OK
//      ) {
//        onError(request, new ResourceAccessException(response));
//        return;
//      }
//      JSONValue value = JSONParser.parseStrict(response.getText());
//      PersonRef entity = value.isObject().getJavaScriptObject().cast();
//      callback.onSuccess(entity);
//    }
    writer.println();
    writer.println("@Override");
    writer.println("public void onResponseReceived(Request request, Response response) {");
    writer.indent();
    writer.println("if (");
    writer.println("  response.getStatusCode() != Response.SC_CREATED &&");
    writer.println("  response.getStatusCode() != Response.SC_OK");
    writer.println(") {");
    writer.println("  onError(request, new ResourceAccessException(response));");
    writer.println("  return;");
    writer.println("}");
    writer.println();
    writer.println("JSONValue value = JSONParser.parseStrict(response.getText());");
    writer.print(callbackType);
    writer.println(" entity = value.isObject().getJavaScriptObject().cast();");
    writer.print(callbackParam);
    writer.println(".onSuccess(entity);");
    writer.outdent();
    writer.println("}");

//    @Override
//    public void onError(Request request, Throwable e) {
//      callback.onFailure(e);
//    }
//  });
    writer.println();
    writer.println("@Override");
    writer.println("public void onError(Request request, Throwable e) {");
    writer.print("  ");
    writer.print(callbackParam);
    writer.println(".onFailure(e);");
    writer.println("}");
    writer.outdent();
    writer.println("});");
    writer.outdent();

//  } catch (RequestException e) {
//    callback.onFailure(e);
//  }
    writer.println();
    writer.println("} catch (RequestException e) {");
    writer.print("  ");
    writer.print(callbackParam);
    writer.println(".onFailure(e);");
    writer.println("}");
    writer.outdent();
    
  }

  private void saveMethodReturnsVoid(GeneratorInfo info, SourceWriter writer, JMethod method) throws UnableToCompleteException {
    
    String callbackParam = info.asyncCallbackParam.getName();
    String resourceParam = info.resourceParam.getName();
    String methodName = info.httpMethod.toString();
    
    
    // RequestBuilder request = new RequestBuilder(RequestBuilder.POST, getResourceURL());
    writer.println();
    writer.indent();
    writer.println("RequestBuilder request = new RequestBuilder(RequestBuilder." + methodName + ", getResourceURL());");
    writer.indent();
    // request.setHeader("Content-Type", "application/vnd.example.v1.person");
    String consumes = info.consumes(method);
    if (consumes != null) {
      writer.print("request.setHeader(\"Content-Type\", \"");
      writer.print(consumes);
      writer.println("\");");
    }
    
    String produces = info.produces(method);
    if (produces != null) {
      writer.print("request.setHeader(\"Accept\", \"");
      writer.print(produces);
      writer.println("\");");
    }
    
    // JSONObject json = new JSONObject(person);
    // String payload = json.toString();
    writer.println();
    writer.print("JSONObject json = new JSONObject(");
    writer.print(resourceParam);
    writer.println(");");
    writer.println("String payload = json.toString();");

//    try {
//     request.sendRequest(payload, new RequestCallback() {
    writer.println();
    writer.println("try {");
    writer.indent();
    writer.println("request.sendRequest(payload, new RequestCallback() {");
    
//    @Override
//    public void onResponseReceived(Request request, Response response) {
//      
//      if (
//        response.getStatusCode() != Response.SC_CREATED &&
//        response.getStatusCode() != Response.SC_OK
//      ) {
//        onError(request, new ResourceAccessException(response));
//        return;
//      }
//      callback.onSuccess(null);
//    }
    writer.println();
    writer.println("@Override");
    writer.println("public void onResponseReceived(Request request, Response response) {");
    writer.indent();
    writer.println("if (");
    writer.println("  response.getStatusCode() != Response.SC_CREATED &&");
    writer.println("  response.getStatusCode() != Response.SC_OK");
    writer.println(") {");
    writer.println("  onError(request, new ResourceAccessException(response));");
    writer.println("  return;");
    writer.println("}");
    writer.println();
    writer.print(callbackParam);
    writer.println(".onSuccess(null);");
    writer.outdent();
    writer.println("}");

//    @Override
//    public void onError(Request request, Throwable e) {
//      callback.onFailure(e);
//    }
//  });
    writer.println();
    writer.println("@Override");
    writer.println("public void onError(Request request, Throwable e) {");
    writer.print("  ");
    writer.print(callbackParam);
    writer.println(".onFailure(e);");
    writer.println("}");
    writer.outdent();
    writer.println("});");
    writer.outdent();

//  } catch (RequestException e) {
//    callback.onFailure(e);
//  }
    writer.println();
    writer.println("} catch (RequestException e) {");
    writer.print("  ");
    writer.print(callbackParam);
    writer.println(".onFailure(e);");
    writer.println("}");
    writer.outdent();
    
    
  }


  private void buildGetMethod(GeneratorInfo info, SourceWriter writer, JMethod method) {

    if (info.asyncType == null) {
      info.logger.log(TreeLogger.ERROR, 
          "GET method must have a single parameter of type AsyncCallback<T> where T is the resource type.");
      return;
    }
    
    String callbackParam = info.asyncCallbackParam.getName();
    
    String resourceSimpleName = info.asyncType.getSimpleSourceName();
    String contentType = info.produces(method);
    
    writer.indent();
    // RequestBuilder request = new RequestBuilder(RequestBuilder.GET, getResourceURL());
    writer.println("RequestBuilder request = new RequestBuilder(RequestBuilder.GET, getResourceURL());");
    
    // request.setHeader("Content-Type", "...");
    if (contentType != null) {
      writer.print("request.setHeader(\"Accept\", \"");
      writer.print(contentType);
      writer.println("\");");
    }
    
    /*
    request.setCallback(new RequestCallback() {
      
      @Override
      public void onResponseReceived(Request request, Response response) {
        if (response.getStatusCode() != Response.SC_OK) {
          onError(request, new ResourceAccessException(response));
          return;
        }
        JSONValue value = JSONParser.parseStrict(response.getText());
        Person entity = toPerson(value.isObject().getJavaScriptObject());
        callback.onSuccess(entity);
      }      
   */
    
    writer.println("request.setCallback(new RequestCallback() {");
    writer.indent();
    writer.println();
    writer.println("@Override");
    writer.println("public void onResponseReceived(Request request, Response response) {");
    writer.indent();
    writer.println("if (response.getStatusCode() != Response.SC_OK) {");
    writer.indent();
    writer.println("onError(request, new ResourceAccessException(response));");
    writer.println("return;");
    writer.outdent();
    writer.println("}");
    writer.println("JSONValue value = JSONParser.parseStrict(response.getText());");
    writer.print(resourceSimpleName);
    writer.print(" entity = ");
    writer.println("value.isObject().getJavaScriptObject().cast();");
    writer.print(callbackParam);
    writer.println(".onSuccess(entity);");
    
    writer.outdent();
    writer.println("}");
    
    /*/
      @Override
      public void onError(Request request, Throwable exception) {
        callback.onFailure(exception);
      }
    });
     */
    writer.println();
    writer.println("@Override");
    writer.println("public void onError(Request request, Throwable exception) {");
    writer.indent();
    writer.print(callbackParam);
    writer.println(".onFailure(exception);");
    writer.outdent();
    writer.println("}");
    writer.outdent();
    writer.println("});");
    
    /*
    try {
      request.send();
    } catch (RequestException e) {
      callback.onFailure(e);
    }
     */
    writer.println("try {");
    writer.println("  request.send();");
    writer.println("} catch (RequestException e) {");
    writer.print("  ");
    writer.print(callbackParam);
    writer.println(".onFailure(e);");
    writer.println("}");
    writer.outdent();
    
  }


  private void analyzeHttpMethod(GeneratorInfo info, JMethod method) {
    
    Annotation[] annotations = method.getAnnotations();
    for (int i=0; i<annotations.length; i++) {
      Annotation note = annotations[i];
      if (note.annotationType() == GET.class) {
        info.httpMethod = RequestBuilder.GET;
        return;
      } else if (note.annotationType() == POST.class) {
        info.httpMethod = RequestBuilder.POST;
        return;
        
      } else if (note.annotationType() == PUT.class) {
        info.httpMethod = RequestBuilder.PUT;
        return;
      }
    }
    
  }

  private void addParameters(GeneratorInfo info, SourceWriter writer, JParameter[] parameters) throws UnableToCompleteException {
    info.asyncCallbackParam = null;
    info.asyncType = null;
    info.resourceParam = null;
    info.resourceType = null;
    
    for (int i=0; i<parameters.length; i++) {
      if (i > 0) {
        writer.print(", ");
      }
      JParameter param = parameters[i];
      String paramType = param.getType().getSimpleSourceName();
      String paramName = param.getName();
      
      JParameterizedType generic = param.getType().isParameterized();

      if (AsyncCallback.class.getName().equals(param.getType().getQualifiedSourceName())) {
        info.asyncCallbackParam = param;
        info.asyncType = generic.getTypeArgs()[0];
        writer.print("final ");
      } else {
        info.resourceParam = param;
        info.resourceType = param.getType().isClassOrInterface();
      }
      writer.print(paramType);
      if (generic != null) {
        addParameterizedType(info, writer, generic);
      }
      writer.print(" ");
      writer.print(paramName);
    }
    
  }

  private void addParameterizedType(GeneratorInfo info, SourceWriter writer, JParameterizedType generic) {
    
    JClassType param[] = generic.getTypeArgs();
    writer.print("<");
    for (int i=0; i<param.length; i++) {
      if (i>0) {
        writer.print(", ");
      }
      writer.print(param[i].getSimpleSourceName());
    }
    
    writer.print(">");
    
  }

 
  static class GeneratorInfo {
    TreeLogger logger;
    JParameter asyncCallbackParam;
    JClassType asyncType;
    JClassType clientType;
    
    JParameter resourceParam;
    JClassType resourceType;
    
    RequestBuilder.Method httpMethod;
    
    public GeneratorInfo(TreeLogger logger, JClassType clientInterfaceType) {
      this.logger = logger;
      clientType = clientInterfaceType;
    }
    
    String produces() {
      if (clientType == null) return null;
      Produces note = clientType.getAnnotation(Produces.class);
      return note == null ? null : note.value()[0];
    }
   
    String produces(JMethod method) {
      Produces note = method.getAnnotation(Produces.class);
      return note==null ? produces() : note.value()[0];
    }
    
    String consumes() {
      if (clientType == null) return null;
      Consumes note = clientType.getAnnotation(Consumes.class);
      return note==null ? null : note.value()[0];
    }

    
    String consumes(JMethod method) {
      Consumes note = method.getAnnotation(Consumes.class);
      return note==null ? consumes() : note.value()[0];
    }
    
    
  }

}
