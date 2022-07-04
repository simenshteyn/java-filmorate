package ru.yandex.practicum.filmorate.storage;

class InMemoryFilmStorageTest extends FilmStorageTest<InMemoryFilmStorage>{

    public InMemoryFilmStorageTest() { this.storage = new InMemoryFilmStorage(); }

}
