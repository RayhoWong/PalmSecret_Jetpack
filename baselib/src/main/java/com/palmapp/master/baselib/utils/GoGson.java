package com.palmapp.master.baselib.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nullable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * Created by zhangliang on 17/1/11
 *
 * @email: zhangliang@gomo.com
 */
public class GoGson {

    private static Gson sGson;

    private static Gson getGson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .enableComplexMapKeySerialization()
//                    .setPrettyPrinting()
                    .serializeSpecialFloatingPointValues()
                    .create();
//            sGson = new Gson();
        }
        return sGson;
    }

    /**
     * @param src the object for which Json representation is to be created setting for Gson
     * @return Json representation of {@code src}.
     */

    public static String toJson(Object src) {
        return getGson().toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return getGson().toJson(src,typeOfSrc);
    }


    /**
     * @param <T>      the type of the desired object
     * @param json     the string from which the object is to be deserialized
     * @param classOfT the class of T
     * @return an object of type T from the string. Returns {@code null} if {@code json} is {@code null}.
     * @throws JsonSyntaxException if json is not a valid representation for an object of type
     *                             classOfT
     */
    @Nullable
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return fromJson(json, (Type) classOfT);
    }

    /**
     * @param <T>     the type of the desired object
     * @param json    the reader producing Json from which the object is to be deserialized
     * @param typeOfT The specific genericized type of src. You can obtain this type by using the
     *                {@link com.google.gson.reflect.TypeToken} class. For example, to get the type for
     *                {@code Collection<Foo>}, you should use:
     *                <pre>
     *                                                                            Type typeOfT = new TypeToken&lt;
     *                                                                            Collection&lt;
     *                                                                            Foo&gt;&gt;(){}
     *                                                                            .getType();
     *                                                                            </pre>
     * @return an object of type T from the json. Returns {@code null} if {@code json} is at EOF.
     * @since 1.2
     */
    @Nullable
    public static <T> T fromJson(String json, Type typeOfT) {

        try {
            if (json == null) {
                return null;
            }
            StringReader reader = new StringReader(json);
            return getGson().fromJson(reader, typeOfT);
        } catch (Exception e) {
            LogUtil.e("xmr",e.getMessage());
        }

        return null;
    }

    /**
     * Reads the next JSON value from {@code reader} and convert it to an object
     * of type {@code typeOfT}. Returns {@code null}, if the {@code reader} is at EOF.
     * Since Type is not parameterized by T, this method is type unsafe and should be used carefully
     */
    public static <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
        try {
            if (json == null) {
                return null;
            }
            return getGson().fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }



}
