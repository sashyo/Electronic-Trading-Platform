package Project.UnitTesting;

import static org.junit.jupiter.api.Assertions.*;

import Project.*;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeClass;
//import org.junit.Test;
import Project.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
//import org.testng.annotations.AfterTest;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class hold the unit testing methods for User.
 */
public class UserTest {

    // Testing Variables //
    static User user = new User();
    static Admin admin = new Admin();
    static User user2 = new User("firstNameTest", "lastNameTest", "test", 1, 1, "password");

    // Testing Methods //

    // Initialise data before all tests

    /**
     * The setup test for mock data.
     */
    @BeforeAll
    public static void setup() {
    }

    /**
     * Add a user.
     */
    @Test
    /* Test passed */
    public void addUserTest() {
        user.addUser(user2);
        assertTrue(user.userNameExists("test"));
        try {
            admin.removeUser("test");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Get the first name of a user.
     */
    @Test
    /* Test passed */
    public void getFirstName() {
        user.addUser(user2);
        assertEquals("firstNameTest", user2.getFirstName());
        try {
            admin.removeUser("test");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Get the first name of a user that does not exist.
     */
    @Test
    /* Test Passed*/
    public void getFirstNameUserNotExist(){
        user.addUser(user2);
        assertNotEquals("UserName does not exist", user2.getFirstName());
        try {
            admin.removeUser("test");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Validate login on a valid user.
     */
    @Test
    /* Test passed */
    public void userLoginValidationTestShouldBeValid() {
        User user4 = new User("firstNameTest", "lastNameTest", "usernameTest4", 1, 1, "password");
        user.addUser(user4);
        boolean valid = false;
        try {
            valid = user.userLoginValidation("usernameTest4", "password");
            admin.removeUser("userNameTest4");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //assertEquals(true, valid);
        assertTrue(valid);
    }

    /**
     * Validate login on an invalid user.
     */
    @Test
    /* Test passed */
    public void userLoginValidationTestShouldBeNotValid() {
        //User user2 = new User("firstNameTest", "lastNameTest", "usernameTest", 1, 1, "password");
        boolean valid = false;
        try {
            valid = user.userLoginValidation("bob", "password1");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertFalse(valid);
    }

    /**
     * Get the permission type of a user.
     */
    @Test
    /*Test passed*/
    public void getPermType(){
        Boolean check = false;
        try {
            check = "Administrator".equals(user.getPermType(1));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertTrue(check);
    }

    /**
     * Get the list of permission types.
     */
    @Test
    /*Test passed */
    public void getAllPermissionsTest(){
        ArrayList permissionsList = new ArrayList<String>();
        ArrayList<String> permissionsExpected = new ArrayList<String>(
                Arrays.asList("Administrator",
                        "Team Leader",
                        "User"));
        try {
            permissionsList = user.getAllPermissions();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals(permissionsExpected,permissionsList);
    }

    /**
     * Get the list of organisations.
     */
    @Test
    /*Test passed */
    public void getAllOrganisationsTest(){
        ArrayList orgList = new ArrayList();
        try{
            orgList  = user.getAllOrganisations();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals("Computer Cluster", orgList.get(0));
    }

    /**
     * Get the permission ID of a user.
     */
    @Test
    /*Test passed */
    public void getPermIDTest(){
        int idTest = 0;
        try {
            idTest = user.getPermId("Administrator");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals(1, idTest);
    }

    /**
     * Get a user that des not exist.
     */
    @Test
    /* Test Passed*/
    public void getThisUserExpectNull(){
        User newUserTest;
        try {
             user.getThisUser("NotAValidUsername");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertNull(user.getFirstName());
        //assertEquals("Administrator", user.getFirstName());
        //assertNotEquals("", user.getFirstName());
    }

    /**
     * Get a user that exists.
     */
    @Test
    /* Test Passed*/
    public void getThisUser(){
        try {
            user.getThisUser("test");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Get the username of a user.
     */
    @Test
    /*Test passed, but fails when new sql dump*/
    public void userNameExistsExpectTrue(){
        User userTest3 = new User("firstNameTest", "lastNameTest", "test3", 1, 1, "password");
        user.addUser(userTest3);
        assertTrue(user.userNameExists("test3"));
    }

    /**
     * Get the username of a user that does not exist.
     */
    @Test
    /* Test passed*/
    public void userNameExistsExpectFalse(){
        assertFalse(user.userNameExists("notaUserName"));
    }

    /**
     * Get the ID of a user.
     */
    @Test
    /*Test passed*/
    public void getUserIDTestValid(){
        int userId = -1;
        userId = user.getUserID("admin");
        assertEquals(1,userId);
    }

    /**
     * Get the ID of a user that does not exist.
     */
    @Test
    /*Test passed*/
    public void getUserIDTestNotValid(){
        int userId = -1;
        userId = user.getUserID("notAUsername");
        assertEquals(-1,userId);
    }

    /**
     * Get all the permissions.
     */
    @Test
    public void getAllPermissions(){
        try {
            System.out.print(user.getAllPermissions());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Get all organisational units.
     */
    @Test
    /*Test passed */
    public void getAllOrganisations(){
        try {
            System.out.print(user.getAllOrganisations());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Get the permission ID of a user, using username.
     */
    @Test
    /*Test*/
    public void getPermId(){
        try {
            assertEquals(3,user.getPermId("user"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Get all users.
     */
    @Test
    /*Test Passed*/
    public void getAllUser(){
        try {
            user.getAllUser();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Get the credits of a user.
     */
    @Test
    /*Test Passed*/
    public void getCredits(){
        BigDecimal bd1 = new BigDecimal("63745");
        BigDecimal bd2 = new BigDecimal("09872");
        User userTest = new User(bd1,bd2);
        assert userTest.getCredits() == bd1;
    }

    /**
     * Get the pending credits.
     */
    @Test
    /*Test Passed*/
    public void getPendingCredits(){
        BigDecimal bd1 = new BigDecimal("63745");
        BigDecimal bd2 = new BigDecimal("09872");
        User userTest = new User(bd1,bd2);
        assert userTest.getPending() == bd2;
    }

    /**
     * Get the accesses level of a user.
     */
    public void getAccessLvl(){
        assertEquals(1, user.getAccessLvl("admin"));
    }

    /**
     * Get the organisational unit of a user.
     */
    @Test
    public void getUserOrg() {
        try {
            user.getUserOrg("test");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Remove test users.
     * @throws SQLException when something goes wrong with the database.
     */
    //Test required to ensure not dupilpcates
    @Test
    public void adminRemoveTests() throws SQLException {
        Admin admin = new Admin();
        admin.removeUser("test3");
        admin.removeUser("test");
        admin.removeUser("user4");
        admin.removeUser("usernameTest4");

    }

}






