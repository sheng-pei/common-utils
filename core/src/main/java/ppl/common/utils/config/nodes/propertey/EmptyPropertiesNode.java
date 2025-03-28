package ppl.common.utils.config.nodes.propertey;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;

public class EmptyPropertiesNode extends AbstractNode {

    protected EmptyPropertiesNode(String path) {
        super(path);
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
        return def;
    }

    @Override
    public String textValue() {
        return null;
    }

    @Override
    public Byte byteValue(Byte def) {
        return def;
    }

    @Override
    public Byte byteValue() {
        return null;
    }

    @Override
    public Short shortValue(Short def) {
        return def;
    }

    @Override
    public Short shortValue() {
        return null;
    }

    @Override
    public Integer intValue(Integer def) {
        return def;
    }

    @Override
    public Integer intValue() {
        return null;
    }

    @Override
    public Long longValue(Long def) {
        return def;
    }

    @Override
    public Long longValue() {
        return null;
    }

    @Override
    public BigInteger bigintValue(BigInteger def) {
        return def;
    }

    @Override
    public BigInteger bigintValue() {
        return null;
    }

    @Override
    public Boolean boolValue(Boolean def) {
        return def;
    }

    @Override
    public Boolean boolValue() {
        return null;
    }

    @Override
    public Float floatValue(Float def) {
        return def;
    }

    @Override
    public Float floatValue() {
        return null;
    }

    @Override
    public Double doubleValue(Double def) {
        return def;
    }

    @Override
    public Double doubleValue() {
        return null;
    }

    @Override
    public Double doubleValue(int scale) {
        return null;
    }

    @Override
    public BigDecimal decimalValue(BigDecimal def) {
        return def;
    }

    @Override
    public BigDecimal decimalValue() {
        return null;
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        return null;
    }
}
