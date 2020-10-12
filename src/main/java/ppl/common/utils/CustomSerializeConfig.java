package ppl.common.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomSerializeConfig extends SerializeConfig {

    @Override
    protected ObjectSerializer getEnumSerializer() {
        return CustomEnumSerializer.instance;
    }

    private static class CustomEnumSerializer implements ObjectSerializer {

        private static final CustomEnumSerializer instance = new CustomEnumSerializer();

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            SerializeWriter out = serializer.out;
            if (object == null) {
                out.writeNull();
                return;
            }

            if (!(object instanceof Enum)) {
                throw new JSONException(StringUtils.format("Required enum, but: " + object.getClass().getCanonicalName()));
            }

            if (EnumUtils.isEncodeSupport((Class<? extends Enum>)object.getClass())) {
                Object key = EnumUtils.encode((Enum) object);

                if (key instanceof String) {
                    char quote = out.isEnabled(SerializerFeature.UseSingleQuotes) ? '\'' : '"';
                    out.write(quote);
                    out.write((String) key);
                    out.write(quote);
                } else if (key.getClass().equals(Character.class)) {
                    char quote = out.isEnabled(SerializerFeature.UseSingleQuotes) ? '\'' : '"';
                    out.write(quote);
                    out.write(new char[] {(Character) key});
                    out.write(quote);
                } else {
                    if (key instanceof BigInteger) {
                        throw new JSONException(StringUtils.format(
                                "Serialize enum({}) to BigInteger is not supported.",
                                object.getClass().getCanonicalName()));
                    } else {
                        out.writeLong(((Number) key).longValue());
                    }
                }
            } else {
                EnumSerializer.instance.write(serializer, object, fieldName, fieldType, features);
            }
        }
    }

}
