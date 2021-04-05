package com.sss.safesecure;

import org.junit.Assert;
import org.junit.Test;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SafeSecure_UnitTests {

    int count;
    String userPassCode, randomPassCode;

    /**
     * Purpose: this test executes the user pass code test and secret key test in the required order
     * for the tests to pass.
     * The secret key test requires the values from the correct user pass code to pass.
     */
    @Test
    public void passCodeAndKeyTest(){
        userPassCodeTestPass();
        secretKeyTest();
    }

    /**
     * Purpose: this test generates a random 4 digit pass code -
     * and compares it to the hard coded user pass code.
     * This test compares 4 digit codes that include, lower case, upper case and numbers.
     * This test will pass if the pass codes match.
     * This test will run until the pass codes match.
     */
    @Test
    public void userPassCodeTestPass() {
        System.out.println("\n-----------------------------START--------------------------------");

        //counter to count the pass code attempts
        count = 0;

        //instantiates a string used to store test user pass code -
        //  this value can be changed to any 4 digit combination.
        String userPassCodeTest = "Z1d2";
        //logs the test pass code
        System.out.println("User Pass Code Test Pass - The test pass code is: " + userPassCodeTest);

        //instantiates a string used to store randomised user pass code -
        //  this value is changed as the test runs.
        String randomPassCodeTest = "0000";

        //instantiates a hash set to store unique codes
        Set<String> uniqueCodes = new HashSet<>();

        while (!userPassCodeTest.equals(randomPassCodeTest)) {

            //generates a random 4 digit user code
            String randomPassCode = generateRandomPassCode();

            //if the hash set adds the random code it is unique
            if (uniqueCodes.add(randomPassCode)) {

                randomPassCodeTest = randomPassCode;

                if (uniqueCodes.size() > 10000000) {
                    uniqueCodes.clear();
                }
            }
        }

        //clears the unique codes set
        uniqueCodes.clear();

        //logs the time of matching the user pass code
        System.out.println("User Pass Code Test Pass - The random generated pass code is: "
                + randomPassCodeTest);

        //logs the count of attempts
        System.out.println("User Pass Code Test Pass - Total attempts: " + count);

        //asserts the hard coded and random pass codes are equal
        Assert.assertEquals(userPassCodeTest, randomPassCodeTest);

        userPassCode = userPassCodeTest;
        randomPassCode = randomPassCodeTest;

        System.out.println("------------------------------END--------------------------------\n");
    }

    /**
     * Purpose: This test will compare two different pass codes and assert they are equal.
     * This test will fail.
     */
    @Test
    public void userPassCodeTestFail(){
        System.out.println("\n-----------------------------START--------------------------------");

        //instantiates a string used to store test user pass code -
        //  this value can be changed to any 4 digit combination.
        String userPassCodeTest = "Z1d2";
        //logs the test pass code
        System.out.println("User Pass Code Test Fail - The test pass code is: " + userPassCodeTest);

        //instantiates a string used to store randomised user pass code -
        //  this value is changed as the test runs.
        String randomPassCodeTest = "0z2D";
        //logs the test pass code
        System.out.println("User Pass Code Test Fail - The random pass code is: " + randomPassCodeTest);

        //asserts the hard coded and random pass codes are equal
        Assert.assertEquals(userPassCodeTest, randomPassCodeTest);

        System.out.println("------------------------------END--------------------------------\n");
    }

    /**
     * Purpose: this test will pass if called by the User Pass Code Test method.
     * This test will encode the hard coded and random pass codes and assert they are equal.
     * This test will fail if it runs independently.
     */
    @Test
    public void secretKeyTest() {
        System.out.println("\n-----------------------------START--------------------------------");

        if(userPassCode != null && randomPassCode != null){

            //instantiates and assigns values to the secret key test variable - creates a 32 byte string
            String userSecretKey = String.valueOf((userPassCode.hashCode() * 13))
                    + String.valueOf((userPassCode.hashCode() * 7))
                    + String.valueOf((userPassCode.hashCode() * 3) + "SSDZH2020");

            //instantiates and assigns values to the random secret key variable
            String randomSecretKeyTest = String.valueOf((randomPassCode.hashCode() * 13))
                    + String.valueOf((randomPassCode.hashCode() * 7))
                    + String.valueOf((randomPassCode.hashCode() * 3) + "SSDZH2020");

            //logs the user hard coded and randomly generated secret keys
            System.out.println("Secret Key Test Pass - User Secret Key: " + userSecretKey);
            System.out.println("Secret Key Test Pass - Random Secret Key: " + randomSecretKeyTest);

            //asserts the hard coded and random secret keys are equal
            Assert.assertEquals(userSecretKey, randomSecretKeyTest);

        } else{

            //instantiates a string used to store test user pass code -
            //  this value can be changed to any 4 digit combination.
            String userPassCodeTest = "Z1d2";
            //instantiates a string used to store randomised user pass code -
            //  this value is not equal so the test fails.
            String randomPassCodeTest = "0z2D";

            //instantiates and assigns values to the secret key test variable - creates a 32 byte string
            String userSecretKey = String.valueOf((userPassCodeTest.hashCode() * 13))
                    + String.valueOf((userPassCodeTest.hashCode() * 7))
                    + String.valueOf((userPassCodeTest.hashCode() * 3) + "SSDZH2020");

            //instantiates and assigns values to the random secret key variable
            String randomSecretKeyTest = String.valueOf((randomPassCodeTest.hashCode() * 13))
                    + String.valueOf((randomPassCodeTest.hashCode() * 7))
                    + String.valueOf((randomPassCodeTest.hashCode() * 3) + "SSDZH2020");

            //logs the user hard coded and randomly generated secret keys
            System.out.println("Secret Key Test Fail - User Secret Key: " + userSecretKey);
            System.out.println("Secret Key Test Fail - Random Secret Key: " + randomSecretKeyTest);

            //asserts the are equal
            Assert.assertEquals(userSecretKey, randomSecretKeyTest);
        }
        System.out.println("------------------------------END--------------------------------\n");
    }

    /**
     * Purpose: generates a random code for comparing to the hard coded user pass code.
     * This method generates a pass code that includes lower case, upper case and numbers.
     */
    private String generateRandomPassCode() {

        //increments the counter each time this method is called
        count++;

        //instantiates a strings for selecting test pass code characters from
        String acceptedChars = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLOMOPQRSTUVWXYZ0123456789";

        //generates random combination
        final Random random = new Random();
        //builds string at length of parameter
        final StringBuilder stringbuilder = new StringBuilder(1);

        String testCode = "";

        //iterates over the pwLength length
        for (int length = 0; length < 4; length++) {

            //appends random characters from the character strings to the string builder
            stringbuilder.append(acceptedChars.charAt(random.nextInt(acceptedChars.length())));

            //assigns values to the test code
            testCode = stringbuilder.toString();
        }

        //returns the random test pass code
        return testCode;
    }
}
