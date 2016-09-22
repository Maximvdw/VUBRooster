package be.vubrooster.ejb;

import be.vubrooster.ejb.models.User;

import java.util.List;

/**
 * UserServer
 *
 * Created by maxim on 21-Sep-16.
 */
public interface UserServer {
    /**
     * Find all users
     * @return list of users
     */
    List<User> findUsers();
}
