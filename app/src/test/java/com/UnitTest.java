package com;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.go4lunch.BuildConfig;
import com.go4lunch.R;
import com.go4lunch.model.firestore.User;
import com.go4lunch.model.nearbysearch.OpeningHours;
import com.go4lunch.model.nearbysearch.PhotosItem;
import com.go4lunch.model.nearbysearch.ResultsItem;
import com.go4lunch.ui.home.ListViewFragmentAdapter;
import com.go4lunch.ui.home.MapViewFragment;
import com.go4lunch.ui.home.WorkmatesFragmentAdapter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class UnitTest {

    @Test
    public void test_getRatingOnThree() {
        ListViewFragmentAdapter listViewFragmentAdapter = new ListViewFragmentAdapter(null, null);

        float rating1 = listViewFragmentAdapter.getRatingOnThree(1);
        assertEquals(0.60, rating1, 0.1);

        float rating2 = listViewFragmentAdapter.getRatingOnThree(2);
        assertEquals(1.20, rating2, 0.1);

        float rating3 = listViewFragmentAdapter.getRatingOnThree(3);
        assertEquals(1.80, rating3, 0.1);

        float rating4 = listViewFragmentAdapter.getRatingOnThree(4);
        assertEquals(2.40, rating4, 0.1);
    }

    @Test
    public void test_getOpeningHoursText() {
        ListViewFragmentAdapter adapter = new ListViewFragmentAdapter(null, null);

        ResultsItem resultsItem = new ResultsItem();
        assertEquals(R.string.we_dont_know, adapter.getOpeningHoursText(resultsItem));

        resultsItem.setOpeningHours(new OpeningHours());
        resultsItem.getOpeningHours().setOpenNow(true);
        assertEquals(R.string.Open_now, adapter.getOpeningHoursText(resultsItem));

        resultsItem.getOpeningHours().setOpenNow(false);
        assertEquals(R.string.Close_now, adapter.getOpeningHoursText(resultsItem));
    }

    @Test
    public void test_getPhotoRequest() {
        List<PhotosItem> photosItemList = new ArrayList<>();
        PhotosItem photosItem1 = new PhotosItem();
        photosItem1.setPhotoReference("https://maps.googleapis.com/maps/api/place/photo?key=" + BuildConfig.MAPS_API_KEY + "&photoreference=photoReference1&maxheight=157&maxwidth=157");
        PhotosItem photosItem2 = new PhotosItem();
        photosItem2.setPhotoReference("https://maps.googleapis.com/maps/api/place/photo?key=" + BuildConfig.MAPS_API_KEY + "&photoreference=photoReference2&maxheight=157&maxwidth=157");

        photosItemList.add(photosItem1);
        photosItemList.add(photosItem2);

        List<ResultsItem> resultsItemList = new ArrayList<>();
        ResultsItem resultsItem1 = new ResultsItem();
        resultsItem1.setPhotos(photosItemList);
        ResultsItem resultsItem2 = new ResultsItem();
        resultsItem2.setPhotos(photosItemList);

        resultsItemList.add(resultsItem1);
        resultsItemList.add(resultsItem2);

        ListViewFragmentAdapter adapter = new ListViewFragmentAdapter(resultsItemList, null);
        assertEquals(resultsItemList.get(0).getPhotos().get(0).getPhotoReference(), adapter.getPhotoRequest("photoReference1"));
        assertEquals(resultsItemList.get(1).getPhotos().get(1).getPhotoReference(), adapter.getPhotoRequest("photoReference2"));
    }

    @Test
    public void test_getNumberOfReservations() {
        String eatingPlaceId = "Del Arte";
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        user1.setEatingPlaceId(eatingPlaceId);
        user2.setEatingPlaceId(eatingPlaceId);
        user3.setEatingPlaceId(eatingPlaceId);
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        String placeId = "Del Arte";
        List<ResultsItem> resultsItemList = new ArrayList<>();
        ResultsItem resultsItem1 = new ResultsItem();
        resultsItem1.setPlaceId(placeId);
        resultsItemList.add(resultsItem1);

        ListViewFragmentAdapter adapter = new ListViewFragmentAdapter(null, null);
        int n = adapter.getNumberOfReservations(resultsItemList.get(0).getPlaceId(), userList);
        assertEquals(3, n);


        String eatingPlaceId2 = "Burger";
        List<User> userList2 = new ArrayList<>();
        User user4 = new User();
        User user5 = new User();
        User user6 = new User();
        user4.setEatingPlaceId(eatingPlaceId2);
        user5.setEatingPlaceId(eatingPlaceId2);
        user6.setEatingPlaceId(eatingPlaceId);
        userList2.add(user4);
        userList2.add(user5);
        userList2.add(user6);

        String placeId2 = "Burger";
        List<ResultsItem> resultsItemList2 = new ArrayList<>();
        ResultsItem resultsItem2 = new ResultsItem();
        resultsItem2.setPlaceId(placeId2);
        resultsItemList2.add(resultsItem2);

        ListViewFragmentAdapter adapter2 = new ListViewFragmentAdapter(null, null);
        int n2 = adapter2.getNumberOfReservations(resultsItemList2.get(0).getPlaceId(), userList2);
        assertEquals(2, n2);
    }

    @Test
    public void test_isBookedOrNot() {
        String eatingPlaceId = "Del Arte";
        String eatingPlaceId2 = "Burger";
        String placeId = "Del Arte";
        String placeId2 = "Burger";
        String placeId3 = "Sandwich";

        List<User> userList = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        user1.setEatingPlaceId(eatingPlaceId);
        user2.setEatingPlaceId(eatingPlaceId2);
        userList.add(user1);
        userList.add(user2);

        MapViewFragment mapViewFragment = new MapViewFragment();
        assertTrue(mapViewFragment.isBookedOrNot(placeId, userList));
        assertTrue(mapViewFragment.isBookedOrNot(placeId2, userList));
        assertFalse(mapViewFragment.isBookedOrNot(placeId3, userList));
    }

    @Test
    public void test_getFilterResults() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        user1.setUsername("a");
        user2.setUsername("b");
        user3.setUsername("c");
        user4.setUsername("c");

        WorkmatesFragmentAdapter workmatesFragmentAdapter = new WorkmatesFragmentAdapter(userList, userList);

        List<User> filteredResult = workmatesFragmentAdapter.getFilterResults("a");
        assertEquals(filteredResult.size(), 1);
        assertEquals(filteredResult.get(0).getUsername(), "a");

        List<User> filteredResult2 = workmatesFragmentAdapter.getFilterResults("b");
        assertEquals(filteredResult2.size(), 1);
        assertEquals(filteredResult2.get(0).getUsername(), "b");

        List<User> filteredResult3 = workmatesFragmentAdapter.getFilterResults("c");
        assertEquals(filteredResult3.size(), 2);
        assertEquals(filteredResult3.get(0).getUsername(), "c");
        assertEquals(filteredResult3.get(1).getUsername(), "c");
    }


}