import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class Launcher {

	public static void main(String[] args) throws ClientProtocolException, IOException {

		CryptocurrencyModel cryptocurrencyModel = new CryptocurrencyModel();
		AbstractCryptocurrencyController cryptocurrencyController = new BitcoinCryptocurrencyController(cryptocurrencyModel);
		CryptocurrencyView cryptocurrencyView = new CryptocurrencyView(cryptocurrencyController);
		cryptocurrencyModel.addObserver(cryptocurrencyView);
		cryptocurrencyController.startService();
	}
}