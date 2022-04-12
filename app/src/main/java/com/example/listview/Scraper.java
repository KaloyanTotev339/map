package com.example.listview;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

public class Scraper extends AsyncTask<Void,Void,Void> {
    private ArrayList<String> courseAll = new ArrayList<>();
    private ArrayList<Course>  courseList= new ArrayList<>();
    private String courseName;
    private String courseDate;
    private String courseStartTime;
    private String courseTeacherTitle;
    private String courseTeacherName;
    private String courseTeacher;
    private String courseRoom;
    private String html;
    private String currentDate;
    private String rowspan;



    @Override
    protected void onPreExecute(){

    }

    @Override
    protected Void doInBackground(Void... voids) {
        Document doc;
        String[] sites = {"https://e-services.bfu.bg/common/graphic.php?c=3&o=1&k=4",
                "https://e-services.bfu.bg/common/graphic.php?c=3&o=1&k=3",
                "https://e-services.bfu.bg/common/graphic.php?c=3&o=1&k=2",
                "https://e-services.bfu.bg/common/graphic.php?c=3&o=1&k=1",
                "https://e-services.bfu.bg/common/graphic.php?c=2&o=1&k=2"};
        for (String site : sites) {
            findWeek(site);
            try {


                doc = Jsoup.connect(html).get();

                Elements data2 = doc.select("table");

                for (Element tr : data2.select("tr")) {

                    //  Log.d("TR", tr.text());
                    for (Element td : tr.select("td.info")) {

                        // Log.d("TD", Arrays.asList(td.text().split(" ")).toString());
                        courseDate = tr.select("td.blue").text().split(" ")[1];
                        courseStartTime = tr.select("td.blue").text().split(" ")[2];

                        if (courseDate.equals(currentDate)) {
                            rowspan = td.attr("rowspan").toString();
                            courseAll.removeAll(courseAll);
                            courseAll.addAll(Arrays.asList(td.text().split(" ")));
                            //Log.d( "ALL: ",courseAll.toString());

                            if (courseAll.get(courseAll.size() - 1).equals("лекции")) {
                                courseAll.remove(courseAll.size() - 1);
                            }
                            courseRoom = courseAll.get(courseAll.size() - 1);
                            courseAll.remove(courseRoom);
                            courseTeacherTitle = courseAll.get(courseAll.size() - 2);

                            // Log.d( "ROOM_NUMBER: ",courseRoom);

                            courseTeacherName = courseAll.get(courseAll.size() - 1);

                            courseAll.remove(courseTeacherTitle);
                            courseAll.remove(courseTeacherName);
                            courseName = String.join(" ", courseAll);
                            //Log.d( "COURSE_NAME: ",courseName);
                            courseTeacher = courseTeacherTitle + " " + courseTeacherName;
                            //Log.d( "TEACHER_NAME: ",courseTeacher);
                            String[] splitted = courseStartTime.split("-");
                            String timeHolder = "" + (Integer.parseInt(splitted[1])
                                    + Integer.parseInt(rowspan));
                            courseStartTime = splitted[0] + "-" + timeHolder;


                            Course entry = new Course(courseName, courseStartTime, courseDate, courseTeacher, courseRoom);
                            Log.d("courseEntry", "doInBackground: " + entry.toString());
                            this.courseList.add(entry);


                        }

                    }

                }


            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
            Log.d("onExecute", "getCourseList: " + courseList.size());
            return null;

    }

    @Override
    protected void onPostExecute(Void unused) {

    }

    private void findWeek(String site){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM");
        currentDate = formatter.format(calendar.getTime());
        try {
            Document document = Jsoup.connect(site).get();
            Elements links = document.select("a[href]");

            for(Element e : links){

                if(e.text().split(" ")[0].equals("Седмица")) {
                    //Log.d("Week", e.text());
                    String[] eSplit = e.text().split(" ");
                    int startMonth =Integer.parseInt(eSplit[2].split("\\.")[1]);
                    int startDay = Integer.parseInt(eSplit[2].split("\\.")[0]);
                    int endMonth = Integer.parseInt(eSplit[eSplit.length-1].split("\\.")[1]);
                    int endDay =Integer.parseInt(eSplit[eSplit.length-1].split("\\.")[0]);
                    int currDay = Integer.parseInt(currentDate.split("\\.")[0]);
                    int currMonth =Integer.parseInt(currentDate.split("\\.")[1]);

                    if(currMonth >= startMonth && currMonth <= endMonth){
                        if(currDay >= startDay && currDay <= endDay){

                            this.html = "https://e-services.bfu.bg/common/" + e.attr("href");

                               // Log.d("LINK", this.html);
                        }
                    }

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Course> getCourseList() {
        Log.d("Scrapper", "getCourseList: " + courseList.size());
        return this.courseList;
    }
}
