package com.re.paas.api.listable.search;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;


public interface AbstractDocument {

	  Iterator<AbstractIndexableField> iterator();

	  /**
	   * <p>Adds a field to a document.  Several fields may be added with
	   * the same name.  In this case, if the fields are indexed, their text is
	   * treated as though appended for the purposes of search.</p>
	   * <p> Note that add like the removeField(s) methods only makes sense 
	   * prior to adding a document to an index. These methods cannot
	   * be used to change the content of an existing index! In order to achieve this,
	   * a document has to be deleted from an index and a new changed version of that
	   * document has to be added.</p>
	   */
	  void add(AbstractIndexableField field);
	  
	  /**
	   * <p>Removes field with the specified name from the document.
	   * If multiple fields exist with this name, this method removes the first field that has been added.
	   * If there is no field with the specified name, the document remains unchanged.</p>
	   * <p> Note that the removeField(s) methods like the add method only make sense 
	   * prior to adding a document to an index. These methods cannot
	   * be used to change the content of an existing index! In order to achieve this,
	   * a document has to be deleted from an index and a new changed version of that
	   * document has to be added.</p>
	   */
	  void removeField(String name);
	  
	  /**
	   * <p>Removes all fields with the given name from the document.
	   * If there is no field with the specified name, the document remains unchanged.</p>
	   * <p> Note that the removeField(s) methods like the add method only make sense 
	   * prior to adding a document to an index. These methods cannot
	   * be used to change the content of an existing index! In order to achieve this,
	   * a document has to be deleted from an index and a new changed version of that
	   * document has to be added.</p>
	   */
	  void removeFields(String name);

	  /**
	  * Returns an array of byte arrays for of the fields that have the name specified
	  * as the method parameter.  This method returns an empty
	  * array when there are no matching fields.  It never
	  * returns null.
	  *
	  * @param name the name of the field
	  * @return a <code>BytesRef[]</code> of binary field values
	  */
	  ByteBuffer[] getBinaryValues(String name);
	  
	  /**
	  * Returns an array of bytes for the first (or only) field that has the name
	  * specified as the method parameter. This method will return <code>null</code>
	  * if no binary fields with the specified name are available.
	  * There may be non-binary fields with the same name.
	  *
	  * @param name the name of the field.
	  * @return a <code>BytesRef</code> containing the binary field value or <code>null</code>
	  */
	  ByteBuffer getBinaryValue(String name);

	  /** Returns a field with the given name if any exist in this document, or
	   * null.  If multiple fields exists with this name, this method returns the
	   * first value added.
	   */
	  AbstractIndexableField getField(String name);

	  /**
	   * Returns an array of {@link AbstractIndexableField}s with the given name.
	   * This method returns an empty array when there are no
	   * matching fields.  It never returns null.
	   *
	   * @param name the name of the field
	   * @return a <code>Field[]</code> array
	   */
	  AbstractIndexableField[] getFields(String name);
	  
	  /** Returns a List of all the fields in a document.
	   * 
	   * @return an immutable <code>List&lt;Field&gt;</code> 
	   */
	  List<AbstractIndexableField> getFields();
	  

	  /**
	   * Returns an array of values of the field specified as the method parameter.
	   * This method returns an empty array when there are no
	   * matching fields.  It never returns null.
	   * For a numeric field, it returns the string value of the number. If you want
	   * the actual numeric field instances back, use {@link #getFields}.
	   * @param name the name of the field
	   * @return a <code>String[]</code> of field values
	   */
	  String[] getValues(String name);

	  /** Returns the string value of the field with the given name if any exist in
	   * this document, or null.  If multiple fields exist with this name, this
	   * method returns the first value added. If only binary fields with this name
	   * exist, returns null.
	   * For a numeric field it returns the string value of the number. If you want
	   * the actual numeric field instance back, use {@link #getField}.
	   */
	  String get(String name);
	 
	  /** Removes all the fields from document. */
	  void clear();
	
}
