package com.re.paas.api.fusion;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class Buffer {

	protected static BufferFactory factory;
	
	/**
	 * Create a new, empty buffer.
	 *
	 * @return the buffer
	 */
	public static Buffer buffer() {
		return factory.buffer();
	}

	/**
	 * Create a new buffer given the initial size hint.
	 * <p>
	 * If you know the buffer will require a certain size, providing the hint can
	 * prevent unnecessary re-allocations as the buffer is written to and resized.
	 *
	 * @param initialSizeHint the hint, in bytes
	 * @return the buffer
	 */
	public static Buffer buffer(int initialSizeHint) {
		return factory.buffer(initialSizeHint);
	}

	/**
	 * Create a new buffer from a string. The string will be UTF-8 encoded into the
	 * buffer.
	 *
	 * @param string the string
	 * @return the buffer
	 */
	public static Buffer buffer(String string) {
		return factory.buffer(string);
	}

	/**
	 * Create a new buffer from a string and using the specified encoding. The
	 * string will be encoded into the buffer using the specified encoding.
	 *
	 * @param string the string
	 * @return the buffer
	 */
	public static Buffer buffer(String string, String enc) {
		return enc != null ? factory.buffer(string, enc) : factory.buffer(string);
	}

	/**
	 * Create a new buffer from a byte[]. The byte[] will be copied to form the
	 * buffer.
	 *
	 * @param bytes the byte array
	 * @return the buffer
	 */
	public static Buffer buffer(byte[] bytes) {
		return factory.buffer(bytes);
	}

	/**
	 * Returns a {@code String} representation of the Buffer with the
	 * {@code UTF-8 }encoding
	 */
	public abstract String toString();

	/**
	 * Returns a {@code String} representation of the Buffer with the encoding
	 * specified by {@code enc}
	 */
	public abstract String toString(String enc);

	/**
	 * Returns a {@code String} representation of the Buffer with the encoding
	 * specified by {@code enc}
	 */
	public abstract String toString(Charset enc);

	/**
	 * Returns a Json object representation of the Buffer
	 */
	public abstract JsonObject toJsonObject();

	/**
	 * Returns a Json array representation of the Buffer
	 */
	public abstract JsonArray toJsonArray();

	/**
	 * Returns the {@code byte} at position {@code pos} in the Buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 1} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract byte getByte(int pos);

	/**
	 * Returns the unsigned {@code byte} at position {@code pos} in the Buffer, as a
	 * {@code short}.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 1} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract short getUnsignedByte(int pos);

	/**
	 * Returns the {@code int} at position {@code pos} in the Buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 4} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract int getInt(int pos);

	/**
	 * Gets a 32-bit integer at the specified absolute {@code index} in this buffer
	 * with Little Endian Byte Order.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 4} is greater
	 *                                   than {@code this.capacity}
	 */
	public abstract int getIntLE(int pos);

	/**
	 * Returns the unsigned {@code int} at position {@code pos} in the Buffer, as a
	 * {@code long}.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 4} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract long getUnsignedInt(int pos);

	/**
	 * Returns the unsigned {@code int} at position {@code pos} in the Buffer, as a
	 * {@code long} in Little Endian Byte Order.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 4} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract long getUnsignedIntLE(int pos);

	/**
	 * Returns the {@code long} at position {@code pos} in the Buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 8} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract long getLong(int pos);

	/**
	 * Gets a 64-bit long integer at the specified absolute {@code index} in this
	 * buffer in Little Endian Byte Order.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 8} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract long getLongLE(int pos);

	/**
	 * Returns the {@code double} at position {@code pos} in the Buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 8} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract double getDouble(int pos);

	/**
	 * Returns the {@code float} at position {@code pos} in the Buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 4} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract float getFloat(int pos);

	/**
	 * Returns the {@code short} at position {@code pos} in the Buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 2} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract short getShort(int pos);

	/**
	 * Gets a 16-bit short integer at the specified absolute {@code index} in this
	 * buffer in Little Endian Byte Order.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 2} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract short getShortLE(int pos);

	/**
	 * Returns the unsigned {@code short} at position {@code pos} in the Buffer, as
	 * an {@code int}.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code pos} is less than
	 *                                   {@code 0} or {@code pos + 2} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract int getUnsignedShort(int pos);

	/**
	 * Gets an unsigned 16-bit short integer at the specified absolute {@code index}
	 * in this buffer in Little Endian Byte Order.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 2} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract int getUnsignedShortLE(int pos);

	/**
	 * Gets a 24-bit medium integer at the specified absolute {@code index} in this
	 * buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 3} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract int getMedium(int pos);

	/**
	 * Gets a 24-bit medium integer at the specified absolute {@code index} in this
	 * buffer in the Little Endian Byte Order.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 3} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract int getMediumLE(int pos);

	/**
	 * Gets an unsigned 24-bit medium integer at the specified absolute
	 * {@code index} in this buffer.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 3} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract int getUnsignedMedium(int pos);

	/**
	 * Gets an unsigned 24-bit medium integer at the specified absolute
	 * {@code index} in this buffer in Little Endian Byte Order.
	 *
	 * @throws IndexOutOfBoundsException if the specified {@code index} is less than
	 *                                   {@code 0} or {@code index + 3} is greater
	 *                                   than the length of the Buffer.
	 */
	public abstract int getUnsignedMediumLE(int pos);

	/**
	 * Returns a copy of the entire Buffer as a {@code byte[]}
	 */
	public abstract byte[] getBytes();

	/**
	 * Returns a copy of a sub-sequence the Buffer as a {@code byte[]} starting at
	 * position {@code start} and ending at position {@code end - 1}
	 */
	public abstract byte[] getBytes(int start, int end);

	/**
	 * Transfers the content of the Buffer into a {@code byte[]}.
	 *
	 * @param dst the destination byte array
	 * @throws IndexOutOfBoundsException if the content of the Buffer cannot fit
	 *                                   into the destination byte array
	 */
	public abstract Buffer getBytes(byte[] dst);

	/**
	 * Transfers the content of the Buffer into a {@code byte[]} at the specific
	 * destination.
	 *
	 * @param dst the destination byte array
	 * @throws IndexOutOfBoundsException if the content of the Buffer cannot fit
	 *                                   into the destination byte array
	 */
	public abstract Buffer getBytes(byte[] dst, int dstIndex);

	/**
	 * Transfers the content of the Buffer starting at position {@code start} and
	 * ending at position {@code end - 1} into a {@code byte[]}.
	 *
	 * @param dst the destination byte array
	 * @throws IndexOutOfBoundsException if the content of the Buffer cannot fit
	 *                                   into the destination byte array
	 */
	public abstract Buffer getBytes(int start, int end, byte[] dst);

	/**
	 * Transfers the content of the Buffer starting at position {@code start} and
	 * ending at position {@code end - 1} into a {@code byte[]} at the specific
	 * destination.
	 *
	 * @param dst the destination byte array
	 * @throws IndexOutOfBoundsException if the content of the Buffer cannot fit
	 *                                   into the destination byte array
	 */
	public abstract Buffer getBytes(int start, int end, byte[] dst, int dstIndex);

	/**
	 * Returns a copy of a sub-sequence the Buffer as a
	 * {@link io.vertx.core.buffer.Buffer} starting at position {@code start} and
	 * ending at position {@code end - 1}
	 */
	public abstract Buffer getBuffer(int start, int end);

	/**
	 * Returns a copy of a sub-sequence the Buffer as a {@code String} starting at
	 * position {@code start} and ending at position {@code end - 1} interpreted as
	 * a String in the specified encoding
	 */
	public abstract String getString(int start, int end, String enc);

	/**
	 * Returns a copy of a sub-sequence the Buffer as a {@code String} starting at
	 * position {@code start} and ending at position {@code end - 1} interpreted as
	 * a String in UTF-8 encoding
	 */
	public abstract String getString(int start, int end);

	/**
	 * Appends the specified {@code Buffer} to the end of this Buffer. The buffer
	 * will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendBuffer(Buffer buff);

	/**
	 * Appends the specified {@code Buffer} starting at the {@code offset} using
	 * {@code len} to the end of this Buffer. The buffer will expand as necessary to
	 * accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendBuffer(Buffer buff, int offset, int len);

	/**
	 * Appends the specified {@code byte[]} to the end of the Buffer. The buffer
	 * will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendBytes(byte[] bytes);

	/**
	 * Appends the specified number of bytes from {@code byte[]} to the end of the
	 * Buffer, starting at the given offset. The buffer will expand as necessary to
	 * accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendBytes(byte[] bytes, int offset, int len);

	/**
	 * Appends the specified {@code byte} to the end of the Buffer. The buffer will
	 * expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendByte(byte b);

	/**
	 * Appends the specified unsigned {@code byte} to the end of the Buffer. The
	 * buffer will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendUnsignedByte(short b);

	/**
	 * Appends the specified {@code int} to the end of the Buffer. The buffer will
	 * expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendInt(int i);

	/**
	 * Appends the specified {@code int} to the end of the Buffer in the Little
	 * Endian Byte Order. The buffer will expand as necessary to accommodate any
	 * bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendIntLE(int i);

	/**
	 * Appends the specified unsigned {@code int} to the end of the Buffer. The
	 * buffer will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendUnsignedInt(long i);

	/**
	 * Appends the specified unsigned {@code int} to the end of the Buffer in the
	 * Little Endian Byte Order. The buffer will expand as necessary to accommodate
	 * any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendUnsignedIntLE(long i);

	/**
	 * Appends the specified 24bit {@code int} to the end of the Buffer. The buffer
	 * will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendMedium(int i);

	/**
	 * Appends the specified 24bit {@code int} to the end of the Buffer in the
	 * Little Endian Byte Order. The buffer will expand as necessary to accommodate
	 * any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendMediumLE(int i);

	/**
	 * Appends the specified {@code long} to the end of the Buffer. The buffer will
	 * expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendLong(long l);

	/**
	 * Appends the specified {@code long} to the end of the Buffer in the Little
	 * Endian Byte Order. The buffer will expand as necessary to accommodate any
	 * bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendLongLE(long l);

	/**
	 * Appends the specified {@code short} to the end of the Buffer.The buffer will
	 * expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendShort(short s);

	/**
	 * Appends the specified {@code short} to the end of the Buffer in the Little
	 * Endian Byte Order.The buffer will expand as necessary to accommodate any
	 * bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendShortLE(short s);

	/**
	 * Appends the specified unsigned {@code short} to the end of the Buffer.The
	 * buffer will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendUnsignedShort(int s);

	/**
	 * Appends the specified unsigned {@code short} to the end of the Buffer in the
	 * Little Endian Byte Order.The buffer will expand as necessary to accommodate
	 * any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendUnsignedShortLE(int s);

	/**
	 * Appends the specified {@code float} to the end of the Buffer. The buffer will
	 * expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendFloat(float f);

	/**
	 * Appends the specified {@code double} to the end of the Buffer. The buffer
	 * will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 */
	public abstract Buffer appendDouble(double d);

	/**
	 * Appends the specified {@code String} to the end of the Buffer with the
	 * encoding as specified by {@code enc}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together.
	 * <p>
	 */
	public abstract Buffer appendString(String str, String enc);

	/**
	 * Appends the specified {@code String str} to the end of the Buffer with UTF-8
	 * encoding.
	 * <p>
	 * The buffer will expand as necessary to accommodate any bytes written.
	 * <p>
	 * Returns a reference to {@code this} so multiple operations can be appended
	 * together
	 * <p>
	 */
	public abstract Buffer appendString(String str);

	/**
	 * Sets the {@code byte} at position {@code pos} in the Buffer to the value
	 * {@code b}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setByte(int pos, byte b);

	/**
	 * Sets the unsigned {@code byte} at position {@code pos} in the Buffer to the
	 * value {@code b}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setUnsignedByte(int pos, short b);

	/**
	 * Sets the {@code int} at position {@code pos} in the Buffer to the value
	 * {@code i}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setInt(int pos, int i);

	/**
	 * Sets the {@code int} at position {@code pos} in the Buffer to the value
	 * {@code i} in the Little Endian Byte Order.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setIntLE(int pos, int i);

	/**
	 * Sets the unsigned {@code int} at position {@code pos} in the Buffer to the
	 * value {@code i}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setUnsignedInt(int pos, long i);

	/**
	 * Sets the unsigned {@code int} at position {@code pos} in the Buffer to the
	 * value {@code i} in the Little Endian Byte Order.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setUnsignedIntLE(int pos, long i);

	/**
	 * Sets the 24bit {@code int} at position {@code pos} in the Buffer to the value
	 * {@code i}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setMedium(int pos, int i);

	/**
	 * Sets the 24bit {@code int} at position {@code pos} in the Buffer to the value
	 * {@code i}. in the Little Endian Byte Order
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setMediumLE(int pos, int i);

	/**
	 * Sets the {@code long} at position {@code pos} in the Buffer to the value
	 * {@code l}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setLong(int pos, long l);

	/**
	 * Sets the {@code long} at position {@code pos} in the Buffer to the value
	 * {@code l} in the Little Endian Byte Order.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setLongLE(int pos, long l);

	/**
	 * Sets the {@code double} at position {@code pos} in the Buffer to the value
	 * {@code d}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setDouble(int pos, double d);

	/**
	 * Sets the {@code float} at position {@code pos} in the Buffer to the value
	 * {@code f}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setFloat(int pos, float f);

	/**
	 * Sets the {@code short} at position {@code pos} in the Buffer to the value
	 * {@code s}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setShort(int pos, short s);

	/**
	 * Sets the {@code short} at position {@code pos} in the Buffer to the value
	 * {@code s} in the Little Endian Byte Order.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setShortLE(int pos, short s);

	/**
	 * Sets the unsigned {@code short} at position {@code pos} in the Buffer to the
	 * value {@code s}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setUnsignedShort(int pos, int s);

	/**
	 * Sets the unsigned {@code short} at position {@code pos} in the Buffer to the
	 * value {@code s} in the Little Endian Byte Order.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setUnsignedShortLE(int pos, int s);

	/**
	 * Sets the bytes at position {@code pos} in the Buffer to the bytes represented
	 * by the {@code Buffer b}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setBuffer(int pos, Buffer b);

	/**
	 * Sets the bytes at position {@code pos} in the Buffer to the bytes represented
	 * by the {@code Buffer b} on the given {@code offset} and {@code len}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setBuffer(int pos, Buffer b, int offset, int len);

	/**
	 * Sets the bytes at position {@code pos} in the Buffer to the bytes represented
	 * by the {@code ByteBuffer b}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setBytes(int pos, ByteBuffer b);

	/**
	 * Sets the bytes at position {@code pos} in the Buffer to the bytes represented
	 * by the {@code byte[] b}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setBytes(int pos, byte[] b);

	/**
	 * Sets the given number of bytes at position {@code pos} in the Buffer to the
	 * bytes represented by the {@code byte[] b}.
	 * <p>
	 * </p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setBytes(int pos, byte[] b, int offset, int len);

	/**
	 * Sets the bytes at position {@code pos} in the Buffer to the value of
	 * {@code str} encoded in UTF-8.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setString(int pos, String str);

	/**
	 * Sets the bytes at position {@code pos} in the Buffer to the value of
	 * {@code str} encoded in encoding {@code enc}.
	 * <p>
	 * The buffer will expand as necessary to accommodate any value written.
	 */

	public abstract Buffer setString(int pos, String str, String enc);

	/**
	 * Returns the length of the buffer, measured in bytes. All positions are
	 * indexed from zero.
	 */
	public abstract int length();

	/**
	 * Returns a copy of the entire Buffer.
	 */
	public abstract Buffer copy();

	/**
	 * Returns a slice of this buffer. Modifying the content of the returned buffer
	 * or this buffer affects each other's content while they maintain separate
	 * indexes and marks.
	 */
	public abstract Buffer slice();

	/**
	 * Returns a slice of this buffer. Modifying the content of the returned buffer
	 * or this buffer affects each other's content while they maintain separate
	 * indexes and marks.
	 */
	public abstract Buffer slice(int start, int end);

	public abstract void writeToBuffer(Buffer buffer);

	public abstract int readFromBuffer(int pos, Buffer buffer);

	/**
	 * Returns the underlying Buffer
	 * <p>
	 * The returned buffer is a duplicate.
	 * <p>
	 * The returned {@code ByteBuf} might have its {@code readerIndex > 0} This
	 * method is meant for internal use only.
	 * <p>
	 */

	public abstract Object getByteBuf();

}