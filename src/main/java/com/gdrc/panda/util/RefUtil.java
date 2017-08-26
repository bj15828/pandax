/*
 * Copyright 2016, YXP.
 * Copyrights licensed under the New BSD License.
 * See the accompanying LICENSE file for terms.
 */

package com.gdrc.panda.util;

import java.lang.reflect.Constructor;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.plugin.IKeyGenerator;
import com.gdrc.panda.plugin.TranKeyGenerator;
import com.gdrc.panda.store.ILogStorage;

/**
 * new  Config Object  
 * 
 * 
 * 
 * */

public class RefUtil {     
   
    
    
    /**   
     * 新建实例   
     *   
     * @param className   
     *            类名   
     * @param args   
     *            构造函数的参数   
     * @return 新建的实例   
     * @throws Exception   
     */    
    public static Object newInstance(String className, Object[] args) throws Exception {     
        Class newoneClass = Class.forName(className);     
    
         return RefUtil.newInstance(newoneClass, args)  ; 
    
    }     
    
    /**   
     * 新建实例   
     *   
     * @param className   
     *            类名   
     * @param args   
     *            构造函数的参数   
     * @return 新建的实例   
     * @throws Exception   
     */    
    public static Object newInstance(Class cls, Object[] args) throws Exception {     
            
      Constructor cons = null;
      if( null != args)
      {
        Class[] argsClass = new Class[args.length];     
    
        for (int i = 0, j = args.length; i < j; i++) {     
            argsClass[i] = args[i].getClass();     
        }     
    
        cons = cls.getConstructor(argsClass);     
      }else{
        cons = cls.getConstructor(null);
        
      }
        return cons.newInstance(args);     
    
    }   
    
    
    public static ILogStorage getConfigLogStore(CorePeer peer ,Config config) throws PandaException{
    	
    	
    	ILogStorage store = null;

		String pre = config.get("log_storage.impl");

		String storeCls = config.get(pre + ".class");

		Object[] args = new Object[2];
		args[0] = peer;
		args[1] = config;

		try {
			store = (ILogStorage) RefUtil.newInstance(storeCls, args);
		} catch (Exception e) {
			throw new PandaException(e);
		}
    	
    	return store;
    	
    }
    
         
 public static IKeyGenerator getKeyGenerator() throws PandaException{
    	
    	
    	
    	return new TranKeyGenerator();
    	
    }
    
}    
