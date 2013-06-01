/**
 * Copyright 2012 Muzima Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.muzima.search.api.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtil {

    static final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };

    private static MessageDigest getDigest(final String defaultAlgorithm) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(defaultAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Unable to find that algorithm for the message digest!", e);
        }
        return digest;
    }

    private static byte[] createChecksum(final InputStream inputStream) throws IOException {
        MessageDigest digest = getDigest("SHA1");
        try {
            int count;
            byte[] buffer = new byte[1024];
            while ((count = inputStream.read(buffer)) != -1)
                digest.update(buffer, 0, count);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return digest.digest();
    }

    private static String getHexString(final byte[] raw) throws UnsupportedEncodingException {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "ASCII");
    }

    public static String getSHA1Checksum(final String data)  throws IOException {
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        return getHexString(createChecksum(inputStream));
    }

    public static String getSHA1Checksum(final File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        return getHexString(createChecksum(inputStream));
    }
}
