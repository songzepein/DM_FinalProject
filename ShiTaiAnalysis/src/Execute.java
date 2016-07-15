import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Execute {
	private static int maxP = 0;
	private static int maxRateP = 0;
	private static int minRateP = 0;
	private static String las = "";
	
	public static TreeMap<String, Integer> dataSummary(String fpath){
		setup st = new setup();
		ArrayList<String[]> e = st.Init(fpath);
		TreeMap<String, Integer> trMap = new TreeMap<String, Integer>();
		for(String[] test : e){
			int cnt = 0;
			try {
				cnt = Integer.parseInt(test[3].substring(1, test[3].length()-1));
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
			if(trMap.containsKey(test[2])){
				trMap.put(test[2], (Integer)(cnt+(int)trMap.get(test[2])));
			}else{
				trMap.put(test[2], (Integer)(cnt));
			}
		}
		return trMap;
	}
	
	public static ArrayList<Integer> getVisNumFromData(TreeMap<String, Integer> trMap){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(Entry<String, Integer> ee : trMap.entrySet()){
			ret.add(ee.getValue());
		}
		return ret;
	}
	
	public static ArrayList<String> getVisDateFromData(TreeMap<String, Integer> trMap){
		ArrayList<String> ret = new ArrayList<String>();
		for(Entry<String, Integer> ee : trMap.entrySet()){
			ret.add(ee.getKey());
		}
		return ret;
	}
	
	public static void getOutputEcharts(TreeMap<String, Integer> trMap,String outPath) throws IOException{
		fileUtil deal = new fileUtil();
		deal.Init(trMap);
		
		String htmlEcharts = "var data = ["
				+ deal.getDataString()
				+"];"
				+ "var date = [];"
				+ "var cnt = "
				+ deal.getCntNumber()
				+ ";var year = ["
				+ deal.getYearString()
				+ "];var mont = ["
				+ deal.getMontString()
				+ "];var day = ["
				+ deal.getDayString()
				+ "];for(var i=0;i<cnt;i++){date.push([year[i],mont[i],day[i]].join('-'));}option = {tooltip:{trigger: 'axis'},"
				+ "title: {left: 'center',text: '"
				+ las 
				+"事态分析结果图',},legend: {top: 'bottom',data:['意向']},toolbox: {show: false,feature: {dataView: {show: true, readOnly: false},magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},restore: {show: true},saveAsImage: {show: true}}},"
				+ "xAxis: {type: 'category',boundaryGap: false,data: date},yAxis: {type: 'value',boundaryGap: [0, '100%']},dataZoom: [{type: 'inside',start: 0,end: 10}, {start: 0,end: 10}],series: [{name:'当前日期事件访问量',type:'line',smooth:true,symbol: 'none',sampling: 'average',itemStyle: {normal: {color: 'rgb(255, 70, 131)'}},areaStyle: {normal: {color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{offset: 0,color: 'rgb(255, 158, 68)'}, {offset: 1,color: 'rgb(255, 70, 131)'}])}},data: data}]};";
	
		String strEcharts = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>ECharts</title><script src=\"echarts.js\"></script></head><body><div id=\"main\" style=\"width: 600px;height:400px;\"></div><script type=\"text/javascript\">var myChart = echarts.init(document.getElementById('main'));"; 
		strEcharts += htmlEcharts;
		strEcharts += "myChart.setOption(option);</script></body></html>";
		
		File file = new File(outPath);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		Files.write(Paths.get(outPath),strEcharts.getBytes());
	}
	
	public static void planA(ArrayList<Double> lis){
		double maxRate = 0.0;
		double minRate = 0.0;
		for(int i = 1; i < lis.size(); i++){
			double rate = 0.0;
			if((lis.get(i) - lis.get(i-1)) > 0){
				rate = (lis.get(i) - lis.get(i-1))/lis.get(i-1); 
				if(rate > maxRate){
					maxRate = rate;
					maxRateP = i;
				}
			}else{
				if(i > maxP){
					rate = (lis.get(i-1) - lis.get(i))/lis.get(i);
					if(rate > minRate){
						minRate = rate;
						minRateP = i;
					}	
				}
			}
		}
	}
	
	public static void planB(ArrayList<Double> lis){
		double mmx = lis.get(maxP);
		double thetal = mmx - mmx * 0.18;
		for(int i=0;i < maxP; i++){
			double tmp = mmx - lis.get(i);
			if(tmp < thetal){
				maxRateP = i;
				break;
			}
		}
		
		thetal = mmx - mmx * 0.05;
		for(int i = maxP+1;i < lis.size();i++){
			double tmp = mmx - lis.get(i);
			if(tmp > thetal){
				minRateP = i;
				break;
			} 
		}
	}
	
	public static void getWork(ArrayList<Integer> plis){
		ArrayList<Double> lis = new ArrayList<Double>();
		
		for(int i=0;i<plis.size();i++){
			double xch = (double)plis.get(i);
			lis.add(i, xch);
		}
		
		//首先获取最大值
		double maxNum = -1.0;
		double minNum = 999999999.0;
		for(int i = 0; i < lis.size(); i++){
			if (lis.get(i) > maxNum) {
				maxNum = lis.get(i);
				maxP = i;
			}
			if(lis.get(i) < minNum ){
				minNum = lis.get(i);
			}
		}

		//接下来做归一化处理
		double gap = maxNum - minNum;
		for(int i = 0; i < lis.size(); i++){
			double tmp = lis.get(i);
			lis.set(i, tmp / gap);
		}
		
		//接下来找最大上升率的点，和最大下降率的点
		//plan A是采用归一化后的访问量增长比进行求解
		//plan B是采用设置阈值thetal来判断这个位置是否是发展点
		planB(lis);
	}
	
	public static void main(String[] args) throws IOException {
		//这里应该首先需要一个config文件读取输入输出路径
//		String inPath = args[0];
//		String outPath = args[1];
		
		String inPath = "";
		String outPath = "";
		
		if(inPath == null || inPath.length()==0) {
			inPath = "E:\\peinballboy\\work\\AQZX\\话题热度语料\\习马会.txt";
		}
		
		String[] slis = inPath.split("\\\\");
		las = slis[slis.length-1];
		las = las.substring(0, las.length()-4);
		
		if(outPath == null || outPath.length()==0) {
			outPath = "E:\\peinballboy\\work\\AQZX\\dist\\results\\习马会.html";
		}
		
//		System.out.println(inPath + " " + outPath);
		
		//接下来是处理输出进行画图
		TreeMap<String, Integer> trMap = dataSummary(inPath);
		getOutputEcharts(trMap, outPath);
		
		System.out.println("over");
		
		//最后是进行事态分析
		getWork(getVisNumFromData(trMap));
		
		ArrayList<String> atLast = getVisDateFromData(trMap);
		
		System.out.println("高峰点：" + atLast.get(maxP));
		System.out.println("发展点：" + atLast.get(maxRateP));
		System.out.println("衰退点：" + atLast.get(minRateP));
		
		System.out.println("高峰点数组位置：" + maxP);
		System.out.println("发展点数组位置：" + maxRateP);
		System.out.println("衰退点数组位置：" + minRateP);
	}
}
