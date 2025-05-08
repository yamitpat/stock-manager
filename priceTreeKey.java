public class priceTreeKey implements Comparable<priceTreeKey> {
    private float price;
    private String stockId;

    public priceTreeKey(float price, String stockId) {
        this.price = price;
        this.stockId = stockId;
    }

    @Override
    public int compareTo(priceTreeKey other) {
        if (this.price < other.price) {
            return -1;
        }else if (this.price > other.price) {
            return 1;
        }
        return this.stockId.compareTo(other.stockId);
    }

    public float getPrice() {
        return price;
    }


    @Override
    public String toString() {
        return "StockKey{" + "price=" + price + ", stockId='" + stockId + "'}";
    }


    @Override
    public boolean equals(Object obj) {
        // בדוק אם האובייקט הוא הפניה לאותו אובייקט
        if (this == obj) {
            return true;
        }
        // בדוק אם האובייקט הוא null או לא מאותו סוג
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // המרת האובייקט ל- priceTreeKey
        priceTreeKey other = (priceTreeKey) obj;

        // השוואת המחיר וה-stockId
        if (Float.compare(price, other.price) != 0) {
            return false;
        }
        return stockId.equals(other.stockId);
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(price);
        result = 31 * result + stockId.hashCode();
        return result;
    }
}
