package org.openpaas.ieda.web.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
	public static String encryption(String password) throws NoSuchAlgorithmException {
		String encrypted = "";
		MessageDigest md = MessageDigest.getInstance("SHA-512");

        md.update(password.getBytes());
        byte[] mb = md.digest();
        for (int i = 0; i < mb.length; i++) {
            byte temp = mb[i];
            String s = Integer.toHexString(new Byte(temp));
            while (s.length() < 2) {
                s = "0" + s;
            }
            s = s.substring(s.length() - 2);
            encrypted += s;
        }
        
        return encrypted;
	}
}
