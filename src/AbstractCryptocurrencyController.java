import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public abstract class AbstractCryptocurrencyController {

	private CryptocurrencyModel model;
	private ScheduledExecutorService executorService;

	public AbstractCryptocurrencyController(CryptocurrencyModel model) {
		this.model = model;
	}

	/**
	 * @return an icon for the title bar of the window.
	 */
	public abstract ImageIcon getCryptocurrencyIcon();

	/**
	 * @return the cryptocurrency name.
	 */
	public abstract String getCryptocurrencyName();

	/** @return un flux de données vers les données de la monnaie suivie */
	public abstract InputStream getInputStreamFromTokenData() throws ClientProtocolException, IOException;

	/**
	 * Read the USD value of this cryptocurrency.
	 * @param jsonObject JSON representation of data provided by the API
	 * @return the last price of the cryptocurrency
	 */
	public abstract BigDecimal readCryptocurrencyValue(JsonObject jsonObject);

	/**
	 * Start the update service.
	 */
	public void startService() {
		// launch the program with automatic updates of price
		this.executorService = Executors.newSingleThreadScheduledExecutor();

		Runnable autoUpdateService = new Runnable() {
			@Override
			public void run() {
				AbstractCryptocurrencyController.this.updatePrice();
			}
		};

		//update the price every 60 seconds
		this.executorService.scheduleAtFixedRate(autoUpdateService, 0, 60, TimeUnit.SECONDS);

	}

	/**
	 * Update the model with the latest price.
	 */
	public void updatePrice() {

		InputStream inputStream;
		JsonObject jsonObject;

		try {
			// on récupère les données de la cryptomonnaie sélectionnée
			inputStream = this.getInputStreamFromTokenData();
			jsonObject = this.getJSONfromInputStream(inputStream);

			BigDecimal cryptocurrencyValue = this.readCryptocurrencyValue(jsonObject);

			// we update of the model
			this.model.updateData(this.getCurrentDateTime(), cryptocurrencyValue);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return  current date time
	 */
	private String getCurrentDateTime(){
		LocalDateTime now = LocalDateTime.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String formatDateTime = now.format(formatter);

		return formatDateTime;
	}

	/**
	 * Parse the data from the API provider.
	 * @param stream stream of data to parse
	 * @return a JSON representation of the data
	 * @throws UnsupportedEncodingException
	 */
	private JsonObject getJSONfromInputStream(InputStream stream) throws UnsupportedEncodingException{

		InputStreamReader inputStreamReader = new InputStreamReader(stream, "UTF-8");
		JsonObject jsonObject = new Gson().fromJson(inputStreamReader, JsonObject.class);
		return jsonObject;
	}
}