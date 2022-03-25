package my.edu.utar.mypassword;

import java.util.ArrayList;
import java.util.Random;

public class PasswordGenerator {

    private String generatedPassword;
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER = "0123456789";
    private static final String SYMBOL = "!@#$%&*()_+-=[]|,./?<>";
    private Integer length;
    private boolean uppercase;
    private boolean lowercase;
    private boolean numbers;
    private boolean symbols;

    public PasswordGenerator() {
        length = 12;
        uppercase = true;
        lowercase = true;
        numbers = true;
        symbols = true;
    }

    public PasswordGenerator(Integer length, Boolean uppercase, Boolean lowercase, Boolean numbers, Boolean symbols) {
        this.length = length;
        this.uppercase = uppercase;
        this.lowercase = lowercase;
        this.numbers = numbers;
        this.symbols = symbols;
    }

    public String generate() {
        StringBuilder password = new StringBuilder(length);
        Random random = new Random(System.nanoTime());

        // append str
        ArrayList<String> strList = new ArrayList<>();
        if (uppercase) {
            strList.add(UPPER);
        }
        if (lowercase) {
            strList.add(LOWER);
        }
        if (numbers) {
            strList.add(NUMBER);
        }
        if (symbols) {
            strList.add(SYMBOL);
        }

        // Build the password.
        for (int i = 0; i < length; i++) {
            String str = strList.get(random.nextInt(strList.size()));
            int position = random.nextInt(str.length());
            password.append(str.charAt(position));
        }
        return new String(password);
    }

    public String checkStrength(String password) {
        int pLength = password.length();
        boolean hasLower = false, hasUpper = false, hasNumber = false, hasSymbol = false;
        ArrayList<Boolean> strCategory = new ArrayList<Boolean>();
        String strength;

        for (char i : password.toCharArray())
        {
            if (Character.isUpperCase(i))
                hasUpper = true;
            if (Character.isLowerCase(i))
                hasLower = true;
            if (Character.isDigit(i))
                hasNumber = true;
            if (SYMBOL.contains(String.valueOf(i)))
                hasSymbol = true;
        }

        if (hasUpper) {
            strCategory.add(true);
        }
        if (hasLower) {
            strCategory.add(true);
        }
        if (hasNumber) {
            strCategory.add(true);
        }
        if (hasSymbol) {
            strCategory.add(true);
        }

        if ((strCategory.size() == 4) && (pLength >= 8))
            strength = "Strong";
        else if ((strCategory.size() >= 2) && (pLength >= 6))
            strength = "Moderate";
        else
            strength = "Weak";

        return strength;
    }

    public void setGeneratedPassword(String generatedPassword) {
        this.generatedPassword = generatedPassword;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public void setUppercase(boolean uppercase) {
        this.uppercase = uppercase;
    }

    public void setLowercase(boolean lowercase) {
        this.lowercase = lowercase;
    }

    public void setNumbers(boolean numbers) {
        this.numbers = numbers;
    }

    public void setSymbols(boolean symbols) {
        this.symbols = symbols;
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    public Integer getLength() {
        return length;
    }

    public boolean isUppercase() {
        return uppercase;
    }

    public boolean isLowercase() {
        return lowercase;
    }

    public boolean isNumbers() {
        return numbers;
    }

    public boolean isSymbols() {
        return symbols;
    }
}
