
package com.futureagent.lib.utils;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppSignatureUtils {

    public static String getSignatureFileMd5(String path) {
        ZipFile file = null;
        String SHA1 = null;
        SequenceInputStream ins = null;

        Vector<InputStream> vector = new Vector<InputStream>();
        List<ZipEntry> list = new ArrayList<ZipEntry>();

        try {
            file = new ZipFile(path);
            Enumeration<?> e = file.entries();
            while (e.hasMoreElements()) {
                ZipEntry entryTmp = (ZipEntry) e.nextElement();
                String srcName = entryTmp.getName().trim();
                if (entryTmp.isDirectory() || !srcName.startsWith("META-INF")) {
                    continue;
                } else {
                    String name = srcName.replace("META-INF/", "");
                    if (name.contains("/")) {
                        continue;
                    }
                    if (name.endsWith(".RSA") || name.endsWith(".DSA")) {
                        String sfFileName = srcName.substring(0,
                                srcName.length() - 4)
                                + ".SF";
                        ZipEntry sfFile = file.getEntry(sfFileName);
                        if (sfFile != null) {
                            list.add(sfFile);
                        }
                    }

                }
            }

            Collections.sort(list, new Comparator<ZipEntry>() {
                public int compare(ZipEntry obj1, ZipEntry obj2) {
                    return obj1.getName().compareTo(obj2.getName());
                }
            });

            for (ZipEntry entry : list) {
                vector.add(file.getInputStream(entry));
            }

            ins = new SequenceInputStream(vector.elements());

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            int len = 0;
            byte buffer[] = new byte[32 * 1024]; // 32 KB
            while ((len = ins.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            SHA1 = DigestEncodingUtils.encodeHexString(messageDigest.digest());

        } catch (Exception e) {
        } finally {
            FileUtils.close(ins);
            FileUtils.close(file);
        }
        return SHA1;
    }
}
