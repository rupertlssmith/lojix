#!/bin/sh
find . -name '*.java' -exec sed -i -e '
s/\/\* private static final Logger trace = Logger\.getLogger(\(.*\)); \*\//private static final java.util.logging.Logger trace = java.util.logging.Logger\.getLogger(\1);/
s/\/\*\(trace\.fine.*;\)\*\//\1/
/trace\.fine/{
N
N
s/\/\*\(trace\.fine.*;\)\*\//\1/
}
s/private static final boolean TRACE = false; \/\/log.isLoggable(Level.FINE)/private static final boolean TRACE = trace.isLoggable(Level.FINE);/' {} \;

