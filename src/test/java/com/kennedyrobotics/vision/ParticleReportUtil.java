package com.kennedyrobotics.vision;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ParticleReportUtil {

	public static final File PARTICLE_DATA = new File("src/test/resources/particleData.txt");
	
	public static List<ParticleReport> load(File file) throws IOException {
		List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
		lines.remove(0);
		lines.remove(0);

		List<ParticleReport> ret = new ArrayList<ParticleReport>();

		for (String line : lines) {
			if (line.isEmpty()) continue;
			String elements[] = line.split("\t");
//			System.out.println(Arrays.deepToString(elements));
			ParticleReport report = new ParticleReport();
			report.index = Integer.valueOf(elements[0]);
			report.comX = Double.valueOf(elements[1]);
			report.comY = Double.valueOf(elements[2]);
			report.boundingRectLeft = Double.valueOf(elements[3]);
			report.boundingRectTop = Double.valueOf(elements[4]);
			report.boundingRectRight = Double.valueOf(elements[5]);
			report.boundingRectBottom = Double.valueOf(elements[6]);
			report.area = Double.valueOf(elements[7]);
			report.percentAreaToImageArea = 0;
			
			ret.add(report);
		}
		return ret;
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		load(PARTICLE_DATA);
	}

}
