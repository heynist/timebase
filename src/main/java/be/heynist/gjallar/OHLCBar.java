package be.heynist.gjallar;

import java.util.Date;

public class OHLCBar implements Comparable<Object> {

	private Long timestamp;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Long volume;
	private Double adjClose;
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}
	public Double getHigh() {
		return high;
	}
	public void setHigh(Double high) {
		this.high = high;
	}
	public Double getLow() {
		return low;
	}
	public void setLow(Double low) {
		this.low = low;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}
	public Double getAdjClose() {
		return adjClose;
	}
	public void setAdjClose(Double adjClose) {
		this.adjClose = adjClose;
	}
	
	@Override
	public int compareTo(Object anotherOHLCBar) {
		 if (!(anotherOHLCBar instanceof OHLCBar))
		      throw new ClassCastException("An OHLCBar object expected.");
		    Long anotherOHLCBarTimestamp = ((OHLCBar) anotherOHLCBar).getTimestamp();  
		    return this.getTimestamp().compareTo(anotherOHLCBarTimestamp);  	
	    }

}