package Project.UnitTesting;

import Project.OrganisationalUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;

//import static org.testng.Assert.fail;

/**
 * This class hold the unit testing methods for Organisational Unit.
 */
public class OrgUnitTest {

    // Testing Variables //

    static OrganisationalUnit orgUnit = new OrganisationalUnit("Organisation", 10);


    // Testing Methods //
    // Named alphabetically so they run in the correct order

    /**
     * The setup test for mock data.
     */
    // Initialise data before all tests
    @BeforeAll
    public static void setup() {
        //User user = new User("GetOrg", "OrgID", "getOrgID", 1, 1, 3, "password");

        orgUnit.addOrgUnit(orgUnit);
    }

    // add org unit (the constructor)
    //      - create new org unit
    //      - org unit must not already exist

    /**
     * Add an organisation.
     */
    @Test
    /* Test passed */
    public void A_addOrgUnit() {
        String name = "Test Organisation";
        double credits = 75;

        OrganisationalUnit orgUnit = new OrganisationalUnit(name, credits);
        orgUnit.addOrgUnit(orgUnit);
    }

    /**
     * Add an organisation that exists.
     */
    @Test
    /* Test passed */
    public void B_addOrgUnitExists() {
        String name = "Test Organisation";
        double credits = 75;

        OrganisationalUnit orgUnit = new OrganisationalUnit(name, credits);
        orgUnit.addOrgUnit(orgUnit);
    }


    // update credits
    //      - check if org unit exists and new credits not negative
    //      - check if org unit does not exist
    //      - check negative credits

    /**
     * Update an organisation's credits.
     */
    @Test
    /* Test passed */
    public void E_updateCredits() {
        int orgUnitID = 1;
        double credits = 100;

        orgUnit.updateCredits(orgUnitID, credits);
    }

    /**
     * Update an organisation's credits, where organisation does not exist.
     */
    @Test
    /* Test passed */
    public void F_updateNotExists() {
        int orgUnitID = 100;
        double credits = 20;

        orgUnit.updateCredits(orgUnitID, credits);
    }

    /**
     * Update an organisation's credits, where credits are negative.
     */
    @Test
    /* Test passed */
    public void G_negativeCredits() {
        int orgUnitID = 1;
        double credits = -10;

        orgUnit.updateCredits(orgUnitID, credits);
    }


    // get org unit
    //      - if it exists, it returns it
    //      - if not exists, it is null

    /**
     * Get an organisation.
     */
    @Test
    /* Test passed */
    public void H_getOrgExists() {
        OrganisationalUnit myOrgUnit;
        int orgUnitID = 1;

        myOrgUnit = orgUnit.getOrgUnit(orgUnitID);
        if (myOrgUnit == null) {
            Assertions.fail();
        }
    }

    /**
     * Get an organisation that does not exist.
     */
    @Test
    /* Test passed */
    public void I_getOrgNotExists() {
        OrganisationalUnit myOrgUnit;
        int orgUnitID = 100;

        myOrgUnit = orgUnit.getOrgUnit(orgUnitID);
        if (myOrgUnit != null) {
            Assertions.fail();
        }
    }


    // get org unit id
    //      - if it exists, it returns ID
    //      - if not exists, it is 0

    /**
     * Get an organisation's ID.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    /* Test passed */
    public void J_getOrgIDExists() throws SQLException {
        String orgUnitName = "Organisation";
        int orgUnitID;

        orgUnitID = orgUnit.getOrg(orgUnitName);
        if (orgUnitID == 0) {
            Assertions.fail();
        }

        System.out.println(orgUnitID);
    }

    /**
     * Get an organisation's ID, where the organisation does not exist.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    /* Test passed */
    public void K_getOrgIDNotExists() throws SQLException {
        String orgUnitName = "Organisation Not Exists";
        int orgUnitID;

        orgUnitID = orgUnit.getOrg(orgUnitName);
        if (orgUnitID != 0) {
            Assertions.fail();
        }

        System.out.println(orgUnitID);
    }

    // get credits
    //      - check org unit exists
    //      - check org unit does not exist

    /**
     * Get the credits of an organisation.
     */
    @Test
    /* Test Passed */
    public void L_getCreditsExists() {
        int orderID = 1;
        Double credits;

        credits = OrganisationalUnit.getCredits(orderID);

        if (credits != null){
            System.out.println(credits);
        }
        else {
            System.out.println("Credits null - org unit does not exist.");
        }
    }

    /**
     * Get the credits of an organisation, where organisation does not exist.
     */
    @Test
    /* Test Passed */
    public void M_getCreditsNotExists() {
        int orgUnitID = 100;
        Double credits;

        credits = OrganisationalUnit.getCredits(orgUnitID);

        if (credits != null){
            System.out.println(credits);
        }
        else {
            System.out.println("Credits null - org unit does not exist.");
        }
    }


    // get org unit ID from user
    //      - check if user exists
    //      - check if user does not exist

    /**
     * Get the organisation ID of a user.
     */
    @Test
    /* Test passed */
    public void N_orgIDUserExists() {
        Integer myOrgUnitID;
        int userID = 1;

        myOrgUnitID = OrganisationalUnit.getOrgUnitID(userID);
        if (myOrgUnitID == null) {
            Assertions.fail();
        }

        System.out.println(myOrgUnitID);
    }

    /**
     * Get the organisation ID of a user, where the user does not exist.
     */
    @Test
    /* Test passed */
    public void O_orgIDUserNotExists() {
        Integer myOrgUnitID;
        int userID = 100;

        myOrgUnitID = OrganisationalUnit.getOrgUnitID(userID);
        if (myOrgUnitID != null) {
            Assertions.fail();
        }
    }


    // org unit exists
    //      - check if org unit exists - returns true
    //      - check if org unit does not exist - returns false

    /**
     * Check an organisation exists.
     */
    @Test
    /* Test passed */
    public void P_orgUnitExistsTrue() {
        Boolean exists;
        String name = "Organisation";

        exists = orgUnit.orgUnitExists(name);
        if (!exists) {
            Assertions.fail();
        }

        System.out.println(exists);
    }

    /**
     * Check an organisation does not exist.
     */
    @Test
    public void Q_orgUnitExistsFalse() {
        Boolean exists;
        String name = "Does Not Exist";

        exists = orgUnit.orgUnitExists(name);
        if (exists) {
            Assertions.fail();
        }

        System.out.println(exists);
    }


    // get user org
    //      - if org unit exists
    //      - if org unit does not exist

    /**
     * Get a users organisation.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    /* Test passed */
    public void R_getUserOrgExist() throws SQLException {
        ArrayList<String> userOrgList = null;
        String userName = "admin";

        userOrgList = orgUnit.getUserOrg(userName);

        System.out.println(userOrgList);
    }

    /**
     * Get a users organisation, where user does not exist.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    /* Test passed */
    public void S_getUserOrgNotExist() throws SQLException {
        ArrayList<String> userOrgList = null;
        String userName = "User Not Exists";

        userOrgList = orgUnit.getUserOrg(userName);

        System.out.println(userOrgList);
    }


    // get pending
    //      - org unit exists
    //      - org unit does not exist

    /**
     * Get the pending credits of an organisation.
     */
    @Test
    /* Test passed */
    public void T_getPendingExist() {
        Double pending;
        int orgUnitID = 1;

        pending = OrganisationalUnit.getPending(orgUnitID);

        System.out.println(pending);
    }

    /**
     * Get the pending credits where organisation does not exist.
     */
    @Test
    /* Test passed */
    public void U_getPendingNotExist() {
        Double pending;
        int orgUnitID = 100;

        pending = OrganisationalUnit.getPending(orgUnitID);

        System.out.println(pending);
    }


    // add pending
    //      - org unit exists
    //      - org unit does not exist
    //      - add negative
    //      - add exceeds credits

    /**
     * Add pending credits.
     */
    @Test
    /* Test passed */
    public void V_addPendingExist() {
        int orgUnitID = 1;
        double addCredits = 10;

        OrganisationalUnit.addPending(orgUnitID, addCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Adding pending credits, where organisation does not exist.
     */
    @Test
    /* Test passed */
    public void W_addPendingNotExist() {
        int orgUnitID = 100;
        double addCredits = 10;

        OrganisationalUnit.addPending(orgUnitID, addCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Add pending credits, where pending is negative.
     */
    @Test
    /* Test passed */
    public void X_addPendingNegative() {
        int orgUnitID = 1;
        double addCredits = -10;

        OrganisationalUnit.addPending(orgUnitID, addCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Add pending credits, where pending exceed credits.
     */
    @Test
    /* Test passed */
    public void Y_addPendingExceeds() {
        int orgUnitID = 1;
        double addCredits = 1000;

        OrganisationalUnit.addPending(orgUnitID, addCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }


    // revert pending
    //      - org unit exists
    //      - org unit does not exist
    //      - remove negative
    //      - remove exceeds current pending

    /**
     * Revert pending credits.
     */
    @Test
    /* Test passed */
    public void Z_revertPendingExist() {
        int orgUnitID = 1;
        double removeCredits = 10;

        OrganisationalUnit.revertPending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Revert pending credits, where organisation does not exist.
     */
    @Test
    /* Test passed */
    public void Z1_revertPendingNotExist() {
        int orgUnitID = 100;
        double removeCredits = 10;

        OrganisationalUnit.revertPending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Revert pending credits, where pending negative.
     */
    @Test
    /* Test passed */
    public void Z2_revertPendingNegative() {
        int orgUnitID = 1;
        double removeCredits = -10;

        OrganisationalUnit.revertPending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Revert pending credits, where pending exceeds credits.
     */
    @Test
    /* Test passed */
    public void Z3_revertPendingExceeds() {
        int orgUnitID = 1;
        double removeCredits = 1000;

        OrganisationalUnit.revertPending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }


    // remove pending
    //      - org unit exists
    //      - org unit does not exist
    //      - remove negative
    //      - remove exceeds current pending

    /**
     * Remove pending credits.
     */
    @Test
    /* Test passed */
    public void Z4_removePendingExist() {
        int orgUnitID = 1;
        double removeCredits = 10;

        OrganisationalUnit.removePending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Remove pending credits, where organisation does not exist.
     */
    @Test
    /* Test passed */
    public void Z5_removePendingNotExist() {
        int orgUnitID = 100;
        double removeCredits = 10;

        OrganisationalUnit.removePending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Remove pending credits, where pending negative.
     */
    @Test
    /* Test passed */
    public void Z6_removePendingNegative() {
        int orgUnitID = 1;
        double removeCredits = -10;

        OrganisationalUnit.removePending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

    /**
     * Remove pending credits, where pending exceeds credits.
     */
    @Test
    /* Test passed */
    public void Z7_removePendingExceeds() {
        int orgUnitID = 1;
        double removeCredits = 1000;

        OrganisationalUnit.removePending(orgUnitID, removeCredits);

        System.out.println(OrganisationalUnit.getPending(orgUnitID));
    }

}
