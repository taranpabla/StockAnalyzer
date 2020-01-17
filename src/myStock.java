import java.io.*;
import java.math.*;
import java.util.*;

import yahoofinance.*;

public class myStock {

	// Initialize Hash map and Tree set to hold stock info
	HashMap<String, stockInfo> stockInfoHashMap;
	TreeSet<Map.Entry<String, stockInfo>> stockInfoTreeSet;
	
	private static class stockInfo {
		private String name;
		private BigDecimal price;
		public stockInfo(String nameIn, BigDecimal priceIn) {
			name = nameIn;
			price = priceIn;
		}
		public String toString() {
			StringBuilder stockInfoString = new StringBuilder("");
			stockInfoString.append(name + " " + price.toString());
			return stockInfoString.toString();
		}
	}

	// Class comparator for TreeSet Compares the Values of stockA and stockB
	class treeSetCompare implements Comparator<Map.Entry<String, stockInfo>>
	{
		public int compare(Map.Entry<String, stockInfo> stock1, Map.Entry<String, stockInfo> stock2)
		{
			// get the value to 2 stocks
			BigDecimal price1 = stock1.getValue().price;
			BigDecimal price2 = stock2.getValue().price;
			// Return the comparsion result Between the 2 stocks
			return price2.compareTo(price1);
		}
	}

	
	public myStock () {
		// make hashmap and tresset for the stocks
		stockInfoHashMap = new HashMap<>();
		stockInfoTreeSet = new TreeSet<Map.Entry<String, stockInfo>>(new treeSetCompare());
	}
    
	public void insertOrUpdate(String symbol, stockInfo stock) {
		if(stockInfoHashMap.containsKey(symbol))
		{
			// currStock hold the current stock value in the hash map
			stockInfo currStock = stockInfoHashMap.get(symbol);
			// replace the current stock with the new stock info
			stockInfoHashMap.replace(symbol, currStock, stock);

			// Update treeset as well with the new stock info. Create entry for Treeset
			AbstractMap.SimpleEntry<String, stockInfo> data = new AbstractMap.SimpleEntry<>(symbol, stock);
			// Add the Data to the TreeSet
			stockInfoTreeSet.add(data);
		} else {
			// use put
			stockInfoHashMap.put(symbol, stock);
			// Create New Data for the TreeSet
			AbstractMap.SimpleEntry<String, stockInfo> data = new AbstractMap.SimpleEntry<>(symbol, stock);
			// Add the Data to the TreeSet
			stockInfoTreeSet.add(data);
		}
	}
	
	public stockInfo get(String symbol) {

		return stockInfoHashMap.get(symbol);
	}
	
	public List<Map.Entry<String, stockInfo>> top(int k) {
		// init topstock to hold the top 10 stocks
		List<Map.Entry<String, stockInfo>> topStock = new ArrayList<>();

		// Create an Iterator to add the values of the top stocks
		Iterator<Map.Entry<String, stockInfo>> value = stockInfoTreeSet.iterator();
		int itr = 0;
		while(value.hasNext() && itr < k) {
			topStock.add(value.next());
			itr++;
		}
		return topStock;
	}
	

    public static void main(String[] args) throws IOException {   	
    	
    	// test the database creation based on the input file
    	myStock techStock = new myStock();
    	BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("./US-Tech-Symbols.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] var = line.split(":");
				
				// YahooFinance API is used
				// make sure the lib files are included in the project build path
				Stock stock = YahooFinance.get(var[0]);
				
				// test the insertOrUpdate operation 
				// here we are initializing the database
				if(stock.getQuote().getPrice() != null) {
					techStock.insertOrUpdate(var[0], new stockInfo(var[1], stock.getQuote().getPrice())); 
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int i = 1;
		System.out.println("===========Top 10 stocks===========");
		
		// test the top operation
		for (Map.Entry<String, stockInfo> element : techStock.top(10)) {
		    System.out.println("[" + i + "]" +element.getKey() + " " + element.getValue());
		    i++;
		}
		
		// test the get operation
		System.out.println("===========Stock info retrieval===========");
    	System.out.println("VMW" + " " + techStock.get("VMW"));
    	System.out.println("CHL" + " " + techStock.get("CHL"));
    }
}