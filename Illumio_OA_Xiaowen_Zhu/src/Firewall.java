import java.io.File;
import java.io.IOException;
import java.util.*;
public class Firewall {
    /**
     * main data member of the Firewall class.
     * compress the direction/protocol to one pair string,
     * map the pair to another hashmap, then match the port to ranges of available IP addresses.
     */
    public HashMap<String,HashMap<Integer,ArrayList<long[]>>> map = new HashMap<>();
    /**
     * helper function to convert a IP address string to long.
     */
    public long ip2long(String ip){
        String [] nums = ip.split("\\.");
        long multiplier = 1;
        long result = 0;
        for (int i=nums.length-1; i>=0; i--){
            long val = Long.parseLong(nums[i]);
            result += val*multiplier;
            multiplier *= 256;
        }
        return result;

    }




    /**
     * After reading the file, for each port we have a list of intervals. now, sort and merge these intervals
     * so that we could apply binary search when accepting packets.
     */
    private ArrayList<long[]> mergeIntervals(ArrayList<long[]> intervals){
        ArrayList<long[]> res = new ArrayList<>();
        // sort based on the start of each interval with increasing order.
        Collections.sort(intervals,(a1,a2) -> {
            if (a1[0] < a2[0]){
                return -1;
            }else if (a1[0] == a2[0]){
                return 0;
            }else{
                return 1;
            }
        });

        int ind = 0;
        // the process of merging the intervals.
        while (ind < intervals.size()){
            long start = intervals.get(ind)[0];
            long finish = intervals.get(ind)[1];
            while (ind + 1 < intervals.size() && intervals.get(ind+1)[0] < finish){
                ind++;
                finish = Math.max(finish,intervals.get(ind)[1]);
            }
            ind++;
            long [] merged = {start,finish};
            res.add(merged);
        }
        return res;
    }

    /**
     * each direction-protocal pair, we find all ports. then for each ports, sort and merge
     * all the ip intervals. more detail could be found on the description of above function.
     */
    private void sortLists() {
        for (Map.Entry<String,HashMap<Integer,ArrayList<long[]>>> entry : map.entrySet()){
            for (Map.Entry<Integer,ArrayList<long[]>> nestedEntry : entry.getValue().entrySet()){
                entry.getValue().put(nestedEntry.getKey(),mergeIntervals(nestedEntry.getValue()));
            }
        }
    }
    /**
     * reading the file, then store the raw data(without merging) inside 'map' data member.
     * the description of 'map' data member can be found at the beginning of this file.
     */
    private boolean readFile(String fileName){
        try {
            File file = new File(fileName);
            Scanner sc = new Scanner(file);
            // read each line from the file. (It is assumed the file is valid and no empty line exists at the end of file.
            while(sc.hasNextLine()){
                String [] elements = sc.nextLine().split("\\,");
                // get the pair for direction and protocol;
                String pair = elements[0]+elements[1];

                if (!map.containsKey(pair)){
                    map.put(pair,new HashMap<>());
                }
                HashMap<Integer,ArrayList<long[]>> thisMap = map.get(pair);

                String [] port = elements[2].split("\\-");
                int [] portRange = new int[2];
                // create the range of the port. if there is only one number in input, make it a range s.t. src and dest is the same.
                if (port.length == 2){
                    portRange[0] = Integer.parseInt(port[0]);
                    portRange[1] = Integer.parseInt(port[1]);
                }else{ // when the given port information is a single number.
                    int portValue = Integer.parseInt(port[0]);
                    portRange[0] = portValue;
                    portRange[1] = portValue;
                }

                /*
                 * create the range of ip address. similarly, if there is only one number in input, make it a range s.t.
                 * src and dest is the same.
                 */
                String [] ip = elements[3].split("\\-");
                long [] ipRange = new long[2];
                if (ip.length == 2){
                    ipRange[0] = ip2long(ip[0]);
                    ipRange[1] = ip2long(ip[1]);
                }else{
                    long ipValue = ip2long(ip[0]);
                    ipRange[0] = ipValue;
                    ipRange[1] = ipValue;
                }
                for (int i=portRange[0]; i<=portRange[1]; i++){
                    if (!thisMap.containsKey(i)) {
                        thisMap.put(i,new ArrayList<long[]>());
                    }
                    thisMap.get(i).add(ipRange);
                }
            }
        }catch(IOException e){
            // if there is any error, stop reading the file and return false.
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public Firewall(String fileName){
        boolean isFileOK = readFile(fileName);
        if (!isFileOK){
            return;
        }
        this.sortLists();
        // sort and merge intervals so that we could binary search it later.
    }

    public boolean accept_packet(String direction, String protocal, int port, String ip){
        String pair = direction+protocal;
        HashMap<Integer,ArrayList<long[]>> nestedMap = map.get(pair);
        if (nestedMap == null){
            return false;
        }
        ArrayList<long[]> ipList = nestedMap.get(port);
        if (ipList == null){ // means this port does not exist.
             return false;
        }
        //binary search the ipList.
        int start = 0;
        int end = ipList.size() - 1;
        long ipVal = this.ip2long(ip);
        while (start <= end){
            int mid = start + (end-start)/2;
            if (ipVal <= ipList.get(mid)[1] && ipVal >= ipList.get(mid)[0]){
                return true;
            }else if (ipVal > ipList.get(mid)[1]){
                start = mid+1;
            }else{
                end = mid-1;
            }
        }
        return false;
        // the last criteria, IP ,fails to match. Thus, we return false.
    }
    public static void main (String [] args){
        int fileIndex = 4;
        Firewall wall = new Firewall("./src/input"+fileIndex+".csv");
        switch (fileIndex) {
            case 0 : // default test set:
                System.out.println(wall.accept_packet("inbound","tcp",80,"192.168.1.2")+" expected: true"); // expected: true
                System.out.println(wall.accept_packet("inbound","udp",53,"192.168.2.1")+" expected: true"); // expected: true
                System.out.println(wall.accept_packet("outbound","tcp",10324,"192.168.10.11")+" expected: true"); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",81,"192.168.1.2")+" expected: false"); // expected: false
                System.out.println(wall.accept_packet("inbound","udp",24,"52.12.48.92")+" expected: false"); // expected: false
                break;
            case 1: // test set with changing port data and fixed others. test the correctness of the mapping of ports.
                System.out.println(wall.accept_packet("inbound","tcp",2,"192.168.10.11")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",4,"192.168.10.11")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",7,"192.168.10.11")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",10,"192.168.10.11")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",18,"192.168.10.11")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",15,"192.168.10.11")); // expected: false
                System.out.println(wall.accept_packet("inbound","tcp",1,"192.168.10.11"));  // expected: false
                break;
            case 2: /**
                        test set with changing ip address and fixed others. test the correctness of merging intervals.
                        also test nonexistant direction/protocal pair as well as port.

                    */
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.2")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.5")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.4")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.9")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.33")); // expected: false
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.48")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.10")); // expected: false
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.1")); // expected: false
                System.out.println(wall.accept_packet("inbound","tcp",0,"0.0.0.51")); // expected: false
                System.out.println(wall.accept_packet("outbound","tcp",0,"0.0.0.2")); // expected: false
                System.out.println(wall.accept_packet("inbound","tcp",1,"0.0.0.2")); // expected: false
                break;
            case 3: // testing cases that uses lots of spaces.
                System.out.println(wall.accept_packet("inbound","tcp",32321,"255.255.255.255")); // expected: true
                System.out.println(wall.accept_packet("inbound","tcp",56723,"0.0.255.255")); // expected: true
                break;
            case 4: // stress test on having 10 million inputs. check the elapsed time.
                Date start = new Date();
                for (int i=0; i<10000000; i++){
                    wall.accept_packet("inbound","tcp",43263,"0.0.0.2");
                }
                Date end = new Date();
                System.out.println("Time elapsed: "+(float)(end.getTime() - start.getTime())/1000+" seconds");
                break;
            default:
                System.out.println("no such file.");
                break;
        }




    }
}
