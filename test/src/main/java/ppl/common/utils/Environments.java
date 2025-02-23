//package ppl.common.utils;
//
//import ppl.common.utils.string.Strings;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.lang.reflect.Array;
//import java.util.*;
//import java.util.stream.Stream;
//
//public class Environments {
//
//    /**
//     * 格式化打印key-value配置信息
//     *
//     * @return 格式化打印类
//     */
//    public static PrettyPrint prettyPrint() {
//        return new PrettyPrint();
//    }
//
//    public final static class PrettyPrint {
//        private String title;
//        private final List<Env> envs;
//        private int _max_key_len = 4, _max_desc_len = 0, _max_value_len = 0;
//
//        public PrettyPrint() {
//            this.envs = new LinkedList<>();
//        }
//
//        public PrettyPrint title(String title) {
//            this.title = title;
//            return this;
//        }
//
//        public PrettyPrint addConf(String conf, Object value) {
//            return addConf(conf, null, value);
//        }
//
//        public String toString(Object object) {
//            if (object == null) {
//                return "@null";
//            } else if (object.getClass().isArray()) {
//                StringBuilder sb = new StringBuilder();
//                int len = Array.getLength(object);
//                for (int k = 0; k < len; k++) {
//                    sb.append(toString(Array.get(object, k))).append(',');
//                }
//                return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
//            } else {
//                return object.toString();
//            }
//        }
//
//        public PrettyPrint addConf(String conf, String desc, Object value) {
//            Objects.requireNonNull(conf);
//
//            if (Objects.isNull(value)) {
//                return _addConf(conf, desc, "@null");
//            } else if (value.getClass().isArray()) {
//                return _addConf(conf, desc, Arrays.stream((Object[]) value).map(this::toString).toArray(String[]::new));
//            } else if (Map.class.isAssignableFrom(value.getClass())) {
//                ((Map) value)
//                        .forEach((k, v) -> _addConf(String.format("%s[%s]", conf, String.valueOf(k)), desc, String.valueOf(v)));
//                return this;
//            } else if (Collection.class.isAssignableFrom(value.getClass())) {
//                Collection collection = (Collection) value;
//                String[] args = new String[collection.size()];
//                int k = 0;
//                Iterator ltr = collection.iterator();
//                while (ltr.hasNext()) {
//                    args[k++] = toString(ltr.next());
//                }
//                _addConf(conf, desc, args);
//                return this;
//            } else {
//                return _addConf(conf, desc, String.valueOf(value));
//            }
//        }
//
//        private PrettyPrint _addConf(String conf, String desc, String... values) {
//            _max_key_len = Math.max(_max_key_len, conf.length());
//            _max_desc_len = Objects.isNull(desc) ? _max_desc_len : Math.max(_max_desc_len, desc.length());
//            _max_value_len = Math.max(_max_value_len, Stream.of(values).map(String::length).max(Comparator.comparingInt(x -> x)).get());
//
//            this.envs.add(new Env(conf, desc, Arrays.asList(values)));
//            return this;
//        }
//
//        public static String center(String string, char ch, int length) {
//            if (string.length() < length) {
//                int pad = (length - string.length()) / 2;
//                return Strings.repeat(ch, pad) +
//                        string +
//                        Strings.repeat(ch, pad);
//            } else {
//                return string;
//            }
//        }
//
//        public static String leftPad(String string, int size) {
//            Objects.requireNonNull(string);
//            if (string.length() < size) {
//                return Strings.repeat(' ', size - string.length()) + string;
//            } else {
//                return string;
//            }
//        }
//
//        public static String rightPad(String string, int size) {
//            Objects.requireNonNull(string);
//            if (string.length() < size) {
//                return string + Strings.repeat(' ', size - string.length());
//            } else {
//                return string;
//            }
//        }
//
//        public String toPlainText() {
//            this.title = String.format("**%s**", Objects.isNull(title) ? "No Title" : title);
//            boolean skipDesc = _max_desc_len == 0;
//
//            int totalLen = this.envs.isEmpty()
//                    ? title.length()
//                    : Math.max(this.title.length(),
//                    skipDesc ? 1 + 2 + _max_value_len + 2 + 2 + _max_value_len + 2 + 1 :
//                            1 + 2 + _max_value_len + 2 + 2 + _max_desc_len + 2 + 2 + _max_value_len + 2 + 1);
//
//            StringBuffer sb = new StringBuffer();
//            //Title
//            sb.append(System.lineSeparator())
//                    .append(center(title, '*', totalLen));
//
//            for (Env conf : envs) {
//                sb.append(System.lineSeparator())
//                        .append("* ")
//                        .append(rightPad(conf.key, _max_key_len))
//                        .append("  ");
//                if (!skipDesc) {
//                    sb.append(rightPad(Strings.isBlank(conf.desc) ? "-" : conf.desc, _max_desc_len));
//                }
//
//                sb.append(" = ");
//
//                if (conf.values.size() > 1) {
//                    conf.values.forEach(v -> {
//                                sb.append(System.lineSeparator())
//                                        .append("* ")
//                                        .append(rightPad("", _max_key_len + 2));
//                                if (!skipDesc) {
//                                    sb.append(rightPad("", _max_desc_len));
//                                }
//
//                                sb.append("  -> ")
//                                        .append(v);
//                            }
//                    );
//                } else {
//                    sb.append(conf.values.get(0));
//                }
//            }
//
//            //Footer
//            sb.append(System.lineSeparator()).append(Strings.repeat('*', totalLen));
//            return sb.toString();
//        }
//
//        public void print(OutputStream os) throws IOException {
//            os.write(toPlainText().getBytes());
//            os.flush();
//        }
//    }
//
//    static class Env {
//
//        private final String key;
//        private final String desc;
//        private final Object values;
//
//        public Env(String key, String desc, Object values) {
//            this.key = key;
//            this.desc = desc;
//            this.values = values;
//        }
//
//        public String getKey() {
//            return key;
//        }
//
//        public String getDesc() {
//            return desc;
//        }
//
//        public Object getValues() {
//            return values;
//        }
//    }
//}
