
package com.project.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.dao.TweetsDAO;
import com.project.utils.Correlations;
import com.project.utils.Utils;

/**
 * Klasa TweetsDAOImpl koja implementira TweetsDAO interfejs i izvrsava
 * implementaciju definisanih metoda iz interfejsa. Svaka od metoda vrsi
 * transakciju sa bazom u cilju obrade i preuzimanja podataka
 * 
 * @author Srdjan Ristic
 *
 */
public class TweetsDAOImpl implements TweetsDAO {

	// Loger klase
	private Logger LOGGER = Logger.getLogger(TweetsDAOImpl.class);
	// Data source koji je definisan u xml fajlu kako bi se znalo koja baza se
	// korisi
	private DataSource dataSource;
	private ObjectMapper mapper = new ObjectMapper();
	private ArrayList<String> alreadyCorrelated = new ArrayList<String>();
	private ObjectNode twoCorrelatedTags = mapper.createObjectNode();
	private int calculatedCorrelationSample = 0;

	/**
	 * Seter dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca ukupan broj tvitova koji
	 * sadrze prosledjeni hestag
	 */
	@Override
	public String findNumberOfTweetsByHashtag(String hashtag) {
		String sql = "SELECT COUNT(user) FROM tweets WHERE text LIKE ?";
		StringBuilder logBuilder = new StringBuilder();
		StringBuilder hashtagForQueryBuilder = new StringBuilder();
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, hashtagForQueryBuilder.append("%").append(hashtag).append("%").toString());
			String number = null;
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" params: ").append("[")
					.append(hashtagForQueryBuilder.toString()).append("]"));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				number = rs.getString(1);
			}
			rs.close();
			ps.close();
			return number;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql)
					.append(" params: ").append("[").append(hashtag).append("]"));
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
					throw new InternalError(logBuilder.toString());
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca ukupan broj tvitova za svaki
	 * dan (izmedju prosledjenih datuma)
	 */
	@Override
	public ArrayNode findNumberOfTweetsByDate(String fromDate, String toDate) {
		String sql = "SELECT COUNT(create_date) AS 'number_of_tweets', create_date FROM tweets WHERE create_date BETWEEN ? AND ? GROUP BY DATE(create_date)";
		StringBuilder logBuilder = new StringBuilder();
		StringBuilder fromDateForQuery = new StringBuilder();
		StringBuilder toDateForQuery = new StringBuilder();
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, fromDateForQuery.append(fromDate).append(" 00:00:00.00").toString());
			ps.setString(2, toDateForQuery.append(toDate).append(" 23:59:59.999").toString());
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" params: ").append("[")
					.append(fromDate).append(", ").append(toDate).append("]"));
			ResultSet rs = ps.executeQuery();
			int numberOfTweets = 0;
			String createDate = null;
			ArrayNode arrayNode = mapper.createArrayNode();
			while (rs.next()) {
				ObjectNode node = mapper.createObjectNode();
				numberOfTweets = rs.getInt("number_of_tweets");
				createDate = rs.getString("create_date");
				node.put("number_of_tweets", numberOfTweets);
				node.put("create_date", createDate);
				arrayNode.add(node);
			}
			rs.close();
			ps.close();
			return arrayNode;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql)
					.append(" params: ").append("[").append(fromDate).append(", ").append(toDate).append("]"));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
					throw new InternalError(logBuilder.toString());
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca broj tvitova za
	 * najkoriscenije jezike prilikom tvitovanja
	 */
	@Override
	public ArrayNode getNumberOfLanguagesPerTweets(String hashtag) {
		String sql = "";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			if (hashtag == null) {
				sql = "SELECT lang AS 'language', count(lang) as 'total_number' FROM tweets GROUP by lang ORDER BY count(lang) DESC LIMIT 5";
			} else {
				sql = "SELECT lang AS 'language', count(lang) as 'total_number' FROM tweets WHERE text LIKE ? GROUP by lang ORDER BY count(lang) DESC LIMIT 1";
			}
			PreparedStatement ps = conn.prepareStatement(sql);
			if (hashtag != null) {
				ps.setString(1, "%" + hashtag + "%");
			}
			LOGGER.info(logBuilder.append("Executing query: ").append(sql));
			ResultSet rs = ps.executeQuery();
			ArrayNode arrayNode = mapper.createArrayNode();
			String language = null;
			String totalNumber = null;
			while (rs.next()) {
				ObjectNode node = mapper.createObjectNode();
				language = rs.getString("language");
				totalNumber = rs.getString("total_number");
				node.put("language", language);
				node.put("totalNumber", totalNumber);
				arrayNode.add(node);
			}
			rs.close();
			ps.close();
			return arrayNode;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
					throw new InternalError(logBuilder.toString());
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca tri najdominantnija hestaga
	 * za prosledjeni dan
	 */
	@Override
	public ArrayNode getThreeMostDominantTagsForSpecificDate(String date) {
		String sql = "SELECT tags.name, count(tweets_tags.id) AS total_number FROM tags JOIN tweets_tags ON tags.id = tweets_tags.tag_id JOIN tweets ON tweets.id = tweets_tags.tweet_id WHERE tweets.create_date LIKE ? GROUP BY tags.name ORDER BY total_number DESC LIMIT 3";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			StringBuilder buildDate = new StringBuilder();
			buildDate.append(date).append("%");
			ps.setString(1, buildDate.toString());
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" params: ").append("[").append(date)
					.append("]"));
			ResultSet rs = ps.executeQuery();
			ArrayNode arrayNode = mapper.createArrayNode();
			String hashtagName = null;
			int totalNumber = 0;
			while (rs.next()) {
				ObjectNode node = mapper.createObjectNode();
				hashtagName = rs.getString("name");
				totalNumber = rs.getInt("total_number");
				node.put("hashtagName", hashtagName);
				node.put("totalNumber", totalNumber);
				arrayNode.add(node);
			}
			rs.close();
			ps.close();
			return arrayNode;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql)
					.append(" params: ").append("[").append(date).append("]"));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
					throw new InternalError(logBuilder.toString());
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca 5 hestagova koji su se
	 * najvise puta koristili sa hestagom koji je prosledjen metodi
	 */
	@Override
	public ArrayNode getRelatedHashtags(String hashtag, String date) {
		String sql = "SELECT tags.id, count(*) as total FROM tweets_tags JOIN tags ON tweets_tags.tag_id = tags.id JOIN tweets ON tweets.id = tweets_tags.tweet_id WHERE tweets_tags.tweet_id in (SELECT tweet_id  FROM tweets_tags JOIN tags ON tweets_tags.tag_id = tags.id WHERE tags.name = ?)  and tweets_tags.tag_id not in (select id from tags where name = ?) and date(create_date) = ? group by tags.id order by 2 desc limit 5";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		if (hashtag.startsWith("#")) {
			hashtag = hashtag.substring(1);
		}
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, hashtag);
			ps.setString(2, hashtag);
			ps.setString(3, date);
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" params: ").append("[")
					.append(hashtag).append(date).append("]"));
			ResultSet rs = ps.executeQuery();
			ArrayNode arrayNode = mapper.createArrayNode();
			ArrayList<Integer> listOfIDs = new ArrayList<Integer>();
			ArrayList<Integer> listOfValues = new ArrayList<Integer>();
			while (rs.next()) {
				listOfIDs.add(rs.getInt("id"));
				listOfValues.add(rs.getInt("total"));
			}
			rs.close();
			ps.close();
			ArrayList<String> hashtagNames = getHashtagNamesByID(listOfIDs);
			for (int i = 0; i < hashtagNames.size(); i++) {
				ObjectNode node = mapper.createObjectNode();
				node.put("hashtagName", hashtagNames.get(i));
				node.put("totalNumber", listOfValues.get(i));
				arrayNode.add(node);
			}
			return arrayNode;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql)
					.append(" params: ").append("[").append(hashtag).append("]"));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
					throw new InternalError(logBuilder.toString());
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca ime hestaga u zavisnosti od
	 * njegovog ID-ja
	 * 
	 * @param listOFIDs
	 * @return
	 */
	private ArrayList<String> getHashtagNamesByID(ArrayList<Integer> listOFIDs) {
		String sql = "SELECT name FROM tags WHERE id = ?";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		ArrayList<String> hashtagNames = new ArrayList<String>();
		try {
			conn = dataSource.getConnection();
			for (Integer element : listOFIDs) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, element);
				LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
						.append(element).append("]"));
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					hashtagNames.add(rs.getString("name"));
				}
				rs.close();
				ps.close();
			}
			return hashtagNames;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca listu tekstova sa lokacijama
	 * koje su korisnici koristili za tvitovanje
	 */
	@Override
	public List<String> getUserLocations() {
		String sql = "SELECT user_location as location FROM tweets WHERE user_location IS NOT NULL";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			LOGGER.info(logBuilder.append("Executing query: ").append(sql));
			ResultSet rs = ps.executeQuery();
			List<String> array = new ArrayList<String>();
			while (rs.next()) {
				array.add(rs.getString("location"));
			}
			rs.close();
			ps.close();
			return array;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja vrsi uporedjivanje vise prosledjenih hestagova
	 */
	@Override
	public ArrayNode compareHashtags(ArrayList<String> arrayOfHashtags, String correlation, int correlationSample) {
		alreadyCorrelated.clear();
		ArrayNode correlationWithOtherTags = mapper.createArrayNode();
		for (String hashtag : arrayOfHashtags) {
			correlationWithOtherTags(hashtag, arrayOfHashtags, correlation, correlationSample);
		}
		correlationWithOtherTags.add(twoCorrelatedTags);
		return correlationWithOtherTags;
	}

	/**
	 * Metoda koja vrsi iteraciju kroz svaki od prosledjenih elemenata
	 * (prosledjenih hestagova) kako bi se odredila korelacija
	 * 
	 * @param hashtag
	 * @param arrayOfHashtags
	 * @param correlation
	 * @param correlationSample
	 */
	private void correlationWithOtherTags(String hashtag, ArrayList<String> arrayOfHashtags, String correlation,
			int correlationSample) {
		for (String otherHashtag : arrayOfHashtags) {
			if (!otherHashtag.equals(hashtag)) {
				if (!alreadyCorrelated.contains(otherHashtag.toUpperCase() + " - " + hashtag.toUpperCase())) {
					String combined = hashtag.toUpperCase() + " - " + otherHashtag.toUpperCase();
					calculatedCorrelationSample = 0;
					twoCorrelatedTags.put(combined,
							findCorrelation(hashtag, otherHashtag, correlation, correlationSample));
					alreadyCorrelated.add(combined);
				}
			}
		}
	}

	/**
	 * Metoda koja vrsi kalkulaciju korelacije (u zavisnosti koja je
	 * prosledjena), za svaki od prosledjenih hestagova medjusobno
	 * 
	 * @param hashtag
	 * @param otherHashtag
	 * @param correlation
	 * @param correlationSample
	 * @return
	 */
	private double findCorrelation(String hashtag, String otherHashtag, String correlation, int correlationSample) {
		Date startDate = getStartDate();
		String startDateString = Utils.formatedDate(startDate);
		Date startDatePlusOneDay = addOneDay(startDate);
		String startDatePlusOneDayString = Utils.formatedDate(startDatePlusOneDay);
		ArrayList<Double> array1 = new ArrayList<Double>();
		ArrayList<Double> array2 = new ArrayList<Double>();

		while (calculatedCorrelationSample < correlationSample) {
			array1.add(addToArray(startDateString, startDatePlusOneDayString, hashtag));
			array2.add(addToArray(startDateString, startDatePlusOneDayString, otherHashtag));
			startDate = startDatePlusOneDay;
			startDateString = Utils.formatedDate(startDate);
			startDatePlusOneDay = addOneDay(startDatePlusOneDay);
			startDatePlusOneDayString = Utils.formatedDate(startDatePlusOneDay);
			calculatedCorrelationSample++;
		}
		double[] arrayOneConverted = Utils.convertDoubles(array1);
		double[] arrayTwoConverted = Utils.convertDoubles(array2);
		double correlationValue = 0.0;
		if (correlation.equals("Pearson")) {
			correlationValue = Correlations.pearsonCorrelation(arrayOneConverted, arrayTwoConverted);
		} else {
			correlationValue = Correlations.spearmanCorrelation(arrayOneConverted, arrayTwoConverted);
		}
		if (Double.isNaN(correlationValue)) {
			correlationValue = 0;
		}

		correlationValue = Math.round(correlationValue * 100.0) / 100.0;

		return correlationValue;
	}

	/**
	 * Metoda koja vrsi upit nad bazom i vraca datum kada je prvi tvit
	 * prikupljen
	 * 
	 * @return
	 */
	private Date getStartDate() {
		String sql = "SELECT DATE(create_date) as date FROM tweets Where id = 1";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			LOGGER.info(logBuilder.append("Executing query: ").append(sql));
			ResultSet rs = ps.executeQuery();
			String result = "";
			while (rs.next()) {
				result = rs.getString("date");
			}
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = format.parse(result);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			rs.close();
			ps.close();
			return date;
		} catch (SQLException e) {
			LOGGER.error(logBuilder.append("An error occured while trying to execute query: ").append(sql));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja vrsi upit nad bazom i vraca broj tvitova sa prosledjenim
	 * hestagom izmedju dva datuma (takodje prosledjena metodi), u cilju
	 * dobijanja decimalnog broja koji se koristi za odredjivanje korelacije
	 * 
	 * @param startDate
	 * @param startDatePlusOneDay
	 * @param hashtag
	 * @return
	 */
	private double addToArray(String startDate, String startDatePlusOneDay, String hashtag) {
		String sql = "select count(*) as number from tweets where create_date BETWEEN ? AND ? AND text like ?";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, startDate.toString());
			ps.setString(2, startDatePlusOneDay.toString());
			ps.setString(3, "%" + hashtag + "%");
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append(startDate)
					.append(" ").append(startDatePlusOneDay).append(" ").append(hashtag));
			ResultSet rs = ps.executeQuery();
			double number = 0;
			while (rs.next()) {
				number = Double.parseDouble(rs.getString("number"));
			}
			rs.close();
			ps.close();
			return number;
		} catch (SQLException e) {
			LOGGER.info(logBuilder.append("An error occured while trying to execute query: ").append(sql)
					.append(" parameters: ").append(startDate).append(startDatePlusOneDay).append(hashtag));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja dodaje jedan dan na prosledjeni datum
	 * 
	 * @param date
	 * @return
	 */
	private Date addOneDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca rezultat da li postoji hestag
	 * u bazi ili ne
	 */
	@Override
	public boolean checkIfHashtagExists(String hashtag) {
		if (hashtag.startsWith("#")) {
			hashtag = hashtag.substring(1);
		}
		String sql = "select id from tags where name = ?";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, hashtag);
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			ResultSet rs = ps.executeQuery();
			int number = 0;
			while (rs.next()) {
				number = rs.getInt("id");
			}
			rs.close();
			ps.close();
			if (number != 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca vreme u koje je prosledjeni
	 * hestag najvise puta koriscen za tvitovanje
	 */
	@Override
	public String getMostlyTweetedTime(String hashtag) {
		String sql = "select HOUR(create_date) as hourTime, count(*) from tweets where text like ? group by HOUR(create_date) order by count(*) desc LIMIT 1 ";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "%" + hashtag + "%");
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			ResultSet rs = ps.executeQuery();
			String number = "";
			while (rs.next()) {
				number = rs.getString("hourTime") + "h";
			}
			rs.close();
			ps.close();
			return number;
		} catch (SQLException e) {
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca ukupan broj retvitova koji
	 * sadrze prosledjeni hestag
	 */
	@Override
	public int numberOfReweetedTweets(String hashtag) {
		String sql = "select count(*) as number from tweets where text like ? and is_retweet = 1";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "%" + hashtag + "%");
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			ResultSet rs = ps.executeQuery();
			int number = 0;
			while (rs.next()) {
				number = rs.getInt("number");
			}
			rs.close();
			ps.close();
			return number;
		} catch (SQLException e) {
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca ukupan broj tvitova kojia
	 * sadrze linkove u sebi i prosledjeni hestag
	 */
	@Override
	public int numberOfTweetsWithLinks(String hashtag) {
		String sql = "select count(*) as number from tweets where text like ? and tweet_url IS NOT NULL";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "%" + hashtag + "%");
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			ResultSet rs = ps.executeQuery();
			int number = 0;
			while (rs.next()) {
				number = rs.getInt("number");
			}
			rs.close();
			ps.close();
			return number;
		} catch (SQLException e) {
			LOGGER.info(logBuilder.append("Executing query: ").append(sql).append(" parameters: ").append("[")
					.append(hashtag).append("]"));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}

	/**
	 * Metoda koja izvrsava upit nad bazom i vraca ukupan broj prikupljenih
	 * tvitova
	 */
	@Override
	public ObjectNode getTotalNumberOfData() {
		String sql = "select count(*) as number from tweets";
		StringBuilder logBuilder = new StringBuilder();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			LOGGER.info(logBuilder.append("Executing query: ").append(sql));
			ResultSet rs = ps.executeQuery();
			ObjectNode node = mapper.createObjectNode();
			while (rs.next()) {
				int amount = rs.getInt("number");
				DecimalFormat formatter = new DecimalFormat("#,###");
				node.put("totalNumberOfData", formatter.format(amount));
			}
			rs.close();
			ps.close();
			return node;
		} catch (SQLException e) {
			LOGGER.info(logBuilder.append("Executing query: ").append(sql));
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error(logBuilder.append("An error occured while trying to close the connection."));
				}
			}
		}
	}
}