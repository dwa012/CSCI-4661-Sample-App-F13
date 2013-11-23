package edu.uno.csci4661.grocerylist;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

@Api(
        name = "groceryitemendpoint",
        namespace = @ApiNamespace(
                ownerDomain = "uno.edu",
                ownerName = "uno.edu",
                packagePath = "csci4661.grocerylist"
        ),
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE}
)
public class GroceryItemEndpoint {

    /**
     * This method lists all the entities inserted in datastore.
     * It uses HTTP GET method and paging support.
     *
     * @return A CollectionResponse class containing the list of all entities
     * persisted and a cursor to the next page.
     */
    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "listGroceryItem")
    public CollectionResponse<GroceryItem> listGroceryItem(
            @Nullable @Named("cursor") String cursorString,
            @Nullable @Named("limit") Integer limit, User user) throws OAuthRequestException {

        if (user == null) {
            throw new OAuthRequestException("User was not authorized");
        }

        EntityManager mgr = null;
        List<GroceryItem> execute = null;

        try {
            mgr = getEntityManager();
            Query query = mgr.createQuery("select from GroceryItem as GroceryItem where userId = :userEmail");
            query.setParameter("userEmail", user.getEmail());
            Cursor cursor;
            if (cursorString != null && cursorString.trim().length() > 0) {
                cursor = Cursor.fromWebSafeString(cursorString);
                query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
            }

            if (limit != null) {
                query.setFirstResult(0);
                query.setMaxResults(limit);
            }

            execute = (List<GroceryItem>) query.getResultList();
            cursor = JPACursorHelper.getCursor(execute);
            if (cursor != null) cursorString = cursor.toWebSafeString();

            // Tight loop for fetching all entities from datastore and accomodate
            // for lazy fetch.
            for (GroceryItem obj : execute) ;
        } finally {
            if (mgr != null) {
                mgr.close();
            }
        }

        return CollectionResponse.<GroceryItem>builder()
                .setItems(execute)
                .setNextPageToken(cursorString)
                .build();
    }

    /**
     * This method gets the entity having primary key id. It uses HTTP GET method.
     *
     * @param id the primary key of the java bean.
     * @return The entity with primary key id.
     */
    @ApiMethod(name = "getGroceryItem")
    public GroceryItem getGroceryItem(@Named("id") Long id, User user) throws OAuthRequestException {

        if (user == null) {
            throw new OAuthRequestException("User was not authorized");
        }

        EntityManager mgr = getEntityManager();
        GroceryItem groceryItem = null;
        try {
            groceryItem = mgr.find(GroceryItem.class, id);

            if (!groceryItem.getUserId().equals(user.getUserId())) {
                groceryItem = null;
            }
        } finally {
            mgr.close();
        }
        return groceryItem;
    }

    /**
     * This inserts a new entity into App Engine datastore. If the entity already
     * exists in the datastore, an exception is thrown.
     * It uses HTTP POST method.
     *
     * @param groceryItem the entity to be inserted.
     * @return The inserted entity.
     */
    @ApiMethod(name = "insertGroceryItem")
    public GroceryItem insertGroceryItem(GroceryItem groceryItem, User user) throws OAuthRequestException {

        if (user == null) {
            throw new OAuthRequestException("User was not authorized");
        }

        groceryItem.setUserId(user.getEmail());

        EntityManager mgr = getEntityManager();
        try {
            if (containsGroceryItem(groceryItem)) {
                throw new EntityExistsException("Object already exists");
            }
            mgr.persist(groceryItem);

            Sender sender = new Sender(Constants.API_KEY);
            CollectionResponse<DeviceInfo> response = endpoint.listDeviceInfo(null, 10, user);
            for (DeviceInfo deviceInfo : response.getItems()) {
                try {
                    doSendViaGcm("new_grocery_item_added", sender, deviceInfo);
                } catch (Exception e) { /* left blank */ }

            }
        } finally {
            mgr.close();
        }

        return groceryItem;
    }

    /**
     * This method is used for updating an existing entity. If the entity does not
     * exist in the datastore, an exception is thrown.
     * It uses HTTP PUT method.
     *
     * @param groceryItem the entity to be updated.
     * @return The updated entity.
     */
    @ApiMethod(name = "updateGroceryItem")
    public GroceryItem updateGroceryItem(GroceryItem groceryItem, User user) throws OAuthRequestException {

        if (user == null) {
            throw new OAuthRequestException("User was not authorized");
        }

        EntityManager mgr = getEntityManager();
        try {
            if (!containsGroceryItem(groceryItem)) {
                throw new EntityNotFoundException("Object does not exist");
            }
            mgr.persist(groceryItem);
        } finally {
            mgr.close();
        }
        return groceryItem;
    }

    /**
     * This method removes the entity with primary key id.
     * It uses HTTP DELETE method.
     *
     * @param id the primary key of the entity to be deleted.
     * @return The deleted entity.
     */
    @ApiMethod(name = "removeGroceryItem")
    public GroceryItem removeGroceryItem(@Named("id") Long id, User user) throws OAuthRequestException {

        if (user == null) {
            throw new OAuthRequestException("User was not authorized");
        }

        EntityManager mgr = getEntityManager();
        GroceryItem groceryItem = null;
        try {
            groceryItem = mgr.find(GroceryItem.class, id);
            mgr.remove(groceryItem);
        } finally {
            mgr.close();
        }
        return groceryItem;
    }

    private boolean containsGroceryItem(GroceryItem groceryItem) {
        EntityManager mgr = getEntityManager();

        if (groceryItem.getId() == null)
            return false;

        boolean contains = true;

        try {
            GroceryItem item = mgr.find(GroceryItem.class, groceryItem.getId());
            if (item == null) {
                contains = false;
            }
        } finally {
            mgr.close();
        }
        return contains;
    }

    private static final DeviceInfoEndpoint endpoint = new DeviceInfoEndpoint();

    private static Result doSendViaGcm(String message, Sender sender, DeviceInfo deviceInfo) throws IOException {
        // Trim message if needed.
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }

        // This message object is a Google Cloud Messaging object, it is NOT
        // related to the MessageData class
        Message msg = new Message.Builder().addData("message", message).build();
        Result result = sender.send(msg, deviceInfo.getDeviceRegistrationID(), 5);
        if (result.getMessageId() != null) {
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
                deviceInfo.setDeviceRegistrationID(canonicalRegId);
                endpoint.insertDeviceInfo(deviceInfo);
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(com.google.android.gcm.server.Constants.ERROR_NOT_REGISTERED)) {
                endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
            }
        }

        return result;
    }

    private static EntityManager getEntityManager() {
        return EMF.get().createEntityManager();
    }

}
