package ch.aaap.harvestclient.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

import ch.aaap.harvestclient.api.UsersApi;
import ch.aaap.harvestclient.core.Harvest;
import ch.aaap.harvestclient.domain.User;
import ch.aaap.harvestclient.domain.param.UserCreationInfo;
import ch.aaap.harvestclient.domain.param.UserInfo;
import ch.aaap.harvestclient.exception.NotFoundException;
import ch.aaap.harvestclient.exception.RequestProcessingException;

class UsersApiImplTest {

    private final static Logger log = LoggerFactory.getLogger(UsersApiImplTest.class);
    // user created and deleted in every test
    private final static String userFirst = "First";
    private final static String userLast = "Last";
    private final static String userEmail = "test@example.com";
    // user that is assumed to exist already
    private final static String fixUserFirst = "FixFirst";
    private final static String fixUserLast = "FixLast";
    private final static String fixUserEmail = "fix.user@example.com";
    private static UsersApi api = new Harvest(ConfigFactory.load()).users();
    private static User fixUser;

    @BeforeAll
    public static void beforeAll() {

        List<User> users = api.list();

        Optional<User> user = users.stream().filter(u -> u.getEmail().equals(fixUserEmail)).findFirst();
        if (user.isPresent()) {
            log.debug("Fix user exists already, nothing to do");
            fixUser = user.get();
        } else {
            UserCreationInfo creationInfo = new UserCreationInfo.Builder(fixUserFirst, fixUserLast, fixUserEmail)
                    .build();
            fixUser = api.create(creationInfo);
            log.debug("Created Fix user");
        }
    }

    @Test
    void list() {

        List<User> users = api.list();

        assertTrue(users.size() > 0);
    }

    @Test
    void createExistingEmailFails() {
        RequestProcessingException exception = Assertions.assertThrows(RequestProcessingException.class, () -> {
            UserCreationInfo creationInfo = new UserCreationInfo.Builder(fixUserFirst, fixUserLast, fixUserEmail)
                    .build();

            api.create(creationInfo);
        });
        assertEquals(422, exception.getHttpCode());
        assertTrue(exception.getMessage().contains(fixUserEmail));
    }

    @Test
    void createAndDeleteUser() {

        UserCreationInfo creationInfo = new UserCreationInfo.Builder(userFirst, userLast, userEmail).build();

        User user = api.create(creationInfo);

        // cleanup
        api.delete(user);
    }

    @Test
    void createAndDeleteUserById() {

        UserCreationInfo creationInfo = new UserCreationInfo.Builder(userFirst, userLast, userEmail).build();

        User user = api.create(creationInfo);

        // cleanup
        api.delete(user.getId());
    }

    @Test
    void getSelf() {

        // TODO this will fail for another authenticated user
        User self = api.getSelf();

        assertEquals("Marco", self.getFirstName());
        assertEquals("marco.nembrini.co@gmail.com", self.getEmail());
    }

    @Test
    void getUser() {
        User test = api.get(fixUser.getId());

        assertEquals(fixUserFirst, test.getFirstName());
    }

    @Test
    void getUserNotExisting() {
        NotFoundException e = assertThrows(NotFoundException.class, () -> api.get(1));
        assertEquals(404, e.getHttpCode());
    }

    @Test
    void testChangeEmail() {

        String email = "new@example.org";
        long userId = fixUser.getId();

        UserInfo userInfo = new UserInfo.Builder().email(email).build();

        User user = api.update(userId, userInfo);

        assertEquals(email, user.getEmail());

        // restore email

        api.update(userId, new UserInfo.Builder().email(fixUserEmail).build());
    }
}