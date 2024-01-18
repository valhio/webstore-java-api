package com.github.valhio.storeapi.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.valhio.storeapi.model.User;

import java.io.IOException;

public class CustomUserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("userId", user.getId());
        jsonGenerator.writeStringField("firstName", user.getFirstName());
        jsonGenerator.writeStringField("lastName", user.getLastName());
        jsonGenerator.writeStringField("email", user.getEmail());
        // Add other fields you want to include in the JSON representation
        jsonGenerator.writeEndObject();
    }
}
