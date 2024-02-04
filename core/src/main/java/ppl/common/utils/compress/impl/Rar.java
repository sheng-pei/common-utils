package ppl.common.utils.compress.impl;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import ppl.common.utils.compress.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rar extends AbstractSevenZip {

    private static final Pattern INVALID_RAR_FORMAT_MESSAGE = Pattern.compile("Archive file \\(format: ([a-zA-Z0-9]+)\\) can't be opened");

    public static Rar create(File rarFile) {
        try {
            return new Rar(rarFile);
        } catch (IOException e) {
            throw new ArchiveException("Failed to open file: " + rarFile, e);
        }
    }

    private Rar(File rarFile) throws IOException {
        super(rarFile, Rar::open);
    }

    private static IInArchive open(File rarFile) throws IOException {
        IInStream stream = new RandomAccessFileInStream(new RandomAccessFile(rarFile, "r"));
        try {
            return SevenZip.openInArchive(ArchiveFormat.RAR5, stream);
        } catch (SevenZipException e) {
            Matcher matcher = INVALID_RAR_FORMAT_MESSAGE.matcher(e.getMessage());
            if (matcher.find() && "Rar5".equals(matcher.group(1))) {
                return SevenZip.openInArchive(ArchiveFormat.RAR, stream);
            }
            throw e;
        }
    }
}
