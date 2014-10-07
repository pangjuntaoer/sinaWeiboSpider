package com.swust.extractor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.swust.queue.CandidateURL;

public abstract class AbstractExtractor{
	protected Document doc;
	protected CandidateURL url;

	public AbstractExtractor(String baseUrl, CandidateURL url) {
		this.doc = Jsoup.parse(url.getContent(), baseUrl);
		this.url = url;
	}
	public AbstractExtractor(String baseUrl, String html) {
		this.doc = Jsoup.parse(html, baseUrl);
	}
	
	public AbstractExtractor(String html){
		this.doc = Jsoup.parse(html);
	}
	public AbstractExtractor(File file,String baseUrl) throws IOException{
		this.doc = Jsoup.parse(file, "UTF-8", baseUrl);
	}

	public abstract Set<String> extractContent();

}
