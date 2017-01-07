
import java.sql.Date;
import java.sql.SQLException;

import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

/* 
 *  This class is directly related to search the data from Twitter, and insert them into database.
 */
public class QueryExecution extends Main{
	
	/* Instance to create only one execution for searching operation. */ 
	private QueryExecution instance = null;
	private static boolean state1 = false, state2 = false;
	
	QueryExecution() {
		execute();
	}
	
	public QueryExecution getInstance(){
		if(instance == null) instance = new QueryExecution();
		return instance;
	}
	
	/* 
	 * Create query to search Twitter' data.
	 * Get the result by using getTweets() function as a QueryResult.
	 * Insert them into some tables in the database.
	 */
	private void execute(){
		QueryResult[] results = new QueryResult[3];
		int tweetid = 0;
		try{
			while (true) {
				results[0] = twitter.search(query[0]);
				query[0].setCount(100);
				/*results[1] = twitter.search(query[1]);
				query[1].setCount(100);
				results[2] = twitter.search(query[2]);
				query[2].setCount(100);*/
				
				for(int i=0; i<1/*results.length*/; i++){
					for (Status status : results[i].getTweets()) {
						tweetid = insertOriginalTweets(status);
						
						if(!state1 || !state2) continue;
						dbprocess.insertTweetAndTerm(status, dbConn, tweetid);
					}
					results[i] = twitter.search(query[i]);
				}
			}
		} catch(TwitterException e) {
			System.out.println("Try to Connect for Twitter : " + e.getMessage());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbConn.closeDb();
			} catch (ClassNotFoundException | SQLException e) {
				//System.out.println("ClassNotFoundException/SQLException : " + e.getMessage());
			}
		}
	}
	
	/*
	 * Insert twitter data into  OriginalTweets table.
	 */
	private int insertOriginalTweets(Status status) throws Exception{
		int userid = (int) status.getUser().getId();
		int tweetid = (int) status.getId();

		Date time = new java.sql.Date(status.getCreatedAt().getTime());
		String language = status.getLang();
		String msg = status.getText();
		if(msg.contains("http")){
	        String[] splitedPart = msg.split("http");
			msg = splitedPart[0];
		}
		if(!language.equals("en") || !dataFormat.isInTermList(msg)) return 0;
		
		state1 = dbprocess.insertTweet("OriginalTweets", userid, tweetid, msg, time);
		if(!state1) return 0;
		msg = dataFormat.formatData(msg);
		state2 = dbprocess.insertTweet("tweets", userid, tweetid, msg, time);
		
		this.list = dbprocess.getTerm();
		System.out.println("---> " + "userid : " + userid + " tweetid : " + tweetid + " msg : " + msg + " time : " + time);
		return tweetid;
	}
}
