package newbee.jsh.security_jwt.global.util;

import java.util.Random;

public class RandomStringUtil {

    public static String getRandomString(int length){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = length;

        return new Random().ints(leftLimit,rightLimit + 1)
                            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                            .limit(targetStringLength)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();
    }
    
}
