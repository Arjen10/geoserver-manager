package it.geosolutions.geoserver.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Arjen10
 * @since 1.8.0
 */
@Slf4j
public abstract class JacksonUtil {

    /**
     * 确保这个OBJECT_MAPPER在这个工具类中是唯一的
     */
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * json转对象
     * @param json json字符串
     * @param t 字节码对象
     * @return 字节码对象的实例
     * @param <T> 字节码对象
     */
    public static <T> T parseObject(String json, Class<T> t) {
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructType(t));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析引用类型对象
     *
     * @param json json字符串
     * @param tr   tr
     * @return 字节码对象的实例
     */
    public static <T> T parseObjectByTypeReference(String json, TypeReference<T> tr) {
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructType(tr));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对象转json字符串
     * @param t 实例对象
     * @return json字符串
     * @param <T> 实例对象类型
     */
    public static <T> String toJSONString(T t) {
        try {
            return OBJECT_MAPPER.writeValueAsString(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 确保这个类中的OBJECT_MAPPER是唯一的，避免产生线程问题
     *
     * @return ObjectMapper的复制对象
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER.copy();
    }

}
