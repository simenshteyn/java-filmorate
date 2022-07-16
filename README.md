# java-filmorate
## Схема хранилища
![Database scheme](/docs/Java_10_fp.png)

Ссылка: [https://dbdiagram.io/d/62b2df7069be0b672c1b1627](https://dbdiagram.io/d/62b2df7069be0b672c1b1627)

* За хранение фильмов и пользователей отвечают таблицы <code>users</code> и <code>films</code>;
* Режиссеры и отзывы содержатся в таблицах <code>directors</code> и <code>reviews</code>;
* Таблица <code>film_directors</code> содержит информацию о принадлежности фильмов к режиссерам;
* Оценки отзывов содержатся в таблице <code>reviews_feedback</code>, ключ составной (user_id, review_id);
* Таблица <code>films_liked</code> хранит лайки пользователей по фильмам, ключ составной;
* Таблица <code>friendships</code> хранит запросы на добавление в друзья между пользователями и их статусы, ключ составной по двум id пользователей;
* Таблица <code>film_genres</code> содержит информацию о принадлежности фильма к конкретному жанру, ключ составной, жанров у одного фильма может быть несколько;
* Таблицы <code>ratings</code> и <code>genres</code> включают рейтинги и жанры фильмов;
* Таблица <code>events</code> содержит информацию о событиях, <code>event_types</code> и <code>event_operations</code> — типы событий и операций;