# XiaowenZhu_Illumio_OA
## Configuration of the project
  I used Java to program this assignment.<br/>
  It could be opened in either eclipse or IntelliJ. The file's directory is src/Firewall.<br/>
  The method could be invoked using: <br/>
  <code>
    Firewall wall = new Firewall(inputFile);
    wall.accept_packet(direction,protocal,port,IPadress) // returns a boolean
  </code>
## My intuitions and Algorithm
After seeing the problem,I paid special attention to one major requirement: After the input dataset has been loaded, we should be able to deal with subsequent large loads of requests fairly quickly, without major delay.<br/> Scanning through each rule is, obviously, not very ideal. There must be some methods of organizing the data better in expense of the memory. <br/><br/>Thus, my optimization begins here.<br/><br/>
#### step 1 : dealing with "direction"/"protocal" entires
First, I noticed that there are only 4 possibilites of the first 2 entries of one rule,"direction" and "protocal", so we could use a HashMap that maps each possiblity to its associated data. This could, in average, decrease the runtime by 4 times without compensating any space. However, in terms of big O notation of runtime, nothing has changed. The searching time is still O(n).<br/>

#### step 2 : dealing with ports
Then, I realize that one of the other key could be used to map to other datas, so that the runtime complexity could be further reduced. However, ip address is not a good candidate, since the range is way too large. the port data on the other hand, could be used to be the key of another map, which maps to the list of mapped ip addresses. So the process is: given the range, add the associated ip address range to each number inside that port range.

#### step 3: dealing with ip addresses
So far, for each direction/protocal entries, and for each possible port, we have got a list of ip address ranges. We could then sort and merge these ip addresses, so that binary search could be used when searching whether ip address is in one of the ranges.

#### step 4: Summary and Furthur thought
Thus, the final data structure will look like this in Java: <br/><br/>
<code>
  HashMap<String,HashMap<Integer,ArrayList<long[]>>> map
</code> <br/><br/>
where string represents the protocal/direction pair; integer represents port,
and ArrayList<long[]> represents the ip address ranges.

This approach could reduce the worst case runtime to <code>O(logn)</code> where n is the number of rules in the database. 

This approach has a significant increase in the use of spaces. If we do not want this trade off,we could truncate step 2 and 3 and linear search all the port/ip pairs associated with it. This way the runtime is moderately reduced without expense of using more space.

## Testing of my Program
#### I tested my program in the following 5 cases
<ol>
  <li>testing the program against the example test cases</li>
  <li>testing the program with changing port ranges while other datas are fixed.<br/>
    Aiming to test the functionality of mapping ports to others.
  </li>
  <li>testing the program with changing ip ranges while other datas are fixed.
    <br/>Aiming to test the functionality of merging intervals and binary search.
  </li>
  <li>testing the program with full span of ports(0-65536). <br/>
    Aiming to test the performance of the program when lots of space are used.
  </li>
  <li> testing the program with 10 million requests. <br/>
    Aiming to test the running time of the program under long input dataset.</li>
</ol>

#### Result:
  <ul>
  <li> The first 4 cases passed with no errors. </li>
  <li> It took my program roughly 2 seconds to run the fifth test with 10 million 
      requests.
  </li>
  </ul>

## Refinement if I had more time:
I would like to dig deeper into how could I use less space to achieve the same goals. Currently each port in each port range are used as the key of a map, which costs lots of space.
