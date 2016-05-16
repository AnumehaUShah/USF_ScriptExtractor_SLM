package edu.ca.usf.scriptextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforms an input into List<Character>
 * 
 * Ignores whitespace
 */
public class CharacterKeywordTransform extends InputTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8039430004908751825L;
	private String[] reserved = { "download", "document", "break", "case", "catch",
			"continue", "debugger", "default", "delete", "do", "eval", "else",
			"finally", "for", "function", "if", "length", "in", "instanceof",
			"new", "random", "replace", "return", "switch", "this", "throw",
			"try", "typeof", "var", "void", "window", "write", "fromcharcode",
			"while", "with", "unescape", "constructor", "toExponential",
			"toFixed", "toLocaleString", "toPrecision", "toString", "valueOf",
			"toSource", "toString", "valueOf", "charAt", "charCodeAt",
			"concat", "indexOf", "lastIndexOf", "localeCompare", "length",
			"match", "replace", "search", "slice", "split", "substr",
			"substring", "toLocaleLowerCase", "toLocaleUpperCase",
			"toLowerCase", "toString", "toUpperCase", "valueOf", "anchor",
			"big", "blink", "bold", "fixed", "fontcolor", "fontsize",
			"italics", "link", "small", "strike", "sub", "sup", "concat",
			"every", "filter", "forEach", "indexOf", "join", "lastIndexOf",
			"map", "pop", "push", "reduce", "reduceRight", "reverse", "shift",
			"slice", "some", "toSource", "sort", "splice", "toString",
			"unshift", "Date", "getDate", "getDay", "getFullYear", "getHours",
			"getMilliseconds", "getMinutes", "getMonth", "getSeconds",
			"getTime", "getTimezoneOffset", "getUTCDate", "getUTCDay",
			"getUTCFullYear", "getUTCHours", "getUTCMilliseconds",
			"getUTCMinutes", "getUTCMonth", "getUTCSeconds", "getYear",
			"setDate", "setFullYear", "setHours", "setMilliseconds",
			"setMinutes", "setMonth", "setSeconds", "setTime", "setUTCDate",
			"setUTCFullYear", "setUTCHours", "setUTCMilliseconds",
			"setUTCMinutes", "setUTCMonth", "setUTCSeconds", "setYear",
			"toDateString", "toGMTString", "toLocaleDateString",
			"toLocaleFormat", "toLocaleString", "toLocaleTimeString",
			"toSource", "toString", "toTimeString", "toUTCString", "valueOf",
			"abs", "acos", "asin", "atan", "atan2", "ceil", "cos", "exp",
			"floor", "log", "max", "min", "pow", "random", "round", "sin",
			"sqrt", "tan", "toSource", "exec", "test", "toSource", "toString",
			"element", "category", "name", "class", "http://","http","www.",".com","href","html" };

	@Override
	public List<Object> transform(String input) {
		input = input.toLowerCase();
		List<Object> characters = new ArrayList<Object>();

		char curChar;

		boolean inComment = false;
		boolean ignoreUntilNewline = false;

		main: for (int i = 0; i < input.length(); i++) {

			if (!inComment && !ignoreUntilNewline) {
				for (int j = 0; j < reserved.length; j++) {
					if (input.startsWith(reserved[j], i)) {
						characters.add(reserved[j]);
						i += reserved[j].length() - 1;
						continue main;

					}
					
				}
			}

			curChar = input.charAt(i);

			try {
				if (curChar == '/') {
					if (input.charAt(i + 1) == '/') {
						ignoreUntilNewline = true;
						continue main;
					} else if (input.charAt(i + 1) == '*') {
						ignoreUntilNewline = false;
						inComment = true;
						continue main;
					} else if (inComment && input.charAt(i - 1) == '*') {
						ignoreUntilNewline = false;
						inComment = false;
						continue main;
					}
				}
			} catch (Exception e) {

			}
			if (curChar == ' ' || curChar == '\t') {
				// ignore whitespace
			} else if (curChar == '\n' || curChar == '\r') {
				ignoreUntilNewline = false;

			} else if (!inComment && !ignoreUntilNewline) {
				characters.add(input.charAt(i));
			}
		}

		return characters;
	}
}
