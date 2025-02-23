package ppl.common.utils.http.header;

import ppl.common.utils.http.symbol.Lexer;
import ppl.common.utils.character.ascii.CaseIgnoreString;

import java.util.Objects;

public final class HeaderName {

    private final CaseIgnoreString caseIgnoreName;

    private HeaderName(String name) {
        this.caseIgnoreName = CaseIgnoreString.create(name);
    }

    public boolean isInternal() {
            return caseIgnoreName.toString().startsWith("(");
    }

    @Override
    public String toString() {
        return this.caseIgnoreName.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeaderName headerName = (HeaderName) o;
        return Objects.equals(caseIgnoreName, headerName.caseIgnoreName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseIgnoreName);
    }
    
    public static HeaderName create(String name) {
        if (!Lexer.isToken(name) && !Lexer.isInternalHeader(name) && !Lexer.isPseudoHeader(name)) {
            throw new IllegalArgumentException("Invalid name. Not token or internal or pseudo.");
        }
        return new HeaderName(name);
    }

}
