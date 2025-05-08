public class SentinelValueProvider<K extends Comparable<K>> {

    private final Class<K> keyType;

    // Constructor that infers the class from a sample key
    public SentinelValueProvider(K exampleKey) {
        this.keyType = (Class<K>) exampleKey.getClass();
    }

    public K getMinValue() {
        if (keyType == Integer.class) {
            return keyType.cast(Integer.MIN_VALUE);
        } else if (keyType == Double.class) {
            return keyType.cast(Double.NEGATIVE_INFINITY);
        } else if (keyType == Long.class) {
            return keyType.cast(Long.MIN_VALUE);
        } else if (keyType == Float.class) {
            return keyType.cast(Float.NEGATIVE_INFINITY);
        } else if (keyType == String.class) {
            return keyType.cast("/u0000"); // Smallest string lexicographically
        } else if (keyType == priceTreeKey.class) {
            return keyType.cast(new priceTreeKey(Float.NEGATIVE_INFINITY, "/u0000"));
        }

        throw new UnsupportedOperationException("Min value for type " + keyType.getName() + " is not supported.");
    }

    public K getMaxValue() {
        if (keyType == Integer.class) {
            return keyType.cast(Integer.MAX_VALUE);
        } else if (keyType == Double.class) {
            return keyType.cast(Double.POSITIVE_INFINITY);
        } else if (keyType == Long.class) {
            return keyType.cast(Long.MAX_VALUE);
        } else if (keyType == Float.class) {
            return keyType.cast(Float.POSITIVE_INFINITY);
        } else if (keyType == String.class) {
            return keyType.cast(Character.toString(Character.MAX_VALUE));
        } else if (keyType == priceTreeKey.class) {
            // Max value for priceTreeKey: largest price and lexicographically largest stockId
            return keyType.cast(new priceTreeKey(Float.POSITIVE_INFINITY, Character.toString(Character.MAX_VALUE)));
        }
        throw new UnsupportedOperationException("Max value for type " + keyType.getName() + " is not supported.");
    }

    public K getSentinelValue(String type) {
        if ("min".equalsIgnoreCase(type)) {
            return getMinValue();
        } else if ("max".equalsIgnoreCase(type)) {
            return getMaxValue();
        }
        throw new IllegalArgumentException("Invalid sentinel type. Use 'min' or 'max'.");
    }
}
