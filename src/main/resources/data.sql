-- DELETE FROM genres;
-- DELETE FROM ratings;
-- DELETE FROM event_types;
-- DELETE FROM event_operations;
-- DELETE FROM users;
-- DELETE FROM friendships;
-- DELETE FROM films;
-- DELETE FROM FILMS_LIKED;
-- DELETE FROM REVIEWS;
-- DELETE FROM REVIEWS_FEEDBACK;
-- DELETE FROM DIRECTORS;
--
-- ALTER SEQUENCE SYSTEM_SEQUENCE_61B36BAF_DEC4_4A86_B7E3_327C3FDE9132 RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_794384CA_9CF5_4EEB_ACFA_909F78B6599C RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_914999C6_53C2_47EB_B141_92B80471FB23 RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_A8BB0DAA_F8FF_41C7_BCAC_F6E7ADA78502 RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_C2B36696_DB1E_4D33_9132_E3AA49268DFA RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_D441A82E_DAEB_4EF8_97BA_F4470733E69A RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_DF77287F_46D1_4EC9_9EA9_BD1CC7CC900A RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_E5BBF59C_88DF_4504_B38B_1DB350302D6D RESTART WITH 1;
-- ALTER SEQUENCE SYSTEM_SEQUENCE_EF414053_6E94_4AE7_8531_9E9192B9544F RESTART WITH 1;

INSERT INTO genres (genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO ratings (rating_name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO event_types (event_type_name)
VALUES ('LIKE'),
       ('REVIEW'),
       ('FRIEND');

INSERT INTO event_operations (event_operation_name)
VALUES ('REMOVE'),
       ('ADD'),
       ('UPDATE');