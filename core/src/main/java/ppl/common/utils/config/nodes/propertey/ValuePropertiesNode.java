package ppl.common.utils.config.nodes.propertey;

import ppl.common.utils.config.ConvertException;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.convert.Converters;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.scalar.ScalarNode;
import ppl.common.utils.string.Strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;

public class ValuePropertiesNode extends AbstractNode {

    private final Object scalar;

    protected ValuePropertiesNode(String path, Object value) {
        super(path);
        this.scalar = value;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public Node getChild(String fieldName) {
        return new MissingNode(childPath(fieldName));
    }

    @Override
    public Node getChild(Integer index) {
        return new MissingNode(childPath(index));
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<Node> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public String textValue(String def) {
        String ret = textValue();
        return Strings.isEmpty(ret) ? def : ret;
    }

    @Override
    public String textValue() {
        return scalar == null ? "" : Converters.stringValue(scalar);
    }

    @Override
    public Byte byteValue(Byte def) {
        Byte ret = byteValue();
        return ret == null ? def : ret;
    }

    @Override
    public Byte byteValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                BigInteger bi = new BigInteger(s);
                return Converters.byteValue(bi);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not whole number.", e);
            }
        }
        return Converters.byteValue(scalar);
    }

    @Override
    public Short shortValue(Short def) {
        Short ret = shortValue();
        return ret == null ? def : ret;
    }

    @Override
    public Short shortValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                BigInteger bi = new BigInteger(s);
                return Converters.shortValue(bi);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not whole number.", e);
            }
        }
        return Converters.shortValue(scalar);
    }

    @Override
    public Integer intValue(Integer def) {
        Integer ret = intValue();
        return ret == null ? def : ret;
    }

    @Override
    public Integer intValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                BigInteger bi = new BigInteger(s);
                return Converters.intValue(bi);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not whole number.", e);
            }
        }
        return Converters.intValue(scalar);
    }

    @Override
    public Long longValue(Long def) {
        Long ret = longValue();
        return ret == null ? def : ret;
    }

    @Override
    public Long longValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                BigInteger bi = new BigInteger(s);
                return Converters.longValue(bi);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not whole number.", e);
            }
        }
        return Converters.longValue(scalar);
    }

    @Override
    public BigInteger bigintValue(BigInteger def) {
        BigInteger ret = bigintValue();
        return ret == null ? def : ret;
    }

    @Override
    public BigInteger bigintValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                return new BigInteger(s);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not whole number.", e);
            }
        }
        return Converters.bigintValue(scalar);
    }

    @Override
    public Boolean boolValue(Boolean def) {
        Boolean ret = boolValue();
        return ret == null ? def : ret;
    }

    @Override
    public Boolean boolValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }

            if ("true".equals(s)) {
                return true;
            }
            if ("false".equals(s)) {
                return false;
            }
            throw new ConvertException("Invalid boolean value: '" + s + "'.");
        }

        return Converters.boolValue(scalar);
    }

    @Override
    public Float floatValue(Float def) {
        Float ret = floatValue();
        return ret == null ? def : ret;
    }

    @Override
    public Float floatValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                return Float.valueOf(s);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not real number.", e);
            }
        }
        return Converters.floatValue(scalar);
    }

    @Override
    public Double doubleValue(Double def) {
        Double ret = doubleValue();
        return ret == null ? def : ret;
    }

    @Override
    public Double doubleValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                return Double.valueOf(s);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not real number.", e);
            }
        }
        return Converters.doubleValue(scalar);
    }

    @Override
    public Double doubleValue(int scale) {
        if (scalar == null) {
            return null;
        }

        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }

            try {
                double d = Double.parseDouble(s);
                return Converters.doubleValue(d, scale);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not real number.", e);
            }
        }
        return Converters.doubleValue(scalar, scale);
    }

    @Override
    public BigDecimal decimalValue(BigDecimal def) {
        BigDecimal ret = decimalValue();
        return ret == null ? def : ret;
    }

    @Override
    public BigDecimal decimalValue() {
        if (scalar == null) {
            return null;
        }
        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                throw new ConvertException("Not real number.", e);
            }
        }
        return Converters.decimalValue(scalar);
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        if (scalar == null) {
            return null;
        }

        if (scalar instanceof String) {
            String s = (String) scalar;
            if (s.isEmpty()) {
                return null;
            }
        }
        return Converters.enumValue(scalar, enumClass);
    }
}
