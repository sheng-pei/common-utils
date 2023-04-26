package ppl.common.utils.filesystem;

public interface Session {
    Path working();
    Path get(String first, String... more);
}
