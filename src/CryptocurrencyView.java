import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 * Contains elements displayed to the end-user.
 */
public class CryptocurrencyView extends JDialog implements Observer{

	private AbstractCryptocurrencyController controller;

	private JLabel lastPrice;
	private JLabel lastUpdateDatetime;
	private Box boxContainer;

	private ChartPanel chartPanel;
	private JPanel chartjPanel;

	public CryptocurrencyView(AbstractCryptocurrencyController controller)
	{
		super(new JFrame(), " Live price for "+ controller.getCryptocurrencyName());

		this.controller = controller;

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(290, 215);
		this.setLocationRelativeTo(null); // center the window
		this.setResizable(false);
		this.setAlwaysOnTop(true); // "pop-up" style window

		// set our icon for the title bar
		this.setIconImage(this.controller.getCryptocurrencyIcon().getImage());

		// close the app with zero status code
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.boxContainer = Box.createVerticalBox();

		this.lastUpdateDatetime = new JLabel();
		this.lastUpdateDatetime.setFont(new Font("Serif", Font.PLAIN, 18));
		this.lastUpdateDatetime.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.lastUpdateDatetime.setToolTipText("Updated every minute.");
		this.boxContainer.add(this.lastUpdateDatetime);

		this.lastPrice = new JLabel();
		this.lastPrice.setFont(new Font("Serif", Font.PLAIN, 18));
		this.lastPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.boxContainer.add(this.lastPrice);


		this.setContentPane(this.boxContainer);
		this.setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {

		CryptocurrencyModel cryptocurrencyModel = (CryptocurrencyModel) o;

		this.updateLastUpdatedLabel(cryptocurrencyModel.getLastUpdated());
		this.updateLastPrice(cryptocurrencyModel.getLastPrice());
		this.trendChange(cryptocurrencyModel.getPreviousPrice(), cryptocurrencyModel.getLastPrice());
		this.updateChart(cryptocurrencyModel.getHistoricalPrice());
	}

	/**
	 * Change the color of displayed price according to the trend (up or down).
	 * @param oldPrice the old price.
	 * @param newPrice the new price.
	 */
	private void trendChange(BigDecimal oldPrice, BigDecimal newPrice) {

		if(oldPrice != null) { // if there is a value

			if (newPrice.compareTo(oldPrice) > 0)
				this.lastPrice.setForeground(new Color(0, 155, 0)); // green if going up
			else if (newPrice.compareTo(oldPrice) < 0)
				this.lastPrice.setForeground(Color.red); // else red
			else
				this.lastPrice.setForeground(UIManager.getColor("Label.foreground")); // if the price stays the same, the color returns to black
		}
	}

	private void updateChart(ArrayList<BigDecimal> values) {

		// fetch data from the model
		XYSeries xySeries = new XYSeries("Prix");

		for (int i = 0; i < values.size(); i++) {
			xySeries.add
			((i==0)? i : i*5, values.get(i).doubleValue());
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(xySeries);

		// create a chart with the setup below
		JFreeChart chart = ChartFactory.createXYLineChart(
				null,      // chart title
				null,                      // x axis label
				null,                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL,
				false,                     // include legend
				true,                     // tooltips
				false                     // urls
				);

		// get a reference to the plot for further customisation...
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		// use a white background
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.black);
		plot.setDomainGridlinePaint(Color.black);

		XYLineAndShapeRenderer renderer	= (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);

		// to get a tooltip in the local currency format
		renderer.setBaseToolTipGenerator(new XYToolTipGenerator() {
			@Override
			public String generateToolTip(XYDataset dataset, int series, int item) {
				NumberFormat numberFormat = NumberFormat.getInstance();
				//	numberFormat.setCurrency(Currency.getInstance(Locale.US)); // for the US format
				numberFormat.setMaximumFractionDigits(15);
				return "$" + numberFormat.format(dataset.getYValue(series, item));
			}
		});

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// use the  local currency format again
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(6);
		rangeAxis.setNumberFormatOverride(numberFormat);
		rangeAxis.setAutoRangeIncludesZero(false);
		//		rangeAxis.centerRange(xySeries.getY(xySeries.getItemCount()-1).doubleValue());

		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		// domainAxis.setLowerBound(0);
		domainAxis.setVisible(false);
		// domainAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance()); 


		this.setVisible(false);

		if (this.chartjPanel != null)
			this.getContentPane().remove(this.chartjPanel);

		this.chartPanel =  new ChartPanel(chart, 260, 120, 260, 120, 260, 120, false, false, true, false, false, true );
		this.chartjPanel = new JPanel();
		this.chartPanel.setMouseWheelEnabled(true); // allow scroll for zoom-in and out

		this.chartjPanel.add(this.chartPanel);
		this.getContentPane().add(this.chartjPanel);
		this.setVisible(true);
	}


	private void updateLastPrice(BigDecimal value) {
		this.lastPrice.setText("$" + String.valueOf(value));
	}

	private void updateLastUpdatedLabel(String nouvelleDateDeMiseAJour) {
		this.lastUpdateDatetime.setText("Last updated : " + nouvelleDateDeMiseAJour);
	}
}
