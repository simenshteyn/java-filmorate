package ru.yandex.practicum.filmorate.storage;

class InMemoryUserStorageTest extends UserStorageTest<InMemoryUserStorage>{

    public InMemoryUserStorageTest() { this.storage = new InMemoryUserStorage(); }

}
