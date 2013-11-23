package edu.uno.csci4661.grocerylist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.net.URLEncoder;

import edu.uno.csci4661.grocerylist.deviceinfoendpoint.Deviceinfoendpoint;
import edu.uno.csci4661.grocerylist.deviceinfoendpoint.model.DeviceInfo;

/**
 * This class is started up as a service of the Android application. It listens
 * for Google Cloud Messaging (GCM) messages directed to this device.
 * <p/>
 * When the device is successfully registered for GCM, a message is sent to the
 * App Engine backend via Cloud Endpoints, indicating that it wants to receive
 * broadcast messages from the it.
 * <p/>
 * Before registering for GCM, you have to create a project in Google's Cloud
 * Console (https://code.google.com/apis/console). In this project, you'll have
 * to enable the "Google Cloud Messaging for Android" Service.
 * <p/>
 * Once you have set up a project and enabled GCM, you'll have to set the
 * PROJECT_NUMBER field to the project number mentioned in the "Overview" page.
 * <p/>
 * See the documentation at
 * http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class GCMIntentService extends GCMBaseIntentService {

    protected static final String PROJECT_NUMBER = "757292935474";

    private final Deviceinfoendpoint endpoint;

    private static final String GCM_MESSAGE_KEY = "message";
    private static final String GCM_ITEM_ADDED_MESSAGE = "new_grocery_item_added";

    public static final String GCM_ITEM_ADDED_INTENT = "edu.uno.csci4661.grocerylist.new_item_added";

    public GCMIntentService() {
        super(PROJECT_NUMBER);
        Deviceinfoendpoint.Builder endpointBuilder = new Deviceinfoendpoint.Builder(
                AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
                new HttpRequestInitializer() {
                    public void initialize(HttpRequest httpRequest) {
                    }
                });
        endpoint = CloudEndpointUtils.updateBuilder(endpointBuilder).build();
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        if (intent.getStringExtra(GCM_MESSAGE_KEY).equals(GCM_ITEM_ADDED_MESSAGE)) {
            context.sendBroadcast(new Intent(GCM_ITEM_ADDED_INTENT));
        }
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.e("grocery_list", "GCM Error: " + errorId);
    }

    /**
     * Called back when the Google Cloud Messaging service has unregistered the
     * device.
     *
     * @param context the Context
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        if (registrationId != null && registrationId.length() > 0) {
            try {
                endpoint.removeDeviceInfo(registrationId).execute();
            } catch (IOException e) { /* left blank */}
        }
    }

    @Override
    public void onRegistered(Context context, String registration) {
        /*
         * This is some special exception-handling code that we're using to work around a problem
         * with the DevAppServer and methods that return null in App Engine 1.7.5.
         */
        boolean alreadyRegisteredWithEndpointServer = false;

        try {

          /*
           * Using cloud endpoints, see if the device has already been
           * registered with the backend
           */
            DeviceInfo existingInfo = endpoint.getDeviceInfo(registration).execute();

            if (existingInfo != null && registration.equals(existingInfo.getDeviceRegistrationID())) {
                alreadyRegisteredWithEndpointServer = true;
            }
        } catch (IOException e) {
            // Ignore
        }

        try {
            if (!alreadyRegisteredWithEndpointServer) {
                /*
                 * We are not registered as yet. Send an endpoint message
                 * containing the GCM registration id and some of the device's
                 * product information over to the backend. Then, we'll be
                 * registered.
                 */
                SharedPreferences preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);

                DeviceInfo deviceInfo = new DeviceInfo();

                deviceInfo
                        .setUserEmail(preferences.getString("user", null))
                        .setDeviceRegistrationID(registration)
                        .setTimestamp(System.currentTimeMillis())
                        .setDeviceInformation(
                                URLEncoder
                                        .encode(android.os.Build.MANUFACTURER
                                                + " "
                                                + android.os.Build.PRODUCT,
                                                "UTF-8"));

                endpoint.insertDeviceInfo(deviceInfo).execute();
            }
        } catch (IOException e) {
            Log.e(GCMIntentService.class.getName(),
                    "Exception received when attempting to register with server at "
                            + endpoint.getRootUrl(), e);
        }
    }

    /**
     * Register the device for GCM.
     *
     * @param mContext the activity's context.
     */
    public static void register(Context mContext) {
        GCMRegistrar.checkDevice(mContext);
        GCMRegistrar.checkManifest(mContext);
        GCMRegistrar.register(mContext, PROJECT_NUMBER);
    }

    /**
     * Unregister the device from the GCM service.
     *
     * @param mContext the activity's context.
     */
    public static void unregister(Context mContext) {
        GCMRegistrar.unregister(mContext);
    }
}
