import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import org.sillygit.util.Util;


public class ExploreDirectory {

    public static void main(String[] args) throws IOException {
        File repository = new File("C:\\Users\\johannes\\temp\\demo\\.git\\");
        File headFile = new File(repository, Util.asString(new File(repository, "HEAD")).split(" ")[1].trim());

        String commitHash =  Util.asString(headFile).trim();
        File commitFile = new File(repository, "objects/" + commitHash.substring(0,2) + "/" + commitHash.substring(2));
        try(final InputStream inputStream = new InflaterInputStream(new FileInputStream(commitFile))) {
            System.out.println(Util.asString(inputStream).replace('\0', ' '));
        }

        String treeHash;
        try(final InputStream inputStream = new InflaterInputStream(new FileInputStream(commitFile))) {
            String type = Util.stringUntil(inputStream, ' ');
            long length = Long.valueOf(Util.stringUntil(inputStream, (char)0));
            Util.stringUntil(inputStream, ' ');
            treeHash = Util.stringUntil(inputStream, '\n');
        }

        File rootTreeFile = new File(repository, "objects/" + treeHash.substring(0,2) + "/" + treeHash.substring(2));
        Map<String,String> entries = new HashMap<>();
        try(final InputStream inputStream = new InflaterInputStream(new FileInputStream(rootTreeFile))) {
            String type = Util.stringUntil(inputStream, ' ');
            long length = Long.valueOf(Util.stringUntil(inputStream, (char)0));

            while (true) {
                String octalMode = Util.leftPad(Util.stringUntil(inputStream, ' '), 6, '0');
                if (octalMode == null) break;

                String path = Util.stringUntil(inputStream, (char)0);
                StringBuilder hash = new StringBuilder();
                for (int i=0; i<20; i++) {
                    hash.append(Util.leftPad(Integer.toHexString(inputStream.read()), 2, '0'));
                }
                entries.put(path, hash.toString());
            }
        }

        System.out.println(entries);

        String blobHash = entries.get("ExploreDirectory.class");
        File blobFile = new File(repository, "objects/" + blobHash.substring(0,2) + "/" + blobHash.substring(2));
        try(final InputStream inputStream = new InflaterInputStream(new FileInputStream(blobFile))) {
            String type = Util.stringUntil(inputStream, ' ');
            long length = Long.valueOf(Util.stringUntil(inputStream, (char)0));

            String content = Util.asString(inputStream);
            //System.out.println(content);
            System.out.println(length + " => " + content.length());
        }
    }
}
