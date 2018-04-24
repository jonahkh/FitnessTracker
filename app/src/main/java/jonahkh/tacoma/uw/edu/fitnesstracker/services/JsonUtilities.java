package jonahkh.tacoma.uw.edu.fitnesstracker.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by jonah on 4/22/2018.
 */
public class JsonUtilities {
    private static final Gson GSON = new Gson();

    public static <T> String toJson(T content) {
        return GSON.toJson(content);
    }

    public static <T> T fromJson(String content, Class<T> targetClass) {
        return GSON.fromJson(content, targetClass);
    }

    public static <T> T fromJson(String content, TypeToken<T> type) {
        return GSON.fromJson(content, type.getType());
    }

}
