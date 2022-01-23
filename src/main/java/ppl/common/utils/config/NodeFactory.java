package ppl.common.utils.config;

/**
 * Check if the object can be accepted by NodeFactory one by one, until any NodeFactory accepting the object.
 */
public interface NodeFactory {
    /**
     * Please return nonnegative order value, if you want to create your own NodeFactory.
     * You can create NodeFactory with negative order value, if you do know what you are doing.
     * If the different NodeFactories have same order value, please make sure no object can be accepted by them.
     * @return order value.
     */
    int order();

    /**
     * Check if the specified obj can be accepted by this NodeFactory.
     * @param obj target obj.
     * @return true if accepted, otherwise false.
     */
    boolean accept(Object obj);

    Node createRoot(Object obj);
    Node create(String path, Object obj);
}
