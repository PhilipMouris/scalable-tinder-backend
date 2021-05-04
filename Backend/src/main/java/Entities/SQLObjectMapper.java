package Entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SQLObjectMapper extends ObjectMapper {
    public SQLObjectMapper() {
        super();
        this.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}