package ppl.common.utils.command;

import java.util.List;

public interface Option {
    List<String> getLongOptions();

    List<String> getShortOptions();
}
