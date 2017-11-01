
package com.futureagent.lib.utils;

import android.annotation.SuppressLint;
import android.util.SparseIntArray;

import java.io.File;

public class ProcessUtils {

    @SuppressLint("DefaultLocale")
    private static int getUidForPid(int pid) {
        String filedata = FileUtils.readFileAsString(String.format("/proc/%d/status", pid));
        if (filedata != null) {
            for (String line : filedata.split("\n")) {
                if (line.startsWith("Uid:")) {
                    String[] array = line.trim().split("\\s+");
                    return Integer.parseInt(array[1]);
                }
            }
        }
        return -1;
    }

    public static SparseIntArray getProcessSnapshot() {
        SparseIntArray map = new SparseIntArray();
        File procDir = new File("/proc");
        for (File f : procDir.listFiles()) {
            if (f.isDirectory()) {
                try {
                    int pid = Integer.parseInt(f.getName());
                    int uid = getUidForPid(pid);
                    if (uid >= android.os.Process.FIRST_APPLICATION_UID) {
                        map.append(pid, uid);
                    }
                } catch (Exception e) {
                }
            }
        }
        return map;
    }
}
