package com.example.listview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    CourseListAdapter adapter;
    ArrayList<Course> rooms = new ArrayList<>();
    ArrayList<Course> courseArrayList;
    ListView mListView;
    boolean isSearched = false;
    String sequ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkConnection()) {
            Log.d("TAG", "onPreExecute: ");
            Scraper sc = new Scraper();
            //execute the onBackground method and use .get() to wait for it to finish
            //otherwise it will not load the courses in time
            try {
                sc.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //load the active courses of the current day
            courseArrayList = sc.getCourseList();

            //generate all the rooms
            generateRooms2();
        }else{
            generateEmptyRooms();
        }
        //find the list view and add the custom list adapter to it
        mListView =  (ListView) findViewById(R.id.theList);
        adapter = new CourseListAdapter(this, R.layout.item_layout,rooms);
        mListView.setAdapter(adapter);

         // "button"-like ability for when an item is clicked
         mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if there is no search in the searchbar, the user find the room by it's number in the arrayList
                 if(!isSearched) {
                     Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                     intent.putExtra("roomNumber", rooms.get(i).getRoomNumber());
                     startActivityForResult(intent, 1);
                  // else the user find the room by it's roomNumber, otherwise the it's number on the list
                  //won't corespond to the real item the user clicked
                 }else{
                     for(Course c : rooms){
                         if(c.getRoomNumber().equals(sequ)){
                             Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                             intent.putExtra("roomNumber", c.getRoomNumber());
                             startActivityForResult(intent, 1);

                             break;
                         }
                     }
                 }

             }
         });

        //edit filter is responsible for finding an element in the list displayed on the screen
        //it compares the text in the edit text to the "toString" method of the objects in the list
        EditText theFilter = (EditText) findViewById(R.id.searchFilter);

         theFilter.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 (MainActivity.this).adapter.getFilter().filter(charSequence);
                 //used and explained in "onItemClicked()
                 isSearched = true;
                 sequ = charSequence.toString();
                 //if the search text is deleted then go back to normal select
                 if(sequ.length() < 1){
                     isSearched=false;
                 }
             }

             @Override
             public void afterTextChanged(Editable editable) {

             }
         });


    }

    private void generateEmptyRooms() {
        for (Integer i = 0; i <= 33; i++) {
            rooms.add(new Course("No Course Right Now", "", "", "",""+i,"1"));

        }
        for(int j = 0; j <=4; j++) {
            //generating the list of rooms for the first floor
            if(j !=1) {
                for (Integer i = 0; i <= 33; i++) {
                    rooms.add(new Course("No Course Right Now", "", "", "", j + "" + i,j+""));

                }
            }
        }
    }

    private void generateRooms() {

        ArrayList<String> doubledRooms = new ArrayList<>();
        boolean isAdded = false;
        for(int j = 0; j <=4; j++) {
            //generating the list of rooms for the first floor
            for (Integer i = 0; i <= 33; i++) {

                boolean isOccupied = false;
                //goes through every room that has a lecture in the current day
                //and checks if it is the same room

                for (Course c : courseArrayList) {
                    isAdded=false;
                    if(!c.getRoomNumber().equals("Онлайн")) {
                        int num = Integer.parseInt(c.getRoomNumber());
                        Log.d("roomNumberComparison", num + "<- c.roomNum, ji -> " + j + i );
                        //check if the room is the same as i
                        if (num == Integer.parseInt(j + "" + i)) {
                            //if the room is busy the user add it to the final list
                            Course newRoom = new Course(c.getName(), c.getStartTime(), c.getTeacher(), "" + c.getRoomNumber());
                            for(String rn : doubledRooms){
                                if(rn.equals(c.getRoomNumber())){
                                    isAdded = true;
                                }
                            }
                            if(!isAdded) {
                                rooms.add(newRoom);
                                doubledRooms.add(newRoom.getRoomNumber());
                                courseArrayList.remove(newRoom);
                                Log.d("room added", "onCreate: " + c.getName());
                                isOccupied = true;
                            }
                        }
                    }

                }
                if (!isOccupied) {
                    //if the room is not buisy the user add an empty room
                    rooms.add(new Course("No Course Right Now", "", "", j+"" + i));

                    isOccupied = !isOccupied;
                }
            }
        }

    }

    public void generateRooms2(){
        ArrayList<Course> removable = new ArrayList<>();
        ArrayList<Course> addable = new ArrayList<>();
        boolean isAdded = false;
        for (Integer i = 0; i <= 33; i++) {
            rooms.add(new Course("No Course Right Now", "", "", "",""+i,"1"));

        }
        for(int j = 0; j <=4; j++) {
            //generating the list of rooms for the first floor
            if(j !=1) {
                for (Integer i = 0; i <= 33; i++) {
                    rooms.add(new Course("No Course Right Now", "", "", "", j + "" + i,j+""));

                }
            }
        }

        for(Course emptyRoom : rooms){
            for(Course fullRoom : courseArrayList){
                Log.d("repeat test", emptyRoom.getRoomNumber()+"<-emptyRoom, fullRoom->"+fullRoom.getRoomNumber());
                isAdded = false;
                if(emptyRoom.getRoomNumber().equals(fullRoom.getRoomNumber())){
                    for(Course room : addable){
                        if(room.getRoomNumber().equals(fullRoom.getRoomNumber())){
                            if(room.getStartTime().split("-")[0].equals(fullRoom.getStartTime().split("-")[0]))
                            isAdded=true;
                        }
                    }
                    if(!isAdded) {
                        removable.add(emptyRoom);
                        addable.add(fullRoom);
                    }
                }
            }
        }
        //Collections.sort(rooms);
        rooms.removeAll(removable);
        rooms.addAll(addable);
        Collections.reverse(rooms);



    }

    public boolean checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if(ni != null ) {
            if(ni.isConnected()){
                return true;
            }else return false;
        }else return false;
    }

}