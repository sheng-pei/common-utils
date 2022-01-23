package ppl.common.utils.string.substring;

public class SubstringIndexOutOfBoundsException extends StringIndexOutOfBoundsException {
    public SubstringIndexOutOfBoundsException() {
    }

    public SubstringIndexOutOfBoundsException(String s) {
        super(s);
    }

    public SubstringIndexOutOfBoundsException(int index) {
        super("Substring index out of range: " + index);
    }
}
