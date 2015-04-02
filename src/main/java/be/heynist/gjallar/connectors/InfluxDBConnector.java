package be.heynist.gjallar.connectors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import be.heynist.gjallar.OHLCBar;

@Service
public class InfluxDBConnector {
	@Value("${influxdb.host}")
	private String influxdbHost;

	@Value("${influxdb.api_port}")
	private String influxdbApiPort;

	@Value("${influxdb.database}")
	private String influxdbDatabase;

	@Value("${influxdb.user}")
	private String influxdbUser;

	@Value("${influxdb.password}")
	private String influxdbPassword;

	public InfluxDBConnector() {
	}

	private InfluxDB influxDB;
	
	@PostConstruct
	public void initConnection() {
		influxDB = InfluxDBFactory.connect("http://" + influxdbHost + ":" + influxdbApiPort, influxdbUser, influxdbPassword);
		//seedDatabase();
	}
	
	public List<Serie> getHistoricalDataFor(String security, String timeframe, Long form, Long to) {
		String query = "select * from spy_daily order ASC";
		return influxDB.query(influxdbDatabase, query, TimeUnit.MILLISECONDS);
	}
	
	private void seedDatabase() {
		influxDB.createDatabase(influxdbDatabase);
		
		TreeSet<OHLCBar> dataset = readCsvAndPopulateMap();
		
		for(OHLCBar bar : dataset) {
			Serie serie = new Serie.Builder("spy_daily")
			            .columns("timestamp", "open", "high", "low", "close", "volume", "adjclose")
			            .values(bar.getTimestamp(), bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose(), bar.getVolume(), bar.getAdjClose()).build();
			influxDB.write(influxdbDatabase, TimeUnit.MILLISECONDS, serie);
		}
	}

	private TreeSet<OHLCBar> readCsvAndPopulateMap() {
		final String CSV_FOLDER = "src/main/resources";

		TreeSet<OHLCBar> ohlcBarList = new TreeSet<OHLCBar>();
		BufferedReader br = null;
		String line = "";
		String csvFile = CSV_FOLDER + "/SPY-daily.csv";
		String cvsSplitBy = ",";
	 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] barData = line.split(cvsSplitBy);
				
				Date timestamp;
				try {
					if(barData[1].length() != 0) {
						timestamp = sdf.parse(barData[0]);
						if(new Float(barData[1]).intValue() != 0) {
							OHLCBar ohlcBar = new OHLCBar();
							ohlcBar.setTimestamp(new Long(timestamp.getTime()));
							ohlcBar.setOpen(new Double(barData[1]));
							ohlcBar.setHigh(new Double(barData[2]));
							ohlcBar.setLow(new Double(barData[3]));
							ohlcBar.setClose(new Double(barData[4]));
							ohlcBar.setVolume(new Long(barData[5]));
							ohlcBar.setAdjClose(new Double(barData[6]));
							ohlcBarList.add(ohlcBar);
						}
					}
				} catch (ParseException e) {
					//e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ohlcBarList;
	}

	public InfluxDB getInfluxDB() {
		return influxDB;
	}

}
