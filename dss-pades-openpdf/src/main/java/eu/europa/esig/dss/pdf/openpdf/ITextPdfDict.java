/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.pdf.openpdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

import eu.europa.esig.dss.pdf.PdfArray;
import eu.europa.esig.dss.pdf.PdfDict;

public class ITextPdfDict implements eu.europa.esig.dss.pdf.PdfDict {

	private static final Logger LOG = LoggerFactory.getLogger(ITextPdfDict.class);

	PdfDictionary wrapped;

	public ITextPdfDict(PdfDictionary wrapped) {
		if (wrapped == null) {
			throw new IllegalArgumentException();
		}
		this.wrapped = wrapped;
	}

	ITextPdfDict(String dictionaryType) {
		if (dictionaryType != null) {
			wrapped = new PdfDictionary(new PdfName(dictionaryType));
		} else {
			wrapped = new PdfDictionary();
		}
	}

	@Override
	public PdfDict getAsDict(String name) {
		PdfDictionary asDict = wrapped.getAsDict(new PdfName(name));
		if (asDict == null) {
			return null;
		} else {
			return new ITextPdfDict(asDict);
		}
	}

	@Override
	public PdfArray getAsArray(String name) {
		com.lowagie.text.pdf.PdfArray asArray = wrapped.getAsArray(new PdfName(
				name));
		if (asArray == null) {
			return null;
		} else {
			return new ITextPdfArray(asArray);
		}
	}

	@Override
	public boolean hasANameWithValue(String name, String value) {
		PdfName asName = wrapped.getAsName(new PdfName(name));
		if (asName == null) {
			LOG.info("No value with name " + name);
			return false;
		}

		PdfName asValue = new PdfName(value);
		boolean r = asName.equals(asValue);
		LOG.info("Comparison of " + asName + "(" + asName.getClass() + ")"
				+ " and " + asValue + " : " + r);
		return r;
	}

	@Override
	public byte[] get(String name) {
		PdfObject val = wrapped.get(new PdfName(name));
		if (val == null) {
			return null;
		}
		return val.getBytes();
	}

	@Override
	public boolean hasAName(String name) {
		return wrapped.get(new PdfName(name)) != null;
	}

	@Override
	public String[] list() {
		// TODO Auto-generated method stub
		return null;
	}

}
