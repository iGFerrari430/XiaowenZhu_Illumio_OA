# XiaowenZhu_Illumio_OA
## My intuitions
After seeing the problem,I paid special attention to one major requirement: After the input dataset has been loaded, we should be able to deal with subsequent large loads of requests fairly quickly, without major delay.<br/> Scanning through each rule is, obviously, not very ideal. There must be some methods of organizing the data better in expense of the memory. Thus, my optimization begins here <br/>
First, I noticed that there are only 4 possibilites of the first 2 entries of one rule,"direction" and "protocal", so we could use a HashMap that maps each possiblity to its associated data. This could, in average, decrease the runtime by 4 times without compensating any space. However, in terms of big O notation of runtime, nothing has changed. The searching time is still O(n).<br/>
Then,
