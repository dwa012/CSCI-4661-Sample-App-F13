package edu.uno.csci4661.grocerylist;

public class Constants {

    /*
     * You will need to create 3 registered apps in the Cloud Console for your project.
     * 1. A Web application that will serve as the backend.
     * 2. A Web application to serve as a web client applicaiton.
     * 3. A Android application (Using the fingerprint of the keystore)
     */

    public static final String WEB_CLIENT_ID = "CLIENT_ID_WEB_CLIENT";
    public static final String ANDROID_CLIENT_ID = "CLIENT_ID_ANDROID_APP";
    public static final String IOS_CLIENT_ID = "NOT_USED_FOR_THIS_PROJECT";
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;

    // Only requesting the email address for a Google user
    public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

    /*
    * TODO: Fill this in with the server key that you've obtained from the API
    * Console (https://code.google.com/apis/console). This is required for using
    * Google Cloud Messaging from your AppEngine application even if you are
    * using a App Engine's local development server.
    *
    * This is the Server API key from another  Web Application (Which will be the backend App).
    */
    static final String API_KEY = "API_KEY_FOR_THE_SERVER_WEB_APP";
}
