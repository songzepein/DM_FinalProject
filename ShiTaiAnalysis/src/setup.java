import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;


public class setup {
	@SuppressWarnings("resource") 
	public ArrayList<String[]> Init(String path){
		ArrayList<String[]> ret = new ArrayList<String[]>(); 
		try {
			Scanner scanner = new Scanner(new File(path));
			while(scanner.hasNextLine()){
				String str = scanner.nextLine();
				String [] test = str.split("\t");
				test[2] = test[2].replace("/", "-");
				ret.add(test);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return ret;
	}
}
