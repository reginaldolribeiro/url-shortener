package com.reginaldolribeiro.url_shortener.adapter;

import com.reginaldolribeiro.url_shortener.app.exception.IdGenerationException;
import com.reginaldolribeiro.url_shortener.app.port.IdGeneratorPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdGenerator implements IdGeneratorPort {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public String generate() {
        var id = toBase62(UUID.randomUUID().toString());
        if(id.isBlank()){
            throw new IdGenerationException("Error when generating short URL.");
        }
        return id.substring(0,7);
//        return "abc345k";
    }


    private static String toBase62(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();

        return toBase62(mostSignificantBits) + toBase62(leastSignificantBits);
    }

    private static String toBase62(long value) {
        if (value == 0) {
            return "0";
        }

        // Handle negative values by converting them to positive
        if (value < 0) {
            value = Math.abs(value);
        }

        StringBuilder sb = new StringBuilder();

        // Convert the value to Base62 without handling for negative values
        while (value > 0) {
            int remainder = (int) (value % 62);
            sb.append(BASE62.charAt(remainder));
            value /= 62;
        }

        return sb.reverse().toString();
    }

    //    public static void main(String[] args) {
//        var a = toBase62(UUID.randomUUID().toString()).substring(0, 7);
//        System.out.println(a);
//    }
}
