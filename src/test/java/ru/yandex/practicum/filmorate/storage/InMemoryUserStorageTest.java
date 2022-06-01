package ru.yandex.practicum.filmorate.storage;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest extends UserStorageTest<InMemoryUserStorage>{

    public InMemoryUserStorageTest() { this.storage = new InMemoryUserStorage(); }

}