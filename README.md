# Stocks Manager

This project implements a real-time stock management system using a custom generic 2-3 tree data structure. It supports efficient updates, queries, and invalidation of stock price changes, while meeting strict time complexity constraints. The system was built from scratch without external libraries.

## Overview

The system models a dynamic stock market where each stock can be initialized, updated over time with timestamped events, and queried for its current value. Some updates may later be invalidated, and the system accounts for such changes in real time.

## Data Structure

The entire system is built using a **generic 2-3 tree** implementation. Each node in the main 2-3 tree stores another 2-3 tree that manages the update history for a particular stock. This recursive tree-based design ensures logarithmic time performance for insertions, deletions, and queries, both for managing stocks and their respective price updates.

## Core Class

### `StocksManager`

This class implements the main logic and provides the following operations:

#### `void initStocks()`
Initializes the stock management system.  
**Time Complexity**: O(1)

#### `void addStock(String stockId, long timestamp, Float price)`
Adds a new stock with a given initial price and timestamp.  
**Time Complexity**: O(log N)

#### `void removeStock(String stockId)`
Removes a stock and all its history from the system.  
**Time Complexity**: O(log N)

#### `void updateStock(String stockId, long timestamp, Float priceDifference)`
Adds a new timestamped price update to a stock. Updates can be positive or negative.  
**Time Complexity**: O(log N + log M), where M is the number of relevant updates for the stock.

#### `Float getStockPrice(String stockId)`
Returns the current price of the given stock, including all relevant updates.  
**Time Complexity**: O(log N)

#### `void removeStockTimestamp(String stockId, long timestamp)`
Invalidates a previously added update event.  
**Time Complexity**: O(log N + log M)

#### `int getAmountStocksInPriceRange(Float price1, Float price2)`
Returns the number of stocks whose current price lies in a given range.  
**Time Complexity**: O(log N)

#### `String[] getStocksInPriceRange(Float price1, Float price2)`
Returns a list of stock IDs within a given price range, sorted by price and then lexicographically by ID.  
**Time Complexity**: O(log N + K), where K is the number of stocks in the range.

## Implementation Constraints

- All operations were implemented from scratch, with no use of external libraries or built-in data structures.
- No `System.out` or logging statements are used.
- The code compiles and runs on the dedicated Microsoft Azure server.
- Only standard Java features are used.

## Project Files

- `TwoThreeTree.java`: **generic** 2-3 tree implementation 
- `StockManager.java`: Manages the logic for stocks and price updates.
- `StockException.java`: Custom exception handling.
- `Stock.java`: Stock class representing stock data.
- `SentinelValueProvider.java`: Provides sentinel values for the 2-3 tree.
- `priceTreeKey.java`: defines the tuple key used in the main 2-3 tree.
- `Main.java`: Entry point for testing the system.

---

This project demonstrates the use of a custom 2-3 tree data structure to manage and query stock prices efficiently under strict performance constraints.
