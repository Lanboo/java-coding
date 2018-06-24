package com.xych.zookeeper.zkclient.serializer;

import java.io.IOException;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class  JacksonSerializer<T> implements ZkSerializer
{
    private ObjectMapper objectMapper;
    private Class<T> clazz;

    public JacksonSerializer(Class<T> clazz)
    {
        this(clazz, new ObjectMapper());
    }

    public JacksonSerializer(Class<T> clazz, ObjectMapper objectMapper)
    {
        this.clazz = clazz;
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object data) throws ZkMarshallingError
    {
        try
        {
            String json = this.objectMapper.writeValueAsString(data);
            if(json != null)
                return json.getBytes();
            return null;
        }
        catch(JsonProcessingException e)
        {
            throw new ZkMarshallingError(e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws ZkMarshallingError
    {
        if(bytes == null)
            return null;
        try
        {
            return this.objectMapper.readValue(bytes, this.clazz);
        }
        catch(IOException e)
        {
            throw new ZkMarshallingError(e);
        }
    }
}
