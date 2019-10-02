package com.zhwxp.sample.spring.boot.tcp.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;

import lombok.extern.slf4j.Slf4j;

/**
 * Customer Serializer / Deserializer used for communication with MK500
 * terminals The data stream is ended by \r\n (CRLF) characters All characters
 * from message are encoded on 2 bytes - the first is \0, and the second byte is
 * the character that contains data
 * 
 * @author Danut
 *
 */

@Slf4j
public class MK500SerializerDeserializer implements Serializer<String>, Deserializer<String> {

	@Override
	public void serialize(String object, OutputStream outputStream) throws IOException {
		byte[] message = transformForSending(object);
		outputStream.write(message);
		outputStream.flush();
	}

	private byte[] transformForSending(String message) {
		byte[] init = message.getBytes();
		byte[] result = new byte[init.length * 2 + 4];
		int idx = 0;
		for (byte b : init) {
			result[idx] = '\0';
			result[idx + 1] = b;
			idx += 2;
		}
		result[idx] = '\0';
		result[idx + 1] = '\r';
		result[idx + 2] = '\0';
		result[idx + 3] = '\n';

		return result;
	}

	@Override
	public String deserialize(InputStream inputStream) throws IOException {
		String message = parseString(inputStream);
		return message;
	}

	private String parseString(InputStream inputStream) throws IOException {
		StringBuilder builder = new StringBuilder();

		int c1, c2, c3, c4;
		boolean stop = false;
		while (!stop) {
			c1 = inputStream.read();
			checkClosure(c1);
			c2 = inputStream.read();
			checkClosure(c2);
			if (c2 == '\r') {
				c3 = inputStream.read();
				checkClosure(c3);
				c4 = inputStream.read();
				checkClosure(c4);
				if (c4 == '\n') {
					stop = true;
				} else {
					builder.append((char) c2);
					builder.append((char) c4);
				}
			} else {
				builder.append((char) c2);
			}
		}
		return builder.toString();
	}

	/**
	 * Check whether the byte passed in is the "closed socket" byte
	 * 
	 * @param bite
	 * @throws IOException
	 */
	protected void checkClosure(int bite) throws IOException {
		if (bite < 0) {
			log.debug("Socket closed during message assembly");
			throw new IOException("Socket closed during message assembly");
		}
	}
}