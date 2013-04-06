package com.amazonaws.services.util;

import java.beans.Statement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class FluentInterface<T> implements InvocationHandler {
  Object obj;

  public FluentInterface(Object obj) {
    this.obj = obj;
  }
  
  @SuppressWarnings("rawtypes")
  public static <T> T create(Object object, Class fluentInterfaceClass) {
    
	FluentInterface handler = new FluentInterface(object);

    @SuppressWarnings("unchecked")
	T fluentInterface = (T) Proxy.newProxyInstance(
       fluentInterfaceClass.getClassLoader(),
        new Class[]{fluentInterfaceClass},
        handler);
    return fluentInterface;
  }

  public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
    try {
      String name = m.getName();
      if ("create".equals(name)) {
        return obj;
      } else {
        String setter = "set" + name.substring(0, 1).toUpperCase()
          + name.substring(1);
        Statement stmt = new Statement(this.obj, setter, args);
        stmt.execute();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
    return proxy;
  }
}
