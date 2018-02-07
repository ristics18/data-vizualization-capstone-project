package com.project.dao;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Interface TweetsDAO u kome su definisani tipovi metoda i parametri koje
 * primaju
 * 
 * @author Srdjan Ristic
 *
 */
public interface TweetsDAO {

	// Broj tvitova koji sadrze hestag
	public String findNumberOfTweetsByHashtag(String hastag);

	// Broj tvitova za datume
	public ArrayNode findNumberOfTweetsByDate(String fromDate, String toDate);

	// Broj tvitova za najdominantnije jezike
	public ArrayNode getNumberOfLanguagesPerTweets(String hashtag);

	// Tri najdominantnija hestaga za prosledjeni dan
	public ArrayNode getThreeMostDominantTagsForSpecificDate(String date);

	// Hestagovi relevantni sa prosledjenim
	public ArrayNode getRelatedHashtags(String hashtag, String date);

	// Lista lokacija koriscenih za tvitovanje
	public List<String> getUserLocations();

	// Uporedjivanje vise hestagova
	public ArrayNode compareHashtags(ArrayList<String> arrayOfHashtags, String correlation, int correlationSample);

	// Provera da li hestag postoji u bazi
	public boolean checkIfHashtagExists(String hashtag);

	// Vreme kada je hestag najvise koriscen za tvitovanje
	public String getMostlyTweetedTime(String hashtag);

	// Broj tvitova koji su retvitovi
	public int numberOfReweetedTweets(String hashtag);

	// Broj tvitova koji sadrze linkove u sebi
	public int numberOfTweetsWithLinks(String hashtag);

	// Ukupan broj prikupljenih tvitova
	public ObjectNode getTotalNumberOfData();
}
