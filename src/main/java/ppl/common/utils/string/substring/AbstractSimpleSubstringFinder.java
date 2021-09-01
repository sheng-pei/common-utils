package ppl.common.utils.string.substring;

import ppl.common.utils.string.Substring;

public abstract class AbstractSimpleSubstringFinder extends AbstractSubstringFinder {

    @Override
    protected Substring findInternal(char[] input, int start, int end) {
        int matchedIdx = this.match(input, start, end);
        if (matchedIdx != -1) {
            return new ConsistentSubstring(input, matchedIdx, matchedIdx + this.length());
        }
        return null;
    }

    protected abstract int match(char[] input, int start, int end);

    protected abstract int length();

}
