package com.easemywork.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.easemywork.model.Datasource;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class Application {

	private static Set<String> puCheckList = new HashSet<String>();
	private static Set<String> pdCheckList = new HashSet<String>();

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Multimap<String, Datasource> datasourceMap = LinkedHashMultimap.create();

		System.out.println(
				"******************************************************************************************************");
		System.out.println();
		System.out.println("                                  Datasource Config Generator");
		System.out.println();
		System.out.println("                                                              Created By: Shubham Singh");
		System.out.println();
		System.out.println(
				"******************************************************************************************************");

		System.out.println("Enter destination package [e.g org.domain.config]");
		String destination = sc.next();
		System.out.println();
		System.out.println("Enter the number of datasource");
		int noOfDatasource = sc.nextInt();
		System.out.println();
		// Input

		System.out.println("Enter the name of datasource [e.g Oracle] ");
		for (int i = 1; i <= noOfDatasource; i++) {
			System.out.print(i + ". ");
			String nameOfDatasource = sc.next();
			datasourceMap.put(nameOfDatasource,
					new Datasource(nameOfDatasource, nameOfDatasource + String.valueOf(i),
							nameOfDatasource + String.valueOf(i), nameOfDatasource + String.valueOf(i),
							nameOfDatasource + String.valueOf(i)));
		}
		System.out.println();
		datasourceMap.keySet().stream().forEach(datasourceName -> {
			int[] count = { 0 };
			Collection<Datasource> list = datasourceMap.get(datasourceName);
			System.out.println();
			if (list.size() == 1)
				System.out.println("Fill few details to configure " + datasourceName + " datasource\n");
			else
				System.out.println("Fill details to configure multiple " + datasourceName + " datasource\n");
			
			list.stream().forEach(dataSourceObj -> {
				++count[0];
				if (count[0] == 1)
					System.out.println("Enter base package of your 1st " + dataSourceObj.getName()
							+ " repository [ e.g org.domain.abc.repository ]");
				else
					System.out.println("Enter base package of your other " + dataSourceObj.getName()
							+ " repository [ e.g org.domain.abc.repository ]");

				dataSourceObj.setRepositoryPackage(sc.next());
				System.out.println();
				System.out.println("Enter base package of your entity [ e.g org.domain.abc.entity ]");
				dataSourceObj.setEntityPackage(sc.next());
				System.out.println();
				boolean flagPu = true;
				boolean flagPd = true;
				while (flagPu) {
					System.out.println("Enter persistentUnit ** Must be unique for each datasource ** [ e.g oracle ]");
					String pu = sc.next();
					System.out.println();
					try {
						if (persistanceUnitUniqueConstraintCheck(pu)) {
							dataSourceObj.setPersistentUnit(pu);
							puCheckList.add(pu);
							flagPu = false;
						}
					} catch (Exception e) {
						System.out.println(" Exception - " + e.getMessage());
						System.out.println("Persistent Units entered till now : " + puCheckList);
						System.out.println();
					}
				}

				while (flagPd) {

					System.out.println("Is this primary datasource? [Yes,No]");
					String pd = sc.next();
					System.out.println();
					try {
						if (validOptions(pd))
							if (primaryDatasourceUniqueConstraintForYesCheck(pd)) {
								dataSourceObj.setIsPrimaryDatasource(pd);
								pdCheckList.add(pd.toUpperCase());
								flagPd = false;
							}
					} catch (Exception e) {
						System.out.println(" Exception - " + e.getMessage());
						System.out.println();
					}
				}

				Thread thread = new Thread(() -> {
					try {
						generateDatasourceConfigTemplate(dataSourceObj, destination, count[0]);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}, "dataSourceObj" + count[0] + "-Thread");

				thread.start();

				System.out.println();
				System.out.println();

			});
			count[0] = 0;
			System.out.println(
					"-----------------------------------------------------------------------------------------");
		});
		System.out.println("Check files at " + System.getProperty("user.dir")+"\\generated-config");
		sc.close();

	}

	private static void generateDatasourceConfigTemplate(Datasource dataSourceObj, String destination, int count)
			throws IOException {

		String dataSourceName = dataSourceObj.getName();
		String temp = new String();
		File javaFile = null;
		File template = File.createTempFile("temp", "template");

		if (count > 1) {
			javaFile = new File("generated-config/" + String.valueOf(dataSourceName.charAt(0)).toUpperCase()
					+ dataSourceName.substring(1) + count + "Config.java");
		} else {
			javaFile = new File("generated-config/" + String.valueOf(dataSourceName.charAt(0)).toUpperCase()
					+ dataSourceName.substring(1) + "Config.java");
		}

		try {

			String fileName = "JpaTemplate/jpaTemplate.java";
			ClassLoader classLoader = Application.class.getClassLoader();
			InputStream inputStream = classLoader.getResourceAsStream(fileName);
			FileUtils.copyInputStreamToFile(inputStream, template);
			temp = FileUtils.readFileToString(template, Charset.defaultCharset());
//			URL resource = classLoader.getResource(fileName);  use to get file from Resources folder[for JAR use getResourceAsStream(fileName)]
//			template = new File(resource.toURI());

		} catch (Exception e) {
			 e.printStackTrace();
		}

		temp = temp.replaceAll("DestinationPackage", destination)
				.replaceAll("RepoBasePackage", dataSourceObj.getRepositoryPackage())
				.replaceAll("EntityPackage", dataSourceObj.getEntityPackage())
				.replaceAll("PersistentUnit", dataSourceObj.getPersistentUnit())
				.replaceAll("DataSourceBean", dataSourceName + count + "DataSourceBean")
				.replaceAll("EntityManagerBean", dataSourceName + count + "EntityManagerBean")
				.replaceAll("PlatformTransactionBean", dataSourceName + count + "PlatformTransactionBean");

		if (count > 1)
			temp = temp.replaceAll("NewConfig", String.valueOf(dataSourceName.charAt(0)).toUpperCase()
					+ dataSourceName.substring(1) + count + "Config");
		else
			temp = temp.replaceAll("NewConfig", String.valueOf(dataSourceName.charAt(0)).toUpperCase()
					+ dataSourceName.substring(1) + "Config");

		if (dataSourceObj.getIsPrimaryDatasource().equalsIgnoreCase("No")) {
			temp = temp.replaceAll("@Primary", "");
		}

		FileUtils.writeByteArrayToFile(javaFile, temp.getBytes());

	}

	public static boolean persistanceUnitUniqueConstraintCheck(String toCheck) throws Exception {

		if (!puCheckList.contains(toCheck))
			return true;
		else
			throw new Exception("Persistent Unit must be UNIQUE for each and every datasource");
	}

	public static boolean primaryDatasourceUniqueConstraintForYesCheck(String toCheck) throws Exception {

		if (toCheck.equalsIgnoreCase("NO"))
			return true;
		if (!pdCheckList.contains(toCheck.toUpperCase()) && toCheck.equalsIgnoreCase("YES"))
			return true;
		else
			throw new Exception("There can be only one primary datasource");
	}

	public static boolean validOptions(String toCheck) throws Exception {
		String[] validOptions = { "YES", "NO" };
		if (Stream.of(validOptions).filter(x -> x.equalsIgnoreCase(toCheck.toUpperCase())).count() == 1)
			return true;
		else
			throw new Exception("Not a valid option. Try again");

	}

}
