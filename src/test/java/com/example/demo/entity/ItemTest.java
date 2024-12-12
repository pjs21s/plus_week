package com.example.demo.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void itemTest(){
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);

        Assertions.assertEquals(null, item.getStatus());
    }
}
