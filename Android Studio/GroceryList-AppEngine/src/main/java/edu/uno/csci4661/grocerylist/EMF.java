package edu.uno.csci4661.grocerylist;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
    private static final EntityManagerFactory INSTANCE =
            Persistence.createEntityManagerFactory("transactions-optional");

    private EMF() {
    }

    public static EntityManagerFactory get() {
        return INSTANCE;
    }
}