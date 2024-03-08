package ppl.common.utils.config.nodes.map;

import ppl.common.utils.config.*;
import ppl.common.utils.config.ConvertException;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.iterator.ObjectIterator;

import java.util.*;

public final class MapNode extends AbstractNode {

    private final Map<?, ?> map;

    MapNode(String path, Map<?, ?> map) {
        super(path);
        this.map = map;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Node getChild(String fieldName) {
        String path = childPath(fieldName);
        if (!this.map.containsKey(fieldName)) {
            return new MissingNode(path);
        }
        try {
            return Nodes.createByPath(path, this.map.get(fieldName));
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);
        return new MissingNode(path);
    }

    @Override
    public Iterator<Node> iterator() {
        return new ObjectIterator(this.map.entrySet().iterator(), this::childPath);
    }

    @Override
    public String textValue(String def) {
        throw new ConvertException("Map node");
    }

    @Override
    public String textValue() {
        throw new ConvertException("Map node");
    }

    @Override
    public Byte byteValue(Byte def) {
        throw new ConvertException("Map node");
    }

    @Override
    public Byte byteValue() {
        throw new ConvertException("Map node");
    }

    @Override
    public Short shortValue(Short def) {
        throw new ConvertException("Map node");
    }

    @Override
    public Short shortValue() {
        throw new ConvertException("Map node");
    }

    @Override
    public Integer intValue(Integer def) {
        throw new ConvertException("Map node");
    }

    @Override
    public Integer intValue() {
        throw new ConvertException("Map node");
    }

    @Override
    public Long longValue(Long def) {
        throw new ConvertException("Map node");
    }

    @Override
    public Long longValue() {
        throw new ConvertException("Map node");
    }

    @Override
    public Boolean boolValue(Boolean def) {
        throw new ConvertException("Map node");
    }

    @Override
    public Boolean boolValue() {
        throw new ConvertException("Map node");
    }

    @Override
    public Double doubleValue(Double def) {
        throw new ConvertException("Map node");
    }

    @Override
    public Double doubleValue() {
        throw new ConvertException("Map node");
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        throw new ConvertException("Map node");
    }

}
