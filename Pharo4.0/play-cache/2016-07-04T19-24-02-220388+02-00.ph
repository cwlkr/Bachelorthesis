|language aPath anotherPath aType anotherType| aType := 'java'.anotherType := 'xml'.aPath := 'C:\Users\Cédric\Dropbox\pharo\testFiles\compare'.anotherPath := 'C:\Users\Cédric\Dropbox\pharo\testFiles\compare'.language := LanguageDifferentiator  new.language is: aPath with: aType thesameLanguageAs: anotherPath with: anotherType and: 7.