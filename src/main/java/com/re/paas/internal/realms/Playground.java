package com.re.paas.internal.realms;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.Reference;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;

public class Playground {

	public static void main(String[] args) {
		
		String fRef = "ref2";
		Section s = new Section().setTitle("Title").setSummary("summary")
				.withField(new SimpleField(null, InputType.AMOUNT, "REF 1").setReference(Reference.create("ref1")))
				.withField(new SimpleField(null, InputType.AMOUNT, "REF 2").setReference(Reference.create("ref2")))
				;
		
		List<Section> list = Arrays.asList(s);
		
		Iterator<Section> it = list.iterator();
		
		
		while(it.hasNext()) {
			
			Section section = it.next();
			
			Iterator<AbstractField> j = section.getFields().iterator();

			while (j.hasNext()) {

				AbstractField field = j.next();
				
				if(!field.getReference().asString().equals(fRef)) {
					continue;
				}
				
				// section.getFields().add(field.setReference(Reference.create("NEW_REF")));
				j.remove();
				
				// ConcurrentModificationException

				System.out.println(s.getFields());
			}
			
		}
		
		
	}
	
}
