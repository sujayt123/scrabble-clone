package service;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sujay on 8/15/17.
 */
public class DbServiceTest {
    @Test
    public void tests() throws Exception {
        DbService db = new DbService();
        db.deleteExistingAccount("hello", "world");
        db.deleteExistingAccount("hello2", "world2");

        assertTrue(db.createNewAccount("hello", "world"));
        assertTrue(db.createNewAccount("hello2", "world2"));
        assertFalse(db.createNewAccount("CPU", "password"));
        assertFalse(db.createNewAccount("hello", "password"));
        assertFalse(db.createNewAccount("hello2", "password"));

        assertTrue(db.login("hello", "world"));
        assertFalse(db.login("hello", "world2"));

        assertTrue(db.login("hello2", "world2"));
        assertFalse(db.login("hello2", "world"));

        assertFalse(db.deleteExistingAccount("hello", "world2"));
        assertFalse(db.deleteExistingAccount("hello2", "world"));
        assertTrue(db.deleteExistingAccount("hello", "world"));
        assertTrue(db.deleteExistingAccount("hello2", "world2"));

    }

}