package com.re.paas.integrated.models;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.listable.AbstractListableIndexDelegate;
import com.re.paas.api.listable.ListableIndex;
import com.re.paas.api.listable.ListableIndexDeleteEvent;
import com.re.paas.api.listable.search.AbstractDocument;
import com.re.paas.api.listable.search.AbstractTerm;
import com.re.paas.api.models.BaseModel;
import com.re.paas.internal.listable.search.DocumentImpl;
import com.re.paas.internal.listable.search.TermImpl;

public class Search2Model extends BaseModel {

	private static Path searchIndexesPath;

	@Override
	public String path() {
		return "base/search";
	}

	@Override
	public void start() {

		// Get notified when a listable is about to be deleted
		// This also the search index associated with the listable

		BaseEvent.one(ListableIndexDeleteEvent.class, e -> {

			if (hasIndex(e.getListable().asString())) {
				deleteIndex(e.getListable().asString());
			}
		});

		// Scan listable indexes that are searchable to automatically create
		// index directories for then, if not already created

		AbstractListableIndexDelegate delegate = ListableIndex.getDelegate();

		delegate.getNamespaces().forEach(namespace -> {
			delegate.getListableIndexes(namespace).values().forEach(index -> {

				if (!hasIndex(index.asString())) {
					createIndex(index);
				}
			});
		});
	}

	private static Path getSearchIndexesPath() {

		if (searchIndexesPath == null) {

			searchIndexesPath = FileSystems.getDefault().getPath("/search", "indexes");

			if (!Files.exists(searchIndexesPath)) {
				try {
					Files.createFile(searchIndexesPath);
				} catch (IOException e) {
					Exceptions.throwRuntime(e);
				}
			}
		}

		return searchIndexesPath;
	}

	private static Path getSearchIndexPath(String id) {
		return getSearchIndexesPath().resolve(id);
	}

	private static Boolean hasIndex(String id) {
		return Files.exists(getSearchIndexPath(id));
	}

	private static void createIndex(ListableIndex<?> index) {
		Path indexPath = getSearchIndexPath(index.asString());
		if (Files.exists(indexPath)) {
			Exceptions.throwRuntime("ListableIndex: " + index.asString() + " already exists.");
		}
		try {
			Files.createFile(indexPath);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	private static void deleteIndex(String id) {
		Path indexPath = getSearchIndexPath(id);
		if (!Files.exists(indexPath)) {
			Exceptions.throwRuntime("ListableIndex: " + id + " does not exist.");
		}
		try {
			Files.delete(indexPath);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	private static IndexWriter getIndexWriter(String index) {
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriter iwriter = null;
		try {

			Directory directory = FSDirectory.open(getSearchIndexPath(index));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			iwriter = new IndexWriter(directory, config);

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		return iwriter;
	}

	private static IndexSearcher getIndexSearcher(String index) {

		IndexSearcher isearcher = null;

		try {

			Directory directory = FSDirectory.open(getSearchIndexPath(index));
			DirectoryReader ireader = DirectoryReader.open(directory);

			isearcher = new IndexSearcher(ireader);

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return isearcher;
	}

	public static void addDocument(String index, AbstractDocument document) {

		IndexWriter writer = getIndexWriter(index);
		Document doc = new DocumentImpl(document).asLuceneDocument();

		try {
			writer.addDocument(doc);
			writer.close();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public static List<AbstractDocument> getDocument(String index, List<AbstractTerm> terms,
			List<String> fieldsToLoad) {
		return getDocuments(index, terms, 1, fieldsToLoad);
	}

	public static List<AbstractDocument> getDocuments(String index, List<AbstractTerm> terms, Integer limit) {
		return getDocuments(index, terms, limit, null);
	}

	public static List<AbstractDocument> getDocuments(String index, List<AbstractTerm> terms, Integer limit,
			List<String> fieldsToLoad) {

		IndexSearcher searcher = getIndexSearcher(index);

		BooleanQuery.Builder builder = new BooleanQuery.Builder();

		terms.forEach(t -> {
			TermImpl term = new TermImpl(t);
			builder.add(new TermQuery(term.asLuceneTerm()), Occur.FILTER);
		});

		List<AbstractDocument> result = null;

		try {

			Query query = builder.build();
			ScoreDoc[] hits = searcher.search(query, limit).scoreDocs;
			result = new ArrayList<>(hits.length);

			for (ScoreDoc doc : hits) {
				Document d = fieldsToLoad != null ? searcher.doc(doc.doc, new HashSet<>(fieldsToLoad))
						: searcher.doc(doc.doc);

				result.add(new DocumentImpl(d));
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return result;
	}

	public static void deleteDocument(String index, List<AbstractTerm> terms) {
		
		IndexWriter writer = getIndexWriter(index);
		List<Term> termsList = terms.stream().map(e -> new TermImpl(e).asLuceneTerm())
				.collect(Collectors.toList());

		try {
			writer.deleteDocuments(termsList.toArray(new Term[terms.size()]));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

}
