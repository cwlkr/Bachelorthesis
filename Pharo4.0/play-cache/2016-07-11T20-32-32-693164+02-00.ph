|stream|stream := String new writeStream.(0 to: 2) do: [ :k|	|n|	n := 3 - k.	stream nextPutAll: n asString. ].stream contents.