package be.chat;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratePassword {

    public static void main(String[] argc) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String text = "Michal123";
            md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String output = bigInt.toString(16);

            System.out.println(output);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(GeneratePassword.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

}
