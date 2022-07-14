package com.quickwork.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityObjectMapper {
    private static final Logger LOG = LoggerFactory.getLogger(EntityObjectMapper.class);

    private EntityObjectMapper() { }

    public static ObjectMapper objectMapper() {
        return Holder.INSTANCE;
    }

    public static String toJson(Object obj) {
        try {
            return Holder.INSTANCE.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            String message = "Error during marshalling from an entity to JSON";
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    public static String toPrettyJson(Object obj) {
        try {
            return Holder.INSTANCE.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("Error during marshalling from an entity to JSON", e);
            return "{}";
        }
    }

    private static final class Holder {
        private static final ObjectMapper INSTANCE = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
