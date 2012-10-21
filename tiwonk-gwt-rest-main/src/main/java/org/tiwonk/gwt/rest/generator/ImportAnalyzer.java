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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

public class ImportAnalyzer {
  private String packageName;
  private Set<String> importSet;
  
  
  public ImportAnalyzer(String packageName) {
    this.packageName = packageName;
    importSet = new HashSet<String>();
  }

  public void addImports(ClassSourceFileComposerFactory factory) {
    importSet.remove(packageName);
    importSet.remove("void");
    List<String> list = new ArrayList<String>(importSet);
    Collections.sort(list);
    
    for (String typeName : list) {
      factory.addImport(typeName);
    }
  }
 
  public void analyze(JClassType type) {
    add(type.getQualifiedSourceName());
    addImplementedInterfaces(type);
    addMethods(type);
  }

  private void addMethods(JClassType type) {
    JMethod[] methods = type.getMethods();
    if (methods == null) {
      return;
    }
    for (int i=0; i<methods.length; i++) {
      JMethod method = methods[i];
      String returnType = method.getReturnType().getQualifiedSourceName();
      add(returnType);
      importParameters(method.getParameters());
    }
    
  }
  
  public void add(String typeName) {
    if (!typeName.startsWith("java.lang.") && !"void".equals(typeName)) {
      importSet.add(typeName);
    }
  }

  private void importParameters(JParameter[] parameters) {
    if (parameters == null) return;
    
    for (int i=0; i<parameters.length; i++) {
      JParameter param = parameters[i];
      JParameterizedType generic = param.getType().isParameterized();
      add(param.getType().getQualifiedSourceName());
      
      if (generic != null) {
        importParameterizedType(generic);
      }
    }
    
  }

  private void importParameterizedType(JParameterizedType generic) {

    JClassType param[] = generic.getTypeArgs();
    if (param == null) {
      return;
    }
    for (int i=0; i<param.length; i++) {
      add(param[i].getQualifiedSourceName());
    }
    
  }

  private void addImplementedInterfaces(JClassType type) {
    
    JClassType[] interfaceArray = type.getImplementedInterfaces();
    if (interfaceArray == null) {
      return;
    }
    for (int i=0; i<interfaceArray.length; i++) {
      importSet.add(interfaceArray[i].getQualifiedSourceName());
    }
    
  }
  
}
