package me.study.ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

public class FileHandler {
	private static final String SPLIT = ",";
	private static final String FIELDSTART = "<field name=\"";
	private static final String FIELDSTART_2 = "\">";
	private static final String FIELDSEND = "</field>";
	private static final String DOCSTART = "<doc>";
	private static final String DOCEND = "</doc>";
	private static final String NA = "NA";
	private static final String ID = "id";
	private static final String REVIEW = "review";
	private static final String STARS = "stars";
	private static final String LANGUAGE = "language";
	private static final String DATE = "date";

	public void run() {
		BufferedReader br = null;
		try {
			StringBuilder xml = new StringBuilder("<add>");
			File csvFile = new File(this.getClass().getClassLoader().getResource("data.csv").getFile());
			String line = "";
			br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile),StandardCharsets.UTF_8));
			int count = 0;
			int starsCol;
			int langCol;
			int dateCol;
			int startReviewCol;
			String review;
			int good = 0;
			while ((line = br.readLine()) != null) {
				if (count > 0) {
					String[] split = line.split(SPLIT);
					langCol = split.length-4;
					starsCol = findStars(split);
					startReviewCol = starsCol+5;
					dateCol = starsCol+1;
					if (!split[1].equals(NA) && starsCol != -1) {
						xml.append(DOCSTART);
						review = "";
						for (int i = startReviewCol; i < langCol; i++) {
							if (i > 1) {
								review += ",";
							}
							review += split[i];
						}
						xml.append(FIELDSTART + ID + FIELDSTART_2 + count + FIELDSEND);
						xml.append(FIELDSTART + REVIEW + FIELDSTART_2 +  replaceFormat(review) + FIELDSEND);
						xml.append(FIELDSTART + STARS + FIELDSTART_2 + split[starsCol] + FIELDSEND);
						xml.append(FIELDSTART + LANGUAGE + FIELDSTART_2 + getLanguage(split[langCol]) + FIELDSEND);
						xml.append(FIELDSTART + DATE + FIELDSTART_2 + (split[dateCol].length()==19? split[dateCol].replaceAll(" ", "T") + "Z":"2000-01-01T00:00:00Z") + FIELDSEND);
						xml.append(DOCEND);
						good++;
					}
				}
				count++;
			}
			xml.append("</add>");
			Writer writer = new OutputStreamWriter(new FileOutputStream("result.xml"),StandardCharsets.UTF_8);
			writer.flush();
			writer.write(xml.toString());
			writer.close();
			System.out.println("END" + good);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			if (br != null) {
				try {
					br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private String getLanguage(String string) {
		return string.replaceAll("\"", "");
	}

	private int findStars(String[] split) {
		for (int i = 2; i < split.length; i++) {
			if (StringUtils.isNumeric(split[i])) {
				int val = Integer.parseInt(split[i]);
				if (val > 0 && val <= 5) {
					return i;
				}
			}
		}
		return -1;
	}
	
	//throw away html 
	private String replaceFormat(String text) {
		String returnValue = "";
		String[] textSplit = text.split("<");
		int count = 0;
		for(String t : textSplit) {
			if(count > 0) {
				if(t.indexOf(">") > -1) {
					String[] t2Split = t.split(">");
					returnValue+=t2Split.length>1? t2Split[1] :"";
				}else {
					returnValue += t;
				}
				
			}else {
				returnValue+=t;
			}
			count ++;
		}
		returnValue = returnValue.replaceAll("\"", "");
		return returnValue;
	}
	

}
