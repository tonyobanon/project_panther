package com.re.paas.internal.forms;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.forms.BaseSimpleField;
import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.FileTypeDescriptor;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

public class FormHelper {

	public static Path getFormFilesPath(Form form, String fieldId) {

		if (form.getId() == null) {
			Exceptions.throwRuntime("This form must have an identifier");
		}

		FileSystem fsProvider = null;

		for (Section section : form.getSections()) {

			BaseSimpleField f = (BaseSimpleField) section.getField(fieldId);

			if (f != null) {

				if (!(f instanceof SimpleField)) {
					Exceptions.throwRuntime("This field is a composite entry, expecting a simple entry");
				}

				SimpleField sf = (SimpleField) f;

				if (!sf.getInputType().equals(InputType.FILE)) {
					Exceptions.throwRuntime("This field must accept a file");
				}

				FileTypeDescriptor desc = (FileTypeDescriptor) sf.getInputType().getDescriptor();

				fsProvider = desc != null && desc.isStoreInternally() ? FileSystemProviders.getInternal()
						: FileSystems.getDefault();

				break;
			}
		}

		Path result = fsProvider.getPath("/forms/" + form.getId() + "/files/" + fieldId + ".fd");
		return result;
	}

}
