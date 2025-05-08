

public class Stock {
    private final String stockId;
    private float currentPrice ;
    private TwoThreeTree<Long, Float> priceUpdatesEvents;  // Key: timestamp, Value: price change
    private long initialTimeStamp;


    public Stock(String stockId, float initialPrice, long timeStamp) {
        this.stockId = stockId;
       // this.currentPrice = (float) 0;
        this.priceUpdatesEvents = new TwoThreeTree<Long, Float>(0L);
        this.priceUpdatesEvents.insert(timeStamp, initialPrice);
        this.initialTimeStamp = timeStamp;
        this.currentPrice = initialPrice;
    }

    public String getStockId() {
        return stockId;
    }


    // Add a price update event
    public void addUpdateEvent(long timestamp, float priceChange) {
        priceUpdatesEvents.insert(timestamp, priceChange);// Record the price change event
        currentPrice += priceChange;
    }

    // delete a price update event
    public void deleteUpdateEvent(long timestamp) {
        if (this.initialTimeStamp == timestamp) {
            throw new IllegalArgumentException("Cannot delete initialization event for stock: " + stockId);
        }
        TwoThreeTree.Node ireleventEvent = priceUpdatesEvents.search(timestamp);
        if (ireleventEvent == null) {
           throw new StockException("No price update event found for timestamp: " + timestamp);
        }
        float priceDiff = (float) ireleventEvent.getValue();
        this.currentPrice -= priceDiff;
        this.priceUpdatesEvents.delete(ireleventEvent);

    }

    // Compute current price considering only relevant updates
    public float getCurrentPrice() {
        return currentPrice;
    }

    // Override toString to print stock details
    @Override
    public String toString() {
        return "Stock ID: " + stockId + ", Current Price: " + currentPrice;
    }

}
