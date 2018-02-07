package com.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.dao.TweetsDAO;
import com.project.utils.Utils;

/**
 * Klasa koja predstavlja rest kontroler, u kojoj se nalaze API-ji za obradu
 * podataka 
 * 
 * @author Srdjan Ristic
 *
 */
@RestController
public class MainController {

	// Loger klase
	private Logger LOGGER = Logger.getLogger(MainController.class);
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Kontroler koji pri pokretanju sistema otvara index.html stranicu
	 * 
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView list(ModelAndView mav) {
		mav.setViewName("redirect:app/index.html");
		return mav;
	}

	/**
	 * Kontroler koji vraca broj tvitova koji sadrze prosledjeni hestag
	 * 
	 * @param body
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/app/getNumberOfTweetsByHashtag", method = RequestMethod.POST)
	public ObjectNode getNumberOfTweetsByHashtag(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException {
		ObjectNode node = mapper.createObjectNode();
		if (body == null) {
			String errorMsg = "Received body is null!";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			ObjectNode JSONBody = (ObjectNode) mapper.readTree(body);
			if (!JSONBody.has("hashtag")) {
				String errorMsg = "Body does not contain hashtag value in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else {
				ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
				TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
				if (tweetsDAO == null) {
					String errorMsg = "Unable to find tweetsDAO bean";
					LOGGER.error(errorMsg);
					throw new InternalError(errorMsg);
				} else {
					String result = tweetsDAO.findNumberOfTweetsByHashtag(JSONBody.get("hashtag").asText());
					if (result == null) {
						String errorMsg = "Unable to get number of tweets by hashtag!";
						LOGGER.error(errorMsg);
						throw new InternalError(errorMsg);
					} else {
						node.put("result", result);
						response.setStatus(HttpServletResponse.SC_OK);
					}
				}
			}
		}
		return node;
	}

	/**
	 * Kontroler koji vraca broj tvitova prikupljenih za prosledjene datume
	 * 
	 * @param body
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/app/getNumberOfTweetsByDate", method = RequestMethod.POST)
	public ObjectNode getNumberOfTweetsByDate(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException {
		ObjectNode node = mapper.createObjectNode();
		if (body == null) {
			String errorMsg = "Received body is null!";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			ObjectNode JSONBody = (ObjectNode) mapper.readTree(body);
			if (!JSONBody.has("startDate") || !JSONBody.has("endDate")) {
				String errorMsg = "Body does not contain startDate and endDate values in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else {
				ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
				TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
				if (tweetsDAO == null) {
					String errorMsg = "Unable to find tweetsDAO bean";
					LOGGER.error(errorMsg);
					throw new InternalError(errorMsg);
				} else {
					ArrayNode result = tweetsDAO.findNumberOfTweetsByDate(JSONBody.get("startDate").asText(),
							JSONBody.get("endDate").asText());
					node.set("result", result);
					response.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}
		return node;
	}

	/**
	 * Kontroler koji vraca broj prikupljenih tvitova na najdominantnijim
	 * jezicima
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/app/getNumberOfLanguagesPerTweets", method = RequestMethod.GET)
	public ObjectNode getNumberOfLanguagesPerTweets(HttpServletRequest request, HttpServletResponse response) {
		ObjectNode node = mapper.createObjectNode();
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
		if (tweetsDAO == null) {
			String errorMsg = "Unable to find tweetsDAO bean";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			ArrayNode result = tweetsDAO.getNumberOfLanguagesPerTweets(null);
			node.set("result", result);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		return node;
	}

	/**
	 * Kontroler koji vraca 3 najdominantnija hestaga za prosledjeni dan
	 * 
	 * @param body
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/app/getThreeMostDominantTagsForSpecificDate", method = RequestMethod.POST)
	public ObjectNode getThreeMostDominantTagsForSpecificDate(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException {
		ObjectNode node = mapper.createObjectNode();
		if (body == null) {
			String errorMsg = "Received body is null!";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			ObjectNode JSONBody = (ObjectNode) mapper.readTree(body);
			if (!JSONBody.has("date")) {
				String errorMsg = "Body does not contain date value in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else {
				ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
				TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
				if (tweetsDAO == null) {
					String errorMsg = "Unable to find tweetsDAO bean";
					LOGGER.error(errorMsg);
					throw new InternalError(errorMsg);
				} else {
					ArrayNode result = tweetsDAO.getThreeMostDominantTagsForSpecificDate(JSONBody.get("date").asText());
					node.set("result", result);
					response.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}
		return node;
	}

	/**
	 * Kontroler koji vraca hestagove koji su relevantni sa prosledjenim
	 * 
	 * @param body
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/app/getRelatedHashtags", method = RequestMethod.POST)
	public ObjectNode getRelatedHashtags(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException {
		ObjectNode node = mapper.createObjectNode();
		if (body == null) {
			String errorMsg = "Received body is null!";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			ObjectNode JSONBody = (ObjectNode) mapper.readTree(body);
			if (!JSONBody.has("hashtag")) {
				String errorMsg = "Body does not contain hashtag value in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else if (!JSONBody.has("date")) {
				String errorMsg = "Body does not contain hashtag value in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else {
				ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
				TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
				if (tweetsDAO == null) {
					String errorMsg = "Unable to find tweetsDAO bean";
					LOGGER.error(errorMsg);
					throw new InternalError(errorMsg);
				} else {
					ArrayNode result = tweetsDAO.getRelatedHashtags(JSONBody.get("hashtag").asText(),
							JSONBody.get("date").asText());
					node.set("result", result);
					response.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}
		return node;
	}

	/**
	 * Kontroler koji vraca broj prikupljenih tvitova za svaku drzavu sirom
	 * sveta
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/app/getUserLocations", method = RequestMethod.GET)
	public ObjectNode getUserLocations(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ObjectNode node = mapper.createObjectNode();
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
		if (tweetsDAO == null) {
			String errorMsg = "Unable to find tweetsDAO bean";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			List<String> result = tweetsDAO.getUserLocations();
			ObjectNode tweetsFromCountries = Utils.getCountriesBasedOnText(result);
			node.set("result", tweetsFromCountries);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		return node;
	}

	/**
	 * Kontroler koji uporedjuje vise prosledjenih hestagova i vraca niz
	 * rezultata: korelacija, ukupan broj tvitova, jezik, vreme koriscenja, da
	 * li su retvitovi, koliko tvitova sadrzi linkove
	 * 
	 * @param body
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/app/compareHashtags", method = RequestMethod.POST)
	public ObjectNode compareHashtags(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException {
		ObjectNode node = mapper.createObjectNode();
		if (body == null) {
			String errorMsg = "Received body is null!";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			ObjectNode JSONBody = (ObjectNode) mapper.readTree(body);
			if (!JSONBody.has("hashtagsArray")) {
				String errorMsg = "Body does not contain hashtags array value in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else if (!JSONBody.has("correlation")) {
				String errorMsg = "Body does not contain correlation value in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else if (!JSONBody.has("correlationSample")) {
				String errorMsg = "Body does not contain correlation value in JSON!";
				LOGGER.error(errorMsg);
				throw new InternalError(errorMsg);
			} else {
				ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
				TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
				if (tweetsDAO == null) {
					String errorMsg = "Unable to find tweetsDAO bean";
					LOGGER.error(errorMsg);
					throw new InternalError(errorMsg);
				} else {
					ArrayNode finalNode = mapper.createArrayNode();
					ArrayList<String> receivedArray = Utils.jsonArrayIntoArrayList(JSONBody.get("hashtagsArray"));
					ArrayList<String> arrayOfHashtags = new ArrayList<String>();
					// CHECK IF HASHTAG EXISTS
					ArrayNode doesNotExistArray = mapper.createArrayNode();
					for (String element : receivedArray) {
						ObjectNode doesNotExistPerHashtag = mapper.createObjectNode();
						boolean exists = tweetsDAO.checkIfHashtagExists(element);
						if (exists) {
							arrayOfHashtags.add(element);
						} else {
							doesNotExistPerHashtag.put("hashtagName", element);
							doesNotExistArray.add(doesNotExistPerHashtag);
						}
					}
					ObjectNode doesNotExist = mapper.createObjectNode();
					doesNotExist.set("doesNotExist", doesNotExistArray);

					// CORRELATION
					ObjectNode correlation = mapper.createObjectNode();
					if (arrayOfHashtags.size() > 1) {
						ArrayNode correlationArray = tweetsDAO.compareHashtags(arrayOfHashtags,
								JSONBody.get("correlation").asText(), JSONBody.get("correlationSample").asInt());
						correlation.set("correlation", correlationArray);
					}

					// TOTAL NUMBER
					ArrayNode totalNumberArray = mapper.createArrayNode();
					ObjectNode totalNumber = mapper.createObjectNode();
					for (String element : arrayOfHashtags) {
						ObjectNode totalNumberPerTag = mapper.createObjectNode();
						totalNumberPerTag.put(element, tweetsDAO.findNumberOfTweetsByHashtag(element));
						totalNumberArray.add(totalNumberPerTag);
					}
					totalNumber.set("totalNumberPerHashtag", totalNumberArray);

					// LANGUAGE
					ArrayNode languageArray = mapper.createArrayNode();
					ObjectNode language = mapper.createObjectNode();
					for (String element : arrayOfHashtags) {
						ObjectNode languagePerHashtag = mapper.createObjectNode();
						languagePerHashtag.set(element, tweetsDAO.getNumberOfLanguagesPerTweets(element));
						languageArray.add(languagePerHashtag);
					}
					language.set("languages", languageArray);

					// MOSTLY TWEETED DURING TIME
					ArrayNode mostlyTweetedArray = mapper.createArrayNode();
					ObjectNode mostlyTweeted = mapper.createObjectNode();
					for (String element : arrayOfHashtags) {
						ObjectNode mostlyTweetedPerHashtag = mapper.createObjectNode();
						mostlyTweetedPerHashtag.put(element, tweetsDAO.getMostlyTweetedTime(element));
						mostlyTweetedArray.add(mostlyTweetedPerHashtag);
					}
					mostlyTweeted.set("mostlyTweeted", mostlyTweetedArray);

					// IS RETWEET
					ArrayNode isRetweetArray = mapper.createArrayNode();
					ObjectNode isRetweet = mapper.createObjectNode();
					for (String element : arrayOfHashtags) {
						ObjectNode isRetweetPerHashtag = mapper.createObjectNode();
						isRetweetPerHashtag.put(element, tweetsDAO.numberOfReweetedTweets(element));
						isRetweetArray.add(isRetweetPerHashtag);
					}
					isRetweet.set("retweets", isRetweetArray);

					// TWEETS WITH LINKS
					ArrayNode tweetsWithLinksArray = mapper.createArrayNode();
					ObjectNode tweetsWithLinks = mapper.createObjectNode();
					for (String element : arrayOfHashtags) {
						ObjectNode tweetsWithLinksPerHashtag = mapper.createObjectNode();
						tweetsWithLinksPerHashtag.put(element, tweetsDAO.numberOfReweetedTweets(element));
						tweetsWithLinksArray.add(tweetsWithLinksPerHashtag);
					}
					tweetsWithLinks.set("tweetsWithLinks", tweetsWithLinksArray);

					finalNode.add(doesNotExist);
					finalNode.add(correlation);
					finalNode.add(totalNumber);
					finalNode.add(language);
					finalNode.add(mostlyTweeted);
					finalNode.add(isRetweet);
					finalNode.add(tweetsWithLinks);
					node.set("result", finalNode);
					response.setStatus(HttpServletResponse.SC_OK);
				}
			}
		}
		return node;
	}

	/**
	 * Kontroler koji vraca ukupan broj prikupljenih tvitova
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/app/getTotalNumberOfData", method = RequestMethod.GET)
	public ObjectNode getTotalNumberOfData(HttpServletRequest request, HttpServletResponse response) {
		ObjectNode node = mapper.createObjectNode();
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		TweetsDAO tweetsDAO = (TweetsDAO) context.getBean("tweetsDAO");
		if (tweetsDAO == null) {
			String errorMsg = "Unable to find tweetsDAO bean";
			LOGGER.error(errorMsg);
			throw new InternalError(errorMsg);
		} else {
			ObjectNode result = tweetsDAO.getTotalNumberOfData();
			node.set("result", result);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		return node;
	}
}