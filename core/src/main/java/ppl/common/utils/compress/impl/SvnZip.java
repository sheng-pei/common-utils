package ppl.common.utils.compress.impl;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import ppl.common.utils.compress.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SvnZip extends AbstractSevenZip {
    public static SvnZip create(File svnZipFile) {
        try {
            return new SvnZip(svnZipFile);
        } catch (IOException e) {
            throw new ArchiveException("Failed to open file: " + svnZipFile, e);
        }
    }

    public SvnZip(File svnZipFile) throws SevenZipException, IOException {
        super(svnZipFile, SvnZip::open);
    }

    private static IInArchive open(File svnZipFile) throws SevenZipException, IOException {
        IInStream stream = new RandomAccessFileInStream(new RandomAccessFile(svnZipFile, "r"));
        return SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, stream);
    }
}
