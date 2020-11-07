package ch.gianlucafrei.nellygateway.config.customDeserializer;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StringEnvironmentVariableDeserializer extends StdScalarDeserializer<String> {

    private static Logger log = LoggerFactory.getLogger(StringEnvironmentVariableDeserializer.class);

    public StringEnvironmentVariableDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        String value = StringDeserializer.instance.deserialize(jp, ctxt);

        if(value != null && value.startsWith("env:")) {
            String varName = value.substring(4, value.length());
            String envValue = System.getenv(varName);

            if(envValue == null)
                log.warn(String.format("Environment variable '%s' does not exist", varName));

            return envValue;
        }
        else {
            return value;
        }
    }

    // 1.6: since we can never have type info ("natural type"; String, Boolean,
    // Integer, Double):
    // (is it an error to even call this version?)
    @Override
    public String deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
            throws IOException, JsonProcessingException {
        return deserialize(jp, ctxt);
    }
}