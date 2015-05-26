package com.dubboclub.cache;

import com.alibaba.dubbo.cache.Cache;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.serialize.ObjectInput;
import com.alibaba.dubbo.common.serialize.ObjectOutput;
import com.alibaba.dubbo.common.serialize.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by bieber on 2015/5/26.
 */
public abstract class AbstractCache implements Cache{
    
    protected byte[] cachedTarget;
    
    protected URL cachedUrl;
    
    
    private static final Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getAdaptiveExtension();
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractCache.class);
    
    protected byte[] objectToBytes(URL url,Object object){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutput objectOutput = serialization.serialize(url, byteArrayOutputStream);
            objectOutput.writeObject(object);
        } catch (IOException e) {
            logger.error("Failed to serialize object ["+object.toString()+"] for url ["+url.toString()+"]",e);
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    protected Object bytesToObject(URL url,byte[] bytes){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            ObjectInput objectInput = serialization.deserialize(url,byteArrayInputStream);
            return objectInput.readObject();
        } catch (IOException e) {
            logger.error("Failed to deserialize bytes to object for url [" + url.toString() + "]", e);
            return null;
        } catch (ClassNotFoundException e) {
            logger.error("Failed to deserialize bytes to  object for url [" + url.toString() + "] by class not found ",e);
            return null;
        }
    }
    
    
    protected int getExpireSecond(URL url){
        return 0;
    }
    
    protected byte[] generateCacheKey(Object key){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(objectToBytes(cachedUrl,key));
            byteArrayOutputStream.write(cachedTarget);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("Failed to generate cache key for object ["+key.toString()+"]",e);
            return null;
        }
    }
    
}
