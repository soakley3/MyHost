import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class login {
    public void main(String[] args) {
        // TODO code application logic here
        // holds the accounts incase we need to pull the username and password from them (to change them)
        ArrayList<account> accountList = new ArrayList<>();
        String userInput = "Billy:Bob";
        String[] inputParts = userInput.split(":");
        String username = inputParts[0];
        String password = inputParts[1];

        // if (isAccount(username, password) == false){
        try {

            FileWriter writer = new FileWriter("userinfo.txt", true);
            try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                bufferedWriter.newLine();
                bufferedWriter.write(username);
                bufferedWriter.write(password);
            }
        } catch (IOException e) {
        }
        account accountCreated = new account(username, password);
        accountList.add(accountCreated);
        //}
        // else{
        boolean correctUser = isAccount(username, password);
        // testing with a invalid login
        boolean incorrectUser = isAccount("Johnny", password);
        // just making sure NBA youngboy works with the right info
        System.out.println(accountList.get(0).getUsername() + " " + accountList.get(0).getPassword());
        System.out.println(correctUser); // should be true
        System.out.println(incorrectUser); // should be false
        //}
    }
    // checks txt file for the username
    public static boolean isAccount(String givenUserName, String givenPassword) {
        boolean correctUser = false;

        try {
            try (FileReader reader = new FileReader("userinfo.txt")) {
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    if (line.equals(givenUserName + givenPassword)) {
                        correctUser = true;
                    }
                }
            }

        } catch (IOException e) {
        }

        return correctUser;
    }
}
