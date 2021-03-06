package article.senti.jhKang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class MyDataBase{
	
	private Connection connection;
	private PreparedStatement articlePreparedStatement;
	private ResultSet articleResultSet;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private String addSentimentWordCountQuery = "update article_table set posi_word_count=?, nega_word_count=?, non_word_count=? where article_content=?";
	private String searchOriginWordQuery = "select count(*) from originword_table where text=?";
	private String getOriginWordQuery = "select * from originword_table where text=?";
	private String searchSentimentWordQuery = "select count(*) from sentimentword_table where origin_word=?";
	private String getSentimentWordQuery = "select * from sentimentword_table where origin_word=?";
	private String addOriginWordRecordQuery = "insert into originword_table(text, origin_word) values (?,?)";
	private String addSentiWordRecordQuery = "insert into sentimentword_table(origin_word, word_type, sentiment_type, sentiment_score) values (?,?,?,?)";

	private String addNotAvailableWordQuery = "insert into na_table(origin_word) values (?)";
	private String searchNotAvailableWordQuery = "select count(*) from na_table where origin_word=?";
	public MyDataBase() throws ClassNotFoundException, SQLException{
		connection = new MyConnection().getConnection();
		this.articlePreparedStatement = this.connection.prepareStatement("select * from article_table where article_date like '2015-08-2%' or article_date like '2015-08-3%';");
		this.articleResultSet = this.articlePreparedStatement.executeQuery();
		
	}
	public void myDataBaseClose() throws ClassNotFoundException, SQLException{
		articleResultSet.close();
		resultSet.close();
		articlePreparedStatement.close();
		preparedStatement.close();
		connection.close();	
	}
	synchronized
	public String getArticle() throws ClassNotFoundException, SQLException{
		if(articleResultSet.next()){
			int resultInt = articleResultSet.getInt("non_word_count");
			if(resultInt != 0)
				return "continue";
			else{
				String resultString = articleResultSet.getString("article_content");
				return resultString;
			}
		}
		else
			return "exit";
	}
	synchronized
	public void addSentimentWordCount(SentimentWordCounter count, String article) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement(addSentimentWordCountQuery);
		preparedStatement.setInt(1, count.positiveWordCount);
		preparedStatement.setInt(2, count.negativeWordCount);
		preparedStatement.setInt(3, count.nonSentiWordCount);
		preparedStatement.setString(4, article);
		preparedStatement.executeUpdate();
		preparedStatement.close();
		
	}
	synchronized
	public void addNotAvailableWord(String origin) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement(addNotAvailableWordQuery);
		preparedStatement.setString(1,  origin);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}
	synchronized
	public boolean searchNotAvailableWord(String origin) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement(searchNotAvailableWordQuery);
		preparedStatement.setString(1, origin);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		int resultInt = resultSet.getInt(1);
		resultSet.close();
		preparedStatement.close();
		if(resultInt != 0)
			return true;
		else
			return false;
		
		
	}
	synchronized
	public boolean searchOriginWord(String text) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement(searchOriginWordQuery);
		preparedStatement.setString(1, text);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		int resultInt = resultSet.getInt(1);
		resultSet.close();
		preparedStatement.close();
		if(resultInt != 0)
			return true;
		else 
			return false;
	}
	
	synchronized
	public OriginWord getOriginWord(String text) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement(getOriginWordQuery);
		preparedStatement.setString(1, text);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		String resultString = resultSet.getString("origin_word");
		OriginWord originWord = new OriginWord(text, resultString);
		resultString = null;
		resultSet.close();
		preparedStatement.close();
		return originWord;
	}
	synchronized
	public boolean searchSentimentWord(String originWord) throws ClassNotFoundException, SQLException{
		this.preparedStatement = this.connection.prepareStatement(searchSentimentWordQuery);
		preparedStatement.setString(1, originWord);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		int resultInt = resultSet.getInt(1);
		resultSet.close();
		preparedStatement.close();
		if(resultInt != 0)
			return true;
		else 
			return false;
	}
	synchronized
	public SentimentWord getSentimentWord(String originWord) throws ClassNotFoundException, SQLException{
		this.preparedStatement = this.connection.prepareStatement(getSentimentWordQuery);
		preparedStatement.setString(1, originWord);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		String resultOriginWord = resultSet.getString("origin_word");
		String resultWordType = resultSet.getString("word_type");
		int resultSentiType = resultSet.getInt("sentiment_type");
		int resultSentiScore = resultSet.getInt("sentiment_score");
		SentimentWord sentimentWord = new SentimentWord(resultOriginWord, resultWordType , resultSentiType, resultSentiScore);
		resultOriginWord = null;
		resultWordType = null;
		resultSet.close();
		preparedStatement.close();
		return sentimentWord;
		
	}
	synchronized
	public void addOriginWordRecord(OriginWord originWord) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement(addOriginWordRecordQuery);
		preparedStatement.setString(1, originWord.text);
		preparedStatement.setString(2, originWord.originWord);
		preparedStatement.executeUpdate();
		
		preparedStatement.close();
	}
	synchronized
	public void addSentiWordRecord(SentimentWord sentiWord) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement(addSentiWordRecordQuery);
		preparedStatement.setString(1, sentiWord.originWord);
		preparedStatement.setString(2, sentiWord.wordType);
		preparedStatement.setInt(3, sentiWord.sentimentType.ordinal());
		preparedStatement.setInt(4, sentiWord.sentimentScore);
		preparedStatement.executeUpdate();
		
		preparedStatement.close();
	}
}
