package com;

import com.go4lunch.model.nearbysearch.PhotosItem;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_Json_Serialisation() {
        List<String> attributionList2 = Arrays.asList(new String[]{"Jacques", "John", "Marc"});
        PhotosItem photosItem = new PhotosItem();
        Gson gson = new Gson();
        String jsonText = "{\"photo_reference\":\"photoooooo\",\"width\":1555555,\"html_attributions\":[\"Jacqueeeeees\",\"John\",\"Marc\"],\"height\":20}";

        photosItem.setHeight(20);
        photosItem.setPhotoReference("photo");
        photosItem.setWidth(15);
        photosItem.setHtmlAttributions(attributionList2);

        System.out.println(photosItem.toString());
        System.out.println(gson.toJson(photosItem));
        PhotosItem deserializedPhotoItem = gson.fromJson(jsonText, PhotosItem.class);
        System.out.println(deserializedPhotoItem.toString());
        System.out.println(deserializedPhotoItem.getHeight());
    }
}