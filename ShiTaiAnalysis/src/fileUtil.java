import java.util.Map.Entry;
import java.util.TreeMap;


public class fileUtil {
	private String yearStr = "";
	private String montStr = "";
	private String dayStr = "";
	private String dataStr = "";
	private int cnt = 0;
	
	public String getYearString(){
		return yearStr;
	}
	
	public String getMontString(){
		return montStr;
	}
	
	public String getDayString() {
		return dayStr;
	}
	
	public int getCntNumber() {
		return cnt;
	}
	
	public String getDataString() {
		return dataStr;
	}
	
	public void Init(TreeMap<String, Integer> trMap){
		cnt = 0;
		for(Entry<String, Integer> ee : trMap.entrySet()){
			cnt++;
			yearStr = yearStr + ee.getKey().substring(1, 5)+ ",";	
			montStr = montStr + ee.getKey().substring(6, 8)+ ",";
			dayStr = dayStr + ee.getKey().substring(9, 11)+ ",";
			dataStr = dataStr + ee.getValue() + ",";
		}
		yearStr = yearStr.substring(0, yearStr.length());
		dayStr = dayStr.substring(0, dayStr.length());
		montStr = montStr.substring(0, montStr.length());
		dataStr = dataStr.substring(0, dataStr.length());
	}
}
