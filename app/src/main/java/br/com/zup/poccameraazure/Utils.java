package br.com.zup.poccameraazure;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wesleygoes on 31/05/17.
 */

public class Utils {

    public static Gson getGsonDate(String format) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeDeserializer(format));
        return gsonBuilder.create();
    }

    public static Date getDateFromString(String str, String format) {
        try {
            if (!TextUtils.isEmpty(str))
                return new SimpleDateFormat(format, Locale.getDefault()).parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static class DateTypeDeserializer implements JsonDeserializer<Date> {

        private String formatDate;

        public DateTypeDeserializer(String formatDate) {
            this.formatDate = formatDate;
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            return Utils.getDateFromString(json.getAsString(), formatDate);
        }
    }

}
