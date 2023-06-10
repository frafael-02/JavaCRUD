package tvz.java.rafaelprojekt.data;

import tvz.java.rafaelprojekt.main.MainApplication;
import tvz.java.rafaelprojekt.exceptions.LoginDataException;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Data {






    public static Map<String, String> getLoginProfiles() throws LoginDataException
    {
        Map<String,String> loginData = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("dat/loginData.txt")))
        {
            String linija = reader.readLine();
            while(linija != null)
            {
                loginData.put(linija, reader.readLine());
                linija=reader.readLine();
            }
        }
        catch(IOException e)
        {
            throw new LoginDataException("Error while getting login data", e);
        }

        return loginData;
    }

    public static String hashPassword(String password)
    {

        String encodedString = Base64.getEncoder().encodeToString(password.getBytes());
        return encodedString;
    }

    public static boolean register(String username, String password) throws LoginDataException {
        if(usernameAvaliability(username))
        {
            addNewLoginData(username, password);
            return true;
        }

        return false;

    }

    public static boolean usernameAvaliability(String username)
    {
        if(username.startsWith("admin"))
            return false;
        return !MainApplication.loginData.containsKey(username);
    }

    public static void addNewLoginData(String username, String password) throws LoginDataException
    {
        try(BufferedWriter writer= new BufferedWriter(new FileWriter("dat//loginData.txt", true)))
        {
            writer.newLine();
            writer.append(username);
            writer.newLine();
            writer.append(password);
        }
        catch (IOException e)
        {
            throw new LoginDataException("Error while adding new login data", e);
        }

    }


}
