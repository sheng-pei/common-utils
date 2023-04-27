package ppl.common.utils.filesystem;

import ppl.common.utils.string.Strings;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        Iterator<Path> iterator = BasePath.get("/m/.././../a").iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

}
