package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UserService {
    private UserStorage storage;
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    List<User> makeFriends(int firstUserId, int secondUserId) {
        Optional<User> first = Optional.ofNullable(storage.getUser(firstUserId));
        Optional<User> second = Optional.ofNullable(storage.getUser(secondUserId));
        if (first.isPresent() && second.isPresent()) {
            first.get().getFriends().add(secondUserId);
            second.get().getFriends().add(firstUserId);
            return List.of(first.get(), second.get());
        }
        return null;
    }

    List<User> removeFriends(int firstUserId, int secondUserId) {
        Optional<User> first = Optional.ofNullable(storage.getUser(firstUserId));
        Optional<User> second = Optional.ofNullable(storage.getUser(secondUserId));
        if (first.isPresent() && second.isPresent()) {
            first.get().getFriends().remove(secondUserId);
            second.get().getFriends().remove(firstUserId);
            return List.of(first.get(), second.get());
        }
        return null;
    }

    Set<Integer> showCommonFriends(int firstUserId, int secondUserId) {
        Optional<User> first = Optional.ofNullable(storage.getUser(firstUserId));
        Optional<User> second = Optional.ofNullable(storage.getUser(secondUserId));
        if (first.isPresent() && second.isPresent()) {
            Set<Integer> result = first.get().getFriends().stream()
                    .filter(second.get().getFriends()::contains)
                    .collect(Collectors.toSet());
            return result;
        }
        return null;
    }
}
