package com.futureagent.lib.db.activeorm.serializer;

public abstract class TypeSerializer {
    public abstract Class<?> getDeserializedType();

    public abstract Class<?> getSerializedType();

    public abstract Object serialize(Object data);

    public abstract Object deserialize(Object data);
}