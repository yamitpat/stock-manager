public class StockManager {
    private TwoThreeTree<String, Stock> stockById;
    private TwoThreeTree<priceTreeKey, Stock> stockByPrice;
    private final SentinelValueProvider<String> sentinelValueProvider;

    /**
     * Constructor to initialize the StockManager with default sentinel values.
     */
    public StockManager() {
        sentinelValueProvider = new SentinelValueProvider<>("a");
    }


    /**
     * Initializes the two 2-3 trees: stockById and stockByPrice.
     * Complexity: O(1)
     */
    void initStocks() {
        stockById = new TwoThreeTree<>("a");
        stockByPrice = new TwoThreeTree<>(new priceTreeKey(0f, "a"));
    }

    /**
     * Adds a new stock to the system.
     *
     * @param stockId   The unique identifier of the stock.
     * @param timestamp The timestamp of the stock creation.
     * @param price     The initial price of the stock.
     * Complexity: O(log n) for each tree insertion (total O(log n)).
     */
    public void addStock(String stockId, long timestamp, Float price) {
        //check if input is valid
        if(price <= 0){
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        Stock stock = new Stock(stockId, price, timestamp);
        //check if valid
        if(stockById.search(stockId) != null){
            throw new StockException("Stock with id " + stockId + " already exists");
        }
        stockById.insert(stockId, stock);
        priceTreeKey key = new priceTreeKey(price, stockId);
        stockByPrice.insert(key, stock);
    }

    /**
     * Removes a stock from the system by its ID.
     *
     * @param stockId The unique identifier of the stock to remove.
     * @throws StockException if the stock is not found.
     * Complexity: O(log n) for each tree search and deletion (total O(log n)).
     */
    void removeStock(String stockId) {
        TwoThreeTree.Node node = stockById.search(stockId);
        if (node == null) {
            throw new StockException("stock " + stockId + " not found");
        }
        Stock stock = (Stock) node.getValue();
        priceTreeKey key = new priceTreeKey(stock.getCurrentPrice(), stock.getStockId());
        stockById.delete(node);
        TwoThreeTree.Node nodeP = stockByPrice.search(key);
        if (nodeP == null) {
            throw new StockException("stock " + stockId + " not found");
        }
        stockByPrice.delete(nodeP);

    }


    /**
     * Updates the price of an existing stock.
     *
     * @param stockId        The unique identifier of the stock.
     * @param timestamp      The timestamp of the update.
     * @param priceDifference The change in the stock's price.
     * @throws StockException if the stock is not found.
     * Complexity: O(log n) for search, deletion, and insertion (total O(log n)).
     */
    void updateStock(String stockId, long timestamp, Float priceDifference) {
        TwoThreeTree.Node node = stockById.search(stockId);
        //check if node exist
        if (node == null) {
            throw new StockException("stock " + stockId + " not found");
        }
        //check if input is valid
        if (priceDifference == 0){
            throw new IllegalArgumentException("Price difference must be diffrent than zero");
        }
        Stock stock = (Stock) node.getValue();
        priceTreeKey key = new priceTreeKey(stock.getCurrentPrice(), stockId);
        TwoThreeTree.Node nodeP = stockByPrice.search(key);
        if (nodeP == null) {
            throw new StockException("stock " + stockId + " not found");
        }
        stockByPrice.delete(nodeP);
        stock.addUpdateEvent(timestamp, priceDifference);
        priceTreeKey newKey = new priceTreeKey(stock.getCurrentPrice(), stockId);
        stockByPrice.insert(newKey, stock);
    }

    /**
     * Retrieves the current price of a stock.
     *
     * @param stockId The unique identifier of the stock.
     * @return The current price of the stock.
     * @throws StockException if the stock is not found.
     * Complexity: O(log n) for search.
     */
    Float getStockPrice(String stockId) {
        TwoThreeTree.Node node = stockById.search(stockId);
        if (node == null) {
            throw new StockException("stock " + stockId + " not found");
        }
        Stock stock = (Stock) node.getValue();
        return stock.getCurrentPrice();
    }

    /**
     * Removes a specific timestamp event for a stock.
     *
     * @param stockId   The unique identifier of the stock.
     * @param timestamp The timestamp to remove.
     * Complexity: O(log n) for search, deletion, and insertion (total O(log n)).
     */
    void removeStockTimestamp(String stockId, long timestamp) {
        TwoThreeTree.Node node = stockById.search(stockId);
        if (node == null) {
            throw new StockException("stock " + stockId + " not found");
        }
        Stock stock = (Stock) node.getValue();
        priceTreeKey key = new priceTreeKey(stock.getCurrentPrice(), stockId);
        TwoThreeTree.Node nodeP = stockByPrice.search(key);
        stockByPrice.delete(nodeP);
        stock.deleteUpdateEvent(timestamp);
        priceTreeKey newKey = new priceTreeKey(stock.getCurrentPrice(), stockId);
        stockByPrice.insert(newKey, stock);
    }

    /**
     * Counts the number of stocks in a given price range.
     *
     * @param price1 The lower bound of the price range.
     * @param price2 The upper bound of the price range.
     * @return The number of stocks within the range.
     * Complexity: O(log n) for search and rank calculations.
     */
    int getAmountStocksInPriceRange(Float price1, Float price2) {
        //check if valid input
        if(price1 > price2){
            throw new StockException("price1 is greater than price2 - range is not valid");
        }
        //add upper and lower bounds leafs - logN
        priceTreeKey minKey = new priceTreeKey(price1, sentinelValueProvider.getMinValue());
        priceTreeKey maxKey = new priceTreeKey(price2, sentinelValueProvider.getMaxValue());
        this.stockByPrice.insert(minKey, null);
        this.stockByPrice.insert(maxKey, null);
        //find rank for each bound - logN
        TwoThreeTree.Node nodeMin = stockByPrice.search(minKey);
        TwoThreeTree.Node nodeMax = stockByPrice.search(maxKey);
        int min = stockByPrice.rank(nodeMin);
        int max = stockByPrice.rank(nodeMax);
        //delete bounds - logN
        this.stockByPrice.delete(nodeMin);
        this.stockByPrice.delete(nodeMax);
        //calculate range
        return  max - min - 1;
    }

    /**
     * Retrieves an array of stock IDs within a given price range.
     *
     * @param price1 The lower bound of the price range.
     * @param price2 The upper bound of the price range.
     * @return An array of stock IDs within the range.
     * Complexity: O(log n + k), where k is the number of stocks in the range.
     */
    String[] getStocksInPriceRange(Float price1, Float price2){
        //get num of stocks in range - 0(logN)
        int n = getAmountStocksInPriceRange(price1, price2);
        if (n == 0){ return new String[0]; }
        String[] stocks = new String[n];
        priceTreeKey minKey = new priceTreeKey(price1, "");
        //insert lower bound node - O(logN)
        this.stockByPrice.insert(minKey, null);
        //search lower bound node - O(logN)
        TwoThreeTree.Node nodeMin = stockByPrice.search(minKey);
        TwoThreeTree.Node startNode = nodeMin.getNext();
        //insert stock in range to array loop - O(K)
        for (int i = 0; i < n; i++) {
            Stock stock = (Stock) startNode.getValue();
            stocks[i] = stock.getStockId();
            startNode = startNode.getNext();
        }
        ////delete lower bound node - O(logN)
        stockByPrice.delete(nodeMin);
        return stocks;
    }

    void printStocks() {
        System.out.println("stocks by ID tree:");
        stockById.printTree();
        System.out.println("***************************************");
        System.out.println("stocks by Price tree:");
        stockByPrice.printTree();
    }


}






