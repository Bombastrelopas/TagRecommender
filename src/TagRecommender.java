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
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;























import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Set;
import java.util.TreeMap;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class TagRecommender extends JDialog  implements ActionListener {
	
	
    private static JDialog dialog_url_frame=new JDialog();
	private static String tokenKey="";
	static int image_index=0;
	static int[] userSelectedTags=new int[80];
	static boolean wantsMore=true;
	static boolean hasEnoughPhotos=true;
	
	public static void authorization() throws IOException, FlickrException{
		
	}
	
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
        
  
        int numberphotos=Integer.parseInt(properties.getProperty("numberphotos"));
     
        //Initiate Interfaces
        Flickr flickr = new Flickr(properties.getProperty("apiKey"), properties.getProperty("secret"), new REST());
        Flickr.debugStream = false;
        AuthInterface authInterface = flickr.getAuthInterface();
        PhotosInterface photosInterface=flickr.getPhotosInterface();
        GeoInterface geoInterface=flickr.getGeoInterface();
        Scanner scanner = new Scanner(System.in);
        Token requestToken=new Token("nothing","nothing");
        
        
        
        //Authorization variables
        Boolean authorized=false;
        
        
        //Authorization step 1
        Token token = authInterface.getRequestToken();
        //Step 2
        String url = authInterface.getAuthorizationUrl(token, Permission.WRITE);
        JOptionPane.showMessageDialog(null, "Welcome to Tag Recommender an application which recommends tags for your flickr geo-tagged photos. Press OK to begin the login proccess.", "TagRecommender", JOptionPane.INFORMATION_MESSAGE);

        JFrame mainWindow=new JFrame();
        mainWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        
       
        final JTextArea input_pass=new JTextArea(1,20);
        input_pass.setVisible(true);
        input_pass.setSize(100,100);
        input_pass.setRows(1);
        input_pass.setFocusable(true);
        input_pass.setName("Type the code given here");
        
        
        
        JTextPane url_pane=new JTextPane();
        
        url_pane.setContentType("text");
        url_pane.setText("Copy the following URL to the browser get the key and put it at the box below\n\n" +url);
        url_pane.setEditable(false);
    
        url_pane.setBackground(null);
        url_pane.setBorder(null);
        url_pane.setSize(700, 50);
        
        
        JButton okButton = new JButton("Submit Pass");
        okButton.setVisible(true);
        okButton.setSize(100,100);
        okButton.addActionListener(new ActionListener(){
        	 public void actionPerformed(ActionEvent e) {
        		 //System.out.println(input_pass.getText());
        		 tokenKey=input_pass.getText();
        		 dialog_url_frame.dispose();
        		 
        		 
        	 }
        });
        
        JButton exitButton = new JButton("Exit the application");
        exitButton.setVisible(true);
        exitButton.setSize(100,100);
        exitButton.addActionListener(new ActionListener(){
        	 public void actionPerformed(ActionEvent e) {
                 
        		 System.exit(0);
        		 
        		 
        	 }
        });
 

        
        JPanel panel=new JPanel();
        panel.add(url_pane);
        panel.add(input_pass);
        panel.add(okButton);
        panel.add(exitButton);
        
        
        dialog_url_frame.setTitle("TagRecommender");
        dialog_url_frame.setModal(true);
        dialog_url_frame.setSize(800, 150);
        dialog_url_frame.add(panel);
        dialog_url_frame.setVisible(true);
        dialog_url_frame.pack();
        dialog_url_frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        
                
        //Token Retrieval step 3
        while(!authorized){
        try{
        requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
        authorized=true;
        } catch (OAuthException e){
            JOptionPane.showMessageDialog(null, "You put wrong code! Please try again with the new URL given", "TagRecommender", JOptionPane.INFORMATION_MESSAGE);

        	//Authorization step 1
            token = authInterface.getRequestToken();
            //Step 2
            input_pass.setText("");
            url = authInterface.getAuthorizationUrl(token, Permission.WRITE);
            url_pane.setText(url);
            dialog_url_frame.setVisible(true);
            
          
          
            
        }
        
        }
        //Check auth
        System.out.println("Authentication success\n");
        
        

        
        
        
        
        
        
        Auth auth = authInterface.checkToken(requestToken);
        RequestContext.getRequestContext().setAuth(auth);
        
        // Some CHecks to see if everything works OK
        System.out.println("Token: " + requestToken.getToken());
        System.out.println("Secret: " + requestToken.getSecret());
        System.out.println("nsid: " + auth.getUser().getId());
        System.out.println("Realname: " + auth.getUser().getRealName());
        System.out.println("Username: " + auth.getUser().getUsername());
        System.out.println("Permission: " + auth.getPermission().getType());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        
        //Give a hello message to the user (get the real user's name).
        JOptionPane.showMessageDialog(null, "Welcome "  +auth.getUser().getRealName() + ", on the next dialog click on the photo for which one you want to get recommended tags", "TagRecommender", JOptionPane.INFORMATION_MESSAGE);
        
        
        while(wantsMore){
        hasEnoughPhotos=true;
        //Get a photolist of all the geotagged photos of the user
        //User should then see the photos on his screen and choose which one he wants
        PhotoList<Photo> userGeotaggedPhotolist=photosInterface.getWithGeoData(null, null, null, null, 0, "date-posted-desc", null, 5, 1);
        Photo tempPhoto=new Photo();
        System.out.println("Size of list:" +userGeotaggedPhotolist.size());
        
        //Create the ImageArray and an ID array to connect image-ID
        final BufferedImage[] imageArray=new BufferedImage[userGeotaggedPhotolist.size()];
        final String[] idArray=new String[userGeotaggedPhotolist.size()];
        
        for(int i=0;i<=userGeotaggedPhotolist.size()-1;i++){
        	tempPhoto=userGeotaggedPhotolist.get(i);
        	System.out.println(tempPhoto.getTitle());
        	//https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
        	imageArray[i]=photosInterface.getImage("https://farm"+tempPhoto.getFarm()+".staticflickr.com/"+tempPhoto.getServer()+"/"+tempPhoto.getId()+ "_" +tempPhoto.getSecret()+".jpg");
        	idArray[i]=tempPhoto.getId();
        	
        	System.out.println(imageArray[i]);
        	System.out.println(tempPhoto.getFarm());
        	System.out.println(tempPhoto.getServer());
        	System.out.println(tempPhoto.getId());
        	System.out.println(tempPhoto.getSecret());
        	
        	
        	
 
        }
        //Turn buffered image array to icon array
        ImageIcon[] iconArray=new ImageIcon[imageArray.length];
        System.out.println("Imagearraylength" +imageArray.length);
        for(int i=0; i<=imageArray.length-1; i++){
        	iconArray[i]=new ImageIcon(imageArray[i]);
        }
        
        
        
        //Create the image-selection-dialog
        //ˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆ
        //•••••••••••••••••••••••••••••••••
        
        //Image_list
        final JList list=new JList(iconArray);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener(){
        	
        	

        	@Override
			public void valueChanged(ListSelectionEvent arg0) {
        		
				if (!arg0.getValueIsAdjusting()){
        			System.out.println(list.getSelectedIndex());
        			image_index=list.getSelectedIndex();
        		}
			}
        });
        
        //Text to tell the user what to do
        JTextPane image_text=new JTextPane();
        image_text.setSize(1000, 20);
        image_text.setText("Choose the image to get tags for:");
        image_text.setVisible(true);
        image_text.setEditable(false);
        
        //ScrollPane
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setSize(400, 400);
        
        //A text pane for the waitDialog
        JTextPane pleaseWait=new JTextPane();
        pleaseWait.setSize(500,50);
        pleaseWait.setText("Please wait the program is working...");
        pleaseWait.setVisible(true);
        pleaseWait.setEditable(false);
        
        //Dialog used
        final JDialog medialog=new JDialog();
        
        
        //A dialog for when the user is waiting
        final JDialog waitDialog=new JDialog();
        waitDialog.setSize(500,50);
        waitDialog.setVisible(false);
        waitDialog.setModal(false);
        waitDialog.setTitle("Tag Recommender");
        waitDialog.add(pleaseWait);
        
        
        
        //SubmitButton
        final JButton submitImageButton=new JButton();
        submitImageButton.setSize(100, 100);
        submitImageButton.setText("Click here when you have selected a photo");
        submitImageButton.setAlignmentX(RIGHT_ALIGNMENT);
        submitImageButton.addActionListener(new ActionListener(){
       	 public void actionPerformed(ActionEvent e) {
    		   System.out.println("Button was pressed");
    		   System.out.println(image_index);
    		   medialog.dispose();
    		   waitDialog.setVisible(true);
    		   
    		   
    	 }
    });
        
        
        JPanel imagePane=new JPanel(new BorderLayout());
        imagePane.add(image_text,BorderLayout.PAGE_START);
        imagePane.add(scrollPane,BorderLayout.CENTER);
        imagePane.add(submitImageButton,BorderLayout.PAGE_END);
        
        //Dialog properties
        medialog.add(imagePane);
        medialog.setModal(true);
        medialog.setSize(500, 700);
        medialog.setVisible(true);

        
        //Here we get the lat/long of the photo of which the ID we found
        //ˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆˆ
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        GeoData geodata=geoInterface.getLocation(idArray[image_index]);
        System.out.println(geodata.getLatitude());
        System.out.println(geodata.getLongitude());
        
        
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //Make BBox Search around the lat/long found
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø

        
        /*
        //Set the params for the search
        SearchParameters photoParameters=new SearchParameters();
        photoParameters.setLatitude(Float.toString(geodata.getLatitude()));
        photoParameters.setLongitude(Float.toString(geodata.getLongitude()));
        */
        
        
        //Make geosearch with bbox
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //This search must do ±0.5km search around a specific lat/long
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        
        
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
        
        
        //Set base_lat base_long 
        DecimalFormat df=new DecimalFormat("0.0000##");
        double base_lat=geodata.getLatitude();
        double base_long=geodata.getLongitude();
        /*
        System.out.println(">>>>>>>>>");
        System.out.printf("%.6f", base_lat);
        System.out.println(">>>>>>>");
        System.out.printf("%.6f", base_long);
        System.out.println(">>>>>>>>>>>");
        */
        
        
        
        
        //Set search Params
        SearchParameters boxSearch=new SearchParameters();
        /*
        System.out.println("Maxdate" +maxDate);
        System.out.println("Mindate" +minDate);
        */
        boxSearch.setMinUploadDate(minDate);
        boxSearch.setMaxUploadDate(maxDate);
        
        //Calculate max lat-long
        double max_lat=base_lat+0.005;
        double max_long=base_long+0.005;
        double min_lat=base_lat-0.005;
        double min_long=base_long-0.005;
        
        /*
        System.out.println("Max_lat String:" + df.format(max_lat));
        System.out.println("Max_long String:" + df.format(max_long));
        System.out.println("Min_lat:" +df.format(min_lat));
        System.out.println("min_long:" + df.format(min_long));
        */
        boxSearch.setBBox(df.format(min_long), df.format(min_lat), df.format(max_long), df.format(max_lat));
        boxSearch.setAccuracy(16);
        
        
        
        //Find results based on searchparameters
        //If boxxed search yields no result use bigger search (1km)
        //
        //øøøøøøøøøøøøøøøøøøøøøøøøøøøøøø
        //
        //
        PhotoList<Photo> nearPhotoList=photosInterface.search(boxSearch, numberphotos, 1);
        if(nearPhotoList.size()<10){
        	System.out.println(nearPhotoList.size());
        	System.out.println("There are not enough photos near that place where your photo was taken");
        	waitDialog.dispose();
        	hasEnoughPhotos=false;
        }
        else{
        	
        	System.out.println(nearPhotoList.size());
        	
        }
        
        if(hasEnoughPhotos){
        //Initialize some values
        Photo tempPhoto2=new Photo();
        List<String> tagSum=new ArrayList();
        String tempPhotoId;
        
        //Loop to make the TagSum list where all the tags are saved
        for(int i=0;i<=nearPhotoList.size()-1;i++){
        	tempPhoto2=photosInterface.getInfo(nearPhotoList.get(i).getId(),null);
        	System.out.println("Photo ID:" +tempPhoto2.getId());
        	Collection<Tag> tagsCol=tempPhoto2.getTags();
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
        
        //System out checks
        Collections.sort(tagSum);
        /*
        System.out.println("Tagsum:" +tagSum);
        System.out.println("Tagsum sorted:"+tagSum );
        System.out.println("new stuff>>>>>>>>");
        */
        
        
        //Create a set of tagSum to eliminate dups
        Collection<String> tagValues = new HashSet<String>(tagSum);
        /*
        System.out.println(tagValues);
        */
        Map<String, Integer> frequencyMap = new HashMap<String, Integer>();
        
        
        //Create the map loop
        for (String tag : tagValues){
        	/*
        	System.out.println(tag);
        	*/
        	frequencyMap.put(tag, Collections.frequency(tagSum, tag));
        	
        	
        }
        /*
        
        System.out.println(frequencyMap);
       */

        //Sort frequencyMap by value
        ValueComparator bvc=new ValueComparator(frequencyMap);
        TreeMap<String, Integer> sortedFrequencyMap=new TreeMap<String,Integer>(bvc);
        sortedFrequencyMap.putAll(frequencyMap);
        System.out.println("Sorted_Frequency_Map:" +sortedFrequencyMap);
        
        
        //Now that we have a sortedFrequencymap with keys=tag_name values=# appearence
        //∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
        //We get the sorted key values and make them appear to the user
        //∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
        //Do some checks in case of end of tables etc
        //∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
        
        //Make an array from the keys of the sorted map
        Set<String> tagKeys=sortedFrequencyMap.keySet();
        String[] tagArr=tagKeys.toArray(new String[sortedFrequencyMap.size()]);
        
        //Make a Jdialog which will allow you to choose tags and input them
        //To your photo
        
        
        
        JList taglist=new JList(tagArr);
        taglist.setSize(700, 700);
        taglist.setAutoscrolls(true);
        
        JScrollPane tagscrollPane = new JScrollPane(taglist);
        scrollPane.setSize(400, 400);
        
        waitDialog.setVisible(false);
        
        JOptionPane.showMessageDialog(null, "You can now choose the tags to add to your photo. Use ctrl(command on mac) to add more than 1.", "TagRecommender", JOptionPane.INFORMATION_MESSAGE);

        
        JOptionPane.showMessageDialog(null, tagscrollPane, "Select the tags you want", JOptionPane.PLAIN_MESSAGE);
        System.out.println(Arrays.toString(taglist.getSelectedIndices()));
        int indexArray[]=taglist.getSelectedIndices();
        
        String chosenTagNames[]=new String[indexArray.length];
        //Now that we have the indexes of the tags 
        //We will add them to the photo the user chose on the previous dialog
        for (int j=0;j<=indexArray.length-1;j++){
        	chosenTagNames[j]=tagArr[indexArray[j]];
        	
        }
        
        photosInterface.addTags(idArray[image_index], chosenTagNames);
        
        
        JOptionPane.showMessageDialog(null, "Your  tags have been added", "TagRecommender", JOptionPane.INFORMATION_MESSAGE);
        }
        if (JOptionPane.showConfirmDialog(null, "You want to try another photo?", "WARNING",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                
        } else {
        	   wantsMore=false;
               JOptionPane.showMessageDialog(null, "Have a nice day :>", "TagRecommender", JOptionPane.INFORMATION_MESSAGE);

        	   scanner.close();
               System.exit(0);
               
        }
        
        }

        
        
        
        
       
  
        
      
        
        
        
        
        
         
        
                
	}
	
	
	public static void main(String[] args) throws FlickrException {
		
		try {
			TagRecommender.auth();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
		
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
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

}
        
        
        
        
        
        
        
        
        
        
