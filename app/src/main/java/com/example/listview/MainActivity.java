package com.example.listview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    boolean flag = true;
    String sequ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d( "TAG", "onPreExecute: ");
        Scraper sc = new Scraper();
        //execute the onBackground method and use .get() to wait for it to finish
        try {
            sc.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        mListView =  (ListView) findViewById(R.id.theList);
        courseArrayList = sc.getCourseList();


        generateRooms();




        adapter = new CourseListAdapter(this, R.layout.item_layout,rooms );
         mListView.setAdapter(adapter);

         mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(MainActivity.this, "you clicked on: " + personArrayList.get(i).getName(),Toast.LENGTH_SHORT).show();
                 if(flag) {
                     Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                     intent.putExtra("roomNumber", rooms.get(i).getRoomNumber());
                 Log.d("onItemClick if: ", rooms.get(i).getRoomNumber());
                     startActivityForResult(intent, 1);

                 }else{
                     for(Course c : courseArrayList){
                         Log.d("TAG", "onItemClick: " + c.toString());
                         if(c.getRoomNumber().equals(sequ)){
                             Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                             intent.putExtra("roomNumber", c.getRoomNumber());
                             Log.d("onItemClick: else", c.getRoomNumber());
                             startActivityForResult(intent, 1);
                             flag = true;
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
                 flag = false;
                 sequ = charSequence.toString();
                 Log.d("onTextChanged:" ,  sequ);


             }

             @Override
             public void afterTextChanged(Editable editable) {

             }
         });


    }

    private void generateRooms() {

        for(int j = 0; j <=4; j++) {
            //generating the list of rooms for the first floor
            for (Integer i = 0; i <= 33; i++) {
                boolean isOccupied = false;
                //goes through every room that has a lecture in the current day

                for (Course c : courseArrayList) {

                    int num = Integer.parseInt(c.getRoomNumber());
                    Log.d("roomNumberComparison", num + "<- c.roomNum, i -> " + i);
                    //check if the room is the same as i
                    if (num == Integer.parseInt(j+""+ i)) {
                        //if the room is buissy we add it to the final list
                        Course newRoom = new Course(c.getName(), c.getStartTime(), c.getTeacher(), "" + c.getRoomNumber());
                            rooms.add(newRoom);

                            Log.d("room added", "onCreate: " + c.getName());
                            isOccupied = true;


                    }

                }
                if (!isOccupied) {
                    //if the room is not buisy we add an empty room
                    rooms.add(new Course("No Course Right Now", "", "", j+"" + i));

                  //  isOccupied = !isOccupied;
                }


            }
        }
        Collections.sort(rooms);


    }

}