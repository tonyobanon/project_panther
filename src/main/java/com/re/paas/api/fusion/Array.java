package com.re.paas.api.fusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Array<T> extends ArrayList<T> {

	private static final long serialVersionUID = 6242099305793182665L;

	public Array() {
        super();
    }

    public Array(List<T> initialElements) {
        super(initialElements);
    }

    public void splice(int startIndex, int deleteCount, List<T> elementsToAdd) {
        List<T> removedElements = subList(startIndex, startIndex + deleteCount);
        removedElements.clear();
        if (elementsToAdd != null) {
            addAll(startIndex, elementsToAdd);
        }
    }

    public List<T> slice(int startIndex, int endIndex) {
        return subList(startIndex, endIndex);
    }

    public void push(T element) {
        add(element);
    }

    public T shift() {
        if (isEmpty()) {
            return null;
        }
        return remove(0);
    }

    public void unshift(T element) {
        add(0, element);
    }

    public T pop() {
        if (isEmpty()) {
            return null;
        }
        return remove(size() - 1);
    }

    public void reverse() {
        Collections.reverse(this);
    }
    
   
    public void sort(Comparator<? super T> comparator) {
        Collections.sort(this, comparator);
    }

}
