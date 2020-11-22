import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class GoogleMaps {
    /*
    These are the data members of the GoogleMaps class
    attraction stores the attraction and its city
    routes stores an ArrayList of routes objects got from roads.csv file
    visited stores if the city was visited or not
    miles stores the amount of miles traveled to get to that city from the starting city
    travelled stores the previous city the city came from once most efficient route found
    cities is an ArrayList consisting of all the cities visited in irder
    stack stores the cities and is popped accordingly
    mile stores total amount of miles traveled
    check makes sure that each line is only visited once
     */
    ArrayList<Routes> routes=new ArrayList<Routes>(522);
    HashMap<String,String> attraction=new HashMap<String, String>(145);
    HashMap<String,Boolean> visited=new HashMap<String, Boolean>(522);
    HashMap<String,Integer> miles=new HashMap<String,Integer>(522);
    HashMap<String,String> travelled=new HashMap<String, String>(522);
    ArrayList<String> cities=new ArrayList<String>(1000);
    Stack<String> stack=new Stack<String>();
    int mile=0;
    ArrayList<Integer> check=new ArrayList<Integer>(522);
    /*
    This is the routes object meant to store data from the roads.csv file with 4
    data members and a constructor initializing all those data members
     */
    public class Routes {
        String location1;
        String location2;
        int miles;
        int minutes;
        public Routes(String loc,String loc2,int m, int m2){
            location1=loc;
            location2=loc2;
            miles=m;
            minutes=m2;
        }
    }
    /*
    parseFiles is meant to read both roads.csv and attractions.csv and store contents
    of roads.csv into routes arrayList and attractions.csv stores all the attractions
    and its cities into a hashmap called attraction
     */
    public void parseFiles(String fileName, String fileName2){
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line="";
            while((line=br.readLine())!=null){
                String[] temp=line.split(",");
                Routes r=new Routes(temp[0],temp[1],Integer.parseInt(temp[2]),Integer.parseInt(temp[3]));
                routes.add(r);
            }
            br=new BufferedReader(new FileReader(fileName2));
            while((line=br.readLine())!=null){
                String[] temp=line.split(",");
                attraction.put(temp[0],temp[1]);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    /*
    route is the main method which will keep calling updateMaps and find the fastest
    route from the starting city to all other cities. It will find the nearest location to go to,
    go to that location and store the route, then from that location it will again find the nearest location
    and store that route until there is no more locations, then will store the route from the last location
    to the ending_city
     */
    public List<String> route(String starting_city, String ending_city, List<String> attractions){
        updateMaps(starting_city);
        String temp=starting_city;
        while(!(attractions.isEmpty())){
            int t=miles.get(attraction.get(attractions.get(0)));
            int index=0;
            for(int i=0;i<attractions.size();i++){
                if(t>miles.get(attraction.get(attractions.get(i)))){
                    index=i;
                    t=miles.get(attraction.get(attractions.get(i)));
                }
            }
            test(temp,travelled.get(attraction.get(attractions.get(index))),attraction.get(attractions.get(index)));
            temp=attraction.get(attractions.get(index));
            visited=new HashMap<String, Boolean>(522);;
            miles=new HashMap<String,Integer>(522);;
            travelled=new HashMap<String, String>(522);
            check=new ArrayList<Integer>(522);
            updateMaps(temp);
            attractions.remove(index);
        }
        test(temp,ending_city,ending_city);

        return cities;
    }
    /*
    The method updateMaps will take a city and find the most efficient route to
    every other city using dijkstras algorithm. The method will also keep track of
    the previous city as it goes on and can keep getting updated if a more efficient
    route is found and will keep setting visited cities to false, the loop or method
    will end once the stack become empty.
     */
    public void updateMaps(String city) {
        int w=0;
        stack.add(city);
        miles.put(city, 0);
        travelled.put(null, city);
        while (!(stack.isEmpty())) {
            String temp = stack.pop();
            visited.put(temp, false);
            for (int i = 0; i < routes.size(); i++) {
                if (routes.get(i).location1.equals(temp) || routes.get(i).location2.equals(temp)) {
                    if (routes.get(i).location1.equals(temp)&&!(check.contains(i))) {
                        if (visited.get(routes.get(i).location2) == null && !(stack.contains(routes.get(i).location2))) {
                            w++;
                            stack.add(routes.get(i).location2);
                            travelled.put(routes.get(i).location2, temp);
                            miles.put(routes.get(i).location2, routes.get(i).miles + miles.get(temp));
                        }
                        else if (miles.get(temp)+routes.get(i).miles<miles.get(routes.get(i).location2)) {
                            travelled.put(routes.get(i).location2, temp);
                            miles.put(routes.get(i).location2, routes.get(i).miles + miles.get(temp));
                        }
                        check.add(i);
                    }
                    else if (routes.get(i).location2.equals(temp)&&!(check.contains(i))) {
                        if (visited.get(routes.get(i).location1) == null && !(stack.contains(routes.get(i).location1))) {
                            w++;
                            stack.add(routes.get(i).location1);
                            travelled.put(routes.get(i).location1, temp);
                            miles.put(routes.get(i).location1, routes.get(i).miles + miles.get(temp));
                        }
                        else if (miles.get(temp)+routes.get(i).miles<miles.get(routes.get(i).location1)) {
                            travelled.put(routes.get(i).location1, temp);
                            miles.put(routes.get(i).location1, routes.get(i).miles + miles.get(temp));
                        }
                        check.add(i);
                    }
                }
            }
            String[] arr=new String[1000];
            int counter=0;
            while(!(stack.isEmpty())){
                arr[counter]=stack.pop();
                counter++;
            }
            for(int i=0;i<counter;i++){
                boolean truth=false;
                int m=miles.get(arr[i]);
                int index=i;
                for(int j=i;j<counter;j++){
                    if(m<miles.get(arr[j])){
                        m=miles.get(arr[j]);
                        index=j;
                        truth=true;
                    }
                }
                if(truth==true){
                    String t=arr[i];
                    arr[i]=arr[index];
                    arr[index]=t;
                }
            }
            for(int i=0;i<counter;i++)
                stack.add(arr[i]);
            w=0;
        }
    }
    /*
    The test function is just meant to add the cities visited in order
    in the cities arrayList and will also keep track of the total miles
    traveled from one location to another and keeps accumulating it,
    method is called in routes function
     */
    public void test(String city, String city2, String city3){
        updateMaps(city);
        ArrayList<String> temp=new ArrayList<>();
        temp.add(city2);
        String t=city2;
        mile+=miles.get(city3);
        while(travelled.get(t)!=null){
            t=travelled.get(t);
            temp.add(t);
        }
        for(int i=0;i<temp.size();i++){
            cities.add(temp.get(temp.size()-i-1));
        }

    }
    public static void main(String[] args){
        /*
        This is the main method with parseFiles and route being called
        The att arraylist is meant to store all the attractions we will be travelling to
        First parameter of route is the starting city and second parameter is the ending city
        and third parameter is the attractions
        First parameter of parseFiles must be roads.csv and second parameter must be attractions.csv
         */
        GoogleMaps map=new GoogleMaps();
        ArrayList<String> att=new ArrayList<>();
        /*
        IMPORTANT:
        Attraction names must be inputted exactly as spelled in the attractions.csv file
        with the same capital and lowercase letters as well as the spaces like shown below
         */
        att.add("Portland City Tour");
        att.add("The Field of Dreams Filming Locale");
        att.add("Hilton Head");
        map.parseFiles("roads.csv","attractions.csv");
        System.out.println("Route in order: ");
        /*
        IMPORTANT:
        Starting and ending cities must be inputted exactly as spelled in the roads.csv file
        with the same uppercase and lowercase letter as well as the spaces like shown below.
         */
        System.out.println(map.route("San Francisco CA","Daytona Beach FL",att));
        System.out.println("Miles traveled: "+map.mile);
    }
}
