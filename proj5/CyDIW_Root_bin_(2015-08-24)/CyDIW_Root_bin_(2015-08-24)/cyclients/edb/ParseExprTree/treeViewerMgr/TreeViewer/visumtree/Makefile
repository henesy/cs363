
code: VisumTree.nw
	cd visumtree; noweb -t ../VisumTree.nw

class: code
	@ cd visumtree; \
    files=`ls *.java | awk -F. '{print $$1}'`; \
    update=`for f in $$files ; do \
        if [ $$f.java -nt $$f.class -o ! -e $$f.class ] ; then \
            echo $$f.java; \
        fi; \
    done` ; \
    if [ -n "$$update" ] ; then \
        echo javac $$update ; \
        javac -g $$update ; \
        chmod 755 *.class ; \
    fi

run: class
	java visumtree.TreeDemo

zip: class
	rm -rf zip/*
	mkdir zip/visumtree
	cp -f visumtree/License License-GPL.html README UPGRADE VisumTree.ps \
		VisumTree.pdf VisumTree Makefile *.nw visumtree/*.java \
		visumtree/*.class zip/visumtree
	rm -f zip/visumtree/VisumTree.nw
	cd zip; zip -r -v visumtree.zip visumtree/

doc: VisumTree.nw
	noweb -o VisumTree.nw
	latex VisumTree
	@ if grep "Rerun to get cross-references right" VisumTree.log ; then \
		echo latex VisumTree ; \
		latex VisumTree ; \
	fi
	dvips VisumTree

pdf:  
	ps2pdf VisumTree.ps VisumTree.pdf

html: code doc pdf zip
	cp zip/visumtree.zip /home/kromann/html/files
	cp VisumTree.pdf /home/kromann/html/files/visumtree.pdf
	
toc: doc
	latex VisumTree
	dvips VisumTree

VisumTree.nw: Reference.nw Value.nw AVM.nw License.nw Node.nw Tree.nw TreeDemo.nw TreeEvent.nw TreeInfo.nw TreeListener.nw TreePanel.nw VisumTree
	VisumTree License.nw Reference.nw TreeInfo.nw Value.nw AVM.nw \
		Node.nw Tree.nw TreePanel.nw TreeEvent.nw TreeListener.nw \
		TreeDemo.nw > VisumTree.nw

%.doc: %.nw
	VisumTree License.nw $^ > VisumTree.nw
	make doc

%.java: %.nw
	VisumTree License.nw $^ > VisumTree.nw
	make code

