package com.sujayt123.service;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by sujay on 8/15/17.
 */
public class DbServiceTest {
    @Test
    public void tests() throws Exception {
        DbService db = new DbService();

        db.deleteExistingAccount("hello");
        db.deleteExistingAccount("hello2");

        /* Set up dummy accounts */
        assertTrue(db.createNewAccount("hello", "world"));
        assertTrue(db.createNewAccount("hello2", "world2"));
        assertFalse(db.createNewAccount("CPU", "password"));
        assertFalse(db.createNewAccount("hello", "password"));
        assertFalse(db.createNewAccount("hello2", "password"));


        /* Log in to dummy accounts */
        Optional<Integer> hello_id_opt_fail = db.login("hello", "world2");
        assertFalse(hello_id_opt_fail.isPresent());

        Optional<Integer> hello_id_opt_works = db.login("hello", "world");
        assertTrue(hello_id_opt_works.isPresent());

        Optional<Integer> hello2_id_opt_fail = db.login("hello2", "world");
        assertFalse(hello2_id_opt_fail.isPresent());

        Optional<Integer> hello2_id_opt_works = db.login("hello2", "world2");
        assertTrue(hello2_id_opt_works.isPresent());

        /* Create games involving dummy players */
        assertFalse(db.createNewGame(hello_id_opt_works.get(), "notaRealPlayer"));
        assertTrue(db.createNewGame(hello_id_opt_works.get(), "CPU"));
        assertTrue(db.createNewGame(hello2_id_opt_works.get(), "CPU"));
        assertTrue(db.createNewGame(hello2_id_opt_works.get(), "hello"));

        System.out.println(Arrays.toString(db.getGamesForPlayer(hello_id_opt_works.get()).get()));
        System.out.println(Arrays.toString(db.getGamesForPlayer(hello2_id_opt_works.get()).get()));
        System.out.println(Arrays.toString(db.getGamesForPlayer(1).get()));

        /* Remove dummy accounts */
        assertTrue(db.deleteExistingAccount(hello_id_opt_works.get()));
        assertTrue(db.deleteExistingAccount(hello2_id_opt_works.get()));

    }

}