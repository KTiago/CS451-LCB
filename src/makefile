#makefile begins

JFLAGS = -g 
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		Pair.java\
		Utils.java\
		PacketWrapper.java\
		ParserMembership.java\
		DebugSignalHandler.java\
		PerfectLink.java\
		UniformReliableBroadcast.java\
                LCBroadcast.java\
		Da_proc.java\

MAIN = Da_proc

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
