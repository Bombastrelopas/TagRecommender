import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.geo.GeoInterface;
import com.flickr4java.flickr.tags.Tag;
import com.flickr4java.flickr.test.TestInterface;
import com.flickr4java.flickr.util.IOUtilities;

import org.apache.log4j.BasicConfigurator;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.w3c.dom.Element;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Comparator;


public class AuthExample {

    public static void auth() throws IOException, FlickrException {
        Properties properties;
        InputStream in = null;
        try {
            in = AuthExample.class.getResourceAsStream("/setup.properties");
            properties = new Properties();
            properties.load(in);
        } finally {
            IOUtilities.close(in);
        }
        
        
        
        Flickr flickr = new Flickr(properties.getProperty("apiKey"), properties.getProperty("secret"), new REST());
        Flickr.debugStream = false;
        
        AuthInterface authInterface = flickr.getAuthInterface();

        Scanner scanner = new Scanner(System.in);
        
        //Authorization step 1
        Token token = authInterface.getRequestToken();

        
        System.out.println("token: " + token);
        //Step 2
        String url = authInterface.getAuthorizationUrl(token, Permission.WRITE);
        System.out.println("Follow this URL to authorise yourself on Flickr");
        System.out.println(url);
        System.out.println("Paste in the token it gives you:");
        System.out.print(">>");

        String tokenKey = scanner.nextLine();
        
        
        //Token Retrieval step 3
        Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
        System.out.println("Authentication success\n");
        
        //Stuff happening after authentication
        
        Auth auth = authInterface.checkToken(requestToken);
        RequestContext.getRequestContext().setAuth(auth);
        // This token can be used until the user revokes it.
        System.out.println("Token: " + requestToken.getToken());
        System.out.println("Secret: " + requestToken.getSecret());
        System.out.println("nsid: " + auth.getUser().getId());
        System.out.println("Realname: " + auth.getUser().getRealName());
        System.out.println("Username: " + auth.getUser().getUsername());
        System.out.println("Permission: " + auth.getPermission().getType());
        
        
        System.out.println("flickr API_key:");
        System.out.println(flickr.getApiKey());
        
        System.out.println("shared_secret:");
        System.out.println(flickr.getSharedSecret());
               
        PeopleInterface peopleInterface=flickr.getPeopleInterface();
        User usr= peopleInterface.getInfo("134585008@N05");
        
        System.out.println(usr.getDescription());
        System.out.println(usr.getLocation());
        
        TestInterface testInterface=flickr.getTestInterface();
        User usr2=testInterface.login();
        System.out.println(usr2.getId());
        //Random checks_outputs
        PhotosInterface photosInterface=flickr.getPhotosInterface();
        PhotoList<Photo> photoList=photosInterface.getWithGeoData(null, null, null, null, 0, "date-posted-asc", null, 5, 1);
        Photo photo=photoList.get(0);
        System.out.println(photo.getTitle());

        
        //Put tag in a photo try
        String[] tagarray = new String[1];
        tagarray[0]="first-tag";
        photosInterface.addTags(photo.getId(), tagarray);
        /*
        //Getting geo data-first try---WORKS
        
        System.out.println("Get Lat/Long Tries:");
        GeoInterface geoInterface=flickr.getGeoInterface();
        GeoData geodata=geoInterface.getLocation(photo.getId());
        System.out.println(geodata.getLatitude());
        System.out.println(geodata.getLongitude());
        
        //Turn tag from random user into a collection
        Photo volos=photosInterface.getPhoto("19663171063")        ;
        Collection<Tag> tagCol=volos.getTags();
        System.out.println("Col size:" +tagCol.size());
        */
        
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //OK let's try something hard let's do these steps:
        //Get ¬10 photos from geotag search based on lat/long(assume Volos_limani as our example)
        //Lat: 39.360084 Long: 22.936964
        //Get the tags from each one and add the tags to a List<tag>
        //Iterate the list and find the most used tag
        //Print the list to do checks    ˚˚˚˚tempList.setPage(i);˚˚˚˚˚˚
        /*
        //Set the params for the search
        SearchParameters volosParams=new SearchParameters();
        volosParams.setLatitude("39.360084");
        volosParams.setLongitude("22.936964");
        volosParams.setRadiusUnits("km");
        volosParams.setRadius(1);
        volosParams.setAccuracy(16);
        
        //Initialize some values
        PhotoList<Photo> volosList=photosInterface.search(volosParams, 100, 1);
        Photo tempPhoto=new Photo();
        List<String> tagSum=new ArrayList();
        String tempPhotoId;
        
        //Loop to make the TagSum list where all the tags are saved
        for(int i=0;i<=99;i++){
        	tempPhoto=photosInterface.getInfo(volosList.get(i).getId(),null);
        	System.out.println("Photo ID:" +tempPhoto.getId());
        	Collection<Tag> tagsCol=tempPhoto.getTags();
        	System.out.println("Collection size:" +tagsCol.size());
        	List<Tag> tagList=new ArrayList(tagsCol);
        	System.out.println("List size:" + tagList.size());
        	System.out.println(">>>>>>>>>>>>>>>");
        	if(tagList.size()>0){
        		for(int j=0; j<=tagList.size()-1;j++){
        		System.out.println(tagList.get(j));
        		tagSum.add(tagList.get(j).getValue());
        		}
        		
        	}
        	
        }
        
        System.out.println("Tagsum:" +tagSum);
        Collections.sort(tagSum);
        System.out.println("Tagsum sorted:"+tagSum );
        System.out.println("new stuff>>>>>>>>");
        
        //Create a set of tagSum to eliminate dups
        Collection<String> tagValues = new HashSet<String>(tagSum);
        System.out.println(tagValues);
        Map<String, Integer> frequencyMap = new HashMap<String, Integer>();
        
        //Create the map loop
        for (String tag : tagValues){
        	System.out.println(tag);
        	frequencyMap.put(tag, Collections.frequency(tagSum, tag));
        	
        	
        }
        System.out.println(frequencyMap);
        
        
        //Sort frequencyMap by value
        ValueComparator bvc=new ValueComparator(frequencyMap);
        TreeMap<String, Integer> sortedFrequencyMap=new TreeMap<String,Integer>(bvc);
        
        sortedFrequencyMap.putAll(frequencyMap);
        System.out.println("Sorted_Frequency_Map:" +sortedFrequencyMap);
        
        //Find the 3 most used tags of the sorted FrequencyMap
        //∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
        //Let the user decide if he wants to view 3 more every time
        //∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
        //Do some checks in case of end of tables etc
        //∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆

        
        
        
        //Scanner setup
        //Setup variables needed
        Scanner user_input=new Scanner(System.in);
        String inputValue="y";
        int iterator=0;
        boolean hasMoreTags=true;
        
        //Loop until user wants no more tags or until there are no more tags to show
        do{
        
        	
        	//Find 3 most used tags
        	//First make the keys into an array to iterate only the first values
        	String[] tagsArray= sortedFrequencyMap.keySet().toArray(new String[sortedFrequencyMap.size()]);
        	for(int i=iterator*4;i<=iterator*4+3;i++){
				if (i < tagsArray.length) {
					System.out.println(tagsArray[i]);
					if (i == tagsArray.length - 1) {

						System.out.println("There are no more tags to show!!!!");
						hasMoreTags = false;
					}
				}
                
        	}        	
			if (hasMoreTags) {
				System.out.println("Would you like to get more recommended tags?\nType n to exit or y to get 3 more");
				inputValue = user_input.next();
				while (!inputValue.equals("y")&& !inputValue.equalsIgnoreCase("n")) {
					System.out.println("Type either y or n plz");
					inputValue = user_input.next();
				}
        	}
        iterator++;	
        }while(inputValue.equals("y") && hasMoreTags);
        
        user_input.close();
        
        */
        
        //Try to make geo-search with bbox because the other geosearch does 1km
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //This search must do ±0.5km search around a specific lat/long
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //WORKS
        
        
        
        
        
        //Set Date
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal=Calendar.getInstance();
        Date maxDate=cal.getTime();
        System.out.println("Current Date Time : " + dateFormat.format(cal.getTime()));
        cal= Calendar.getInstance();
        cal.add(Calendar.YEAR,  -1);
        cal.add(Calendar.YEAR,  -1);
        cal.add(Calendar.YEAR,  -1);
        Date minDate=cal.getTime();
        
        //Set base_lat base_long to do tests from everywhere change manually
        DecimalFormat df=new DecimalFormat("0.000000##");
        double base_lat=59.413495;
        double base_long=-20.864491;		
        
        
        
        //Set search Params
        SearchParameters boxSearch=new SearchParameters();
        System.out.println("Maxdate" +maxDate);
        System.out.println("Mindate" +minDate);
        boxSearch.setMinUploadDate(minDate);
        boxSearch.setMaxUploadDate(maxDate);
        
        //Calculate max lat-long
        double max_lat=base_lat+0.005;
        double max_long=base_long+0.005;
        double min_lat=base_lat-0.005;
        double min_long=base_long-0.005;
        
        System.out.println("Max_lat String:" + df.format(max_lat));
        System.out.println("Max_long String:" + df.format(max_long));
      
        boxSearch.setBBox(df.format(min_long), df.format(min_lat), df.format(max_long), df.format(max_lat));
        boxSearch.setAccuracy(16);
        
        
        
        //Output Search results
        PhotoList<Photo> karaiskakiList=photosInterface.search(boxSearch, 10, 1);
        if(karaiskakiList.isEmpty()){
        	System.out.println("We couldn't find any photos close to that place");
        }
        else{
        //MUST CHECK IF PHOTOLIST EMPTY
        System.out.println(karaiskakiList.getPages());
        for(int i=0;i<=9;i++){
        	Photo huephoto=karaiskakiList.get(0);
        	System.out.println("SomePhotoUrL:" +huephoto.getUrl());
        }
        }
        
        
       
        
        
        
        
        //Try to add 2 photolists
        /*
        SearchParameters athensParams=new SearchParameters();
        athensParams.setText("Xanthi");
        PhotoList<Photo> volosList=photosInterface.search(volosParams,1,1);
        PhotoList<Photo> athensList=photosInterface.search(athensParams, 1, 1);
        System.out.println("volosList size:" + volosList.size());
        System.out.println("athensList size:" + athensList.size());
        volosList.addAll(athensList);
        System.out.println(volosList.size());
        Photo photoFirst=volosList.get(0);
        
        System.out.println(photoFirst.getUrl());
        photoFirst=athensList.get(0);
        System.out.println(photoFirst.getUrl());
        photoFirst=volosList.get(1);
        System.out.println(photoFirst.getUrl());
        */
        
        
        /*
        
        //Initialize some wanted values
        Photo tempPhoto=new Photo();
        //Do the search
		for (int i = 1; i <=5; i++) {
			PhotoList<Photo> volosList = photosInterface.search(volosParams,20, i);
            for (int j=1; j<=20;j++){
            	tempPhoto= volosList.get(j-1);
            	
            }
		}
        */
        
        
        
        //Create Photolist for xanthi 
        //Parameteres for search
        //Get's some random photo from xanthi SEARCHES WITH TEXT ---->XANTHI<------
        /*
        SearchParameters xanthiParameters=new SearchParameters();
        xanthiParameters.setText("Xanthi");
        
        PhotoList<Photo> xanthi=photosInterface.search(xanthiParameters,10,100);
        System.out.println(xanthi.size());
        Photo photo2=xanthi.get(0);
        System.out.println(photo2.getUrl());
        System.out.println(">>>>>>\n");
        */
        
        
        //Set parameters-example for lat lo-WORKS
        /*
        SearchParameters samothrakiPar=new SearchParameters();
        samothrakiPar.setLatitude("37.938241");
        samothrakiPar.setLongitude("23.648758");
        samothrakiPar.setRadiusUnits("km");
        samothrakiPar.setRadius(1);
        samothrakiPar.setAccuracy(16);
        */
        
        //Get Tag from mysamothraki photo--WORKS       
        /*System.out.println("Dokimi gia tin diki mu photo apo samothraki:");
        Photo taggedSamo=photosInterface.getPhoto("19805005313");
        System.out.println("The photo has the following url:" + taggedSamo.getUrl());
        Collection<Tag> samoPhotoTagsCollection=taggedSamo.getTags();
        System.out.println("Collection has:" + samoPhotoTagsCollection.size());
        List<Tag> list = new ArrayList<Tag>(samoPhotoTagsCollection);
        System.out.println("ArrayList has size:"+list.size());
        Tag the_tag=list.get(0);
        System.out.println(the_tag.getValue());
        the_tag=list.get(1);
        System.out.println(the_tag.getValue());
        */
        
        

        
        
        
        //Geo Lats etc tries
        //>>>>>
        //>>>>>>
        /*
        PhotoList<Photo> samothraki=photosInterface.search(samothrakiPar,10,1);
        System.out.println("Samothraki_Total_List:");
        System.out.println(samothraki.getTotal());
        float[] latArray;
        latArray=new float[10];
        float[] longArray;
        longArray=new float[10];
        
        if(samothraki.get(0)!=null){
        	Photo photo3=samothraki.get(0);
        	for(int i=0; i<10;i++){
        		photo3=samothraki.get(i);
        		GeoData geodat=geoInterface.getLocation(photo3.getId());
        		
        		
        		
        		latArray[i]= geodat.getLatitude();
        		longArray[i]=geodat.getLongitude();
        		
        		
 
        		//System.out.println(photo3.getUrl());
        		//GeoData geodat=geoInterface.getLocation(photo3.getId());
        		//System.out.println("Lat:"+geodat.getLatitude()+ "Long:" +geodat.getLongitude());
        	}
 
    		
        	
        	//Tables to check max/min Long-Lat values
       		System.out.println("First Lat Value");
    		System.out.println(latArray[0]);
    		System.out.println("Last Lat Value");
    		System.out.println(latArray[latArray.length-1]);
      		System.out.println("First Long Value");
    		System.out.println(longArray[0]);
    		System.out.println("Last Lat Value");
    		System.out.println(longArray[longArray.length-1]);
            
        }
         */
        
        
    }
    		
    		
    		
    		
    		

    
        static class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;
        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.    
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
    
    public static void main(String[] args) throws FlickrException {
        try {
            AuthExample.auth();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
        
        
    }
}