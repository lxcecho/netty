package io.serialize.java;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 14:29 23-10-2022
 */
public interface ISerializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data);

}
