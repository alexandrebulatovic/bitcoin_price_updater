# Bitcoin Price Updater

Copyright © 2020 Alexandre BULATOVIC

This personal project was made for fun in 2019. 

It also served to experience how to draw charts with Java using [JFreeChart](http://www.jfree.org/jfreechart/), how to use JSON resources (using [Gson library](https://en.wikipedia.org/wiki/Gson)) and how to consume an API with a GET request (using [Apache HttpClient library](http://hc.apache.org/index.html)).

## User's guide :
If you execute the JAR file (you need at least JRE 7 on your machine), a small window in a pop-up style will open and will display the current bitcoin price. The price is updated every minute and a chart will draw itself remembering the last 10 retrieved prices. Then every new update will delete the oldest stored price.

If the new updated price is higher than the previous one, the price will turn green, if it is lower, it will turn red, and if it stays the same the price stays (or turns) black. You can also zoom-in and out the chart with the scroll wheel or by selecting an area on the chart.

## Class diagram : 
<a href="https://raw.githubusercontent.com/alexandrebulatovic/bitcoin_price_updater/master/design-diagrams/personal-project-bitcoin.png"> 
	<img src="https://raw.githubusercontent.com/alexandrebulatovic/bitcoin_price_updater/master/design-diagrams/personal-project-bitcoin.png" width="150">
</a>
