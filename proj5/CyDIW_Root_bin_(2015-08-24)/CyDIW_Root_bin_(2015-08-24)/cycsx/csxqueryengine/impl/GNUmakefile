include dir.mk
include $(TOP)/rules.mk

DATE=$(shell date +"20%y-%m-%d")

jar:
	$(JAR) -cvf xacute_$(DATE).jar `find . -name "*\.class"`
