all: jcheminf_article.pdf

jcheminf_article.bbl: jcheminf_article.bib
	pdflatex jcheminf_article || true
	bibtex jcheminf_article || true

jcheminf_article.pdf: jcheminf_article.tex jcheminf_article.bbl
	pdflatex jcheminf_article.tex
	pdflatex jcheminf_article.tex
	pdflatex jcheminf_article.tex

distclean: clean

clean:
	rm -f *.bbl *.aux art.pdf jcheminf_article.pdf *.blg *.log *.ps *.fff *.lof *.lot *.ttt *.dvi *~ *.Rout
