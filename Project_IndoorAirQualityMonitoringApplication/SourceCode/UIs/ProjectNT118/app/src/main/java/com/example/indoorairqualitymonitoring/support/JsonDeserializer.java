package com.example.indoorairqualitymonitoring.support;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.List;

public class JsonDeserializer
{
    public static <T> List<T> getListFromJsonArray(String jsonString, Type type)
    {
        if (!isValid(jsonString))
        {
            return null;
        }

        return new Gson().fromJson(jsonString, type);
    }

    public static boolean isValid(String json)
    {
        try
        {
            JsonParser.parseString(json);
            return true;
        }
        catch (JsonSyntaxException error)
        {
            return false;
        }
    }
}