import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.test.TestInterface;
import com.flickr4java.flickr.util.IOUtilities;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

/**
 * Demonstrates the authentication-process.
 * <p>
 * 
 * If you registered API keys, you find them with the shared secret at your <a href="http://www.flickr.com/services/api/registered_keys.gne">list of API
 * keys</a>
 * 
 * @author mago
 * @version $Id: AuthExample.java,v 1.6 2009/08/25 19:37:45 x-mago Exp $
 */
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

        String url = authInterface.getAuthorizationUrl(token, Permission.DELETE);
        System.out.println("Follow this URL to authorise yourself on Flickr");
        System.out.println(url);
        System.out.println("Paste in the token it gives you:");
        System.out.print(">>");

        String tokenKey = scanner.nextLine();
        scanner.close();
        
        //Token Retrieval
        Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
        System.out.println("Authentication success\n");
        
        //Stuff happening after authentication
        Auth auth = authInterface.checkToken(requestToken);
        // This token can be used until the user revokes it.
        System.out.println("Token: " + requestToken.getToken());
        System.out.println("Secret: " + requestToken.getSecret());
        System.out.println("nsid: " + auth.getUser().getId());
        System.out.println("Realname: " + auth.getUser().getRealName());
        System.out.println("Username: " + auth.getUser().getUsername());
        System.out.println("Permission: " + auth.getPermission().getType());
        //
        //Problem
        //
        Token accessToken=authInterface.exchangeAuthToken(auth.getToken());
        ///
        //
        System.out.println("Token: " + accessToken.getToken());
        System.out.println("Secret: " + accessToken.getSecret());
        System.out.println(accessToken);
    }

    public static void main(String[] args) throws FlickrException {
        try {
            AuthExample.auth();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String apiKey = "3f7046fe0897516df587cc3e6226f878";
        String sharedSecret = "9d0ceef5f2f3040f";
        Flickr f = new Flickr(apiKey, sharedSecret, new REST());
        TestInterface testInterface = f.getTestInterface();
        java.util.Collection<Element> results = testInterface.echo(Collections.EMPTY_MAP);
        
        // User usr=testInterface.login();
        //System.out.println(usr);
        System.out.println(f);
        System.out.println(testInterface);
        System.out.println(results);
        System.exit(0);
        
        
    }
}