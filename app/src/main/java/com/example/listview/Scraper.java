package com.example.listview;

import android.os.AsyncTask;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;


public class Scraper extends AsyncTask<Void,Void,Void> {
    private ArrayList<String> courseAll = new ArrayList<>();
    private ArrayList<Course> courseList= new ArrayList<>();
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
        //generate all the links for the schedule of all the centers and courses
        ArrayList<String> sites = new ArrayList<>();
                for(int center = 1; center <=4; center++){
                    for(int year = 1; year<=4; year++){
                        sites.add("https://e-services.bfu.bg/common/graphic.php?c="+center+"&o=1&k="+year);
                    }
                }

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
                            //ectract the rowspan, used to determine the end time of the course
                            rowspan = td.attr("rowspan");
                            //clear the previous course
                            courseAll.removeAll(courseAll);
                            //load the new one with all it's attributes
                            courseAll.addAll(Arrays.asList(td.text().split(" ")));
                            //Log.d( "ALL: ",courseAll.toString());

                            //check weather the text ends on "лекции" and remove it if it does.
                            if (courseAll.get(courseAll.size() - 1).equals("лекции")) {
                                courseAll.remove(courseAll.size() - 1);
                            }
                            //the room of the course is always at the end of the info
                            //except when there is "лекции". That's why it is removed at the beginning
                            //extract the room and then remove it from the list
                            courseRoom = courseAll.get(courseAll.size() - 1);
                            courseAll.remove(courseRoom);

                            //after the room number there is always the teacher's name
                            //and title. extract and then remove them.
                            courseTeacherTitle = courseAll.get(courseAll.size() - 2);
                            courseTeacherName = courseAll.get(courseAll.size() - 1);
                            courseTeacher = courseTeacherTitle + " " + courseTeacherName;
                            courseAll.remove(courseTeacherTitle);
                            courseAll.remove(courseTeacherName);

                            //what the user are left with is the course name.
                            //then join the remaining strings into a single one.
                            courseName = String.join(" ", courseAll);

                            //extract the course start time and replace the end time
                            // ( which is always initially one hour after the start) with
                            //the start time + the rowspan
                            String[] splitted = courseStartTime.split("-");
                            String timeHolder = "" + (Integer.parseInt(splitted[1])
                                    + Integer.parseInt(rowspan));
                            courseStartTime = splitted[0] + "-" + timeHolder;

                            //add the course to the list of courses
                            Course entry = new Course(courseName, courseStartTime, courseDate, courseTeacher, courseRoom);
                            //Log.d("courseEntry", "doInBackground: " + entry);
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

    //downloads the HTML of the list of dates site, finds the necessary week and extracts the link
    //to the schedule page
    private void findWeek(String site){
        //get the date of when the method is called and format it
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM");
        currentDate = formatter.format(calendar.getTime());
        try {
            //download the HTML document
            Document document = Jsoup.connect(site).get();
            //extract all the links available on the page
            Elements links = document.select("a[href]");
            //for each link
            for(Element e : links){
                //find the active ones by checking for attribute "седмица" and then extract
                //the scope of the week
                //for further insight, please uncomment the "Log.d" line
                //Log.d("link", "findWeek: " + e.text());
                if(e.text().split(" ")[0].equals("Седмица")) {
                    //Log.d("Week", e.text());
                    String[] eSplit = e.text().split(" ");
                    int startMonth =Integer.parseInt(eSplit[2].split("\\.")[1]);
                    int startDay = Integer.parseInt(eSplit[2].split("\\.")[0]);
                    int endMonth = Integer.parseInt(eSplit[eSplit.length-1].split("\\.")[1]);
                    int endDay =Integer.parseInt(eSplit[eSplit.length-1].split("\\.")[0]);
                    int currDay = Integer.parseInt(currentDate.split("\\.")[0]);
                    int currMonth =Integer.parseInt(currentDate.split("\\.")[1]);

                    //check wether the current date is part of the link's week
                    if(currMonth >= startMonth && currMonth <= endMonth){
                        if(currDay >= startDay && currDay <= endDay){
                            //attach the coresponding url and break the cycle
                            this.html = "https://e-services.bfu.bg/common/" + e.attr("href");
                                break;
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
