package ppl.common.utils.character;

public class Characters {
    public static String parse(String string) {
        StringBuilder builder = new StringBuilder(string.length());
        char[] chars = string.toCharArray();
        int i = 0;
        while (i < chars.length) {
            if (chars[i] == '\\') {
                switch (chars[i+1]) {
                    case '\\':
                        builder.append('\\');
                        i += 2;
                        break;
                    case 'n':
                        builder.append('\n');
                        i += 2;
                        break;
                    case 't':
                        builder.append('\t');
                        i += 2;
                        break;
                    case 'b':
                        builder.append('\b');
                        i += 2;
                        break;
                    case 'r':
                        builder.append('\r');
                        i += 2;
                        break;
                    case '\'':
                        builder.append('\'');
                        i += 2;
                        break;
                    case '"':
                        builder.append('"');
                        i += 2;
                        break;
                    case 'u':
                        builder.append((char) Integer.parseInt(string.substring(i+2, i+6), 16));
                        i += 6;
                        break;
                    default:
                        builder.append((char) Integer.parseInt(string.substring(i+1, i+4), 8));
                        i += 4;
                        break;
                }
            } else {
                builder.append(chars[i]);
                i++;
            }
        }
        return builder.toString();
    }
}
