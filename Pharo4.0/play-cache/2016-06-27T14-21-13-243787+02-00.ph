|tokenIdentifier|string := '.. asdadf ,d 1d.'.tokenIdentifier := (#word asParser plus/ #punctuation asParser)flatten trim.tokenIdentifier matchesSkipIn: string.