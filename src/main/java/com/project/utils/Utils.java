package com.project.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Klasa Utils koja sadrzi metode koje se cesto koriste u sistemu
 * 
 * @author Srdjan Ristic
 */
public class Utils {

	// Loger klase
	private static final Logger LOGGER = Logger.getLogger(Utils.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	// Lista drzava
	private static ArrayList<String> countriesList = new ArrayList<String>();
	// Mapa tvitova sa drzavama
	private static ObjectNode tweetsByLocation = mapper.createObjectNode();
	// Lista drzava Amerike
	private static Map<String, String> statesOfUSA = new HashMap<String, String>();

	/**
	 * Metoda koja proverava da li je string korektan format JSON-a
	 * 
	 * @param body
	 * @return
	 */
	public static ObjectNode isJSONValid(String body) {
		try {
			ObjectNode JSONBody = (ObjectNode) mapper.readTree(body);
			return JSONBody;
		} catch (IOException e) {
			throw new InternalError("Invalid JSON format!");
		}
	}

	/**
	 * Metoda koja puni mapu tvitova sa drzavama. Iz prosledjene liste tekstova
	 * proverava se da li sadrzi neku od drzava iz liste drzava i proverava da
	 * li sadrzi neku od drzava Ameriku
	 * 
	 * @param userLocationTexts
	 * @return
	 * @throws Exception
	 */
	public static ObjectNode getCountriesBasedOnText(List<String> userLocationTexts) throws Exception {
		tweetsByLocation.removeAll();
		countriesList.clear();
		statesOfUSA.clear();
		getAllCountries();
		getAllStatesOfUSA();
		for (String text : userLocationTexts) {
			boolean countryUsed = checkIsCountryIsUsed(text);
			if (countryUsed == false) {
				countryUsed = checkIfUSAStateIsUsed(text);
				if (countryUsed == false) {
					continue;
				}
			}
		}
		return tweetsByLocation;
	}

	/**
	 * Metoda koja uzima listu svih drzava i popunjava listu drzava tom listom
	 * 
	 * @return
	 */
	private static ArrayList<String> getAllCountries() {
		String[] locales = Locale.getISOCountries();
		for (String countryCode : locales) {
			Locale obj = new Locale("", countryCode);
			countriesList.add(obj.getDisplayCountry());
			Collections.sort(countriesList);
		}
		return countriesList;
	}

	/**
	 * Metoda koja proverava da li tekst sadrzi neku od drzava iz liste drzava
	 * 
	 * @param text
	 * @return
	 */
	private static boolean checkIsCountryIsUsed(String text) {
		for (String country : countriesList) {
			if (text.toLowerCase().contains(country.toLowerCase())) {
				JsonNode node = tweetsByLocation.get(country);
				Integer count = 0;
				if (node == null) {
					count = 1;
				} else {
					count = node.asInt() + 1;
				}
				tweetsByLocation.put(country, count);
				return true;
			}
		}

		return false;
	}

	/**
	 * Metoda koja proverava da li tekst sadrzi neku od drzava Amerike iz liste
	 * drzava Amerike
	 * 
	 * @param text
	 * @return
	 */
	private static boolean checkIfUSAStateIsUsed(String text) {
		for (Map.Entry<String, String> entry : statesOfUSA.entrySet()) {
			if (text.contains(entry.getKey()) || text.contains(entry.getValue())) {
				JsonNode node = tweetsByLocation.get("United States");
				Integer count = 0;
				if (node == null) {
					count = 1;
				} else {
					count = node.asInt() + 1;
				}
				tweetsByLocation.put("United States", count);
				return true;
			}
		}
		return false;
	}

	/**
	 * Metoda koja konvertuje JsonArray u ArrayList
	 * 
	 * @param arrayOfNodes
	 * @return
	 */
	public static ArrayList<String> jsonArrayIntoArrayList(JsonNode arrayOfNodes) {
		ArrayList<String> result = new ArrayList<String>();
		for (JsonNode element : arrayOfNodes) {
			if (!element.has("hashtagName")) {
				String errorMsg = "JSON does not contain hashtagName field!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else {
				result.add(element.get("hashtagName").asText());
			}
		}
		return result;
	}

	/**
	 * Metoda koja popunjava listu drzava Amerike
	 */
	private static void getAllStatesOfUSA() {
		statesOfUSA.put("USA", "United States");
		statesOfUSA.put("AL", "Alabama");
		statesOfUSA.put("MT", "Montana");
		statesOfUSA.put("AK", "Alaska");
		statesOfUSA.put("NE", "Nebraska");
		statesOfUSA.put("AR", "Arkansas");
		statesOfUSA.put("NH", "NewHampshire");
		statesOfUSA.put("CA", "California");
		statesOfUSA.put("NJ", "NewJersey");
		statesOfUSA.put("CO", "Colorado");
		statesOfUSA.put("CT", "Connecticut");
		statesOfUSA.put("NY", "NewYork");
		statesOfUSA.put("DE", "Delaware");
		statesOfUSA.put("NC", "NorthCarolina");
		statesOfUSA.put("FL", "Florida");
		statesOfUSA.put("ND", "NorthDakota");
		statesOfUSA.put("GA", "Georgia");
		statesOfUSA.put("OH", "Ohio");
		statesOfUSA.put("HI", "Hawaii");
		statesOfUSA.put("OK", "Oklahoma");
		statesOfUSA.put("ID", "Idaho");
		statesOfUSA.put("OR", "Oregon");
		statesOfUSA.put("IL", "Illinois");
		statesOfUSA.put("PA", "Pennsylvania");
		statesOfUSA.put("IN", "Indiana");
		statesOfUSA.put("RI", "RhodeIsland");
		statesOfUSA.put("IA", "Iowa");
		statesOfUSA.put("SC", "SouthCarolina");
		statesOfUSA.put("KS", "Kansas");
		statesOfUSA.put("SD", "SouthDakota");
		statesOfUSA.put("KY", "Kentucky");
		statesOfUSA.put("TN", "Tennessee");
		statesOfUSA.put("LA", "Louisiana");
		statesOfUSA.put("TX", "Texas");
		statesOfUSA.put("ME", "Maine");
		statesOfUSA.put("KY", "Kentucky");
		statesOfUSA.put("UT", "Utah");
		statesOfUSA.put("MD", "Maryland");
		statesOfUSA.put("VT", "Vermont");
		statesOfUSA.put("MA", "Massachusetts");
		statesOfUSA.put("VA", "Virginia");
		statesOfUSA.put("MI", "Michigan");
		statesOfUSA.put("WA", "Washington");
		statesOfUSA.put("MN", "Minnesota");
		statesOfUSA.put("WV", "WestVirginia");
		statesOfUSA.put("MS", "Mississippi");
		statesOfUSA.put("WI", "Wisconsin");
		statesOfUSA.put("MO", "Missouri");
		statesOfUSA.put("WY", "Wyoming");
	}

	/**
	 * Metoda koja vraca format datuma godina-mesec-dan
	 * 
	 * @param date
	 * @return
	 */
	public static String formatedDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * Metoda koja konvertuje List<Double> listu u double[] niz
	 * 
	 * @param doubles
	 * @return
	 */
	public static double[] convertDoubles(ArrayList<Double> doubles) {
		double[] ret = new double[doubles.size()];
		Iterator<Double> iterator = doubles.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			ret[i] = iterator.next();
			i++;
		}
		return ret;
	}
}
