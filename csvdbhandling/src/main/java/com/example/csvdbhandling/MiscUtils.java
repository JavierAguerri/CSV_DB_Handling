package com.example.csvdbhandling;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;

// singleton class
public final class MiscUtils {

	private static MiscUtils instance = null;

	public static MiscUtils getInstance() {
		if (instance == null) {
			instance = new MiscUtils();
		}
		return instance;
	}

	public static void downloadCSVfromLink(String csvLink, String csvPath) throws IOException {
		try (InputStream inputStream = new URL(csvLink).openConnection().getInputStream();
				FileOutputStream outputStream = new FileOutputStream(csvPath)) {
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// if there is a problem downloading orders, delete the file in case it was partially written
			Files.deleteIfExists(Paths.get(csvPath));
			e.printStackTrace();
			throw e;
		}
	}

	public static void readFieldsConfig(LinkedHashMap<String, Map<String, String>> fieldPropsMap,
			String orderFieldsConfigPath) throws Exception {
		try (FileReader reader = new FileReader(orderFieldsConfigPath)) {
			Gson gson = new Gson();
			FieldConfig[] fieldConfigs = gson.fromJson(reader, FieldConfig[].class);
			for (FieldConfig fieldConfig : fieldConfigs) {
				fieldPropsMap.put(fieldConfig.name, fieldConfig.properties);
			}
		} catch (Exception e) {
			// if there is a problem setting the fieldPropsMap, empty it
			fieldPropsMap = new LinkedHashMap<>();
			e.printStackTrace();
			throw e;
		}
	}
}
