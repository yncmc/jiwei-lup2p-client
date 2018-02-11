package com.jiwei.lup2p.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.io.FileUtils;

public class CookieUtils {

    public static void write(Cookie[] cookies, String path) throws Throwable {
        File dir = new File(path);
        if (dir.exists()) {
            FileUtils.forceDelete(dir);
        }
        FileUtils.forceMkdir(dir);
        for (Cookie cookie : cookies) {
            File file = new File(path + "/" + cookie.getName());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(cookie);
            objectOutputStream.close();
        }
    }

    public static Cookie[] read(File folder) throws Throwable {
        Collection<File> files = FileUtils.listFiles(folder, null, false);
        Cookie[] results = new Cookie[files.size()];
        int i = 0;
        for (File file : files) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            Cookie cookie = (Cookie) objectInputStream.readObject();
            objectInputStream.close();

            results[i++] = cookie;
        }
        return results;
    }
}
