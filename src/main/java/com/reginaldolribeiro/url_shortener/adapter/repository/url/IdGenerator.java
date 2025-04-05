package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.app.exception.IdGenerationException;
import com.reginaldolribeiro.url_shortener.app.port.IdGeneratorPort;

import java.util.UUID;

@Deprecated
public class IdGenerator implements IdGeneratorPort {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int SHORT_URL_ID_LENGTH = 7;

    @Override
    public String generate() {
        try {
            var id = toBase62(UUID.randomUUID().toString());
            return id.substring(0, SHORT_URL_ID_LENGTH);
        } catch (Exception e) {
            throw new IdGenerationException("Error when generating short URL.", e);
        }

//        String shortId;
//        do {
//            shortId = generate(); // Your existing generate method
//        } while (repository.existsById(shortId));
    }

    protected static String toBase62(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();

        return toBase62(mostSignificantBits) + toBase62(leastSignificantBits);
    }

    protected static String toBase62(long value) {
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

}
