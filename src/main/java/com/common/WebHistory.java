package com.common;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.net.URL;

public class WebHistory{
	
	private static FSDirectory luceneIndexDirectory;
	private static IndexWriter writer;
	private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
	private static String indexPath;// = "E:\\lucene_index";
	public static String basePath;
	//indexPath = getClass().getResourceAsStream("/lucene_index");
	//private static String indexPath = "E:\\Invero\\apache-tomcat-6.0.43\\webapps\\PineappleEasyRealState\\WEB-INF\\classes\\lucene_index";
	public WebHistory(){
		URL resource = getClass().getResource("/");
		String path = resource.getPath();
		basePath = path;
		indexPath = path + "lucene_index";
		InitialiseLucene();
	}
	
	public static void InitialiseLucene()
	{
		try{
			luceneIndexDirectory = FSDirectory.open(new File(indexPath));
			System.out.println("Printing Path ------> " + indexPath);
			File file = new File(basePath + "../../output");
			if(!file.exists())
				file.mkdir();
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(luceneIndexDirectory, config);
		}
		catch (Exception e)
		{
			System.out.println("Error InitialiseLucene " + " : " + e.getMessage());
		}			
	}
	
	public static void addToIndex(String url, String html, String file_name)
	{
		try{
			Document doc = new Document();
			String content = removeTags(html);
			doc.add(new Field("url", url, Field.Store.YES, Field.Index.NO));
			doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED));
			doc.add(new Field("file_name", file_name, Field.Store.YES, Field.Index.NO));
			writer.addDocument(doc);
			writer.commit();
		}
		catch (Exception e)
		{
			System.out.println("Error BuildIndex " + " : " + e.getMessage());
		}			
	}

	public static ArrayList<String[]> getTopResults(String keyword)
	{
		return getTopResults(keyword, 5);
	}
	public static ArrayList<String[]> getTopResults(String keyword, int num_results)
	{
		ArrayList<String[]> list = new ArrayList<String[]>(); 
		try
		{
			//writer.commit();
			QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, "content", analyzer);
			System.out.println(keyword);
			IndexReader reader = DirectoryReader.open(luceneIndexDirectory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(num_results, true);
			BooleanQuery finalQuery = new BooleanQuery();

			Query query = qp.parse(keyword);
			finalQuery.add(query, Occur.MUST);

			System.out.println(finalQuery);
			
			searcher.search(finalQuery, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			System.out.println("Found " + hits.length + " hits.");
			for(int i=0;i<hits.length;++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				String[] result = new String[2];
				result[0] = d.get("url");
				result[1] = d.get("file_name");
		    	list.add(result);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public static String readFile(String fileName){
		BufferedReader br = null;
		String content = "";
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(fileName));
			while ((sCurrentLine = br.readLine()) != null) {
				content += sCurrentLine + "\n";
			}
 		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return content;
	}
	
	public static String removeTags(String html){
		String t = "";
		String s = html;
		s = s.replaceAll("<script>.*</script>", "");
		s = s.replaceAll("<style>.*</style>", "");
		while(s.indexOf('<') > -1){
			int i = 0;
			int j = s.indexOf('<');
			t += s.substring(i, j);
			i = s.indexOf('>', j) + 1;
			s = s.substring(i);
			t += " ";
		}
		t += s;
		return t.trim();
	}
	
	
	public static void main(String[] args)
	{
		WebHistory.InitialiseLucene();
		ArrayList<String[]> results = WebHistory.getTopResults("commodity", 5);
		Gson gson = new GsonBuilder().create();
        String response = gson.toJson(results);
        System.out.println(response);
		/*
		WebHistory handler = new WebHistory();
		
		try {
			handler.addToIndex("www.flipkart.com", removeTags(readFile("flipkart.html")));
			handler.addToIndex("www.ebay.in", removeTags(readFile("ebay.html")));
			handler.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Query: flipkart");
		ArrayList<String[]> results = handler.getTopResults("flipkart", 2);
		for (String[] arr : results)
		{
			System.out.println("URL: " + arr[0]);
			System.out.println("Content: " + arr[1].substring(0, 100) + "...");
			System.out.println();
		}

		System.out.println("Query: ebay");
		results = handler.getTopResults("ebay", 2);
		for (String[] arr : results)
		{
			System.out.println("URL: " + arr[0]);
			System.out.println("Content: " + arr[1].substring(0, 100) + "...");
			System.out.println();
		}
		*/
}
}