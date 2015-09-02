#   FILE: Makefile -- 
# AUTHOR: Albert Weichselbraun <albert.weichselbraun@wu-wien.ac.at> / Gerhard Wohlgenannt
#
FILE=uCompPlugin_v34


all:
	pdflatex $(FILE)
	pdflatex $(FILE)
	-bibtex ${FILE}
	-bibtex ${FILE}
	pdflatex $(FILE)
	pdflatex $(FILE)

clean:
	rm -f *.aux *.out *.log *.bbl *.blg *.pdf 

arno:
	cp -a *.tex /tmp
	cp -a *.pdf /tmp
