package model;

public class Util {

    public static String generateCode() {
        int number = (int) (Math.random() * 1_000_000);
        return String.format("%06d", number);
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    }

    public static boolean isCodeValid(String code) {
        return code.matches("^\\d{4,5}$");
    }

    public static boolean isInteger(String value) {

        return value.matches("^\\d+$");
    }

    public static boolean isDouble(String value) {
        return value.matches("^\\d+(\\.\\d{2})?$");
    }
    
    public static boolean isValidPhone(String phone){
        return phone.matches("^(?:0|94|\\+94)?(?:7[0-8]\\d|(?:11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|912)[0-9])\\d{6}$");
    }
}
