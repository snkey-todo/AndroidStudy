package com.hxsj.telephone.http;

import java.io.IOException;
import java.io.InputStream;

public class UrlBuilder {

	public static InputStream read(String fn) throws IOException {
		InputStream inputStream = UrlBuilder.class.getClassLoader()
				.getResourceAsStream(fn);
		if (inputStream != null)
			return inputStream;
		else
			throw new IOException("在src目录下找不到" + fn);
	}
}
