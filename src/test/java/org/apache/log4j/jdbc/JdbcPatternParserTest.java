/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.jdbc;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class JdbcPatternParserTest {

    // REMINDER: Single quotes within double quotes are interpreted as single quotes. 
    // Two successive single quotes within two single quotes is interpreted as a single quote
    // 'abc''d' means abc'd. 
    
    String[] EMPTY_STRING_ARRAY = new String[] {};
    
    @Test
    public void testSingleQuotesAndSpaces() {
	// '' sees special interpretation by JdbcPatternParser?
	ParserState expected = new ParserState("INSERT INTO A1 (TITLE3) VALUES ( ?, ?, ?, ?,  ?, ? )", "%d", "%t", "%-5p", " '%c", "%x", "  -  %m%n ");
	otherAssert("INSERT INTO A1 (TITLE3) VALUES ( '%d', '%t', '%-5p', ' ''%c',  '%x', '  -  %m%n ' )", expected);
    }
    
    @Test
    public void testWithLiteralsAndSingleQuotes() {
	 
	String prefix = "INSERT INTO A1 (TITLE3) VALUES ( ' aString', 'anotherString with '' xyz'";
	
	ParserState expected = new ParserState(prefix +", ?)", "message: %m");
	otherAssert(prefix+", 'message: %m')", expected);
    }

    
    @Test
    public void testMixedPatterns() {
	ParserState expected = new ParserState("INSERT INTO A1 (TITLE3) VALUES ( ?, ?, ?, ?,  ?, ? )", "%d", "%d", "%-5p", " %c", "%x", "  -  %m%n");
	otherAssert("INSERT INTO A1 (TITLE3) VALUES ( '%d', '%d', '%-5p', ' %c',  '%x', '  -  %m%n' )", expected);
    }

    
    @Test
    public void testSingleLumpedValue() {
	ParserState expected = new ParserState("INSERT INTO A1 (TITLE3) VALUES ( ? )", " %d  -  %c %-5p %c %x  -  %m%n ");
	otherAssert("INSERT INTO A1 (TITLE3) VALUES ( ' %d  -  %c %-5p %c %x  -  %m%n ' )", expected);
    }

    private void otherAssert(String input, ParserState expected) {
	JdbcPatternParser parser = new JdbcPatternParser(input);
	List<String> patternStringReps = parser.getCopyOfpatternStringRepresentationList();
	
	ParserState actual = new ParserState(parser.getParameterizedSql(), patternStringReps.toArray(EMPTY_STRING_ARRAY));
	
	Assert.assertEquals(expected, actual);
    }
    
    
    private static class ParserState {
	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + Arrays.hashCode(args);
	    result = prime * result + ((expected == null) ? 0 : expected.hashCode());
	    return result;
	}

	@Override
	public String toString() {
	    return "ParserState [expected=" + expected + ", args=" + Arrays.toString(args) + "]";
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    ParserState other = (ParserState) obj;
	    if (!Arrays.equals(args, other.args))
		return false;
	    if (expected == null) {
		if (other.expected != null)
		    return false;
	    } else if (!expected.equals(other.expected))
		return false;
	    return true;
	}

	String expected;
	String[] args;
	
	ParserState(String expected, String... args) {
	    this.expected = expected;
	    this.args = args;
	}


    }
}
