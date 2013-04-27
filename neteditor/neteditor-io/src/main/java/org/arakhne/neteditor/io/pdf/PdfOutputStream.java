/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */

package org.arakhne.neteditor.io.pdf ;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/** An output stream for the PDF exporter.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PdfOutputStream extends OutputStream {

	private final CharsetEncoder encoder;
	private final OutputStream stream;
	private int bytes = 0;

	/**
	 * @param stream
	 */
	public PdfOutputStream(OutputStream stream) {
		this.encoder = Charset.forName("ISO-8859-1").newEncoder(); //$NON-NLS-1$
		this.stream = stream;
	}
	
	/** Replies the number of bytes written in the stream.
	 * 
	 * @return the number of bytes.
	 */
	public int size() {
		return this.bytes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(int b) throws IOException {
		this.stream.write(b);
		++this.bytes;
	}
	
	/**
	 * Write a sequence of characters.
	 * 
	 * @param text
	 * @return the number of bytes written.
	 * @throws IOException
	 */
	public int write(String text) throws IOException {
		ByteBuffer bb = this.encoder.encode(CharBuffer.wrap(text));
		byte[] array = bb.array();
		this.stream.write(array);
		this.bytes += array.length;
		return array.length;
	}

	/**
	 * Write a sequence of characters followed by a carriage return.
	 * 
	 * @param text
	 * @return the number of bytes written.
	 * @throws IOException
	 */
	public int writeln(String text) throws IOException {
		int n = write(text);
		if (n>0) {
			n += write("\n"); //$NON-NLS-1$
		}
		return n;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		this.stream.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() throws IOException {
		this.stream.flush();
	}

}