# XiaowenZhu_Illumio_OA
## My intuitions and Algorithm
After seeing the problem,I paid special attention to one major requirement: After the input dataset has been loaded, we should be able to deal with subsequent large loads of requests fairly quickly, without major delay.<br/> Scanning through each rule is, obviously, not very ideal. There must be some methods of organizing the data better in expense of the memory. <br/><br/>Thus, my optimization begins here.<br/><br/>
#### step 1 : dealing with "direction"/"protocal" entires
First, I noticed that there are only 4 possibilites of the first 2 entries of one rule,"direction" and "protocal", so we could use a HashMap that maps each possiblity to its associated data. This could, in average, decrease the runtime by 4 times without compensating any space. However, in terms of big O notation of runtime, nothing has changed. The searching time is still O(n).<br/>

#### step 2 : dealing with ports
Then, I realize that one of the other key could be used to map to other datas, so that the runtime complexity could be further reduced. However, ip address is not a good candidate, since the range is way too large. the port data on the other hand, could be used to be the key of another map, which maps to the list of mapped ip addresses. So the process is: given the range, add the associated ip address range to each number inside that port range.

#### step 3: dealing with ip addresses
So far, for each direction/protocal entries, and for each possible port, we have got a list of ip address ranges. We could then sort and merge these ip addresses, so that binary search could be used when searching whether ip address is in one of the ranges.

#### step 4: Summary and Furthur thought
Thus, the final data structure will look like this in Java: 
<code>
  // where string represents the protocal/direction pair; integer represents port,
  // and ArrayList<long[]> represents the ip address ranges.
  HashMap<String,HashMap<Integer,ArrayList<long[]>>> map
</code>
