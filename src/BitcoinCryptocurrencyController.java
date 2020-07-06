import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.swing.ImageIcon;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.google.gson.JsonObject;

public class BitcoinCryptocurrencyController extends AbstractCryptocurrencyController {

	public BitcoinCryptocurrencyController(CryptocurrencyModel model) {
		super(model);
	}

	@Override
	public InputStream getInputStreamFromTokenData() throws ClientProtocolException, IOException {
		return Request.Get("https://blockchain.info/ticker").execute().returnContent().asStream();
	}

	@Override
	public ImageIcon getCryptocurrencyIcon() {
		return new ImageIcon(this.getClass().getResource("Bitcoin.gif"));
	}

	@Override
	public String getCryptocurrencyName() {
		return "Bitcoin";
	}

	@Override
	public BigDecimal readCryptocurrencyValue(JsonObject jsonObject){
		JsonObject jsObject = jsonObject.getAsJsonObject("USD");
		BigDecimal bigDecimal = jsObject.get("last").getAsBigDecimal();
		return bigDecimal;
	}

}