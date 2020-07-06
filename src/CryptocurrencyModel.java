import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Model containing data displayed to the end-user :
 * <UL>
 * <LI> last update of price
 * <LI> historical price for the last 10 minutes
 * </UL>
 */
public class CryptocurrencyModel extends Observable{

	/**
	 * Last update date
	 */
	private String lastUpdate;

	/**
	 * Historical price of the asset
	 */
	private ArrayList<BigDecimal> historicalPrice;

	public CryptocurrencyModel() {
		this.historicalPrice = new ArrayList<>();
	}


	public ArrayList<BigDecimal> getHistoricalPrice() {
		return this.historicalPrice;
	}

	public BigDecimal getLastPrice() {
		return this.historicalPrice.get(this.historicalPrice.size()-1);
	}

	public String getLastUpdate() {
		return this.lastUpdate;
	}

	public String getLastUpdated() {
		return this.lastUpdate;
	}

	public BigDecimal getPreviousPrice() {
		return (this.historicalPrice.size()<=1 ? null : this.historicalPrice.get(this.historicalPrice.size()-2));
	}

	public void setHistoricalPrice(ArrayList<BigDecimal> historicalPrice) {
		this.historicalPrice = historicalPrice;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void updateData(String lastUpdatedDatetime, BigDecimal newValue) {
		this.lastUpdate = lastUpdatedDatetime;

		// save the current price if it is available
		if (this.historicalPrice.size() > 0){

			if (!(this.historicalPrice.get(this.historicalPrice.size()-1).compareTo(newValue) == 0) ) {

				// clean the oldest element if we are storing 10 prices
				if (this.historicalPrice.size() == 10)
					this.historicalPrice.remove(0);

				this.historicalPrice.add(newValue);
			}

		} else
			this.historicalPrice.add(newValue);

		// we notify views
		this.setChanged();
		this.notifyObservers();
	}
}
