package fall2018.csc2017.gamecentre;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Manages all the users for the gameCentre.
 */
public class UserManager implements Serializable {

    /**
     * Save file for all the users.
     */
    public static final String USERS = "users.ser";

    /**
     * Array list of all the users on the device.
     */
    private ArrayList<User> users;

    /**
     * Current user.
     */
    private User currentUser;

    /**
     * Initalizes a new Usermanager.
     */
    UserManager() {
        this.users = new ArrayList<>();
    }

    /**
     * Method for creating a new user.
     *
     * @param user     the input username
     * @param password the input password
     */
    boolean signUp(String user, String password) {
        boolean nameValid = isValidName(user);
        boolean passwordValid = isValidPassword(password);
        if (nameValid && passwordValid) {
            User newUser = new User(user, password);
            users.add(newUser);
        }
        return (nameValid && passwordValid);
    }

    /**
     * Check if the input username is valid.
     *
     * @param name the input username
     * @return boolean whether it is valid
     */
    private boolean isValidName(String name) {
        boolean nameValid = true;
        for (User u : users) {
            if (u.getUsername().equals(name)) {
                nameValid = false;
            }
        }
        if (name.equals("")) {
            nameValid = false;
        }
        return nameValid;
    }

    /**
     * Checks whether the password is valid.
     *
     * @param password the input password
     * @return boolean whether the password is valid
     */
    private boolean isValidPassword(String password) {
        return !(password.equals(""));
    }

    /**
     * Method for verifying password.
     *
     * @param user     the input user
     * @param password the input password
     * @return whether the password matches the username
     */
    boolean authenticate(String user, String password) {
        for (User u : users) {
            if (u.getUsername().equals(user)) {
                setCurrentUser(u);
                return u.authenticate(password);
            }
        }
        return false;
    }

    /**
     * Sets the current user logged in to currentUser.
     *
     * @param currentUser the input currentUser
     */
    private void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Gets the currentUser that is logged in.
     *
     * @return the current logged in user
     */
    User getCurrentUser() {
        return currentUser;
    }

    /**
     * Adds gameManager to the current user's list of auto saved games
     *
     * @param gameManager the gameManager to be added
     */
    void autoSaveGame(GameManager gameManager) {
        currentUser.addSavedGame(gameManager);
    }

    /**
     * Returns the gameManager in current user's list of auto saved games with gameName
     *
     * @param gameName the gameName of the gameManager to be returned
     * @return the gameManager
     */
    public GameManager getSelectedGame(String gameName) {
        return currentUser.getSelectedGame(gameName);

    }
}
